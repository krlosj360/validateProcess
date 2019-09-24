package com.conecel.tramite.entity;

import java.util.List;

import lombok.Data;

@Data
public class ResponseLevel {
	private Header header = null;
	private ProcessResult processResult  = null;
	private List<Offer> offer = null;
	private Equipment equipment = null;
	private String installment = null;
	private ProcessOptions processOptions = null;
}
