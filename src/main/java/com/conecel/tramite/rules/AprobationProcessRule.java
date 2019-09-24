package com.conecel.tramite.rules;

public class AprobationProcessRule implements RulesInterface{

	@Override
	public boolean condition(String blackList, String debt, String manualQueue) {
		return (blackList.equals("N") && debt.equals("N") && manualQueue.equals("N"));
	}

	@Override
	public String action() {
		return "S";
	}

}
