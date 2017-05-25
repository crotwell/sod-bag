package edu.sc.seis.sod.bag;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public class CmplxTest {

    @Before
    public void setUp() throws Exception {}

    @Test
    public void testFFT() {
        float[] data = new float[16];
        data[0]= 1;
        Cmplx[] fft = Cmplx.fft(data);
        for (int i = 0; i < fft.length; i++) {
            System.out.println(i+" "+fft[i].r+"  "+fft[i].i);
        }
        assertTrue("just to get some output", false);
    }
}
