package com.conecel.tramite.entity;

import lombok.Data;

@Data
public class ProcessOptions {
	private String changeOffer = null;
	private String financingClaro = null ;
	private String initialQuota = null;
	private String cashPayment = null;
	private String recurrentCashPayment = null;
	private String recurrentPayment = null;
}
