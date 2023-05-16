package prerna.sablecc2.reactor.frame.gaas.chat;

import java.util.HashMap;
import java.util.Map;

import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.frame.gaas.GaasBaseReactor;
import prerna.util.Constants;
import prerna.util.DIHelper;

public class MooseChatReactor extends GaasBaseReactor {

	public MooseChatReactor()
	{
		this.keysToGet = new String[]{ReactorKeysEnum.COMMAND.getKey(), ReactorKeysEnum.PROJECT.getKey(), ReactorKeysEnum.MODEL.getKey(), Constants.MOOSE_ENDPOINT};
		this.keyRequired = new int[] {1,0,0,0};
	}
	@Override
	public NounMetadata execute() {
		// TODO Auto-generated method stub	
		organizeKeys();
		String model = DIHelper.getInstance().getProperty(Constants.MOOSE_MODEL);
		if(model == null || model.trim().isEmpty()) {
			model = "gpt_3";
		}
		
		// if you want to force a model you can
		if(keyValue.containsKey(keysToGet[2]))
			model = keyValue.get(keysToGet[2]);
		
		String projectId = getProjectId();
		Map output = new HashMap();
		
		// commenting out for now
		projectId = null;

		if(projectId == null)
		{
			if(model.equalsIgnoreCase("alpaca"))
			{
				// create instruction and get response back
				String template = "Below is an instruction that describes a task, paired with an input that provides further context. Write a response that appropriately completes the request."
								   + "\\n\\n### Instruction:\\n"
								   + keyValue.get(keysToGet[0]) 
								   + "\\n\\n### Response:";
				
				String endpoint = DIHelper.getInstance().getProperty(Constants.MOOSE_ENDPOINT);
				if(endpoint == null || endpoint.trim().isEmpty()) {
					throw new IllegalArgumentException("Must define endpoint to run custom models");
				} 
				int maxTokens = template.length() + 80;
				String answer = (String)insight.getPyTranslator().runScript("smssutil.run_alpaca(\"" + template + "\", " + maxTokens + " ,\" "  + endpoint.trim() + "\")");
				
				output.put("answer", answer);
			}
			else
			{
				output.put("answer", "GPT requires you to entire API Key");
			}
		}
		else
		{
			// do that faiss search on the project
			output.put("answer", "Project chat still needs to be implemented");
		}
		Map outputMap = new HashMap();
		outputMap.put("query",  keyValue.get(keysToGet[0]));
		outputMap.put("data", output);
		
		return new NounMetadata(outputMap, PixelDataType.MAP);
	}

}
