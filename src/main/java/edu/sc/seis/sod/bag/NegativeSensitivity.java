package edu.sc.seis.sod.bag;

import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.sod.model.common.FissuresException;
import edu.sc.seis.sod.model.common.QuantityImpl;
import edu.sc.seis.sod.model.seismogram.LocalSeismogramImpl;
import edu.sc.seis.sod.model.station.Sensitivity;

public class NegativeSensitivity {

    public static boolean check(Sensitivity sensitivity) {
        return sensitivity.sensitivity_factor < 0;
    }
    
    public static boolean check(QuantityImpl sensitivity) {
        return sensitivity.getValue() < 0;
    }

	public static ChannelSeismogram correct(Channel chan,
			LocalSeismogramImpl seis, Sensitivity sensitivity)
			throws FissuresException {
		if (check(sensitivity)) {
			return new ChannelSeismogram(chan, Arithmatic.mul(seis, -1),
					new Sensitivity(-1 * sensitivity.sensitivity_factor,
							sensitivity.frequency));
		}
		return new ChannelSeismogram(chan, seis, sensitivity);
	}
}
