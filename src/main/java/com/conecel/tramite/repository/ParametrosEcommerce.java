package com.conecel.tramite.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="parameters")
public class ParametrosEcommerce {
	private	int _id;
	private	String name;
	private String description;
	private String url;
	private String value;	

}
