package com.conecel.tramite.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Document(collection="Offers")
public class ParametrosMdb {

	private	String offerid;
	private	String description;
	private String status;
	private String url;
	private String request=null;
}
