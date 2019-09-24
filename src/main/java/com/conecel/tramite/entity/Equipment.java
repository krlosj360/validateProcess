package com.conecel.tramite.entity;

import lombok.Data;


@Data
public class Equipment {
	

	private FinancingAmount financingAmount = null;
	private CashAmount cashAmount = null;
	private RecurringAmount recurringAmount = null;
	private InitialQuotaAmount initialQuotaAmount = null;

}
