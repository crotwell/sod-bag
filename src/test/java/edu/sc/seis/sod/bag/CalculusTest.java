package edu.sc.seis.sod.bag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.sod.mock.seismogram.MockSeismogram;
import edu.sc.seis.sod.model.common.SamplingImpl;
import edu.sc.seis.sod.model.seismogram.LocalSeismogramImpl;


/**
 * Generated by JUnitDoclet, a tool provided by ObjectFab GmbH under LGPL.
 * Please see www.junitdoclet.org, www.gnu.org and www.objectfab.de for
 * informations about the tool, the licence and the authors.
 */

public class CalculusTest
{
	Calculus calculus =  new Calculus();

	public void testFloatIntegrate() throws Exception {
		float[] diff = new float[10];
		for (int i = 0; i < diff.length; i++) {
			diff[i] = 1;
		}
		LocalSeismogramImpl diffSeis = MockSeismogram.createTestData("est", new int[0]);
		diffSeis.data.flt_values(diff);
		diffSeis.num_points = diff.length;
		diffSeis.sampling_info = new SamplingImpl(1, TimeUtils.ONE_SECOND);
		LocalSeismogramImpl intSeis = Calculus.integrate(diffSeis);
		float[] intData = intSeis.get_as_floats();
		for (int i = 1; i < diff.length; i++) {
			assertEquals( i,intData[i], 0.001f, ""+i);
		}
	}
	public void testIntegrate() throws Exception {
		int[] diff = new int[10];
		for (int i = 0; i < diff.length; i++) {
			diff[i] = 1;
		}
		LocalSeismogramImpl diffSeis = MockSeismogram.createTestData("est", diff);
		diffSeis.sampling_info = new SamplingImpl(1, TimeUtils.ONE_SECOND);
		LocalSeismogramImpl intSeis = Calculus.integrate(diffSeis);
		float[] intData = intSeis.get_as_floats();
		for (int i = 1; i < diff.length; i++) {
			assertEquals( (float)i,intData[i], ""+i);
		}
	}

}
