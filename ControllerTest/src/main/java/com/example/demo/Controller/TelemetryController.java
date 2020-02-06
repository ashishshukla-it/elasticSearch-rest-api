package com.example.demo.Controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.TelemetryLocReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@RestController
@RequestMapping(value = "/telemetry/v0")
public class TelemetryController {

	static String ip = null;
	static int port = 0;
	static String index = null;
	static String type = null;
	
	// post one record at a time
	@RequestMapping(value = "/location", method = RequestMethod.POST)
	public void pushTelemetry(@RequestBody TelemetryLocReq telemetryLocReq, HttpServletRequest request,
			HttpServletResponse response) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonString = mapper.writeValueAsString(telemetryLocReq);
			System.out.println(jsonString);
			insertJson(jsonString, telemetryLocReq.getDeviceId());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		System.out.println(telemetryLocReq.toString());

	}

	public static void insertJson(String jsonObject, String deviceId) throws UnknownHostException {
		IndexResponse response = getClient().prepareIndex("qualcomm", "deviceId", deviceId)
				.setSource(jsonObject, XContentType.JSON).get();
		System.out.println("Insertion response: " + response.toString());
	}

	@SuppressWarnings("resource")
	public static Client getClient() throws UnknownHostException {
	
		try (InputStream input = new FileInputStream("/Users/ashishshukla/Documents/elasticsearch.properties")) {
			Properties prop = new Properties();
            prop.load(input);
            ip = prop.getProperty("elasticSearch.url");
            port = Integer.parseInt(prop.getProperty("elasticSearch.port"));
            index = prop.getProperty("elasticSearch.index");
            type = prop.getProperty("elasticSearch.type");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
		Client client;
		try {
			client = new PreBuiltTransportClient(
					Settings.builder().put("client.transport.sniff", true).put("cluster.name", "dmi-iot-es").build())
							.addTransportAddress(new TransportAddress(InetAddress.getByName(ip), port));
			return client;
		} catch (java.net.UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	// get all data
	@RequestMapping(value = "/location/getAllData", method = RequestMethod.GET)
	public List<TelemetryLocReq> getAllDocs() {
		Gson gson = new Gson();
		ArrayList<TelemetryLocReq> objList = new ArrayList<TelemetryLocReq>();
		Client client;
		try {
			client = getClient();
			int scrollSize = 1000;
			List<Map<String, Object>> esData = new ArrayList<Map<String, Object>>();
			SearchResponse response = null;
			ArrayList<String> responseString = new ArrayList<String>();
			int i = 0;
			while (response == null || response.getHits().getHits().length != 0) {
				response = client.prepareSearch(index).setTypes(type).setQuery(QueryBuilders.matchAllQuery())
						.setSize(scrollSize).setFrom(i * scrollSize).execute().actionGet();
				for (SearchHit hit : response.getHits()) {
					esData.add(hit.getSourceAsMap());
					String jsonString = gson.toJson(hit.getSourceAsMap());
					TelemetryLocReq t = gson.fromJson(jsonString, TelemetryLocReq.class);
					objList.add(t);
					responseString.add(jsonString);
				}
				i++;
			}
			return objList;

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	// get data on the basis of device id
	@RequestMapping(value = "/location/deviceId", method = RequestMethod.GET)
	public TelemetryLocReq getDataForDeviceId(@QueryParam("deviceId") String deviceId) {
		Gson gson = new Gson();
		Client client;
		try {
			String jsonString=null;
			client = getClient();
			int scrollSize = 1000;
			List<Map<String, Object>> esData = new ArrayList<Map<String, Object>>();
			SearchResponse response = null;
			int i = 0;
			while (response == null || response.getHits().getHits().length != 0) {
				response = client.prepareSearch(index).setTypes(type)
						.setQuery(QueryBuilders.matchQuery("deviceId", deviceId)).setSize(scrollSize)
						.setFrom(i * scrollSize).execute().actionGet();
				for (SearchHit hit : response.getHits()) {
					esData.add(hit.getSourceAsMap());
					jsonString = gson.toJson(hit.getSourceAsMap());
				}
				i++;
			}
			TelemetryLocReq t = gson.fromJson(jsonString, TelemetryLocReq.class);
			return t;

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		return null;
	}

}
