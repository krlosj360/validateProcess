package com.conecel.tramite.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParametersApp {
	/** The list parametros. */
	private List<String> listParametros;

			
	public List<String> getListParametros() {
		return listParametros;
	}

	public void setListParametros(List<String> listParametros) {
		this.listParametros = listParametros;
	}

	/**
	 * Instantiates a new parameters app.
	 *
	 * @param listParametros the list parametros
	 */
	public ParametersApp(List<String> listParametros) {
		this.listParametros = listParametros;
	}

	/**
	 * Instantiates a new parameters app.
	 *
	 * @param cadenaParametros the cadena parametros
	 */
	public ParametersApp(String cadenaParametros) {
		this.separarCadena(cadenaParametros);
	}
	
	/**
	 * Instantiates a new parameters app.
	 *
	 * @param cadenaParametros the cadena parametros
	 * @param separador delimitador de parametros
	 */
	public ParametersApp(String cadenaParametros, String separador) {
		this.separarCadena(cadenaParametros, separador);
	}

	/**
	 * Instantiates a new parameters app.
	 */
	public ParametersApp() {
		this.listParametros = new ArrayList<>();
	}
	

	/**
	 * Reemplazar parametros.
	 *
	 * @param xml the xml
	 * @return the string
	 */
	public String reemplazarParametros(String xml) {
		String respuesta = xml;
		Iterator<String> iterator = this.listParametros.iterator();
		String valor;
		while (iterator.hasNext()) {
			valor = iterator.next();
			Pattern ptr = Pattern.compile(">\\?<");
			Matcher matcher = ptr.matcher(respuesta);
			if (matcher.find()) {
				respuesta = matcher.replaceFirst(">" + valor + "<");
			}
		}
		return respuesta;
	}

	/**
	 * Obtener valor tag.
	 *
	 * @param xml the xml
	 * @param patronBusqueda the patron busqueda
	 * @return the string
	 */
	public String obtenerValorTag(String xml,String patronBusqueda) {
		String result = null;
		Pattern ptr = Pattern.compile("<" + patronBusqueda + ">([\\w|\\s|\\W]*)</" + patronBusqueda + ">");
		Matcher matcher = ptr.matcher(xml);
		if (matcher.find()) {
			result = matcher.group(1);
			
		}
		
		return result;
	}
	
	/**
	 * Obtener valor trama.
	 *
	 * @param trama the trama
	 * @param patronBusquedaIni the patron busqueda ini
	 * @param patronBusquedaFin the patron busqueda fin
	 * @return the string
	 */
	public String obtenerValorTrama(String trama,String patronBusquedaIni,String patronBusquedaFin) {
		String result="";
		Pattern ptr = Pattern.compile(patronBusquedaIni + ":([\\w|\\s|\\W]*)"+ patronBusquedaFin);
		Matcher matcher = ptr.matcher(trama);
		if (matcher.find()) {
			result=matcher.group(1);			
		}
		
		return result;
	}

	/**
	 * Agregar parametro.
	 *
	 * @param valor the valor
	 */
	public void agregarParametro(String valor) {
		this.listParametros.add(valor);
	}

	/**
	 * Separar cadena.
	 *
	 * @param cadenaParametros the cadena parametros
	 */
	private void separarCadena(String cadenaParametros) {
		this.listParametros = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(cadenaParametros, "|");
		while (st.hasMoreElements()) {
			listParametros.add((String) st.nextElement());
		}
	}
	
	/**
	 * Separar cadena.
	 *
	 * @param cadenaParametros the cadena parametros
	 */
	private void separarCadena(String cadenaParametros, String separador) {
		this.listParametros = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(cadenaParametros, separador);
		while (st.hasMoreElements()) {
			listParametros.add((String) st.nextElement());
		}
	}
	
	public String obtenerValorTramaESB(String trama, String patronBusqueda, String separador){		
		for(String parameter : listParametros)
		{
			String [] split = parameter.split(separador);
			
			if (split[0].equals(patronBusqueda))
			{
				return split[1];
			}		
		}
		return "";
	}
}
