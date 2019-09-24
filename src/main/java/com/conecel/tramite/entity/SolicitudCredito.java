package com.conecel.tramite.entity;

import lombok.Data;

@Data
public class SolicitudCredito {
	private String serviceNumber = null;
	private String transactionType = null;
	private String identificationNumber=null;
	private String identificationType=null;
	private String equipment=null;
	private String idOffer=null;
	private String installment=null;
}
