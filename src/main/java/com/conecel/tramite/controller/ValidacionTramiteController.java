package com.conecel.tramite.controller;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.conecel.tramite.services.ValidateProcessService;
import com.conecel.tramite.configuration.properties.InvokeProperties;
import com.conecel.tramite.entity.ResponseLevel;
import com.conecel.tramite.entity.SolicitudCredito;

@RestController
@RequestMapping("/validate/v1")
public class ValidacionTramiteController {
	@Autowired
	private ValidateProcessService business;


	@PostMapping("/validateprocess")
	public ResponseEntity<Object> validacionTramite(@Valid @RequestBody SolicitudCredito solicitudCredito) {
		ResponseLevel pay;
		pay=business.business(solicitudCredito);
		return new ResponseEntity<>(pay,HttpStatus.OK);
	}

	@GetMapping("/refresh")
	public void refresh() {
		InvokeProperties.refresh();
	}





}


