package be.edu.kuleuven.esat.cosic.fresco.prototype.smartgrid.beans;

import dk.alexandra.fresco.framework.value.SInt;

public class BidBean {
	SInt id;
	SInt price;
	SInt volume;
	SInt supplier;
	SInt supply;
	
	public BidBean(SInt id, SInt price, SInt volume, SInt supplier, SInt supply) {
		super();
		this.id = id;
		this.price = price;
		this.volume = volume;
		this.supplier = supplier;
		this.supply = supply;
	}
	
	public SInt getId() {
		return id;
	}
	public void setId(SInt id) {
		this.id = id;
	}
	public SInt getPrice() {
		return price;
	}
	public void setPrice(SInt price) {
		this.price = price;
	}
	public SInt getVolume() {
		return volume;
	}
	public void setVolume(SInt volume) {
		this.volume = volume;
	}
	public SInt getSupplier() {
		return supplier;
	}
	public void setSupplier(SInt supplier) {
		this.supplier = supplier;
	}
	public SInt getSupply() {
		return supply;
	}
	public void setSupply(SInt supply) {
		this.supply = supply;
	}
	

}
