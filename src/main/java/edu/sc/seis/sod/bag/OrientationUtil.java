package edu.sc.seis.sod.bag;

import java.util.ArrayList;
import java.util.List;

import edu.sc.seis.TauP.SphericalCoords;
import edu.sc.seis.sod.model.common.Orientation;
import edu.sc.seis.sod.model.common.QuantityImpl;
import edu.sc.seis.sod.model.common.UnitImpl;
import edu.sc.seis.sod.model.station.ChannelImpl;
import edu.sc.seis.sod.model.station.OrientationRangeImpl;

/**
 * @author groves Created on Oct 7, 2004
 */
public class OrientationUtil {

    public static List<ChannelImpl> inOrientation(OrientationRangeImpl orient,
                                          List<ChannelImpl> chans) {
        double degDist = QuantityImpl.createQuantityImpl(orient.angular_distance)
                .convertTo(UnitImpl.DEGREE).getValue();
        List results = new ArrayList();
        for(ChannelImpl chan : chans) {
            Orientation chanOrient = chan.getOrientation();
            double dist = angleBetween(orient.center, chanOrient);
            if(dist <= degDist) {
                results.add(chan);
            }
        }
        return results;
    }

    public static boolean areEqual(Orientation one, Orientation two) {
        return one.azimuth == two.azimuth && one.dip == two.dip;
    }

    public static boolean areOrthogonal(Orientation one, Orientation two) {
        return areOrthogonal(one, two, 0.0001);
    }

    public static boolean areOrthogonal(Orientation one,
                                        Orientation two,
                                        double tol) {
        double dist = angleBetween(one, two);
        if(Math.abs(dist - 90) < tol) {
            return true;
        }
        return false;
    }

    public static double angleBetween(Orientation one, Orientation two) {
        // Dip = Lat, Az = Lon
        return SphericalCoords.distance(one.dip,
                                        one.azimuth,
                                        two.dip,
                                        two.azimuth);
    }

    public static Orientation getUp() {
        return new Orientation(0, -90);
    }

    public static Orientation getNorth() {
        return new Orientation(0, 0);
    }

    public static Orientation getEast() {
        return new Orientation(90, 0);
    }

    public static Orientation flip(Orientation orient) {
        return new Orientation(-1 * orient.dip, (orient.azimuth + 180) % 360);
    }

    public static ChannelImpl flip(ChannelImpl chan) {
        return new ChannelImpl(chan.get_id(),
                               chan.getName(),
                               flip(chan.getOrientation()),
                               chan.getSamplingInfo(),
                               chan.getEffectiveTime(),
                               chan.getSite());
    }

    public static String toString(Orientation orientation) {
        return "az=" + orientation.azimuth + ", dip=" + orientation.dip;
    }
}