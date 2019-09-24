/**
 * 
 */
package com.conecel.tramite.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.conecel.tramite.configuration.properties.InvokeProperties;
import com.conecel.tramite.entity.Amount;
import com.conecel.tramite.entity.CashAmount;
//import com.conecel.tramite.entity.Amount;
//import com.conecel.tramite.entity.CashAmount;
import com.conecel.tramite.entity.Equipment;
import com.conecel.tramite.entity.FinancingAmount;
import com.conecel.tramite.entity.Header;
import com.conecel.tramite.entity.InitialQuotaAmount;
//import com.conecel.tramite.entity.FinancingAmount;
//import com.conecel.tramite.entity.Header;
//import com.conecel.tramite.entity.InitialQuotaAmount;
import com.conecel.tramite.entity.Offer;
import com.conecel.tramite.entity.ProcessOptions;
import com.conecel.tramite.entity.ProcessResult;
import com.conecel.tramite.entity.RecurringAmount;
//import com.conecel.tramite.entity.RecurringAmount;
import com.conecel.tramite.entity.ResponseLevel;
import com.conecel.tramite.helpers.InvokeRestNew;

/**
 * @author HITSS
 *
 */
@Service
public abstract class ProccessBussinnes {
	
	static final String DATOSRESPONSE = "ns0:datos";
	static final String TARIFABASICA = "ns0:tarifa_basica";
	static final String NEGADO = "N";
	static final String APROBADO = "S";
	static final int UNO = 1;
	static final int CERO = 0;
	static final int MENOSUNO = -1;
	static final int MENOSDOS = -2;
	static final int MENOSTRES = -3;
	static final int MENOSCUARTO = -4;
	static final String LINEANUEVA = "LNV";
	static final String RENOVACION = "RNV";
	static final String EXITO = "Operation Executed Succefully";
	static final String FINANCIAR ="FINANCIAR";
	static final String NOMBRE_PLAN ="NOMBRE_PLAN";
	static final String TIPOS_PRODUCTOS = "TIPOS_PRODUCTOS";
	static final String CAMBIO_PLAN = "CAMBIO_PLAN";
	static final String IDFORMAPAGO = "idFormaPago";
	
	protected ResponseLevel response;
	protected Header header = null;	
	protected Integer paymentMethodType=0;
	InvokeRestNew microMedia;
	protected Map<String,String> resultadoDatos;
	protected Map<String,String> resultadoEquipo;
	protected Map<String,String> datosMigrados;
	protected String datosLegado="";
	protected String datosPlan="";
	protected String datosEquipo="";
	protected String cuotaMensualNew = "";
	protected String resultJson;
	protected String jsonRisk = "";
	protected Boolean banRisk;
	protected String numError = "";
	protected String mensajeBlackList = "";
	protected String cupoDisponible = "";
	protected String nivelRisk = "";
	protected Double initialCreditAmount =0.0;
	protected String bankCode="";
	
	Boolean nextp;
	String fechaInicio = "";
	boolean isMigrated;
	String threadID ="";

	String message = null;


	String fechafinal="";
	String jeisDatosCliente ="";
	String jeisPrecioEquipo ="";
	String jeisDatosEquipo = "";
	int code;
	
	String precioContadoEquipo = "";
	String precioFinanciamientoEquipo = "";
	String plazoBase="";
	String newNumero = "";
	String plazo ="";
	String idPlan ="";
	String tipoTransaccion="";
	String identificadorEquipo="";
	String identificationType="";
	String identificationNumber ="";
	String phoneNumber="";
	String plazoMongo ="";
	String camelURL="";
	Boolean find = false ;
	ProcessResult processResult = null;
	Offer offere = null;
	List<Offer> offer = null;
	Equipment equipment = null;
	ProcessOptions processOptions=null;
	Amount amount = null;
	FinancingAmount financingAmount = null;
	CashAmount cashAmount = null;
	InitialQuotaAmount initialQuotaAmount = null;
	RecurringAmount recurringAmount = null;
	List<String> consultaRUrlJeis = new ArrayList<>();
	Double mongoIva;
	Double financiamientoEquipo;
	Double precioEquipoParseFinanciado;
	Boolean plazoB = false;
	String msgProcessOpen = "Procesando Solicitud -";
	String msgProcessEnd = "Solicitud Procesado en:";
	String msgProcessEndMls = " - Milisegundos";
	long startTime;
	long endTime;
	
	protected void resetValue() {
		resultadoEquipo = null;
		resultadoDatos = null;
		datosMigrados = null;
		precioContadoEquipo = "";
		precioFinanciamientoEquipo = "";
		plazoBase="";
		newNumero = "";
		plazo ="";
		idPlan ="";
		tipoTransaccion="";
		identificadorEquipo="";
		identificationType="";
		identificationNumber ="";
		phoneNumber="";
		plazoMongo ="";
		find = false ;
		datosLegado = "";
		datosEquipo="";
		cuotaMensualNew="";
		code=1;
		camelURL = InvokeProperties.getProperty("urlMicroCamel");
		jeisDatosCliente = InvokeProperties.getProperty("urljeisDatosCliente");
		jeisPrecioEquipo = InvokeProperties.getProperty("urljeisPrecioEquipo");
		jeisDatosEquipo = InvokeProperties.getProperty("urljeisDatosEquipo");
		banRisk = false;	
		financiamientoEquipo = 0.0;
		precioEquipoParseFinanciado=0.0;
		
		
	}
}
