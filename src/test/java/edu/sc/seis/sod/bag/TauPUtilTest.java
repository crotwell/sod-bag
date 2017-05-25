/**
 * TauPUtilTest.java
 *
 * @author Created by Omnicore CodeGuide
 */

package edu.sc.seis.sod.bag;

import java.util.List;

import edu.sc.seis.TauP.Arrival;
import edu.sc.seis.sod.mock.event.MockOrigin;
import edu.sc.seis.sod.mock.station.MockStation;
import junit.framework.TestCase;

public class TauPUtilTest extends TestCase {

    public void testCalcTravelTimes() throws Exception {
        TauPUtil taup = TauPUtil.getTauPUtil();
        List<Arrival> arrivals = taup.calcTravelTimes(MockStation.createStation(),
                             MockOrigin.create(),
                             new String[] { "ttp" });
        for (Arrival arrival : arrivals) {
            System.out.println(" "+arrival.toString());
        }
        assertTrue("num arrivals="+arrivals.size(), arrivals.size()>0);
    }
}

