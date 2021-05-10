package edu.sc.seis.sod.bag;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;


import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.sac.SacTimeSeries;
import edu.sc.seis.sod.mock.seismogram.MockSeismogram;
import edu.sc.seis.sod.model.common.SamplingImpl;
import edu.sc.seis.sod.model.seismogram.LocalSeismogramImpl;
import edu.sc.seis.sod.util.convert.sac.FissuresToSac;

public class IterDeconTest {

    @Test
    public void testNoGauussProcess() throws Exception {
        float delta = 0.05f;
        float[] numData = new float[2048];
        numData[100] = 2;
        numData[200] = -1.5f;
        numData[300] = .25f;
        float[] denomData = new float[2048];
        denomData[100] = .5f;
        // without gaussian filter
        IterDecon zeroGauss = new IterDecon(3, true, 0.001f, 0.0f);
        IterDeconResult result = zeroGauss.process(numData, denomData, delta);
        float[] pred = result.getPredicted();
        int[] s = result.getShifts();
        float[] a = result.getAmps();
        assertEquals( 0, s[0], "zeroGauss spike 0");
        assertEquals( 4 / delta, a[0], 0.0001f, "zeroGauss amp 0");
        assertEquals( 100, s[1], "zeroGauss spike 1");
        assertEquals( -3 / delta, a[1], 0.0001f, "zeroGauss amp 1");
        assertEquals( 200, s[2], "zeroGauss spike 2 a=" + a[2]);
        assertEquals( .5f / delta, a[2], 0.0001f, "zeroGauss amp 2");
        assertEquals( 4 / delta, pred[0], 0.0001f, "zeroGauss pred 0");
        assertEquals( 0, pred[1], 0.0001f, "zeroGauss pred 1");
        assertEquals( -3 / delta, pred[100], 0.0001f, "zeroGauss pred 100");
        assertEquals( 0, pred[3], 0.0001f, "zeroGauss pred 101");
        assertEquals( .5f / delta, pred[200], 0.0001f, "zeroGauss pred 200");
        assertEquals( 0, pred[5], 0.0001f, "zeroGauss pred 201");
    }

    @Test
    public void testWithGauussProcess() throws Exception {
        float delta = 0.05f;
        IterDecon withGaussIterdecon = new IterDecon(200, true, .001f, 2.5f);
        // with gaussian filter
        float[] numData = new float[2048];
        numData[100] = 2;
        numData[200] = -1.5f;
        numData[300] = .25f;
        float[] denomData = new float[numData.length];
        denomData[100] = .5f;
        SamplingImpl sampling = new SamplingImpl(1, TimeUtils.durationFromSeconds(delta));
        LocalSeismogramImpl fakeNum = MockSeismogram.createTestData("num");
        fakeNum.setData(numData);
        fakeNum.channel_id.setChannelCode("BHR");
        fakeNum.sampling_info = sampling;
        LocalSeismogramImpl fakeDenom = MockSeismogram.createTestData("denom");
        fakeDenom.setData(denomData);
        fakeDenom.channel_id.setChannelCode("BHZ");
        fakeDenom.sampling_info = sampling;
        SacTimeSeries sac = FissuresToSac.getSAC(fakeNum);
        sac.write("withGauss.BHR.sac");
        sac = FissuresToSac.getSAC(fakeDenom);
        sac.write("withGauss.BHZ.sac");
        sac = null;
        IterDeconResult result = withGaussIterdecon.process(numData, denomData, delta);
        float[] pred = result.getPredicted();
        pred = withGaussIterdecon.phaseShift(pred, 5, delta);
        LocalSeismogramImpl predSeis = MockSeismogram.createTestData("denom");
        predSeis.setData(pred);
        predSeis.channel_id.setChannelCode("OUT");
        sac = FissuresToSac.getSAC(predSeis);
        sac.write("withGauss.ITR.sac");
        DataInputStream in = new DataInputStream(this.getClass()
                .getClassLoader()
                .getResourceAsStream("edu/sc/seis/sod/bag/withGauss.predicted.sac"));
        sac.read(in);
        float[] fortranData = sac.getY();
        int[] s = result.getShifts();
        float[] a = result.getAmps();
        assertEquals( 0, s[0], "gauss spike 0");
        assertEquals( 4 / delta, a[0], 0.0001f, "gauss amp 0");
        assertEquals( 100, s[1], "gauss spike 1");
        assertEquals( -3 / delta, a[1], 0.0001f, "gauss amp 1");
        assertEquals( 200, s[2], "gauss spike 2 a=" + a[2]);
        assertEquals( .5f / delta, a[2], 0.0001f, "gauss amp 2");
        assertEquals(fortranData[0],
                     pred[0],
                     0.0001f,
                     "position 0 " + fortranData[0] + "  " + pred[0] + "  ratio=" + (fortranData[0] / pred[0]));
        assertEquals(fortranData[100], pred[100], 0.0001f, "position 100 " + fortranData[100] + "  " + pred[100] + "  ratio="
                + (fortranData[100] / pred[100]));
        assertEquals( fortranData[200], pred[200], 0.0001f, 
        		"position 200 " + fortranData[200] + "  " + pred[200] + "  ratio="
                + (fortranData[200] / pred[200]));
        assertEquals( fortranData[300], pred[300], 0.0001f,
        		"position 300 " + fortranData[300] + "  " + pred[300] + "  ratio="
                        + (fortranData[300] / pred[300]));
        assertArrayEquals( fortranData, pred, 0.0001f, "data from fortran");
    }

