package com.conecel.tramite.repository;

public abstract interface IActionable {
	
	void process(String jsonData,String url);
	void processJesi(String jsonData,String url);

}
