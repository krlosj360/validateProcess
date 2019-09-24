/**
 * 
 */
package com.conecel.tramite.business;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.conecel.tramite.entity.SoapEntity;
import com.conecel.tramite.helpers.InvokeSoap;
import lombok.extern.log4j.Log4j2;

/**
 * @author HITSS
 *
 */
@Log4j2
@Component
public class ServiceBusiness {

	HashMap<String,String> resultado = new HashMap<>();

	static final String DATOSRESPONSE = "ns0:datos";
	static final String TARIFABASICA = "ns0:tarifa_basica";

	String code = "";
	String idFormaPago = "";
	String codigoBan = "";
	String descripcionBanco = "";
	String idPlanActual="";
	String online="";
	String cuotaMensualActual;
	String find;
	String colaManual="";
	InvokeSoap soapr=new InvokeSoap();

	public Map<String,String> procesoPrecioEquipo(SoapEntity datos)  {

		soapr = null;
		resultado = new HashMap<>();

		try {
			soapr=new InvokeSoap();
			if(datos.getCode()==200) {
				soapr.addDoc(datos.getBody());
				String validError =soapr.getTagByName("WS_ERROR");
				soapr=new InvokeSoap();
				log.info("Respdonse - "+ datos.getBody());
				if(!validError.equals("true")) {
					resultado.put("code","0");
					resultado.put("PRECIO_LIBRE",soapr.getTagByName("PRECIO_LIBRE"));
					resultado.put("PRECIO_FINANCIADO", soapr.getTagByName("PRECIO_FINANCIADO"));
					resultado.put("PRECIO_CUOTAINICIAL", soapr.getTagByName("PRECIO_CUOTAINICIAL"));
					resultado.put("CAMBIO_PLAN", soapr.getTagByName("CAMBIO_PLAN"));
					resultado.put("PLAZOS", soapr.getTagByName("PLAZOS"));
					resultado.put("TARIFA_BASICA", soapr.getTagByName("TARIFA_BASICA"));
					resultado.put("NOMBRE_PLAN", soapr.getTagByName("NOMBRE_PLAN"));
					resultado.put("TIPOS_PRODUCTOS", soapr.getTagByName("TIPOS_PRODUCTOS"));
					resultado.put("FINANCIAR", soapr.getTagByName("FINANCIAR"));
				} else {
					resultado.put("code","-1");
				}
			}

			return resultado;
		} catch (Exception e) {
			return null;
		}

	}

	public Map<String,String> procesoCliente(SoapEntity datos)  {

		soapr = null;
		resultado = new HashMap<>();
		code = "";
		idFormaPago = "";
		codigoBan = "";
		descripcionBanco = "";
		idPlanActual="";
		online="";
		cuotaMensualActual="";
		find="";
		colaManual="";
		String datosLegado = "";
		try {
			soapr=new InvokeSoap();
			if(datos.getCode()==200) {
				soapr.addDoc(datos.getBody());
				String validError =soapr.getTagByName("ns0:pnerrorOut");
				datosLegado=soapr.getTagByName(DATOSRESPONSE);
				log.info("Response - "+ datos.getBody());
				if(!validError.equals("-2") || validError.equals("0") ) {
					if(datosLegado !=null  ) {
						resultado.put("code","0");
						resultado.put("idFormaPago",soapr.getTagByName("ns0:id_forma_pago") );
						resultado.put("codigoBan", soapr.getTagByName("ns0:id_financiera"));
						resultado.put("descripcionBanco", soapr.getTagByName("ns0:descripcion"));
						resultado.put("idPlanActual", soapr.getTagByName("ns0:id_plan"));
						resultado.put("online", soapr.getTagByName("ns0:on_line"));
						resultado.put("cuotaMensualActual",soapr.getTagByName(TARIFABASICA));
						resultado.put("tipo_servicio", soapr.getTagByName("ns0:tipo_servicio"));

						resultado.put("find","false");
						return resultado;

					} else {
						resultado.put("code","-1");
						resultado.put("find","true");
						resultado.put("colaManual","S");

						log.info("Datos no Encontrados:"+  datosLegado);
						return resultado;

					}

				} else {
					resultado.put("code","-3");
					resultado.put("find","true");
					resultado.put("colaManual","S");
					return resultado;


				}


			}

			return resultado;



		} catch (Exception e) {
			return null;
		}

	}



	public Map<String,String> procesoMigracion(SoapEntity datos)  {
		soapr=null;
		resultado=null;
		soapr=new InvokeSoap();
		resultado = new HashMap<>();
		try {
			if(datos.getCode()==200) {

				log.info("Respuesta del servicio MigratedServices: "+datos.getBody());
				soapr.addDoc(datos.getBody());
				String responseMigracion =soapr.getTagByName("validateSubscriber");
				if (responseMigracion.equals("true")) {

					resultado.put("isMigrated", "0");
					log.info("El número está migrado");
				}
				else {
					resultado.put("isMigrated", "1");
					log.info("El número no está migrado");
				}

			}
			else {
				log.error("El servicio consulta migrado respondió con un código de estado diferente de 200");
			}

		} catch (Exception e) {

			log.error("Error al usar función addDoc: " + e);

		}
		return resultado;

	}

}
