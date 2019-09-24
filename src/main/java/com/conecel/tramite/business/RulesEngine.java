package com.conecel.tramite.business;

import java.util.ArrayList;

import com.conecel.tramite.rules.RulesInterface;

public class RulesEngine {
	private ArrayList<RulesInterface> rulesEngineService;
	
	public RulesEngine(){
		rulesEngineService=new ArrayList<>();
	}
	
	public void addRule(RulesInterface rule) {
		rulesEngineService.add(rule);
	}
	
	public String executeRules(String blackList, String debt, String manualQueue) {
		for(RulesInterface rule:rulesEngineService) {
			if(rule.condition(blackList, debt, manualQueue)) {
				return rule.action();
			}
			
		}
		return "N";
	}
	
	
}