    @Test
    public void testFakeCrustProcess() throws Exception {
        float delta = 0.05f;
        // with more complex demon
        float[] denomData = new float[2048];
        denomData[100] = .15f;
        denomData[101] = .5f;
        denomData[102] = .9f;
        denomData[103] = 1.1f;
        denomData[104] = .8f;
        denomData[105] = .4f;
        denomData[106] = .1f;
        denomData[107] = -.3f;
        denomData[108] = -.6f;
        denomData[109] = -.4f;
        denomData[110] = -.1f;
        denomData[111] = .1f;
        // create fake crust with Vp=6 and Vs=3.5, h=30
        float alpha = 6;
        float beta = 3.5f;
        float h = 30;
        float p = 7.6f / 111.19f;
        float etaP = (float)Math.sqrt(1 / (alpha * alpha) - p * p);
        float etaS = (float)Math.sqrt(1 / (beta * beta) - p * p);
        float timePs = h * (etaS - etaP);
        float timePpPs = h * (etaS + etaP);
        float timePsPs = h * (2 * etaS);
        // System.out.println("timePs="+timePs+"  timePpPs="+timePpPs+"  timePsPs="+timePsPs);
        float[] numData = new float[denomData.length];
        System.arraycopy(denomData, 0, numData, 0, denomData.length);
        // scale num by 1/3
        for (int i = 0; i < numData.length; i++) {
            numData[i] *= .33f;
        }
        float[] temp = new float[numData.length];
        System.arraycopy(denomData, 0, temp, 0, denomData.length);
        temp = IterDecon.phaseShift(temp, timePs, delta);
        // scale num by 1/5
        for (int i = 0; i < temp.length; i++) {
            numData[i] += .33f * .50f * temp[i];
        }
        System.arraycopy(denomData, 0, temp, 0, denomData.length);
        temp = IterDecon.phaseShift(temp, timePpPs, delta);
        // scale num
        for (int i = 0; i < temp.length; i++) {
            numData[i] += .33f * .3f * temp[i];
        }
        System.arraycopy(denomData, 0, temp, 0, denomData.length);
        temp = IterDecon.phaseShift(temp, timePsPs, delta);
        // scale num
        for (int i = 0; i < temp.length; i++) {
            numData[i] += .33f * .2f * temp[i];
        }
        IterDecon iterdecon = new IterDecon(100, true, .0001f, 3);
        IterDeconResult result = iterdecon.process(numData, denomData, delta);
        float[] pred = result.getPredicted();
        int[] s = result.getShifts();
        float[] a = result.getAmps();
        assertEquals( 0, s[0], "fake data spike 0");
        assertEquals( .33 / delta, a[0], 0.0001f, "fake data amp 0");
        assertEquals( Math.round(timePs / delta), s[1], 0.1f, "fake data spike 1");
        assertEquals( .33f * .5f / delta, a[1], 0.01f, "fake data amp 1");
        assertEquals( Math.round(timePpPs / delta), s[2], 0.1f, "fake data spike 2 a=" + a[2]);
        assertEquals( .33f * .3f / delta, a[2], 0.01f, "fake data amp 2");
        assertEquals( Math.round(timePsPs / delta), s[3], 0.1f, "fake data spike 3 a=" + a[3]);
        assertEquals( .33f * .2f / delta, a[3], 0.01f, "fake data amp 3");
        // JUnitDoclet end method process
    }

