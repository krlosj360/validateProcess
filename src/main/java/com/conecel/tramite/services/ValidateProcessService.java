package com.conecel.tramite.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import org.apache.logging.log4j.ThreadContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.conecel.tramite.mongo.ConsultMongo;
import com.conecel.tramite.helpers.InvokeRestNew;
import com.conecel.tramite.business.IActionableProcess;
import com.conecel.tramite.business.IReliable;
import com.conecel.tramite.helpers.InvokeSoap;
import com.conecel.tramite.entity.SoapEntity;
import com.conecel.tramite.business.Validation;
import com.conecel.tramite.business.ServiceBusiness;
import com.conecel.tramite.business.ValidateAprobation;
import com.conecel.tramite.entity.Amount;
import com.conecel.tramite.entity.CashAmount;
import com.conecel.tramite.entity.Equipment;
import com.conecel.tramite.entity.FinancingAmount;
import com.conecel.tramite.entity.Header;
import com.conecel.tramite.entity.InitialQuotaAmount;
import com.conecel.tramite.entity.ProcessOptions;
import com.conecel.tramite.entity.Offer;
import com.conecel.tramite.entity.ResponseLevel;
import com.conecel.tramite.entity.SolicitudCredito;
import com.conecel.tramite.entity.ProcessResult;
import com.conecel.tramite.entity.RecurringAmount;
import com.conecel.tramite.rest.RestConsultService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Component
public class ValidateProcessService extends ProccessBussinnes implements IReliable,IActionableProcess {

	@Autowired
	private ConsultMongo consultMongo;
	@Autowired
	private ServiceBusiness servicebusiness;
	@Autowired
	private ValidateAprobation validateAprobation;
	@Autowired
	private Validation validation;
	@Autowired
	private RestConsultService consultaRest;

