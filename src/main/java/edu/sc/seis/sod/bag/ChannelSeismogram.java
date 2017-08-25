package edu.sc.seis.sod.bag;

import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.sod.model.seismogram.LocalSeismogramImpl;
import edu.sc.seis.sod.model.station.Sensitivity;

public class ChannelSeismogram {

	public ChannelSeismogram(Channel chan, LocalSeismogramImpl seis, Sensitivity sensitivity) {
		this.seis = seis;
		this.chan = chan;
		this.sensitivity = sensitivity;
	}

	public Channel getChannel() {
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

	Channel chan;

	Sensitivity sensitivity;
}