    @Test
    public void testESK1999_312_16_45_41_6() throws Exception {
        DataInputStream in = new DataInputStream(this.getClass()
                .getClassLoader()
                .getResourceAsStream("edu/sc/seis/sod/bag/ESK1999_312_16.predicted.sac"));
        SacTimeSeries sac = SacTimeSeries.read(in);
        in.close();
        float[] fortranData = sac.getY();
        in = new DataInputStream(this.getClass()
                .getClassLoader()
                .getResourceAsStream("edu/sc/seis/sod/bag/ESK_num.sac"));
        sac.read(in);
        in.close();
        float[] num = sac.getY();
        in = new DataInputStream(this.getClass()
                .getClassLoader()
                .getResourceAsStream("edu/sc/seis/sod/bag/ESK_denom.sac"));
        sac.read(in);
        in.close();
        float[] denom = sac.getY();
        IterDecon iterdecon = new IterDecon(100, true, .0001f, 3);
        IterDeconResult result = iterdecon.process(num, denom, sac.getHeader().getDelta());
        float[] pred = result.getPredicted();
        pred = iterdecon.phaseShift(pred, 5, sac.getHeader().getDelta());
        int[] s = result.getShifts();
        float[] a = result.getAmps();
        int i = 0;
        // spikes from fortran are in time, div delta to get index
        // output from fortran iterdecon_tjo is:
        // The maximum spike delay is 102.40012
        //
        // File Spike amplitude Spike delay Misfit Improvement
        // r001 0.384009242E+00 0.100 48.98% 51.0211%
        // r002 -0.132486761E+00 16.250 42.91% 6.0732%
        // r003 0.116493061E+00 2.250 38.21% 4.6952%
        // r004 -0.988256037E-01 10.800 34.83% 3.3792%
        // r005 -0.606716201E-01 15.450 33.56% 1.2736%
        // r006 -0.635700300E-01 20.650 32.16% 1.3983%
        // r007 -0.568093359E-01 41.350 31.04% 1.1166%
        // r008 0.520336218E-01 3.950 30.11% 0.9368%
        // r009 0.494165495E-01 1.000 29.26% 0.8449%
        // r010 -0.416982807E-01 79.850 28.66% 0.6015%
        // ... snip ...
        // r100 0.105094928E-01 3.500 14.55% 0.0382%
        //
        // Last Error Change = 0.0382%
        //
        // Number of bumps in final result: 100
        // The final deconvolution reproduces 85.4% of the signal.
        assertEquals( 0.100 / sac.getHeader().getDelta(), s[i], 0.1f);
        assertEquals( 0.384009242 / sac.getHeader().getDelta(), a[i], 0.001f);
        i++;
        assertEquals( 16.250 / sac.getHeader().getDelta(), s[i], 0.1f);
        assertEquals( -0.132486761 / sac.getHeader().getDelta(), a[i], 0.001f);
        i++;
        assertEquals( 2.250 / sac.getHeader().getDelta(), s[i], 0.1f);
        assertEquals( 0.116493061 / sac.getHeader().getDelta(), a[i], 0.001f);
        i++;
        assertEquals( 10.800 / sac.getHeader().getDelta(), s[i], 0.1f);
        assertEquals( -0.0988256037 / sac.getHeader().getDelta(), a[i], 0.001f);
        i++;
        assertEquals( 15.450 / sac.getHeader().getDelta(), s[i], 0.1f);
        assertEquals( -0.0606716201 / sac.getHeader().getDelta(), a[i], 0.001f);
        i++;
        assertArrayEquals( fortranData, pred, 0.000001f, "fortran predicted");
        assertEquals( 85.4f, result.getPercentMatch(), 0.1f, "percent match");
    }

    @Test
    public void testOnePhaseShift() throws Exception {
        float[] data = new float[1024];
        data[10] = 1;
        float[] out = IterDecon.phaseShift(data, 0.05f, 0.05f);
        assertEquals( data[9], out[10], .001, "9 shifts to 10");
        assertEquals( data[10], out[11], .001, "10 shifts to 11");
        assertEquals( data[11], out[12], .001, "11 shifts to 12");
    }

