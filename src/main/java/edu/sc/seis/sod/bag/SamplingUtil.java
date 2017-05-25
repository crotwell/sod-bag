package edu.sc.seis.sod.bag;

import java.util.ArrayList;
import java.util.List;

import edu.sc.seis.sod.model.common.SamplingImpl;
import edu.sc.seis.sod.model.common.TimeInterval;
import edu.sc.seis.sod.model.common.UnitImpl;
import edu.sc.seis.sod.model.station.ChannelImpl;
import edu.sc.seis.sod.model.station.SamplingRangeImpl;


/**
 * @author groves
 * Created on Oct 7, 2004
 */
public class SamplingUtil {


    public static List<ChannelImpl> inSampling(SamplingRangeImpl sampling, List<ChannelImpl> chans) {
        double minSPS = getSamplesPerSecond(sampling.min);
        double maxSPS = getSamplesPerSecond(sampling.max);
        List results = new ArrayList();
        for(ChannelImpl chan : chans) {
            double chanSPS = getSamplesPerSecond(chan.getSamplingInfo());
            if(minSPS <= chanSPS && chanSPS <= maxSPS) {
                results.add(chan);
            }
        }
        return results;
        
    }

    private static double getSamplesPerSecond(SamplingImpl sampling) {
        double numSeconds = new TimeInterval(sampling.interval).convertTo(UnitImpl.SECOND).getValue();
        return sampling.numPoints / numSeconds;
    }
}
