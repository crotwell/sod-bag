package edu.sc.seis.sod.bag;

import edu.sc.seis.sod.model.common.Location;
import edu.sc.seis.sod.model.common.Orientation;
import edu.sc.seis.sod.model.event.CacheEvent;
import edu.sc.seis.sod.model.station.ChannelGroup;
import edu.sc.seis.sod.model.station.ChannelImpl;
import edu.sc.seis.sod.util.display.EventUtil;

public class ChannelGroupUtil {


    public static ChannelImpl getRadial(ChannelGroup cg, CacheEvent event) {
        return getRadial(cg, EventUtil.extractOrigin(event).getLocation());
    }

    public static ChannelImpl getRadial(ChannelGroup cg, Location eventLoc) {
        ChannelImpl chan = cg.getChannel1();
        return new ChannelImpl(Rotate.replaceChannelOrientation(chan.get_id(),
                                                                "R"),
                               chan.getName() + "Radial",
                               new Orientation((float)Rotate.getRadialAzimuth(chan.getSite().getLocation(),
                                                                              eventLoc),
                                               0),
                               chan.getSamplingInfo(),
                               chan.getEffectiveTime(),
                               chan.getSite());
    }

    public static ChannelImpl getTransverse(ChannelGroup cg, CacheEvent event) {
        return getTransverse(cg, EventUtil.extractOrigin(event).getLocation());
    }

    public static ChannelImpl getTransverse(ChannelGroup cg, Location eventLoc) {
        ChannelImpl chan = cg.getChannel1();
        return new ChannelImpl(Rotate.replaceChannelOrientation(chan.get_id(),
                                                                "T"),
                               chan.getName() + "Transverse",
                               new Orientation((float)Rotate.getTransverseAzimuth(chan.getSite().getLocation(),
                                                                                  eventLoc),
                                               0),
                               chan.getSamplingInfo(),
                               chan.getEffectiveTime(),
                               chan.getSite());
    }
}
