package edu.sc.seis.sod.bag;

import edu.sc.seis.sod.model.common.FissuresException;
import edu.sc.seis.sod.model.common.Orientation;
import edu.sc.seis.sod.model.seismogram.LocalSeismogramImpl;
import edu.sc.seis.sod.model.station.ChannelImpl;
import edu.sc.seis.sod.model.station.Sensitivity;

public class FlippedChannel {
    public static ChannelSeismogram correct(ChannelImpl chan,
                                            LocalSeismogramImpl seis) throws FissuresException{
        return correct(chan, seis, null);
    }

    public static ChannelSeismogram correct(ChannelImpl chan,
                                            LocalSeismogramImpl seis,
                                            Sensitivity sens)
            throws FissuresException {
        if(check(chan)) {
            return new ChannelSeismogram(OrientationUtil.flip(chan),
                                         Arithmatic.mul(seis, -1),
                                         null);
        }
        return new ChannelSeismogram(chan, seis, sens);
    }

    public static boolean check(ChannelImpl chan) {
        return (chan.get_code().charAt(2) == 'Z' && check(OrientationUtil.getUp(),
                                                          chan))
                || (chan.get_code().charAt(2) == 'N' && check(OrientationUtil.getNorth(),
                                                              chan))
                || (chan.get_code().charAt(2) == 'E' && check(OrientationUtil.getEast(),
                                                              chan));
    }

    public static boolean check(Orientation correct, ChannelImpl chan) {
        return OrientationUtil.angleBetween(correct, chan.getOrientation()) >= 180 - tol;
    }

    private static double tol = 0.01;
}
