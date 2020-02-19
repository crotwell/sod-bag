/**
 * TauPUtilTest.java
 *
 * @author Created by Omnicore CodeGuide
 */

package edu.sc.seis.sod.bag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.List;

import edu.sc.seis.TauP.Arrival;
import edu.sc.seis.sod.mock.event.MockOrigin;
import edu.sc.seis.sod.mock.station.MockStation;

public class TauPUtilTest   {

    @Test
    public void testCalcTravelTimes() throws Exception {
        TauPUtil taup = TauPUtil.getTauPUtil();
        List<Arrival> arrivals = taup.calcTravelTimes(MockStation.createStation(),
                             MockOrigin.create(),
                             new String[] { "ttp" });
        for (Arrival arrival : arrivals) {
            System.out.println(" "+arrival.toString());
        }
        assertTrue( arrivals.size()>0);
    }
}

