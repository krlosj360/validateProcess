package com.conecel.tramite.helpers;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class InvokeRestNew {


	private static String REQUEST = "{}";
	private static HttpHeaders HEADERS = null;

	public InvokeRestNew() {
		HEADERS = new HttpHeaders();
	}

	public <T>ResponseEntity<T> call(String urlWS, String methodHHTP, Class<T> responseType) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<T> call = null;
		try {
			HttpEntity<String> requestEntity = null;
			requestEntity = new HttpEntity<>(REQUEST, HEADERS);
			call = restTemplate.exchange(urlWS, HttpMethod.resolve(methodHHTP.toUpperCase()), requestEntity,
					responseType);
		} catch (Exception e) {
			log.info("Error:" + e.getMessage());
		}
		return call;
	}

	public InvokeRestNew setRequest(String jsonORxml) {
		REQUEST = jsonORxml;
		return this;
	}

	public InvokeRestNew setContentType(String contentType) {
		HEADERS.add("Content-Type", contentType);
		return this;
	}

	public InvokeRestNew setAccept(String accept) {
		HEADERS.add("Accept", accept);
		return this;
	}

	public InvokeRestNew addRequestHeaders(String key, Object value) {
		HEADERS.add(key, value.toString());
		return this;
	}

}