	@HystrixCommand(fallbackMethod = "reliableMethod", commandKey = "ValidationProccess", groupKey = "ValidationProccess")
	public ResponseLevel business(SolicitudCredito solicitudCredito) {

		//-----------------------INICIALIZACIÓN DE VARIABLES--------------------------
		resetValue();
		InvokeRestNew microMedia = new InvokeRestNew();
		Boolean plazoB = false;
		plazo=solicitudCredito.getInstallment();
		idPlan=solicitudCredito.getIdOffer();
		tipoTransaccion=solicitudCredito.getTransactionType();
		identificadorEquipo=solicitudCredito.getEquipment();
		identificationType=solicitudCredito.getIdentificationType();
		identificationNumber=solicitudCredito.getIdentificationNumber();
		phoneNumber=solicitudCredito.getServiceNumber();


		try {

			header = new Header();
			processResult = new ProcessResult();
			response = new ResponseLevel();

			equipment=new Equipment();
			processOptions=new ProcessOptions();
			offere = new  Offer();
			offer = new ArrayList<>();
			amount = new Amount();
			financingAmount = new FinancingAmount();
			cashAmount = new CashAmount();
			initialQuotaAmount = new InitialQuotaAmount();
			recurringAmount = new RecurringAmount();
			//----------------------------------------------------------------------------
			//INICIO DE PARÁMETROS DE LOG4J
			String msgProcessOpen = "Procesando Solicitud -";
			String msgProcessEnd = "Solicitud Procesado en:";
			String msgProcessEndMls = " - Milisegundos";
			long startTime;
			long endTime;
			startTime = System.currentTimeMillis();
			threadID = "";
			threadID =UUID.randomUUID().toString();
			ThreadContext.put("sid", threadID);
			log.info("----------------------");
			log.info(msgProcessOpen);
			log.info(solicitudCredito.toString());
			log.info("INICIO DEL MICROSERVICIO VALIDATIONPROCESS");

			Date dateInicio = new Date();
			DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
			fechaInicio  = inputFormat.format(dateInicio);
			//FIN
			plazoMongo = consultMongo.mongoParametrosCo("Plazo");
			plazoB = validation.isPlazo(plazoMongo, plazo);

			if(tipoTransaccion.equals(RENOVACION) || tipoTransaccion.equals(LINEANUEVA) ) {
				if(identificationType.equals("CED") || identificationType.equals("PAS")|| identificationType.equals("RUC") ) {

					message="";
					nextp = validation.isIdentification(identificationType, identificationNumber);
					if(plazoB && nextp) {
						try {
							JsonObject json5 = new JsonObject();
							resultJson = microMedia.setRequest(json5.toString())
									.addRequestHeaders("idRequest", "VALIDATIONPROCESS.PROCESS")
									.setContentType("application/json")
									.call(camelURL,"POST", String.class).getBody();

						} catch (Exception e) {
							log.error("Error microMedia Camel:" + e);
						} 

						//List<CrEntidadFinanciera> referenciasBuro = entidadFinanciera.refBuro("10");

						consultaRUrlJeis = consultaRest.consultJeisRequest(resultJson);

						if((tipoTransaccion.equals(RENOVACION) && validation.isNumeric(phoneNumber))|| (tipoTransaccion.equals(LINEANUEVA) && !validation.isNumeric(phoneNumber))) {
							if(validation.isNumeric(phoneNumber)) {
								newNumero = validation.obtieneTelefonoDNS(phoneNumber, "LEG");
								log.info("Actual Numero: " + phoneNumber);
								log.info("Actual Validacion: " + newNumero);
								SoapEntity consultDatosCliente =new SoapEntity();
								consultDatosCliente=consultaRest.consultJeis(identificationNumber, newNumero, "", "", jeisDatosCliente,consultaRUrlJeis.get(0),consultaRUrlJeis.get(1));
								resultadoDatos =  servicebusiness.procesoCliente(consultDatosCliente);
								if(resultadoDatos.get("code").equals("0")) {
									code = CERO;
									find = false;

								} else  {

									code =Integer.valueOf(resultadoDatos.get("code"));
									processResult.setManualQueue(resultadoDatos.get("colaManual"));
									find = true;

								}

								if(resultadoDatos.containsKey("code") && (resultadoDatos.get("code").equals("0") || resultadoDatos.get("code").equals("1"))) {

									try { 
										//-----------------CONSULTA SI EL NÚMERO ESTÁ MIGRADO O NO--------------------
										SoapEntity responseEntityMigracion=null;
										responseEntityMigracion=new SoapEntity();
										try {
											responseEntityMigracion = consultaRest.consultaMiagraicionSoap(newNumero, "", resultJson);
											datosMigrados =  servicebusiness.procesoMigracion(responseEntityMigracion);

										} catch (Exception e) {
											log.error("Error al consultar Migracion");
										}


										/**
										 * 
										 */

										isMigrated = false;

										if(isMigrated) {
											/**log.info("Migracion:"+ isMigrated);
											log.info("Inicio Proceso Cola Manual");
											//consulta bankCode de ONE
											JsonObject queryAcounts = new JsonObject();
											queryAcounts = queryAccount.queryAccountRest(solicitudCredito.getServiceNumber(),resultJson);
											message = EXITO;
											if(queryAcounts.size()> 0) {
												JsonArray account =  queryAcounts.get("Account").getAsJsonArray();
												if(queryAcounts.get("response").getAsJsonObject().get("idResult").getAsInt() == 0){
													try {
														for (JsonElement jsonAccount : account) {
															JsonObject accountInfo = jsonAccount.getAsJsonObject().get("account").getAsJsonObject().get("accountInformation").getAsJsonObject();
															try {
																paymentMethodType = accountInfo.get("paymentMethod").getAsInt();
																JsonObject paymentChannel = accountInfo.getAsJsonObject().get("PaymentChannel").getAsJsonObject();
																String bankCodee  = paymentChannel.get("bankCode").toString();
																if(!paymentMethodType.equals(0)) {
																	if((paymentMethodType == 1 || paymentMethodType == 01 )){
																		bankCode = bankCodee ;
																		log.info("Metodo de Pago TRC:" + bankCodee);
																		traRes.setManualQueue(APROBADO);
																		log.info("Cola Manual: "+ traRes.getManualQueue());
																	}else if((paymentMethodType == 2 || paymentMethodType == 02)){
																		bankCode  =bankCodee ;
																		log.info("Metodo de Pago BANCO Codigo:" + bankCodee);
																		//	bankOnline=migrado.consultaBancaOnline(bankCode,resultJson);
																		if(!bankOnline) {
																			traRes.setManualQueue(APROBADO);
																		}
																		else {
																			traRes.setManualQueue(NEGADO);
																		}
																		log.info("Cola Manual: "+ traRes.getManualQueue());


																	}else if((paymentMethodType == 3 || paymentMethodType == 03)) {
																		log.info("Metodo de Pago Efectivo Codigo");
																		traRes.setManualQueue(APROBADO);  
																		bankCode  = bankCodee ;
																		banRisk = true;

																		log.info("Cola Manual: "+ traRes.getManualQueue());

																	} else {

																		log.warn("paymentMethodType no parametrizado valor:"+ paymentMethodType);
																		log.info("Cola Manual: "+ traRes.getManualQueue());
																	}


																} else {
																	code = MENOSUNO;
																	message = "Error";
																	traRes.setManualQueue(NEGADO);


																}

															} catch (Exception e) {	
																log.error("Error: "+ e);
																code = MENOSUNO;
																message = "Error";
															}
														}
													}
													catch (Exception e) {
														log.error("Error: "+ e);
														code = MENOSUNO;
														message = "Error";

													}
												}
											} 

											else {
												log.error("Valor se Account:" + queryAcounts);
												code=MENOSUNO;
								 				message = "Error";
											}
											log.info("Fin Proceso Cola Manual");
											 */} 

										else {
											message = EXITO;
											log.info("Migracion:"+ isMigrated);

											if(resultadoDatos.containsKey("idFormaPago")){

												log.info("Forma de Pago :"+ resultadoDatos.get("idFormaPago"));
												if(resultadoDatos.get("idFormaPago")!=null && 	resultadoDatos.get("idFormaPago").equals("BAN")) {

													if(resultadoDatos.get("online")!= null && resultadoDatos.get("online").equals(NEGADO)) {
														processResult.setManualQueue(APROBADO);
														log.info("Banco Ofline - Cola Manual: "+ processResult.getManualQueue());
														banRisk = true;

													}else{

														processResult.setManualQueue(NEGADO);
														log.info("Banco Online - Cola Manual: "+ processResult.getManualQueue());
														banRisk = false;
													}


												} else {
													processResult.setManualQueue(APROBADO);
													log.info("Por Forma de pago Cola Manual: "+ processResult.getManualQueue());
													banRisk = false;
												}

											}
										}

									}catch (Exception e) {
										code=MENOSTRES;
										message = "Error process";
									}


								}else {

									code = MENOSDOS;
									message = "Error consult jeis";
								}

							} else {
								log.info("Transaccion Linea Nueva");
								processResult.setManualQueue(NEGADO);
								newNumero = "0";
								message = EXITO;
							}

							//-----------------------VALIDACIÓN DE LISTA NEGRA--------------------------

							try {
								ResponseEntity<String>  blackList = null;
								blackList= consultaRest.consultaBlackList(identificationType, identificationNumber,resultJson);
								String resultDescription=null;
								if(blackList.getStatusCode()==HttpStatus.OK) {
									code=CERO;
									String responseBlackList=blackList.getBody();
									JsonObject responseJson = new JsonObject();
									responseJson = new Gson().fromJson(responseBlackList, JsonObject.class);
									if(!responseJson.isJsonNull()) {
										JsonObject responseAux = responseJson.getAsJsonObject().get("response").getAsJsonObject();
										if(!responseAux.isJsonNull()) {
											resultDescription=responseAux.get("resultDescription").getAsString();
											log.info(resultDescription);
											if(resultDescription.equals("OK")) {
												processResult.setBlackList(APROBADO);
											}
											else {
												processResult.setBlackList(NEGADO);
											}
										}
									}  
								} else {
									log.error("Error a consultar Lista Negra Codigo devuelto: " + blackList.getStatusCode());
									code=MENOSDOS;
									message = "Error - BlackList";

								}

								log.info("Fin Proceso Lista Negra");

							} catch (Exception e) {
								log.error("Error BlackList:" + e);
								code=MENOSDOS;
								message = "Error - BlackList";
							}

							log.info("Lista Negra: "+ processResult.getBlackList());

							try {
								InvokeSoap soapRisk=new InvokeSoap();
								SoapEntity responseEntityRiskSoap=new SoapEntity();
								responseEntityRiskSoap=consultaRest.consultaRiskSoap(identificationType, identificationNumber,resultJson);
								numError = "";
								cupoDisponible = "";
								nivelRisk = "";

								if(responseEntityRiskSoap.getCode()==200) {
									code=CERO;
									soapRisk.addDoc(responseEntityRiskSoap.getBody());
									String responseRiskSoap=soapRisk.getTagByName("ns0:pvresultadoOut");
									soapRisk.addDoc(responseRiskSoap);
									numError=soapRisk.getTagByName("NUM_ERROR");

									if(numError.equals("0")) {
										cupoDisponible = soapRisk.getTagByName("CUPO_DISPONIBLE");
										nivelRisk = soapRisk.getTagByName("NIVEL_RIESGO");
										if(nivelRisk.equals("ALTO") && nivelRisk.equals("MEDIO")) {
											processResult.setManualQueue(APROBADO);
											log.info("Cola Manual : "+ processResult.getManualQueue());

										}
									}
								}
								else {
									log.error("El servicio valida deuda respondió con un código de estado diferente de 200");
									code=MENOSDOS;
									message = "Error - Risk";
								}

							} catch (Exception e) {
								code=MENOSDOS;
								message = "Error risk";
								processResult.setManualQueue(APROBADO);
								log.error("Error Risk: "+ e);
							}



							//-------------------------VALIDACION DE DEUDA-------------------------------
							if(isMigrated) {
								log.info("Consulta deuda One");
								log.info("Inicio Proceso Deuda");

								try {	 
									//haveDebt=businessDebt.haveDebtOne(solicitudCredito.getServiceNumber(),resultJson);

								} catch (Exception e) {
									log.error(" Error:"+e);
									code=MENOSUNO;
									message = "Error";
								}

								log.info("Fin Proceso Deuda");
							}
							else {
								log.info("Consulta deuda Legacy");
								log.info("Inicio  Deuda");

								try { 
									InvokeSoap soapDeuda =new InvokeSoap();
									SoapEntity responseEntityDeuda=new SoapEntity();
									responseEntityDeuda=consultaRest.debtLegacy(identificationType, identificationNumber,resultJson);
									if(responseEntityDeuda.getCode()==200) {
										try {
											code=CERO;
											soapDeuda.addDoc(responseEntityDeuda.getBody());
											String responsed=soapDeuda.getTagByName("ns0:pvresultadoOut");
											soapDeuda.addDoc(responsed);
											String numErrorDeuda ="";
											numErrorDeuda=soapDeuda.getTagByName("NUM_ERROR");

											if(numErrorDeuda.equals("0")) {
												processResult.setDebt(APROBADO);

											}else {
												processResult.setDebt(NEGADO);

											}

										} catch (Exception e) {
											log.error("Error al usar función addDoc");
										}
									}
									else {
										log.error("El servicio valida deuda respondió con un código de estado diferente de 200");
										code=MENOSDOS;
										message = "Error - Deuda";
									}



								} catch (Exception e) {
									log.error("Error:"+ e);
									code=MENOSDOS;
									message = "Error - Deuda";
								}

								log.info("Inicio Proceso Fin");

							}


							if(find) {
								code =UNO;
								message = "Customer data not found";
								log.error(message);
								log.error(code);
							}





						} else {
							code = MENOSUNO;
							message = "serviceNumber invalid";

						}


						if(code == CERO ) {
							//-------------------VALIDA LA APROBACIÓN DEL TRÁMITE------------------------
							processResult=validateAprobation.validateAprobation(processResult);
							//----------------------------------------------------------------------------

							log.info("Lista Negra -"+processResult.getBlackList()+" Manual Cola -"+ processResult.getManualQueue() + " Deuda - "+processResult.getDebt() +": Tramite - "+processResult.getApproved());
							
							SoapEntity consultPrecio =new SoapEntity();
							consultPrecio=consultaRest.consultPriceEqu(newNumero,identificadorEquipo, idPlan,tipoTransaccion, resultJson);
							resultadoEquipo =  servicebusiness.procesoPrecioEquipo(consultPrecio);

							if((tipoTransaccion.equals(RENOVACION)||tipoTransaccion.equals(LINEANUEVA)) && resultadoEquipo.get("code").equals("0")) {
	
									log.info("Obtener Detalle Equipment");
									log.info("Response Precio de Equipment: " + resultadoEquipo.toString());
									precioContadoEquipo = resultadoEquipo.get("PRECIO_LIBRE");
									precioFinanciamientoEquipo = resultadoEquipo.get("PRECIO_FINANCIADO");
									plazoBase = resultadoEquipo.get("PLAZOS");
									cuotaMensualNew = resultadoEquipo.get("TARIFA_BASICA");
									log.info("PRECIO DEL EQUIPO CONTADO - "+ identificadorEquipo + ": "+ precioContadoEquipo);
									log.info("FINANCIAR - "+resultadoEquipo.get("FINANCIAR"));
									log.info("PRECIO DEL EQUIPO FINANCIAMIENTO - "+ identificadorEquipo + ": "+ precioFinanciamientoEquipo);
									log.info("TARIFA BASICA DEL PLAN -"+idPlan+": "+ cuotaMensualNew);
									log.info("DETALLE DEL PLAN -"+resultadoEquipo.get("NOMBRE_PLAN"));
									log.info("TIPO PRODUCTO -"+resultadoEquipo.get("TIPOS_PRODUCTOS"));									
									log.info("Fin Detalle Equipment");

							} else {

								log.error("Error Obtener Detalle Equipment Codigo:"+resultadoEquipo.get("code"));
							}


							offere.setIdOffer(idPlan);
							String ivaMongo = consultMongo.mongoParametrosCo("iva");

							switch(tipoTransaccion) {

							case RENOVACION:

								if(processResult.getApproved().equals(APROBADO)) {
									processOptions.setInitialQuota(NEGADO);
									processOptions.setCashPayment(APROBADO);
									processOptions.setRecurrentPayment(NEGADO);
									processOptions.setRecurrentCashPayment(NEGADO);

									nameAndTypeProdcut();

									if(!resultadoDatos.get("cuotaMensualActual").equals("") && !cuotaMensualNew.equals("")) {
										if (!resultadoEquipo.get(CAMBIO_PLAN).equals("") && resultadoEquipo.get(CAMBIO_PLAN).equals(APROBADO)  ) {		
											processOptions.setChangeOffer(APROBADO);
											isOffer(cuotaMensualNew);

										} else {
											processOptions.setChangeOffer(NEGADO);							
											isOffer(cuotaMensualNew);
											offere.setIdOffer(resultadoDatos.get("idPlanActual"));
											offere.setTypeProduct(resultadoEquipo.get("TIPOS_PRODUCTOS"));

										}					

									} 



									if((!precioContadoEquipo.equals("")&&!precioFinanciamientoEquipo.equals(""))&&(!precioContadoEquipo.equals("0")&&!precioFinanciamientoEquipo.equals("0"))) {		
										processCash();

										if(!resultadoEquipo.get(FINANCIAR).equals("")&& resultadoEquipo.get(FINANCIAR).equals(APROBADO)){
											processOptions.setFinancingClaro(APROBADO);

											Double mesesBase = Double.parseDouble(plazoBase);	
											Double meses = Double.parseDouble(plazo);	

											if(meses <= mesesBase ) {
												processFinancig();
												if(cupoDisponible != null && !cuotaMensualNew.equals("")) { 

													log.info("Cupo: " + cupoDisponible);
													log.info("nivelRisk: " + nivelRisk);

													if(!nivelRisk.equals("") && nivelRisk.equals("BAJO")) {
														log.info("Inicio Proceso Cuota Inicial Financiamiento ---");
														Double cupo =  Double.parseDouble(cupoDisponible);
														Double planf = Double.parseDouble(cuotaMensualNew);
														Double equipof = financiamientoEquipo;

														if(processOptions.getChangeOffer().equals(APROBADO)) {
															if(cupo>=planf) {
																log.info("Cupo mayor al plan: aplica cambio de plan: ");
																Double cuotaMensual = (equipof/meses)+planf;
																if(cupo>=cuotaMensual ) {
																	log.info("Cupo mayor a la cuota mensual: No Aplica Cuota Inicial");
																	log.info("Cuota mensual: "+ cuotaMensual );
																	processOptions.setInitialQuota(NEGADO);
																	initialQuotaAmount.setRate(0.0);
																	initialQuotaAmount.setTaxRate(0.0);
																	log.info("No Aplica cuota inicial: "+ processOptions.getInitialQuota());

																}  else {
																	log.info("Cupor menor a la cuota mensual: Aplica Cuota Inicial");
																	processOptions.setInitialQuota(APROBADO);
																	Double cuota_inicialF = (cuotaMensual-cupo)*meses;
																	BigDecimal cuota_inicialFr = new BigDecimal(cuota_inicialF).setScale(3, RoundingMode.UP);
																	Double cuota_inicial = cuota_inicialFr.doubleValue();
																	cuotaMensual = ((equipof-cuota_inicial)/meses)+planf;   
																	log.info("Cuota inicial: "+ cuota_inicial );
																	log.info("Cuota mensual: "+ cuotaMensual );
																	log.info("aplica_cuota_inicial: "+processOptions.getInitialQuota());
																	Double mongoIvaF = Double.parseDouble(ivaMongo);

																	Double cuota_inicialIva = cuota_inicial * mongoIvaF;
																	Double sumCuotaFinal = cuota_inicial + cuota_inicialIva;

																	BigDecimal cuotaiva = new BigDecimal(sumCuotaFinal).setScale(3, RoundingMode.UP);
																	Double cuotaivaFinal = cuotaiva.doubleValue();
																	initialQuotaAmount.setRate(cuota_inicial);
																	initialQuotaAmount.setTaxRate(cuotaivaFinal);
																	financingAmount.setRate(financiamientoEquipo - cuota_inicial);
																	financingAmount.setTaxRate(precioEquipoParseFinanciado  - cuota_inicial);

																} 
															}else {
																if(cupo <= 0.0) {
																	processOptions.setChangeOffer(NEGADO);
																	processOptions.setInitialQuota(NEGADO);
																	initialQuotaAmount.setRate(0.0);
																	initialQuotaAmount.setTaxRate(0.0);
																	processOptions.setFinancingClaro(NEGADO);
																	processOptions.setInitialQuota(NEGADO);
																	processOptions.setCashPayment(APROBADO);
																	processOptions.setRecurrentPayment(NEGADO);
																	processOptions.setRecurrentCashPayment(NEGADO);

																} else {
																	log.info("Cupo mayor al plan: No aplica cambio de plan");
																	processOptions.setChangeOffer(NEGADO);
																	Double cuotaMensual = (equipof/meses);
																	log.info("cuota mensual: "+cuotaMensual);
																	if (cupo>=cuotaMensual) { 
																		log.info("Cupo mayor o igual a la cuota mensual: No aplica Cuota inicial");
																		processOptions.setInitialQuota(NEGADO);
																		initialQuotaAmount.setRate(0.0);
																		initialQuotaAmount.setTaxRate(0.0);
																	}else {

																		log.info("Cupo es menor a la cuota mensual: Aplica Cuota Inicial");
																		processOptions.setInitialQuota(APROBADO);	
																		Double cuotaInicialF = (cuotaMensual-cupo)*meses;
																		BigDecimal cuotaInicialFr = new BigDecimal(cuotaInicialF).setScale(3, RoundingMode.UP);
																		Double cuotaInicialFi = cuotaInicialFr.doubleValue();
																		cuotaMensual = ((equipof-cuotaInicialFi)/meses);    
																		log.info("cuota_inicial: "+ cuotaInicialFi);
																		log.info("cuota_mensual: "+ cuotaMensual);
																		Double mongoIvaF = Double.parseDouble(ivaMongo);
																		Double cuota_inicialIva = cuotaInicialFi * mongoIvaF;
																		Double sumCuotaFinal = cuotaInicialFi + cuota_inicialIva;
																		BigDecimal cuotaiva = new BigDecimal(sumCuotaFinal).setScale(3, RoundingMode.UP);
																		Double cuotaivaFinal = cuotaiva.doubleValue();
																		initialQuotaAmount.setRate(cuotaInicialFi);
																		initialQuotaAmount.setTaxRate(cuotaivaFinal);
																		financingAmount.setRate(financiamientoEquipo - cuotaInicialFi);
																		financingAmount.setTaxRate(precioEquipoParseFinanciado  - cuotaInicialFi);
																	}
																}

															}

														} else {

															if(cupo <= 0.0) {
																processOptions.setChangeOffer(NEGADO);
																processOptions.setFinancingClaro(NEGADO);
																processOptions.setInitialQuota(NEGADO);
																processOptions.setCashPayment(APROBADO);
																processOptions.setRecurrentPayment(NEGADO);
																processOptions.setRecurrentCashPayment(NEGADO);


															} else {
																log.info("Inicio de proceso de Cuota Inicial Equipment");
																Double cuotaMensual = (equipof/meses);  
																if (cupo>=cuotaMensual) { 
																	log.info("Cupo es mayor a la cuota mensual del Equipment: no aplica cuota inicial");
																	processOptions.setInitialQuota(NEGADO);

																}else {

																	log.info("Cupo menor a la cuota mensual: aplica cuota inicial");
																	processOptions.setInitialQuota(APROBADO);	
																	Double cuotaInicialF = (cuotaMensual-cupo)*meses;
																	BigDecimal cuotaInicialFr = new BigDecimal(cuotaInicialF).setScale(3, RoundingMode.UP);
																	Double cuotInicialFr = cuotaInicialFr.doubleValue();
																	cuotaMensual = ((equipof-cuotInicialFr)/meses);    
																	log.info("cuota_inicial: "+ cuotInicialFr);
																	log.info("cuota_mensual: "+ cuotaMensual);
																	Double mongoIvaF = Double.parseDouble(ivaMongo);
																	Double cuota_inicialIva = cuotInicialFr * mongoIvaF;
																	Double sumCuotaFinal = cuotInicialFr + cuota_inicialIva;
																	BigDecimal cuotaiva = new BigDecimal(sumCuotaFinal).setScale(3, RoundingMode.UP);
																	Double cuotaivaFinal = cuotaiva.doubleValue();
																	initialQuotaAmount.setRate(cuotInicialFr);
																	initialQuotaAmount.setTaxRate(cuotaivaFinal);
																	financingAmount.setRate(financiamientoEquipo - cuotInicialFr);
																	financingAmount.setTaxRate(precioEquipoParseFinanciado  - cuotInicialFr);
																}
															}
														}

														log.info("Fin Proceso Cuota Inicial Financiamiento ---");

													}else {

														log.info("Riesgo no aplica cuota inicial");
														processOptions.setInitialQuota(NEGADO);
													}
												}
											}
										} else {

											processOptions.setFinancingClaro(NEGADO);
											processOptions.setInitialQuota(NEGADO);
										}

									} else {
										processOptions.setFinancingClaro(null);
										processOptions.setInitialQuota(null);
										processOptions.setCashPayment(null);
										processOptions.setRecurrentPayment(null);
										processOptions.setRecurrentCashPayment(null);
									}

								} else {

									if(processResult.getBlackList().equals(APROBADO)|| processResult.getDebt().equals(APROBADO)) {
										processOptions.setRecurrentCashPayment(NEGADO);
										processOptions.setRecurrentPayment(NEGADO);
									}

									processOptions.setFinancingClaro(NEGADO);
									processOptions.setInitialQuota(NEGADO);
									processOptions.setCashPayment(NEGADO);
									processOptions.setChangeOffer(NEGADO);
									processOptions.setRecurrentCashPayment(null);
									processOptions.setRecurrentPayment(null);

									if(!resultadoEquipo.get("NOMBRE_PLAN").equals("")) {
										offere.setDescriptionOffer(resultadoEquipo.get("NOMBRE_PLAN"));

									}

									if(!resultadoEquipo.get("TIPOS_PRODUCTOS").equals("")) {
										offere.setTypeProduct(resultadoEquipo.get("TIPOS_PRODUCTOS"));

									}


									if(!precioContadoEquipo.equals("") && !precioContadoEquipo.equals("0")) {
										processCash();
										processOptions.setCashPayment(APROBADO);
									}

									if(!cuotaMensualNew.equals("")) {
										isOffer(cuotaMensualNew);

									} 

								}
								break;

							case LINEANUEVA:
								if(processResult.getApproved().equals(APROBADO)) {
									processOptions.setFinancingClaro(APROBADO);
									processOptions.setInitialQuota(null);
									processOptions.setCashPayment(APROBADO);
									processOptions.setRecurrentPayment(null);
									processOptions.setChangeOffer(null);
									processOptions.setRecurrentCashPayment(null);
									
									nameAndTypeProdcut();
									
									if(!cuotaMensualNew.equals("")) {
										isOffer(cuotaMensualNew);
									} 

									if(!precioContadoEquipo.equals("")) {
										processCash();
										if(!resultadoEquipo.get(FINANCIAR).equals("")&& resultadoEquipo.get(FINANCIAR).equals(APROBADO)) {
											Double mesesBase = Double.parseDouble(plazoBase);	
											Double meses = Double.parseDouble(plazo);	
											if(meses <= mesesBase ) {
												processFinancig();
											}
										}
									} else {

										processOptions.setFinancingClaro(null);
										processOptions.setInitialQuota(null);
										processOptions.setCashPayment(null);
										processOptions.setRecurrentPayment(null);
										processOptions.setChangeOffer(null);
										processOptions.setRecurrentCashPayment(null);
									}
								} else {

									nameAndTypeProdcut();
									
									if(processResult.getBlackList().equals(APROBADO)|| processResult.getDebt().equals(APROBADO)) {
										processOptions.setRecurrentCashPayment(NEGADO);
										processOptions.setRecurrentPayment(NEGADO);
									} else {
										processOptions.setRecurrentPayment(null);
										processOptions.setRecurrentCashPayment(null);
										
									}

									processOptions.setFinancingClaro(NEGADO);
									processOptions.setInitialQuota(null);
									processOptions.setCashPayment(NEGADO);
									processOptions.setChangeOffer(null);
									

									if(!precioContadoEquipo.equals("") && !precioContadoEquipo.equals("0")) {
										processCash();
									}else {
										processOptions.setFinancingClaro(null);
										processOptions.setCashPayment(null);

									}


									if(!cuotaMensualNew.equals("")) {
										isOffer(cuotaMensualNew);

									} 
								}
								break;
							default:

							}

						}else {


							log.info("code diferente  de processValidate de 0: validacion negada");
							processResult.setApproved(NEGADO);
						}

					} else if(!nextp) {
						code = MENOSUNO;
						message = "Error - identificationNumber";

					}
					else {
						code = MENOSUNO;
						message = "Number of months not parameterized";
						isNegado();


					}
				} else {
					code = MENOSUNO;
					message = "Type of identification Type not parameterized";
					isNegado();

				}

			} else {

				code = MENOSUNO;
				message = "Type of transaction not parameterized";
				isNegado();

			}

			if(plazoB) {
				response.setInstallment(plazo);
			}else {
				response.setInstallment(null);
			}

			response.setProcessResult(processResult);
			offer.add(offere);
			response.setOffer(offer);
			response.setEquipment(equipment);
			response.setProcessOptions(processOptions);
			header.setCode(code);
			header.setMessage(message);
			header.setTransactionId(threadID);
			header.setTransactionDate(fechaInicio);
			response.setHeader(header);
			response.setOffer(offer);
			equipment.setFinancingAmount(financingAmount);
			equipment.setCashAmount(cashAmount);
			equipment.setInitialQuotaAmount(initialQuotaAmount);
			equipment.setRecurringAmount(recurringAmount);
			response.setEquipment(equipment);
			Date datefinal = new Date();
			fechafinal = inputFormat.format(datefinal);


			try {
				consultMongo.saveMongo(solicitudCredito,response,threadID,fechaInicio,fechafinal);	
			} catch (Exception e) {
				log.error("Error Guardar Mongo:"+e);
			}

			log.info("Response:  "+ response.toString());
			log.info("FIN DEL MICROSERVICIO VALIDATIONPROCESS");
			endTime = System.currentTimeMillis() - startTime;
			log.info(msgProcessEnd + endTime + msgProcessEndMls);

			return response;
		} catch (Exception e) {
			log.error("ERROR DEL message MICROSERVICIO VALIDATIONPROCESS");
			log.error("ERROR: "+ e);
			message  = "Error ValidationProcess";
			code = -3;
			header.setCode(code);
			header.setMessage(message);
			response.setProcessResult(null);

			response.setOffer(null);
			response.setEquipment(null);
			response.setInstallment(null);
			response.setProcessOptions(null);
			return response;
		}
	}

