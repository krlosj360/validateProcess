package com.conecel.tramite.rules;

public interface RulesInterface {
	boolean condition(String blackList, String debt, String manualQueue);
	String action();
}
