package edu.sc.seis.sod.bag;

import edu.iris.Fissures.FissuresException;
import edu.iris.Fissures.model.MicroSecondDate;
import edu.iris.Fissures.model.SamplingImpl;
import edu.iris.Fissures.model.TimeInterval;
import edu.iris.Fissures.model.UnitImpl;
import edu.iris.Fissures.seismogramDC.LocalSeismogramImpl;

/**
 * Calculus.java
 * 
 * 
 * Created: Thu Aug 15 14:47:23 2002
 * 
 * @author <a href="mailto:">Philip Crotwell</a>
 * @version
 */

public class Calculus {
	public Calculus() {

	}

	public static int[] difference(int[] data) {
		int[] out = new int[data.length - 1];
		for (int i = 0; i < out.length; i++) {
			out[i] = data[i + 1] - data[i];
		} // end of for (int i=0; i<out.length; i++)
		return out;
	}

	public static LocalSeismogramImpl difference(LocalSeismogramImpl seis)
			throws FissuresException {
		int[] seisData = seis.get_as_longs();
		int[] out = difference(seisData);
		return new LocalSeismogramImpl(seis, out);
	}

	public static LocalSeismogramImpl differentiate(LocalSeismogramImpl seis)
			throws FissuresException {
		SamplingImpl samp = seis.getSampling();
		double sampPeriod = samp.getPeriod().convertTo(UnitImpl.SECOND)
				.getValue();
		LocalSeismogramImpl outSeis;

		if (seis.can_convert_to_float()) {
			float[] data = seis.get_as_floats();
			float[] out = new float[data.length - 1];
			for (int i = 0; i < out.length; i++) {
				out[i] = (float) ((data[i + 1] - data[i]) / sampPeriod);
			} // end of for (int i=0; i<out.length; i++)
			outSeis = new LocalSeismogramImpl(seis, out);
		} else {
			// must be doubles
			double[] data = seis.get_as_doubles();
			double[] out = new double[data.length - 1];
			for (int i = 0; i < out.length; i++) {
				out[i] = (data[i + 1] - data[i]) / sampPeriod;
			}
			outSeis = new LocalSeismogramImpl(seis, out);
		} // end of else
		outSeis.y_unit = UnitImpl.divide(UnitImpl
				.createUnitImpl(outSeis.y_unit), UnitImpl.SECOND);
		MicroSecondDate begin = outSeis.getBeginTime();
		begin.add(new TimeInterval(samp.getPeriod().divideBy(2)));
		outSeis.begin_time = begin.getFissuresTime();
		return outSeis;
	}

	public static LocalSeismogramImpl integrate(LocalSeismogramImpl seis)
			throws FissuresException {
		SamplingImpl samp = seis.getSampling();
		double sampPeriod = samp.getPeriod().convertTo(UnitImpl.SECOND)
				.getValue();
		LocalSeismogramImpl outSeis;
		if (seis.can_convert_to_float()) {
			float[] data = seis.get_as_floats();
			float[] out = new float[data.length];
			out[0] = 0;
			for (int i = 1; i < out.length; i++) {
                out[i] = out[i-1] + (data[i-1]+data[i])/2 * (float)sampPeriod;
			}
			outSeis = new LocalSeismogramImpl(seis, out);
		} else {
			// must be doubles
			double[] data = seis.get_as_doubles();
			double[] out = new double[data.length];
			out[0] = 0;
			for (int i = 1; i < out.length; i++) {
				out[i] = out[i-1] + (data[i-1]+data[i])/2 * sampPeriod;
			}
			outSeis = new LocalSeismogramImpl(seis, out);
		} // end of else
		outSeis.y_unit = UnitImpl.multiply(UnitImpl
				.createUnitImpl(outSeis.y_unit), UnitImpl.SECOND);
		return outSeis;
	}

}// Calculus
