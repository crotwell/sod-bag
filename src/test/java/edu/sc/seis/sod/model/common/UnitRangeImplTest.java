package edu.sc.seis.sod.model.common;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * QuantityTest.java
 *
 *
 * Created: Thu Feb 20 09:40:13 2003
 *
 * @author <a href="mailto:crotwell@maple.local.">Philip Crotwell</a>
 * @version 1.0
 */
public class UnitRangeImplTest {
          
    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp() {
        q = new UnitRangeImpl(-.005, .00000005, UnitImpl.MICROSECOND.inverse());
        tenMM = new UnitRangeImpl(-10, 10, UnitImpl.MILLIMETER);
        tenMM_per_Sec = new UnitRangeImpl(-10, 10, UnitImpl.MILLIMETER_PER_SECOND);
    }

    /**
     * Tears down the text fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown() {
        q = null;
    }


    @Test
    public void testConvertTo() {
        UnitRangeImpl out = q.convertTo(UnitImpl.HERTZ);
        assertEquals(out.getMinValue(), q.getMinValue()*1000000, 0.001);
        
        out = tenMM.convertTo(UnitImpl.METER);
        assertEquals(out.getMinValue(), tenMM.getMinValue()/1000, 0.001);
        out = out.convertTo(UnitImpl.MILLIMETER);
        assertEquals(out.getMinValue(), tenMM.getMinValue(), 0.001);
        
        out = tenMM_per_Sec.convertTo(UnitImpl.METER_PER_SECOND);
        assertEquals(out.getMinValue(), tenMM_per_Sec.getMinValue()/1000, 0.001);
        out = out.convertTo(UnitImpl.MILLIMETER_PER_SECOND);
        assertEquals(out.getMinValue(), tenMM_per_Sec.getMinValue(), 0.001);
        
    }

    UnitRangeImpl q;
    
    UnitRangeImpl tenMM;
    
    UnitRangeImpl tenMM_per_Sec;
 
} // QuantityTest
