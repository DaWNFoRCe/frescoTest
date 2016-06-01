package be.edu.kuleuven.esat.cosic.fresco.prototype;

import java.util.List;

import be.edu.kuleuven.esat.cosic.fresco.prototype.smartgrid.beans.BidBean;
import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.ComparisonProtocolFactory;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;

public class SmartGridMarket implements Application{

	private List<BidBean> bids; 
	private OInt output;
	public SmartGridMarket(List<BidBean> bids){
		this.bids=bids;
	}
	
	public ProtocolProducer prepareApplication(ProtocolFactory factory) {
		BasicNumericFactory fac = (BasicNumericFactory) factory;
		NumericProtocolBuilder numOperations = new NumericProtocolBuilder(fac);
		ComparisonProtocolFactory compFactory = (ComparisonProtocolFactory)factory;
		SInt output = numOperations.add(this.bids.get(0).getPrice(), this.bids.get(0).getVolume());
		return null;
	}
	

}
