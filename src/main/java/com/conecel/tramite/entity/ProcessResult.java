package com.conecel.tramite.entity;

import lombok.Data;

@Data
public class ProcessResult {
	
	private String approved = null;
	private String manualQueue = null;
	private String debt = null;
	private String blackList=null;
	
}
