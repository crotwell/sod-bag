package edu.sc.seis.sod.bag;

import edu.sc.seis.sod.model.seismogram.LocalSeismogramImpl;
import edu.sc.seis.sod.model.station.ChannelImpl;
import edu.sc.seis.sod.model.station.Sensitivity;

public class ChannelSeismogram {

	public ChannelSeismogram(ChannelImpl chan, LocalSeismogramImpl seis, Sensitivity sensitivity) {
		this.seis = seis;
		this.chan = chan;
		this.sensitivity = sensitivity;
	}

	public ChannelImpl getChannel() {
		return chan;
	}

	public LocalSeismogramImpl getSeismogram() {
		return seis;
	}

	public Sensitivity getSensitivity() {
        if(sensitivity == null){
            throw new UnsupportedOperationException("This channelseismogram has no sensitivity");
        }
		return sensitivity;
	}

	LocalSeismogramImpl seis;

	ChannelImpl chan;

	Sensitivity sensitivity;
}
