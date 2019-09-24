package com.conecel.tramite.business;

import org.springframework.stereotype.Component;
import com.conecel.tramite.entity.ProcessResult;
import com.conecel.tramite.rules.AprobationProcessRule;
@Component
public class ValidateAprobation {
	private RulesEngine rulesEngine;
	
	public ValidateAprobation(){
		rulesEngine=new RulesEngine();
		rulesEngine.addRule(new AprobationProcessRule());

	}
	
	public ProcessResult validateAprobation(ProcessResult resultadoTramite) {
		String approved=null;
		approved=rulesEngine.executeRules(resultadoTramite.getBlackList(), resultadoTramite.getDebt(), resultadoTramite.getManualQueue());
		resultadoTramite.setApproved(approved);
		return resultadoTramite;
	}
}
