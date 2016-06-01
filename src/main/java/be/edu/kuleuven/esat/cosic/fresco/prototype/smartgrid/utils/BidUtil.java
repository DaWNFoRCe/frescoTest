package be.edu.kuleuven.esat.cosic.fresco.prototype.smartgrid.utils;

import be.edu.kuleuven.esat.cosic.fresco.prototype.smartgrid.beans.BidBean;
import dk.alexandra.fresco.framework.value.SInt;

public class BidUtil {
	public static final BidBean buildBidBean(SInt id, SInt price, SInt volume, SInt supplier, SInt supply   ){
		return new BidBean(id, price, volume, supplier, supply);
	};

}