    @Test
    public void testFivePhaseShift() throws Exception {
        float[] data = new float[1024];
        data[10] = 1;
        data[11] = 2;
        data[12] = 1.1f;
        float[] out = IterDecon.phaseShift(data, 5f, 0.05f);
        // expected actual
        assertEquals( data[9], out[109], .001, "9 shifts to 109");
        assertEquals( data[10], out[110], .001, "10 shifts to 110");
        assertEquals( data[11], out[111], .001, "11 shifts to 111");
        assertEquals( data[11], out[111], .001, "12 shifts to 112");
        assertEquals( data[11], out[111], .001, "13 shifts to 113");
    }

    @Test
    public void testNextPowerTwo() throws Exception {
        // JUnitDoclet begin method phaseShift
        assertEquals(IterDecon.nextPowerTwo(3), 4);
        assertEquals(IterDecon.nextPowerTwo(4), 4);
        assertEquals(IterDecon.nextPowerTwo(1024), 1024);
        assertEquals(IterDecon.nextPowerTwo(1025), 2048);
        // JUnitDoclet end method phaseShift
    }

    @Test
    public void testGaussianFilter() throws Exception {
        DataInputStream in = new DataInputStream(this.getClass()
                .getClassLoader()
                .getResourceAsStream("edu/sc/seis/sod/bag/gauss1024.sac"));
        SacTimeSeries sac = SacTimeSeries.read(in);
        float[] data = new float[sac.getHeader().getNpts()];
        data[100] = 1 / sac.getHeader().getDelta();
        float[] sacData = sac.getY();
        float[] out = IterDecon.gaussianFilter(data, 2.5f, sac.getHeader().getDelta());
        assertArrayEquals( sacData, out, 0.001f, "gaussian filter");
        if (IterDecon.useNativeFFT) {
            // test non-native as well
            IterDecon.useNativeFFT = false;
            out = IterDecon.gaussianFilter(data, 2.5f, sac.getHeader().getDelta());
            assertArrayEquals( sacData, out, 0.001f, "gaussian filter");
            IterDecon.useNativeFFT = true;
        }
    }

    @Test
    public void testNativeVsJavaInverseFFT() {
        float[] data = new float[32];
        data[10] = 1;
        float[] javaFFT = new float[data.length];
        System.arraycopy(data, 0, javaFFT, 0, data.length);
        javaFFT = IterDecon.shortenFFT(Cmplx.four1Forward(javaFFT));
        float[] invJavaFFT = Cmplx.four1Inverse(IterDecon.lengthenFFT(javaFFT));
        assertArrayEquals(data, invJavaFFT, 0.00001f);
        for (int i = 0; i < data.length; i++) {
            assertEquals( data[i], invJavaFFT[i], 0.00001f);
        }
    }

    @Test
    public void testGetMinIndex() {
        float[] data = {3, 4, -5, 0, 4, 4, 0, -5, 4, 3};
        int index = IterDecon.getMinIndex(data);
        assertEquals( 2, index, "min index");
        index = IterDecon.getMaxIndex(data);
        assertEquals( 1, index, "max index");
        index = IterDecon.getAbsMaxIndex(data);
        assertEquals( 2, index, "abs max index");
    }

    @Test
    public void testPower() {
        float[] data = {0, 2, 3, -1, -2, 0};
        assertEquals(18f, IterDecon.power(data), 0.00001f);
    }

    @Test
    public void testCorrelationNorm() {
        float[] fData = {0, 0, 2, 0, 0, 0, 0, 0};
        float[] gData = {0, 2, 0, 0, 0, 0, 0, 0};
        float[] corr = IterDecon.correlateNorm(fData, gData);
        assertEquals( 0f, corr[0], 0.00001f);
        assertEquals( 1f, corr[1], 0.00001f);
        assertEquals( 0f, corr[2], 0.00001f);
        assertEquals( 0f, corr[3], 0.00001f);
    }
    


