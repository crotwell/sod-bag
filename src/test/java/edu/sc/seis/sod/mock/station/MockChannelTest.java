package edu.sc.seis.sod.mock.station;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.sod.mock.station.MockChannel;
import edu.sc.seis.sod.mock.station.MockStation;

public class MockChannelTest {

    @Test
    public void testCreateChannel() {
        Channel c = MockChannel.createChannel();
        assertEquals("Channel end", MockStation.YEAR_1999, c.getEndDate());
    }

}
