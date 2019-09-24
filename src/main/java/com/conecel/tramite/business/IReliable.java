/**
 * 
 */
package com.conecel.tramite.business;

import com.conecel.tramite.entity.ResponseLevel;
import com.conecel.tramite.entity.SolicitudCredito;

/**
 * @author HITSS
 *
 */

@FunctionalInterface
public interface IReliable {
	public abstract ResponseLevel reliableMethod(SolicitudCredito solicitudCredito);
}