    @Test
    public void testCorrelationNormTwo() throws Exception {
        int n = 1024;
        float[] fData = new float[n];
        float[] gData = new float[n];
        for (int i = 0; i < gData.length; i++) {
            fData[i] = 1;
            gData[i] = 1;
        }
        int lag = 6;
        gData[1] = 2;  fData[lag+1] = gData[1];
        gData[2] = 4;  fData[lag+2] = gData[2];
        gData[3] = -1;  fData[lag+3] = gData[3];

        float[] corr = IterDecon.correlateNorm(fData, gData);
        for (int i = 0; i < corr.length && i < 10; i++) {
            System.out.println(" cpu corr "+i+" "+corr[i]);
        }
        System.out.println();
        for (int i = corr.length-10; i < corr.length; i++) {
            System.out.println("cpu corr "+i+" "+corr[i]);
        }
        float zlg = 1042;
        assertEquals( 1028f/zlg, corr[0], 0.00001f);
        assertEquals( 1028f/zlg, corr[1], 0.00001f);
        assertEquals( 1028f/zlg, corr[2], 0.00001f);
        assertEquals( 1f, corr[lag], 0.00001f);
    }
    
    @Test
    public void testConvolve() throws Exception {
        int n = 1024;
        float delta = 0.1f;
        float[] fData = new float[n];
        float[] gData = new float[n];
        fData[1] = 2;
        fData[2] = 4;
        fData[3] = -1;
        gData[1] = 1/delta; // should shift all values by 1 place
        float[] convolve = Cmplx.convolve(fData, gData, delta);
        float[] fShifted = new float[n];
        System.arraycopy(fData, 0, fShifted, 1, fData.length-1);
        assertArrayEquals(fShifted, convolve, 0.0001f);
    }

    
    @Test
    public void testBuildSpikes() throws Exception {
        int n = 1024;
        int bumps = 400;
        float delta = 0.1f;
        float[] amps = new float[bumps];
        int[] shifts = new int[bumps];
        for (int i = 0; i < shifts.length; i++) {
            amps[i] = i;
            shifts[i] = i;
        }
        float[] cpu = IterDecon.buildSpikes(amps, shifts, n);
        for (int i = 0; i < cpu.length; i++) {
            if (i < shifts.length) {
                assertEquals( i, cpu[i], 0.0001f);
            } else {
                assertEquals( 0, cpu[i], 0.0001f);
            }
        }
        
    }
    
    
    @Test
    public void testIterDeconIdentity() throws Exception {
        // JUnitDoclet begin method phaseShift
        float[] data = new float[128];
        data[49] = .5f;
        IterDecon iterdecon = new IterDecon(100, true, .0001f, 3);
        IterDeconResult out = iterdecon.process(data, data, 1.0f);
        IterDecon.gaussianFilter(out.predicted, 3.0f, 1.0f);
        // for ( int i=0; i<out.predicted.length; i++) {
        // System.out.println("predicted "+i+"  data="+data[i]+"  out="+out.predicted[i]);
        // assertEquals("predicted "+i+"  data="+data[i]+"  out="+out.predicted[i],
        // data[i], out.predicted[i], 0.001);
        // } // end of for ()
        /*
         * these values come from running New_Decon_Process on a impulse
         * generated with sac's fg impulse command (100 datapoints, 1 at 49) The
         * receiver function of data from itself should be unity at lag 0 and
         * zero elsewhere, of course the gaussian tends to smear it out.
         * 
         * piglet 51>../New_Decon_Process/iterdecon_tjo
         * 
         * Program iterdeconfd - Version 1.0X, 1997-98 Chuck Ammon, Saint Louis
         * University
         * 
         * impulse100.sac impulse100.sac output 100 10 .001 3.0 1 0 output
         * 
         * The maximum spike delay is 64.00000
         * 
         * File Spike amplitude Spike delay Misfit Improvement r001
         * 0.100000012E+01 0.000 0.00% 100.0000% r002 -0.126299312E-06 0.000
         * 0.00% 0.0000%
         * 
         * Last Error Change = 0.0000%
         * 
         * Hit the min improvement tolerance - halting. Number of bumps in final
         * result: 1 The final deconvolution reproduces 100.0% of the signal.
         */
        assertEquals(0.9156569, out.predicted[0], .000001);
        assertEquals(0.04999885, out.predicted[1], .000001);
        assertEquals(-0.01094833, out.predicted[2], .000001);
        assertEquals(0.004774094, out.predicted[3], .000001);
        assertEquals(-0.002670953, out.predicted[4], .000001);
        // JUnitDoclet end method phaseShift
    }
}
