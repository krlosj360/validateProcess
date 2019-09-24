package com.conecel.tramite.helpers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.conecel.tramite.entity.SoapEntity;

/**
 * @Autor Ronny Gabriel Matute Granizo 
 * Email: rgmatute91@gmail.com 
 * Whatsapp: +593 981851214
 **/
public class InvokeSoap {

	//public int code;
	// public Object body;

	private int code;     

	private  Object body;                      

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	private static final Log log = LogFactory.getLog(InvokeSoap.class);
	private static Document doc = null;

	public SoapEntity call(String requestWS, String urlWS) {
		SoapEntity response = new SoapEntity();
		try {

			URL url = new URL(urlWS);

			HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
			conexion.setDoOutput(true);
			conexion.setRequestMethod("POST");
			conexion.setRequestProperty("Content-Type", "text/xml");
			OutputStream outputStream = conexion.getOutputStream();
			outputStream.write(requestWS.getBytes("utf-8"));
			outputStream.flush();
			code = conexion.getResponseCode();

			if(code == 200) {
				InputStream is = conexion.getInputStream();
				BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(is));
				StringBuilder output = new StringBuilder();
				String aux;

				while ((aux = responseBuffer.readLine()) != null) {
					output.append(aux);
				}
				is.close();
				outputStream.close();
				responseBuffer.close();
				response.setBody(output.toString());
				response.setCode(conexion.getResponseCode());
				// ADD PARA LA MANIPULACION DEL RESPONSE
				addDoc(output.toString());
			}else {
				response.setCode(conexion.getResponseCode());
			}
		} catch (Exception ex) {
			log.info("Error:" + ex.getMessage());
			response.setCode(500);
		}
		return response;
	}

	public SoapEntity call2(String requestWS, String urlWS) {

		SoapEntity response = new SoapEntity();

		try {
			//log.info("URL WS: " + urlWS);
			//log.info(requestWS);
			URL url = new URL(urlWS);

			HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
			conexion.setDoOutput(true);
			conexion.setRequestMethod("POST");
			conexion.setRequestProperty("Content-Type", "text/xml");
			OutputStream outputStream = conexion.getOutputStream();
			outputStream.write(requestWS.getBytes("utf-8"));
			outputStream.flush();
			this.code = conexion.getResponseCode();

			if(this.code == 200) {
				InputStream is = conexion.getInputStream();
				BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(is));
				StringBuilder output = new StringBuilder();
				String aux;

				while ((aux = responseBuffer.readLine()) != null) {
					output.append(aux);
				}
				is.close();
				outputStream.close();
				responseBuffer.close();
				//log.info("ResponseActivation: " + output.toString());
				log.info("ResponseActivation: "+output.toString());
				response.setBody(output.toString());
				response.setCode(conexion.getResponseCode());
				// ADD PARA LA MANIPULACION DEL RESPONSE
				addDoc(output.toString());
			}else {


				response.setCode(500);
			}
		} catch (Exception ex) {
			log.info("Error:" + ex.getMessage());

			response.setCode(500);
		}
		return response;
	}

	public void addDoc(String respuest) throws Exception {
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new InputSource(new StringReader(respuest)));
	}

	public String getTagByName(String name) {
		try {
			return (doc != null) ? doc.getElementsByTagName(name).item(0).getTextContent() : "No service has been called";
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getTagByName(String xml,String tagName) {
		Document tags = null;
		try {
			tags = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(xml)));
			return  (tags.getElementsByTagName(tagName).item(0) != null)? tags.getElementsByTagName(tagName).item(0).getTextContent(): "Tag not found";
		} catch (Exception e) {
			log.error("Error: " + e.getMessage());
		}
		
		return null;
	}

	public List<String> getTagByNames(String name) {
		List<String> l = new ArrayList<>();
		if (doc != null) {
			if (doc.getElementsByTagName(name).getLength() > 1) {
				for (int i = 0; i < doc.getElementsByTagName(name).getLength(); i++) {
					l.add(doc.getElementsByTagName(name).item(i).getTextContent());
				}
			} else {
				l.add(doc.getElementsByTagName(name).item(0).getTextContent());
			}
		} else {
			try {
				throw new Exception("No service has been called");
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		return l;
	}
}



