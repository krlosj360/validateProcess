package com.conecel.tramite.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "ValidationProcess")
public class ValidationProcess {
	private String idTransaccion;
	private String dateTimeLlegada;
	private String dateTimeSalida;
	private Object request;
	private Object response;
}