	@Override
	public ResponseLevel reliableMethod(SolicitudCredito solicitudCredito) {
		try {
			response = new ResponseLevel();
			code = -4;
			message = "Error time response validationProcess";
			header = new Header();
			header.setCode(code);
			header.setMessage(message);
			response.setHeader(header);
			response.setProcessResult(null);

			response.setOffer(null);
			response.setEquipment(null);
			response.setInstallment(null);
			response.setProcessOptions(null);
			log.error("Mensaje" + message);
			log.error("Response:-" + response.toString());
			Date datefinal = new Date();
			DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			fechafinal = inputFormat.format(datefinal);
			Date dateInicio = new Date();
			fechaInicio  = inputFormat.format(dateInicio);
			try {
				consultMongo.saveMongo(solicitudCredito,response,threadID,fechaInicio,fechafinal);	
			} catch (Exception e) {
				log.error("Error Guardar Mongo:"+e);
			}
			log.error("FIN MICROSERVICIO VALIDATIONPROCESS");
			return response;
		} catch (Exception e) {
			log.error("Error" + e.getStackTrace());
			log.error(e.getMessage());
			return response;
		}
	}


	@Override
	public void isNegado() {
		processOptions.setFinancingClaro(NEGADO);
		processOptions.setInitialQuota(NEGADO);
		processOptions.setCashPayment(NEGADO);
		processOptions.setRecurrentPayment(NEGADO);
		processOptions.setChangeOffer(NEGADO);
		processOptions.setRecurrentCashPayment(NEGADO);
		processResult.setApproved(NEGADO);
		response.setInstallment(null);

	}

