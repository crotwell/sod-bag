package edu.sc.seis.sod.model.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class SamplingTest   {
    
    @BeforeEach
    public void setUp() {
        sampling = (SamplingImpl)createInstance();
    }

    @Test
    public void testGetFrequency() {
        QuantityImpl freq = sampling.getFrequency();
        assertTrue(freq.getUnit().isConvertableTo(UnitImpl.HERTZ));
    }
    
    @Test
    public void testEqualsHash() {
        assertTrue( createInstance().equals(createInstance()));
        assertFalse( createInstance().equals(createNotEqualInstance()));
    }
    
    protected Object createInstance(){
        return SamplingImpl.ofSamplesSeconds(20, 1.0);
    }
    
    protected Object createNotEqualInstance(){
        return SamplingImpl.ofSamplesSeconds(30, 1.0);
    }

    SamplingImpl sampling;
}
