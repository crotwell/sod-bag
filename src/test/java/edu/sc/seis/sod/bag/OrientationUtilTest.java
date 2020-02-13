package edu.sc.seis.sod.bag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import edu.sc.seis.TauP.SphericalCoords;
import edu.sc.seis.sod.model.common.Orientation;

public class OrientationUtilTest  {

    public void testOrthognalPlusNintyDip() {
        for(float az = -180; az <= 180; az += .1) {
            Orientation one = new Orientation(az, 1);
            Orientation two = new Orientation(az + 90, 1);
            assertFalse(OrientationUtil.areOrthogonal(one, two),
            		az + " to az plus 90, dip1 "
                                + SphericalCoords.distance(one.dip, one.azimuth, two.dip, two.azimuth)
                        );
        }
    }

    public void testOrthognalPlusNinty() {
        for(float az = -180; az <= 180; az += .1) {
            Orientation one = new Orientation(az, 0);
            Orientation two = new Orientation(az + 90, 0);
            assertTrue(OrientationUtil.areOrthogonal(one, two),
            		az + " to az plus 90  " + SphericalCoords.distance(one.dip, one.azimuth, two.dip, two.azimuth)
                       );
        }
    }

    public void testAreOrthogonal() {
        float[] az = new float[] {0, 5, 89, 359};
        float[] dip = new float[] {3, 10, -80, 80};
        // test dip=0
        for(int i = 0; i < az.length; i++) {
            Orientation one = new Orientation(az[i], 0);
            Orientation two = new Orientation(az[i] + 90, 0);
            assertTrue( OrientationUtil.areOrthogonal(one, two), az[i] + " to az plus 90");
            two = new Orientation(az[i] - 90, 0);
            assertTrue( OrientationUtil.areOrthogonal(one, two), az[i] + " to az minus 90");
            two = new Orientation(az[i], 90);
            assertTrue( OrientationUtil.areOrthogonal(one, two), az[i] + " to dip 90");
            two = new Orientation(az[i], -90);
            assertTrue( OrientationUtil.areOrthogonal(one, two), az[i] + " to dip -90");
            two = new Orientation(az[i], 0);
            assertFalse( OrientationUtil.areOrthogonal(one, two), az[i] + " to az");
            two = new Orientation(az[i] + 89, 0);
            assertFalse( OrientationUtil.areOrthogonal(one, two), az[i] + " to az plus 89");
            two = new Orientation(az[i] - 89, 0);
            assertFalse( OrientationUtil.areOrthogonal(one, two), az[i] + " to az minus 89");
            for(int j = 0; j < dip.length; j++) {
                two = new Orientation(az[i], dip[j]);
                assertFalse( OrientationUtil.areOrthogonal(one, two), az[i] + " to az+90, dip=" + dip[j]);
            }
        }
    }
}