	@Override
	public void isOffer(String cuotaMensualNew) {
		Double cuotaNew =null;
		cuotaNew = Double.parseDouble(cuotaMensualNew);
		Double cuotaIva = cuotaNew * mongoIva;
		Double cuotaFinalIva = cuotaNew + cuotaIva;
		amount.setTaxRate(cuotaFinalIva);
		amount.setRate(cuotaNew);
		offere.setAmount(amount);		
	}

	@Override
	public void processCash() {

		Double contadoEquipo =  Double.parseDouble(precioContadoEquipo);
		Double contadoEquipoIva = contadoEquipo * mongoIva;
		Double contadoEquipoIvaFinal = contadoEquipo + contadoEquipoIva;
		BigDecimal contadoEquipoIvaFinalBi = new BigDecimal(contadoEquipoIvaFinal).setScale(3, RoundingMode.UP);
		Double precioEquipoParseContado = contadoEquipoIvaFinalBi.doubleValue();
		cashAmount.setRate(contadoEquipo);
		cashAmount.setTaxRate(precioEquipoParseContado);

	}

	@Override
	public void processFinancig() {
		financiamientoEquipo = Double.parseDouble(precioFinanciamientoEquipo);
		Double financiamientoEquipoIva = financiamientoEquipo * mongoIva;
		Double financiamientoEquipoIvaFinal = financiamientoEquipo + financiamientoEquipoIva;
		BigDecimal contadoEquipoIvafiFinalBi = new BigDecimal(financiamientoEquipoIvaFinal).setScale(3, RoundingMode.UP);
		precioEquipoParseFinanciado = contadoEquipoIvafiFinalBi.doubleValue();
		financingAmount.setRate(financiamientoEquipo);
		financingAmount.setTaxRate(precioEquipoParseFinanciado);

	}

	@Override
	public void nameAndTypeProdcut() {
		if(!resultadoEquipo.get(NOMBRE_PLAN).equals("")) {
			offere.setDescriptionOffer(resultadoEquipo.get(NOMBRE_PLAN));

		}
		if(!resultadoEquipo.get(TIPOS_PRODUCTOS).equals("")) {
			offere.setTypeProduct(resultadoEquipo.get(TIPOS_PRODUCTOS));

		}
		
	}
}
