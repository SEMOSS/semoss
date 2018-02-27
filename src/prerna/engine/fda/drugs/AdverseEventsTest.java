package prerna.engine.fda.drugs;

import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AdverseEventsTest {

	private static Gson gson = new GsonBuilder()
			.disableHtmlEscaping()
			.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
			.setPrettyPrinting()
			.create();

	public static void main(String[] args) {

		/*
		 * We have the following high level fields:
		 * 
		 * headers which contains some information about the event
		 * patient which contains details about the patient
		 * drugs which contains drugs taken while the event was happening
		 * reactions is information about the reaction the patient experienced
		 * 
		 */
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String url = "https://api.fda.gov/drug/event.json";
//		printResponse(httpclient, url,  "");
		
		// lets look at how we can narrow down what we want
		url += "?limit=2?search=";

		// filter values based on serious or not
		// serious 1 = death or hospitalization / disability
		// serious 2 = not 1
		String component = "serious:1";
//		printResponse(httpclient, url,  component);
		
		// drug name
		// the response is "HUGE"
		component = "patient.drug.medicinalproduct:IBUPROFEN";
//		printResponse(httpclient, url,  component);

		// drug characterization for the event
		// 1 = suspect - cause
		// 2 = concomitant - taken with suspect
		// 3 = interacting - interacted with suspect
		component = "patient.drug.drugcharacterization:1";
//		printResponse(httpclient, url,  component);

		// yay, i can combine this as well!!!
		component = "patient.drug.medicinalproduct:IBUPROFEN+AND+patient.drug.drugcharacterization:2";
		printResponse(httpclient, url,  component);
		
		// where the event occurred
		component = "occurcountry:US";
//		printResponse(httpclient, url,  component);
		
		// can have the above be exact
		component = "occurcountry.exact:US";
//		printResponse(httpclient, url,  component);
		
		// patient sex
		// 0 = unknown
		// 1 = male
		// 2 = female
		component = "patient.patientsex:1";
//		printResponse(httpclient, url,  component);
		
		// yay, i can combine this as well!!!
		component = "occurcountry.exact:US&patient.patientsex:1";
//		printResponse(httpclient, url,  component);
		
		// the way a drug is adminsitered 
		// between 0 to 67
		component = "patient.drug.drugadministrationroute:067";
//		printResponse(httpclient, url,  component);
		
		
	}

	private static void printResponse(CloseableHttpClient httpclient, String baseUrl, String params) {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + params);
		HttpResponse response = null;
		InputStreamReader isr = null;
		try {
			HttpGet getRequest = new HttpGet(baseUrl + params);
			response = httpclient.execute(getRequest);
			isr = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> jsonMap = new Gson().fromJson(isr, new TypeToken<Map<String, Object>>() {}.getType());
		System.out.println(gson.toJson(jsonMap));
	}

}
