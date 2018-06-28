package org.redhat.hhellbus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class CreditWih implements WorkItemHandler {

	private String baseApiServiceAddr = "http://pam-credit-service-pam7-dallas-take3.apps.na37.openshift.opentlc.com/api";
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("paymentProcessed", this.creditAuth());

		manager.completeWorkItem(workItem.getId(), results);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub

	}
	
	//TODO better error handling
	//TODO maybe change name of API end point... technically trying process payment
	private Boolean creditAuth()
	{
		HttpClient client = HttpClientBuilder.create().build();
		Boolean paymentProcessed = null;
		try {
			HttpGet request = new HttpGet(this.baseApiServiceAddr + "/payment/process");
			HttpResponse response;
			response = client.execute(request);
			BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
			
			JsonParser parser = new JsonParser();
			JsonElement jsonTree = parser.parse(rd);
			JsonObject jsonObject = jsonTree.getAsJsonObject();
			JsonObject data = jsonObject.get("data").getAsJsonObject();
			paymentProcessed = data.get("paymentProcessed").getAsBoolean();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return paymentProcessed;
	}
	

}