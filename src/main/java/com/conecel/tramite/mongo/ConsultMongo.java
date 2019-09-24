package com.conecel.tramite.mongo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.conecel.tramite.entity.ParametrosEcommerce;
import com.conecel.tramite.repository.primary.IValidationProcessRespository;
import com.conecel.tramite.repository.secondary.IParametrosEcommerceRepository;
import lombok.extern.log4j.Log4j2;

import com.conecel.tramite.entity.ValidationProcess;

@Log4j2
@Component
public class ConsultMongo {

	@Autowired
	private IValidationProcessRespository validationRepository;
	
	@Autowired
	private IParametrosEcommerceRepository patametrosRepository;
	
	 List<ParametrosEcommerce>   primaries = null;

	String resultadoParametros = null;

	public String mongoParametrosCo(String name) {
		resultadoParametros = null;
		try {
			primaries = patametrosRepository.findAll();
			primaries.forEach(x ->{ if(x.getName().equals(name)) {resultadoParametros = x.getValue();}});
			return resultadoParametros;
			
		} catch (Exception e) {
			return resultadoParametros;
		}
	
	}
	
	public String mongoParametrosIva(String name) {
		resultadoParametros = null;
		try {
			primaries.forEach(x ->{ if(x.getName().equals(name)) {resultadoParametros = x.getValue();}});
			return resultadoParametros;
			
		} catch (Exception e) {
			return resultadoParametros;
		}
	
	}


	public void saveMongo(Object request, Object response,String threadID,String fechaIn,String fechafin) {

		String secuencialMongo = "";

		log.info("Inicio de Guardado Mongo");
		log.info(threadID);
		secuencialMongo = threadID;

		// ===============================================
		// GUARDAR EN MONGOSRE
		// ===============================================

		try {
			ValidationProcess sql = new ValidationProcess();
			sql.setRequest(request);
			sql.setResponse(response);
			sql.setIdTransaccion(secuencialMongo);
			sql.setDateTimeLlegada(fechaIn);
			sql.setDateTimeSalida(fechafin);
			validationRepository.save(sql);
			log.info("Guardado Exitoso");
			

		} catch (Exception e) {
			log.error("Error:"+ e);
		}

		log.info("Fin Guardado Mongo");
	}

}
