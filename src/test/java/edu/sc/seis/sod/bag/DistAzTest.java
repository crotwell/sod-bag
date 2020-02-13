package edu.sc.seis.sod.bag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


import edu.sc.seis.sod.model.common.DistAz;


public class DistAzTest
{
  
    @Test
    public void sueTest() {
        DistAz dz = new DistAz(29.331, -113.697, 29.331, -113.697);
        assertEquals(0, dz.getDelta(), 0.000001);
    }
  
}
