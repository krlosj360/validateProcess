package com.conecel.tramite.rest;

import java.util.ArrayList;
import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.conecel.tramite.entity.ParametersApp;
import com.conecel.tramite.helpers.InvokeRestNew;
import com.conecel.tramite.helpers.InvokeSoap;
import com.conecel.tramite.entity.SoapEntity;
import com.conecel.tramite.repository.IActionable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RestConsultService  implements IActionable{

	static final String ERRORRIESGO = "Error al consultar el Riesgo: ";
	String requestMongo = "";
	String	urlMongo = "";
	JsonObject jsonRequest = new JsonObject();
	String requestSoap="";
	SoapEntity responseEntity=new SoapEntity();
	InvokeSoap soap=new InvokeSoap();

	InvokeRestNew microMedia = new InvokeRestNew();

	public SoapEntity consultJeis(String param1, String param2,String param3,String param4, String codiCon,String url,String request) {

		soap=new InvokeSoap();
		responseEntity=new SoapEntity();
		requestMongo= request;
		urlMongo= url;
		try {
			String requestWS="";
			ParametersApp cadenaParametros = new ParametersApp(codiCon+"|"+param1+"|"+param2+"|"+param3+"|"+param4);
			requestWS = cadenaParametros.reemplazarParametros(requestMongo);
			System.out.println(requestWS);
			log.info("Request Parametros Jeis: "+requestWS);
			responseEntity =  soap.call(requestWS, urlMongo); 
			System.out.println(responseEntity.getBody());
			requestMongo= "";
			urlMongo= "";

		} catch (Exception e) {
			log.error("Error en el Proceso  WebService Rest de Financiamiento",e);
			requestMongo= "";
			urlMongo= "";
			responseEntity = null;

		}

		return responseEntity;
	}


	public List<String> consultJeisRequest(String jsonData) {

		ArrayList<String> respo = new ArrayList<String> ();

		try {
			processJesi(jsonData,"ConsultJeis");
			respo.add(urlMongo);
			respo.add(requestMongo);
			return respo;
		} catch (Exception e) {
			log.error("Error en el Proceso  WebService Rest de Financiamiento",e);
			respo = null;

		}

		return respo;
	}
	
	
	
	public SoapEntity consultPriceEqu(String serviceNumber,String equipment,String idOffer,String transactionType,String jsonData) {
		log.info("Inicio Consulta Precio Equipo");	  

		try {
			process(jsonData,"ConsultPrecioSRE");
			ParametersApp cadenaParametros = new ParametersApp(serviceNumber+"|"+equipment+"|"+idOffer+"|"+transactionType);
			requestSoap = cadenaParametros.reemplazarParametros(requestMongo);
			System.out.println(requestSoap);
			log.info("Resquet Equipo: "+ requestSoap);	    
			responseEntity =  soap.call(requestSoap, urlMongo);
			log.info("Respuesta Equipo: "+ responseEntity.getBody());	 
			System.out.println(responseEntity.getBody());
			log.info("Fin Consulta Deuda Legado");	    
		} catch (Exception e) {
			log.error("Error: "+e);	               
		}

		return responseEntity;
	}

	public SoapEntity debtLegacy(String identificationType,String identificationNumber,String jsonData) {
		log.info("Inicio Consulta Deuda Legado");	  

		try {
			process(jsonData,"DeudaLegado");
			ParametersApp cadenaParametros = new ParametersApp(identificationType+"|"+identificationNumber);
			requestSoap = cadenaParametros.reemplazarParametros(requestMongo);
			log.info("Resquet Deuda: "+ requestSoap);	    
			responseEntity =  soap.call(requestSoap, urlMongo);
			log.info("Respuesta Deuda: "+ responseEntity.getBody());	    
			log.info("Fin Consulta Deuda Legado");	    
		} catch (Exception e) {
			log.error("Error: "+e);	               
		}

		return responseEntity;
	}

	public ResponseEntity<String> consultaBlackList(String identificationType, String identificationNumber,String jsonData) {
		log.info("Inicio Consulta Lista Negra");
		try {
			process(jsonData,"BlackList");
			jsonRequest = new Gson().fromJson(requestMongo, JsonObject.class);
			jsonRequest.addProperty("identificationType", identificationType);
			jsonRequest.addProperty("identificationNumber", identificationNumber);
			microMedia.setRequest(jsonRequest.toString());
			ResponseEntity<String> responseEntity=microMedia.call(urlMongo, "post", String.class); 
			log.info("Respuesta: " + responseEntity.getBody());
			log.info("Fin de consulta lista Negra");
			return responseEntity;
		} catch (Exception e) {
			log.error("Error al momento de consultar Liste Negra: " + e);
			return null;
		}
	}


	public SoapEntity consultaMiagraicionSoap(String identificationType, String identificationNumber,String jsonData) {
		log.info("Consultar Inicio Migracion");

		try {
			process(jsonData,"Migracion");
			ParametersApp cadenaParametros = new ParametersApp(identificationType+"|"+identificationNumber);
			requestSoap = cadenaParametros.reemplazarParametros(requestMongo);
			log.info("Resquet Migracion: "+ requestSoap);	    
			responseEntity =  soap.call(requestSoap, urlMongo);
			log.info("Respuesta Migracion: "+ responseEntity.getBody());	        
			log.info("Fin Consultar Migracion");
			return responseEntity;
		} catch (Exception e) {
			log.info("Error al consultar el Migracion: "+ e);
			return null;
		}	

	}

	public SoapEntity consultaRiskSoap(String identificationType, String identificationNumber,String jsonData) {
		log.info("Consultar Inicio Riesgo");

		try {
			process(jsonData,"ConsultRiesgoSoap");
			ParametersApp cadenaParametros = new ParametersApp(identificationType+"|"+identificationNumber);
			requestSoap = cadenaParametros.reemplazarParametros(requestMongo);
			log.info("Resquet Riesgo: "+ requestSoap);	    
			responseEntity =  soap.call(requestSoap, urlMongo);
			log.info("Respuesta Riesgo: "+ responseEntity.getBody());	        
			log.info("Fin Consultar Riesgo");
			return responseEntity;
		} catch (Exception e) {
			log.info(ERRORRIESGO+ e);
			return null;
		}	

	}

	public SoapEntity consultaBlackListSoap(String identificationType, String identificationNumber,String jsonData) {
		log.info("Consultar BlackList");

		try {
			process(jsonData,"BlackListSoap");
			ParametersApp cadenaParametros = new ParametersApp(identificationType+"|"+identificationNumber);
			requestSoap = cadenaParametros.reemplazarParametros(requestMongo);
			log.info("Resquet BlackList: "+ requestSoap);	    
			responseEntity =  soap.call(requestSoap, urlMongo);
			log.info("Respuesta BlackList: "+ responseEntity.getBody());	    
			log.info("Fin Consulta BlackList");	    
			return responseEntity;

		} catch (Exception e) {
			log.info("Error al consultar el BlackListSoap: "+ e);
			return null;
		}	

	}

	public ResponseEntity<String> consultaRisk(String identificationType, String identificationNumber,String jsonData) {
		log.info("Consultar Inicio Riesgo");

		try {
			process(jsonData,"ConsultRiesgo");
			jsonRequest = new Gson().fromJson(requestMongo, JsonObject.class);
			jsonRequest.addProperty("identificationType", identificationType);
			jsonRequest.addProperty("identificationNumber", identificationNumber);
			microMedia.setRequest(jsonRequest.toString());
			ResponseEntity<String> responseEntity=microMedia.call(urlMongo, "post", String.class);
			log.info("Fin Consultar Riesgo");
			return responseEntity;

		} catch (Exception e) {

			log.info(ERRORRIESGO + e);
			return null;
		}	

	}


	@Override
	public void process(String jsonData, String url) {
		responseEntity=new SoapEntity();
		requestSoap = "";
		requestMongo = "";
		urlMongo ="";
		jsonRequest = new JsonObject();
		JsonObject jsons = new Gson().fromJson(jsonData, JsonObject.class);
		JsonObject dataArray = jsons.getAsJsonObject().get("data").getAsJsonObject();
		JsonArray routinesArray = dataArray.getAsJsonObject().get("routines").getAsJsonArray();
		for (JsonElement jsonElement : routinesArray) {
			JsonArray resource =  jsonElement.getAsJsonObject().get("resources").getAsJsonArray();
			if(resource.size() > 0) {
				resource.forEach(x ->{ if( x.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(url)) {
					requestMongo =  x.getAsJsonObject().get("request").getAsString();
					urlMongo = x.getAsJsonObject().get("url").getAsString();
				}});
				break;
			}
		}


	}

	@Override
	public void processJesi(String jsonData,String url) {

		requestMongo = "";
		urlMongo ="";
		jsonRequest = new JsonObject();
		JsonObject jsons = new Gson().fromJson(jsonData, JsonObject.class);
		JsonObject dataArray = jsons.getAsJsonObject().get("data").getAsJsonObject();
		JsonArray routinesArray = dataArray.getAsJsonObject().get("routines").getAsJsonArray();
		for (JsonElement jsonElement : routinesArray) {
			JsonArray resource =  jsonElement.getAsJsonObject().get("resources").getAsJsonArray();
			if(resource.size() > 0) {
				resource.forEach(x ->{ if( x.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(url)) {
					requestMongo =  x.getAsJsonObject().get("request").getAsString();
					urlMongo = x.getAsJsonObject().get("url").getAsString();
				}});

				break;
			}
		}


	}
}
