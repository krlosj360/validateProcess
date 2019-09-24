package com.conecel.tramite.business;

import org.springframework.stereotype.Component;
import lombok.extern.log4j.Log4j2;



@Log4j2
@Component
public class Validation {

	private static final String MENSSAGE = "Error parametro no parametrizado";
	public boolean isIdentificationLnv(String identificationType,String identificationNumber){
		boolean nextp;
		switch(identificationType) {
		case "CED":
			if(!isSpaceWhitec(identificationNumber) && identificationNumber.matches("[0-9]+")) {
				nextp = true; 
			}else {
				nextp = false;
			}
			break;

		case "PAS":

			if(!isSpaceWhitec(identificationNumber)) {
				nextp = true; 
			}else {
				nextp = false;

			}
			break;

		case "RUC":
			if(!isSpaceWhitec(identificationNumber) && identificationNumber.matches("[0-9]+") ) {

				nextp = true; 
			}else {
				nextp = false;
			}

			break;
		default:
			nextp = false;
			break;
		}

		return nextp;
	}

	public boolean isIdentification(String identificationType,String identificationNumber){
		boolean nextp;
		switch(identificationType) {
		case "CED":
			if(!isSpaceWhitec(identificationNumber) && identificationNumber.matches("[0-9]+")) {
				nextp = true; 
			}else {
				nextp = false;
			}
			break;

		case "PAS":

			if(!isSpaceWhitec(identificationNumber)) {
				nextp = true; 
			}else {
				nextp = false;

			}
			break;

		case "RUC":
			if(!isSpaceWhitec(identificationNumber) && identificationNumber.matches("[0-9]+") ) {

				nextp = true; 
			}else {
				nextp = false;
			}

			break;
		default:
			nextp = false;
			break;
		}

		return nextp;
	}

	public boolean isPlazo(String cadena,String plazo){

		String[] palabras = cadena.split("\\,");
		Boolean plazob = null;
		for (String palabra : palabras) {
			if (palabra.equals(plazo)) {
				plazob  = true;
				break;
			}else {
				plazob = false;

			}

		}
		return plazob;
	}







	public boolean isSpaceWhitec(String cadena){
		if(cadena.contains(" ")) {

			return true;
		}else {

			return false;
		}
	}

	public boolean isNumeric(String cadena){
		try {
			Integer.parseInt(cadena);
			return true;
		} catch (NumberFormatException nfe){
			return false;
		}
	}

	public String obtieneTelefonoDNS(String telefono,String versionTelefono) {
		String numFinal = "";
		switch(versionTelefono) {
		case "INT":
			switch(telefono.length()) {
			case 12:
				numFinal = "593" + telefono.substring(3,telefono.length());
				break;
			case 10:
				numFinal = "593" + telefono.substring(1,telefono.length());
				break;
			case 9:
				numFinal = "593" + telefono.substring(0,telefono.length());
				break;
			case 8:
				numFinal = "5939" + telefono.substring(0,telefono.length());
				break;

			default: 
				numFinal = telefono;
				break;

			}
			break;
		case "NAC":
			switch(telefono.length()) {
			case 12:
				numFinal = "0" + telefono.substring(3,telefono.length());
				break;
			case 10:
				numFinal = "0" + telefono.substring(1,telefono.length());
				break;
			case 9:
				numFinal = "0" + telefono.substring(0,telefono.length());
				break;
			case 8:
				numFinal = "09" + telefono.substring(0,telefono.length());
				break;
			default:
				log.info(MENSSAGE);
				break;
			}
			break;
		case "LOC":
			switch(telefono.length()) {
			case 12:
				numFinal = "" + telefono.substring(3,telefono.length());
				break;
			case 10:
				numFinal = "" + telefono.substring(1,telefono.length());
				break;
			case 9:
				numFinal = "" + telefono.substring(0,telefono.length());
				break;
			case 8:
				numFinal = "9" + telefono.substring(0,telefono.length());
				break;
			default:
				log.info(MENSSAGE);
				break;
			}
			break;
		case "LEG":
			switch(telefono.length()) {
			case 12:
				numFinal = "" + telefono.substring(4,telefono.length());
				break;
			case 10:
				numFinal = "" + telefono.substring(2,telefono.length());
				break;
			case 9:
				numFinal = "" + telefono.substring(1,telefono.length());
				break;
			case 8:
				numFinal = "" + telefono.substring(0,telefono.length());
				break;
			default:

				log.info(MENSSAGE);
				break;
			}
			break;
		default:

			log.info(MENSSAGE);
			break;
		}

		return numFinal;
	}



	public Double isEquipoIva(Double contadoEquipo, String ivaMongo) {
		// TODO Auto-generated method stub
		return null;
	}


}
