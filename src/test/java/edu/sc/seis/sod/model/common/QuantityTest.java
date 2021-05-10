package edu.sc.seis.sod.model.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class QuantityTest  {

    @BeforeEach
    public void setUp() {
        q = (QuantityImpl)createInstance();
        tenMM = (QuantityImpl)createNotEqualInstance();
        tenMM_per_Sec = new QuantityImpl(10, UnitImpl.MILLIMETER_PER_SECOND);
    }

    @Test
    public void testConvertTo() {
        QuantityImpl out = q.convertTo(UnitImpl.HERTZ);
        assertEquals(out.getValue(), q.getValue() * 1000000, 0.001);
        out = tenMM.convertTo(UnitImpl.METER);
        assertEquals( out.getValue(), tenMM.getValue() / 1000, 0.001, "meter");
        out = out.convertTo(UnitImpl.MILLIMETER);
        assertEquals( out.getValue(), tenMM.getValue(), 0.001, "millimeter");
        out = tenMM_per_Sec.convertTo(UnitImpl.METER_PER_SECOND);
        assertEquals( out.getValue(), tenMM_per_Sec.getValue() / 1000, 0.001, "m/s");
        out = out.convertTo(UnitImpl.MILLIMETER_PER_SECOND);
        assertEquals( out.getValue(), tenMM_per_Sec.getValue(), 0.001, "mm/s");
    }

    @Test
    public void testInverse() {
        QuantityImpl out = q.inverse().inverse();
        assertEquals(out.getValue(), q.getValue(), 0.00001);
    }

    @Test
    public void testConvertEquals() {
        UnitImpl fakeKM = new UnitImpl(UnitBase.METER, 1, 3);
        QuantityImpl one = new QuantityImpl(378.0, UnitImpl.KILOMETER);
        QuantityImpl two;
        two = new QuantityImpl(378.0, UnitImpl.KILOMETER);
        assertTrue( one.equals(two), "equals with unit object equals");
        two = new QuantityImpl(378.0, fakeKM);
        assertTrue( one.equals(two), "equals with non-object equals");
        UnitImpl[] sub = new UnitImpl[1];
        sub[0] = fakeKM;
        two = new QuantityImpl(378.0, new UnitImpl(sub, 0, "", 1, 1));
        assertTrue( one.equals(two), "equals with array subunit " + two);
    }

    @Test
    public void testTemperature() {
        QuantityImpl zeroK = new QuantityImpl(0, UnitImpl.KELVIN);
        QuantityImpl zeroC = new QuantityImpl(0, UnitImpl.CELSIUS);
        QuantityImpl zeroF = new QuantityImpl(0, UnitImpl.FAHRENHEIT);
        QuantityImpl out;
        assertEquals(-273.15, zeroK.getValue(UnitImpl.CELSIUS), 0.000000001);
        assertEquals(-459.67, zeroK.getValue(UnitImpl.FAHRENHEIT), 0.000000001);
        assertEquals(273.15, zeroC.getValue(UnitImpl.KELVIN), 0.000000001);
        assertEquals(0, zeroC.getValue(UnitImpl.CELSIUS), 0.000000001);
        assertEquals(32, zeroC.getValue(UnitImpl.FAHRENHEIT), 0.000000001);
        assertEquals(-17.7777777778, zeroF.getValue(UnitImpl.CELSIUS), 0.0000001);
        assertEquals(255.37, zeroF.getValue(UnitImpl.KELVIN), 0.01);
    }

    @Test
    public void convertDayToMicroSecTest() {
        QuantityImpl q = new QuantityImpl(1, UnitImpl.DAY);
        q = q.convertTo(UnitImpl.MICROSECOND);
        assertEquals((long)q.get_value(), 86400l*1000*1000);
    }
    
    protected Object createInstance() {
        return new QuantityImpl(.00000005, UnitImpl.MICROSECOND.inverse());
    }

    protected Object createNotEqualInstance() {
        return new QuantityImpl(10, UnitImpl.MILLIMETER);
    }

    QuantityImpl q;

    QuantityImpl tenMM;

    QuantityImpl tenMM_per_Sec;
} // QuantityTest
