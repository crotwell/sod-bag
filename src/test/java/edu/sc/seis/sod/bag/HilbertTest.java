package edu.sc.seis.sod.bag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.IOException;

import edu.sc.seis.sod.mock.seismogram.MockSeismogram;
import edu.sc.seis.sod.model.common.FissuresException;
import edu.sc.seis.sod.model.seismogram.LocalSeismogramImpl;
import edu.sc.seis.sod.util.convert.sac.SacToFissures;


/**
 * @author crotwell
 * Created on Apr 26, 2005
 */
public class HilbertTest  {

    public void testImpluseResponse() throws Exception {
        Hilbert hilbert = new Hilbert();
        LocalSeismogramImpl testSeis = MockSeismogram.createDelta();
        LocalSeismogramImpl hilbertSeis = hilbert.apply(testSeis);
        
        Cmplx[] c = Cmplx.fft(testSeis.get_as_floats());
        Cmplx[] h = Cmplx.fft(hilbertSeis.get_as_floats());
        
        for(int i = 0; i < hilbertSeis.num_points && i < 10; i++) {
            assertEquals(hilbertSeis.get_as_floats()[i], (i%2==0 || i==0 ? 0 : (2/(Math.PI*i))), 0.0001);
        }
    }
    
    public void testAnalyticSignal() throws FissuresException {
        Hilbert hilbert = new Hilbert();
        LocalSeismogramImpl testSeis = MockSeismogram.createDelta();
        Cmplx[] c = Cmplx.fft(testSeis.get_as_floats());
        Cmplx[] a = hilbert.analyticSignal(testSeis);
        for(int i = 0; i < a.length; i++) {
            assertEquals(testSeis.get_as_floats()[i], a[i].r, 0.001);
        }
        LocalSeismogramImpl hilbertSeis = hilbert.apply(testSeis);
        for(int i = 0; i < hilbertSeis.get_as_floats().length; i++) {
            assertEquals(hilbertSeis.get_as_floats()[i], a[i].i, 0.001);
        }
    }
    
    public void testVsSAC() throws IOException, FissuresException {
        DataInputStream in =
            new DataInputStream(this.getClass().getClassLoader().getResourceAsStream("edu/sc/seis/sod/bag/delta.sac"));
        LocalSeismogramImpl delta = SacToFissures.getSeismogram(in);
        in =
            new DataInputStream(this.getClass().getClassLoader().getResourceAsStream("edu/sc/seis/sod/bag/hilbert_delta.sac"));
        LocalSeismogramImpl hilbert = SacToFissures.getSeismogram(in);
        LocalSeismogramImpl fisHilbert = (new Hilbert()).apply(delta);
        for(int i = 0; i < fisHilbert.get_as_floats().length; i++) {
            assertEquals( hilbert.get_as_floats()[i], fisHilbert.get_as_floats()[i], 0.01, i+" ");
        }
    }
}
