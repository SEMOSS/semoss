package prerna.engine.impl.model;

import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import prerna.om.Insight;
import prerna.util.Utility;

public class OpenAiEngine extends AbstractModelEngine {
		
	@SuppressWarnings("unchecked")
	@Override
	public String askQuestion(String question, String context, Insight insight, Map <String, Object> parameters) 
	{
		String varName = (String) generalEngineProp.get("VAR_NAME");
	
		StringBuilder callMaker = new StringBuilder().append(varName).append(".ask(");
		callMaker.append("question=\"").append(question).append("\"");
		if(context != null)
			callMaker.append(",").append("context=").append(context);
		
		
		if(parameters != null) {
			if (parameters.containsKey("ROOM_ID")) { //always have to remove roomId so we dont pass it to py client
				String roomId = (String) parameters.get("ROOM_ID");
				parameters.remove("ROOM_ID");
				if (Utility.isModelInferenceLogsEnabled()) { // have to check that inference logs are enabled so that query works
					String history = getConversationHistory(roomId);
					if(history != null) //could still be null if its the first question in the convo
						callMaker.append(",").append("history=").append(history);
				}
			}

			Iterator <String> paramKeys = parameters.keySet().iterator();
			while(paramKeys.hasNext()) {
				String key = paramKeys.next();
				callMaker.append(",").append(key).append("=");
				Object value = parameters.get(key);
				if(value instanceof String)
				{
					callMaker.append("'").append(value+"").append("'");
				}
				else
				{
					callMaker.append(value+"");
				}
			}
		}
		callMaker.append(")");
		System.out.println(callMaker.toString());
		Object output = pyt.runScript(callMaker.toString());
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement element = gson.fromJson ((String) output, JsonElement.class);
		JsonObject jsonObj = element.getAsJsonObject();
		Map<String,Object> outputMap = new Gson().fromJson(jsonObj, Map.class);
		return (String) outputMap.get("content");
	}
}
