package edu.sc.seis.sod.bag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.sod.mock.seismogram.MockSeismogram;
import edu.sc.seis.sod.mock.station.MockChannelId;
import edu.sc.seis.sod.model.common.SamplingImpl;
import edu.sc.seis.sod.model.seismogram.LocalSeismogramImpl;

public class DecimateTest  {

    public void testSimpleDecimate() throws Exception {
        int factor = 5;
        LocalSeismogramImpl seis = MockSeismogram.createTestData("test",
                                                                           new int[100],
                                                                           Instant.now(),
                                                                           MockChannelId.createVerticalChanId(),
                                                                           new SamplingImpl(20,
                                                                                            TimeUtils.ONE_SECOND));
        Decimate decimate = new Decimate(factor);
        LocalSeismogramImpl out = decimate.apply(seis);
        assertEquals( seis.getNumPoints()/factor, out.getNumPoints(), "seis length");
        assertEquals(seis.getSampling().getPeriod().multipliedBy(factor),
                     out.getSampling().getPeriod(), "sampling period");
    }
}
