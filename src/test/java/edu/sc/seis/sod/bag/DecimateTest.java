package edu.sc.seis.sod.bag;

import java.time.Instant;

import edu.sc.seis.sod.mock.seismogram.MockSeismogram;
import edu.sc.seis.sod.mock.station.MockChannelId;
import edu.sc.seis.sod.model.common.SamplingImpl;
import edu.sc.seis.sod.model.seismogram.LocalSeismogramImpl;
import edu.sc.seis.sod.util.time.ClockUtil;
import junit.framework.TestCase;

public class DecimateTest extends TestCase {

    public void testSimpleDecimate() throws Exception {
        int factor = 5;
        LocalSeismogramImpl seis = MockSeismogram.createTestData("test",
                                                                           new int[100],
                                                                           Instant.now(),
                                                                           MockChannelId.createVerticalChanId(),
                                                                           new SamplingImpl(20,
                                                                                            ClockUtil.ONE_SECOND));
        Decimate decimate = new Decimate(factor);
        LocalSeismogramImpl out = decimate.apply(seis);
        assertEquals("seis length", seis.getNumPoints()/factor, out.getNumPoints());
        assertEquals("sampling period", seis.getSampling().getPeriod(),
                     out.getSampling().getPeriod());
    }
}
