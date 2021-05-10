package edu.sc.seis.sod.model.station;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;



public class NetworkIdUtilTest {

    @Test
    public void testTempNetId() {
        String[] tempCodes = new String[] { "XA", "YB", "ZC", "1A", "22", "34" };
        String[] permCodes = new String[] { "II", "G", "W7" };
        for (String netCode : tempCodes) {
            assertTrue( NetworkIdUtil.isTemporary(netCode));
        }
        for (String netCode : permCodes) {
            assertFalse( NetworkIdUtil.isTemporary(netCode));
        }
    }
}
