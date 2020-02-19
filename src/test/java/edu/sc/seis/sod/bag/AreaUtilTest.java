package edu.sc.seis.sod.bag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import edu.sc.seis.TauP.SphericalCoords;
import edu.sc.seis.sod.model.common.BoxAreaImpl;
import edu.sc.seis.sod.model.common.GlobalAreaImpl;
import edu.sc.seis.sod.model.common.Location;
import edu.sc.seis.sod.model.common.LocationType;
import edu.sc.seis.sod.model.common.PointDistanceAreaImpl;
import edu.sc.seis.sod.model.common.QuantityImpl;
import edu.sc.seis.sod.model.common.UnitImpl;

public class AreaUtilTest {

    @Test
    public void testSimpleConvertToBox() {
        BoxAreaImpl b = new BoxAreaImpl(-90, 90, -180, 180);
        assertEquals(b, AreaUtil.makeContainingBox(b));
        assertEquals(b, AreaUtil.makeContainingBox(new GlobalAreaImpl()));
    }

    @Test
    public void testDonutConvertToBoxNearPole() {
        PointDistanceAreaImpl meridian = new PointDistanceAreaImpl(80,
                                                               0,
                                                               ZERO,
                                                               TEN_DEG);
        double eastFiveLon = SphericalCoords.lonFor(80, 0, 5, 90);
        BoxAreaImpl box = AreaUtil.makeContainingBox(meridian);
        assertTrue( eastFiveLon > box.min_longitude, "eastFive > min lon");
        assertTrue(eastFiveLon < box.max_longitude, "eastFive < max lon");
    }

    @Test
    public void testDonutConvertToBox() {
        PointDistanceAreaImpl meridian = new PointDistanceAreaImpl(0,
                                                               0,
                                                               ZERO,
                                                               TEN_DEG);
        assertEquals(new BoxAreaImpl(-10, 10, -10, 10),
                     AreaUtil.makeContainingBox(meridian));
        PointDistanceAreaImpl westOfDateLine = new PointDistanceAreaImpl(0,
                                                                     175,
                                                                     ZERO,
                                                                     TEN_DEG);
        assertEquals(new BoxAreaImpl(-10, 10, 165, -175),
                     AreaUtil.makeContainingBox(westOfDateLine));
        PointDistanceAreaImpl eastOfDateLine = new PointDistanceAreaImpl(0,
                                                                     -175,
                                                                     ZERO,
                                                                     TEN_DEG);
        assertEquals(new BoxAreaImpl(-10, 10, 175, -165),
                     AreaUtil.makeContainingBox(eastOfDateLine));
    }

    @Test
    public void testInPolygon() {
        QuantityImpl el = new QuantityImpl(0, UnitImpl.METER);
        QuantityImpl depth = new QuantityImpl(0, UnitImpl.METER);
        Location[] bounds = new Location[] {new Location(1,
                                                         1,
                                                         el,
                                                         depth),
                                            new Location(2,
                                                         3,
                                                         el,
                                                         depth),
                                            new Location(2,
                                                         2,
                                                         el,
                                                         depth),
                                            new Location(-1,
                                                         1,
                                                         el,
                                                         depth),
                                            new Location(-2,
                                                         -1,
                                                         el,
                                                         depth),
                                            new Location(1,
                                                         -1,
                                                         el,
                                                         depth)};
        Location point;
        point = new Location(0, 0, el, depth);
        assertTrue( AreaUtil.inArea(bounds, point), "in 0,0");
        point = new Location(4, 4, el, depth);
        assertFalse( AreaUtil.inArea(bounds, point), "out 4,4");
        point = new Location(1, 1, el, depth);
        assertTrue( AreaUtil.inArea(bounds, point), "on boundary 1,1");
        point = new Location(3.00001f,
                             2.00001f,
                             el,
                             depth);
        assertFalse( AreaUtil.inArea(bounds, point), "barely out 3+,2+");
    }

    public static final QuantityImpl ZERO = new QuantityImpl(0, UnitImpl.DEGREE);

    public static final QuantityImpl TEN_DEG = new QuantityImpl(10,
                                                                UnitImpl.DEGREE);
}
