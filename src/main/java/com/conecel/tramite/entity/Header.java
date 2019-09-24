/**
 * 
 */
package com.conecel.tramite.entity;

import lombok.Data;

/**
 * @author HITSS
 *
 */
@Data
public class Header {
	
	private String transactionDate = null;
	private String transactionId = null;
	private int code;
	private String message =null;

}
