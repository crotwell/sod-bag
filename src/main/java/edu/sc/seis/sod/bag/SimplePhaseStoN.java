/**
 * SimplePhaseStoN.java
 *
 * @author Created by Omnicore CodeGuide
 */

package edu.sc.seis.sod.bag;

import java.util.List;

import edu.sc.seis.TauP.Arrival;
import edu.sc.seis.TauP.TauModelException;
import edu.sc.seis.sod.model.common.FissuresException;
import edu.sc.seis.sod.model.common.Location;
import edu.sc.seis.sod.model.common.MicroSecondDate;
import edu.sc.seis.sod.model.common.TimeInterval;
import edu.sc.seis.sod.model.common.UnitImpl;
import edu.sc.seis.sod.model.event.OriginImpl;
import edu.sc.seis.sod.model.seismogram.LocalSeismogramImpl;

/** Calculates a signal to noise ration around a phase. The short time window
 * (numerator of the ratio) is given by the standard deviation of the section of the seismogram
 * from phase + shortOffsetBegin to phase + shortOffsetEnd. The long time
 * window (demominator of the ratio) is similar. The first arriving phase of
 * the calculated arrivals is used. */
public class SimplePhaseStoN {

    public SimplePhaseStoN(String phase,
                           TimeInterval shortOffsetBegin,
                           TimeInterval shortOffsetEnd,
                           String longPhase,
                           TimeInterval longOffsetBegin,
                           TimeInterval longOffsetEnd,
                           TauPUtil taup) throws TauModelException {
        this.phase = phase;
        this.longPhase = longPhase;
        this.shortOffsetBegin = shortOffsetBegin;
        this.shortOffsetEnd = shortOffsetEnd;
        this.longOffsetBegin = longOffsetBegin;
        this.longOffsetEnd = longOffsetEnd;

        if (shortOffsetBegin == null) {
            throw new NullPointerException("shortOffsetBegin cannot be null");
        }
        if (shortOffsetEnd == null) {
            throw new NullPointerException("shortOffsetEnd cannot be null");
        }
        if (longOffsetBegin == null) {
            throw new NullPointerException("longOffsetBegin cannot be null");
        }
        if (longOffsetEnd == null) {
            throw new NullPointerException("longOffsetEnd cannot be null");
        }
        this.taup = taup;
        shortCut = new PhaseCut(taup, phase, shortOffsetBegin, phase, shortOffsetEnd);
        longCut = new PhaseCut(taup, longPhase, longOffsetBegin, longPhase, longOffsetEnd);
    }


    public SimplePhaseStoN(String phase,
                           TimeInterval shortOffsetBegin,
                           TimeInterval shortOffsetEnd,
                           TimeInterval longOffsetBegin,
                           TimeInterval longOffsetEnd,
                           TauPUtil taup) throws TauModelException {
        this(phase,
             shortOffsetBegin,
             shortOffsetEnd,
             phase,
             longOffsetBegin,
             longOffsetEnd,
             taup);
    }

    public SimplePhaseStoN(String phase,
                           TimeInterval shortOffsetBegin,
                           TimeInterval shortOffsetEnd,
                           TimeInterval longOffsetBegin,
                           TimeInterval longOffsetEnd) throws TauModelException {
        this(phase,
             shortOffsetBegin,
             shortOffsetEnd,
             phase,
             longOffsetBegin,
             longOffsetEnd);
    }
    
    public SimplePhaseStoN(String phase,
                           TimeInterval shortOffsetBegin,
                           TimeInterval shortOffsetEnd,
                           String longPhase,
                           TimeInterval longOffsetBegin,
                           TimeInterval longOffsetEnd) throws TauModelException {
        this(phase,
             shortOffsetBegin,
             shortOffsetEnd,
             longPhase,
             longOffsetBegin,
             longOffsetEnd,
             TauPUtil.getTauPUtil("prem"));
    }

    /** Defaults to plus and minus 5 seconds around the phase for the short
     * time interval, and the preceding 100 seconds before that for the long
     * time interval. */
    public SimplePhaseStoN(String phase) throws TauModelException {
        this(phase,
             new TimeInterval(-1, UnitImpl.SECOND),
             new TimeInterval(+5, UnitImpl.SECOND),
             new TimeInterval(-100, UnitImpl.SECOND),
             new TimeInterval(-5, UnitImpl.SECOND));
    }

    /** Calculates the trigger value for the given windows. Returns null if
     * either of the windows have no data in them. */
    public LongShortTrigger process(Location stationLoc,
                                    OriginImpl origin,
                                    LocalSeismogramImpl seis) throws FissuresException, TauModelException, PhaseNonExistent {
        LocalSeismogramImpl shortSeis = shortCut.cut(stationLoc, origin, seis);
        LocalSeismogramImpl longSeis = longCut.cut(stationLoc, origin, seis);
        if (shortSeis == null || longSeis == null || shortSeis.getNumPoints() <= 1 || longSeis.getNumPoints() <= 1) { return null; }

        
        Statistics longStat = new Statistics(longSeis);
        double denominator = longStat.stddev();
        if (denominator == 0) {
            // check for all zero seis
            return null;
        }
        Statistics shortStat = new Statistics(shortSeis);
        // use the stddev of the short, but based on the mean of the
        // long term
        double numerator = Math.sqrt(shortStat.var(longStat.mean()));
        
        List<Arrival> arrivals = taup.calcTravelTimes(stationLoc, origin, new String[] {phase});
        MicroSecondDate phaseTime = null;
        MicroSecondDate originTime = new MicroSecondDate(origin.getOriginTime());
        if (arrivals.size() != 0) {
            phaseTime = originTime.add(new TimeInterval(arrivals.get(0).getTime(),
                                                        UnitImpl.SECOND));
        }

        TimeInterval sampPeriod = (TimeInterval)seis.getSampling().getPeriod().convertTo(UnitImpl.SECOND);
        int phaseIndex = (int)seis.getBeginTime().subtract(phaseTime).convertTo(UnitImpl.SECOND).divideBy(sampPeriod).get_value();
        float ratio = (float)(numerator/denominator);
        return new LongShortTrigger(seis, phaseIndex, ratio, (float)numerator, (float)denominator);
    }

    protected String phase, longPhase;
    protected TimeInterval shortOffsetBegin;
    protected TimeInterval shortOffsetEnd;
    protected TimeInterval longOffsetBegin;
    protected TimeInterval longOffsetEnd;
    protected PhaseCut shortCut;
    protected PhaseCut longCut;
    protected TauPUtil taup;
}

