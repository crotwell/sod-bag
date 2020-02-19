package edu.sc.seis.sod.bag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class CmplxTest {


    @Test
    public void testFFT() {
        float[] data = new float[16];
        data[0]= 1;
        Cmplx[] fft = Cmplx.fft(data);
        for (int i = 0; i < fft.length; i++) {
            System.out.println(i+" "+fft[i].r+"  "+fft[i].i);
        }
        assertTrue( true);
    }
}
