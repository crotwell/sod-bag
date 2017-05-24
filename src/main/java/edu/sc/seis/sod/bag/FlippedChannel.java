package edu.sc.seis.sod.bag;

import edu.iris.Fissures.FissuresException;
import edu.iris.Fissures.Orientation;
import edu.iris.Fissures.IfNetwork.Channel;
import edu.iris.Fissures.IfNetwork.Sensitivity;
import edu.iris.Fissures.network.ChannelImpl;
import edu.iris.Fissures.seismogramDC.LocalSeismogramImpl;

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

    public static boolean check(Channel chan) {
        return (chan.get_code().charAt(2) == 'Z' && check(OrientationUtil.getUp(),
                                                          chan))
                || (chan.get_code().charAt(2) == 'N' && check(OrientationUtil.getNorth(),
                                                              chan))
                || (chan.get_code().charAt(2) == 'E' && check(OrientationUtil.getEast(),
                                                              chan));
    }

    public static boolean check(Orientation correct, Channel chan) {
        return OrientationUtil.angleBetween(correct, chan.getOrientation()) >= 180 - tol;
    }

    private static double tol = 0.01;
}
