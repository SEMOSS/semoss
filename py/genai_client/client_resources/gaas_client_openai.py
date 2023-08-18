
import openai
from genai_client.client_resources.gaas_client_base import BaseClient
import json
from string import Template

class OpenAiClient(BaseClient):
  # template to use
  # prefix
  #'vicuna-7b-v1.3'
  # openai.api_base = "https://play.semoss.org/fastchat/v1/"
  def __init__(self, template_file=None, endpoint=None, model_name='gpt-3.5-turbo', api_key="EMPTY", **kwargs):
    super().__init__(template_file=template_file)
    self.kwargs = kwargs
    self.template_file = template_file
    self.model_name = model_name
    self.instance_history = []
    if (endpoint!=None):
      self.endpoint = endpoint
    else:
      self.endpoint = openai.api_base
    assert self.endpoint is not None
    self.api_key = api_key
    self.available_models = []
    self.chat_type = 'chat-completion'
    if 'chat_type' in kwargs.keys():
      self.chat_type = kwargs.pop('chat_type')
    
  def ask(self, question:str=None, context=None, template_name=None, history:list=None, page_size=100, max_new_tokens=100, stop_sequences=["#", ";"], temperature_val=0.01, top_p_val=0.5, **kwargs):
    openai_base = openai.api_base
    # forcing the api_key to a dummy value
    if openai.api_key is None:
      openai.api_key = self.api_key
    openai.api_base = self.endpoint
    # first we determine the type of completion, since this determines how we
    # structure the payload
    final_query = "Please specify a valid completion type. Use 'chat-completion' or 'completion'"
    if self.chat_type == 'chat-completion':
      # the list to construct the payload from
      message_payload = []
      if 'full_prompt' not in kwargs.keys():
        # if the user provided context, use that. Otherwise, try to get it from the template
        mapping = {"question": question} | kwargs
        if context is not None:
          if isinstance(context, str):
            context = self.fill_context(context, **mapping)[0]
            message_payload.append({"role": "system", "content": context})
        else:
          if template_name != None:
            possibleContent = self.fill_template(template_name=template_name, **mapping)[0]
            if possibleContent != None:
              message_payload.append({"role": "system", "content": possibleContent})

        # if history was added, then add it to the payload. Currently history is being like OpenAI prompts
        if history is not None:
          message_payload.extend(history)

        # add the new question to the payload
        if (question != None and len(question) > 0):
          message_payload.append({"role": "user", "content": question})
        
        # add the message payload as a kwarg
        kwargs['messages'] = message_payload
        
        completion = openai.ChatCompletion.create(model=self.model_name, **kwargs)
        response = completion.choices[0].message.content
        final_query = response
      else:
        full_prompt = kwargs.pop('full_prompt')
        if isinstance(full_prompt, list):
          listOfDicts = set([isinstance(x, dict) for x in full_prompt]) == {True}
          if (listOfDicts == False):
            raise ValueError("The provided payload is not valid")
          # now we have to check the key value pairs are valid
          all_keys_set = {key for d in full_prompt for key in d.keys()}
          validOpenAiDictKey = sorted(all_keys_set) == ['content', 'role']
          if (validOpenAiDictKey == False):
            raise ValueError("There are invalid OpenAI dictionary keys")
          # add it the message payload
          message_payload.extend(full_prompt)
        
        kwargs['messages'] = message_payload
        print(message_payload)
        completion = openai.ChatCompletion.create(model=self.model_name, **kwargs)
        response = completion.choices[0].message.content
        final_query = response

    elif self.chat_type == 'completion':
      prompt = ""
      mapping = {"question": question}

      # default the template name based on model
      if template_name is None and context is None:
        template_name = f"{self.model_name}.default.nocontext"
      elif context is not None:
        template_name = f"{self.model_name}.default.context" 

      # generate the prompt
      if context is not None:
        mapping = {"question": question, "context":context}
      # merge kwargs
      mapping = mapping | kwargs

      prompt = super().fill_template(template_name=template_name, **mapping)

      if prompt is None:
        prompt = question

      # Add history if one is provided
      if history is not None:
        prompt = f"{prompt} {history}"

      final_query = question
      tokens = 0
      finish = False
      while tokens < max_new_tokens and not finish:
        full_prompt = f"{prompt}{final_query}"
        response = openai.Completion.create(model=self.model_name, prompt=full_prompt, temperature=temperature_val, max_tokens=page_size, top_p=top_p_val, stop=stop_sequences, **kwargs)
        output=response.choices[0].text
        final_query = f"{final_query}{output}"
        tokens = tokens + page_size
        finish = response.choices[0].finish_reason == "stop"
    openai.api_base = openai_base  
    return final_query

  def embeddings(self, question:str = None)->dict:
    openai_base = openai.api_base
    # forcing the api_key to a dummy value
    if openai.api_key is None:
      openai.api_key = self.api_key
    openai.api_base = self.endpoint
    return openai.Embedding.create(model=self.model_name, input=question)
  
  def get_available_models(self)->list:
    if len(self.available_models) == 0:
      self.available_models = [model.get('root') for model in openai.Model.list()['data']]
    return self.available_models
  
  def is_model_available(self,model_name:str)->bool:
    return (model_name in self.get_available_models())