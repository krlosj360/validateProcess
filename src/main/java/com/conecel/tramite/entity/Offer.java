package com.conecel.tramite.entity;

import lombok.Data;

@Data
public class Offer {
	private String idOffer = null;
	private String descriptionOffer = null;
	private String typeProduct = null;
	private Amount amount = null;

}
