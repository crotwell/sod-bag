package edu.sc.seis.sod.bag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sc.seis.TauP.Arrival;
import edu.sc.seis.TauP.TauModelException;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.sod.model.common.Location;
import edu.sc.seis.sod.model.common.LocationUtil;
import edu.sc.seis.sod.model.common.MicroSecondDate;
import edu.sc.seis.sod.model.common.TimeInterval;
import edu.sc.seis.sod.model.common.UnitImpl;
import edu.sc.seis.sod.model.event.CacheEvent;
import edu.sc.seis.sod.model.event.OriginImpl;
import edu.sc.seis.sod.model.seismogram.RequestFilter;
import edu.sc.seis.sod.model.station.StationIdUtil;
import edu.sc.seis.sod.util.display.EventUtil;

public class PhaseRequest  {

    protected PhaseRequest(String beginPhase, String endPhase, String model)
            throws TauModelException {
        this.beginPhase = beginPhase;
        this.endPhase = endPhase;
            util = TauPUtil.getTauPUtil(model);
    }

    public PhaseRequest(String beginPhase,
                        TimeInterval beginOffest,
                        String endPhase,
                        TimeInterval endOffset,
                        String model) throws TauModelException {
        this(beginPhase, endPhase, model);
        this.beginOffset = beginOffest;
        this.endOffset = endOffset;
    }
    
    public PhaseRequest(String beginPhase,
                        TimeInterval beginOffset,
                        String endPhase,
                        double endOffestRatio,
                        TimeInterval endOffsetMinimum,
                        boolean negateEndOffsetRatio,
                        String model) throws TauModelException {
        this(beginPhase, endPhase, model);
        this.beginOffset = beginOffset;
        this.endOffset = null;
        this.endOffsetRatio = endOffestRatio;
        this.endOffsetRatioMinimum = endOffsetMinimum;
        this.negateEndOffsetRatio = negateEndOffsetRatio;
    }
    
    public PhaseRequest(String beginPhase,
                        double beginOffestRatio,
                        TimeInterval beginOffsetMinimum,
                        boolean negateBeginOffsetRatio,
                        String endPhase,
                        TimeInterval endOffset,
                        String model) throws TauModelException {
        this(beginPhase, endPhase, model);
        this.beginOffset = null;
        this.beginOffsetRatio = beginOffestRatio;
        this.beginOffsetRatioMinimum = beginOffsetMinimum;
        this.negateBeginOffsetRatio = negateBeginOffsetRatio;
        this.endOffset = endOffset;
    }
    
    public PhaseRequest(String beginPhase,
                        double beginOffestRatio,
                        TimeInterval beginOffsetMinimum,
                        boolean negateBeginOffsetRatio,
                        String endPhase,
                        double endOffestRatio,
                        TimeInterval endOffsetMinimum,
                        boolean negateEndOffsetRatio,
                        String model) throws TauModelException {
        this(beginPhase, endPhase, model);
        this.beginOffset = null;
        this.beginOffsetRatio = beginOffestRatio;
        this.beginOffsetRatioMinimum = beginOffsetMinimum;
        this.negateBeginOffsetRatio = negateBeginOffsetRatio;
        this.endOffset = null;
        this.endOffsetRatio = endOffestRatio;
        this.endOffsetRatioMinimum = endOffsetMinimum;
        this.negateEndOffsetRatio = negateEndOffsetRatio;
    }

    public RequestFilter generateRequest(CacheEvent event,
                                         Channel channel) throws Exception {
        OriginImpl origin = EventUtil.extractOrigin(event);

        synchronized(this) {
            if(prevRequestFilter != null
                    && origin.getOriginTime().equals( prevOriginTime)
                    && LocationUtil.areEqual(origin.getLocation(), prevOriginLoc)
                    && LocationUtil.areSameLocation(channel, prevSiteLoc)) {
                // don't need to do any work
                return new RequestFilter(channel.get_id(),
                                         prevRequestFilter.start_time,
                                         prevRequestFilter.end_time);
            }
        }
        double begin = getArrivalTime(beginPhase, channel, origin);
        double end = getArrivalTime(endPhase, channel, origin);
        if(begin == -1 || end == -1) {
            // no arrivals found, return zero length request filters
            return null;
        }
        MicroSecondDate originDate = new MicroSecondDate(origin.getOriginTime());
        MicroSecondDate bDate = originDate.add(new TimeInterval(begin, UnitImpl.SECOND));
        MicroSecondDate eDate = originDate.add(new TimeInterval(end, UnitImpl.SECOND));

        TimeInterval bInterval;
        TimeInterval eInterval;
        if(beginOffset != null) {
            bInterval = beginOffset;
        } else {
            bInterval = getTimeIntervalFromRatio(bDate,
                                                 eDate,
                                                 beginOffsetRatio,
                                                 beginOffsetRatioMinimum,
                                                 negateBeginOffsetRatio);
        }
        if(endOffset != null) {
            eInterval = endOffset;
        } else {
            eInterval = getTimeIntervalFromRatio(bDate,
                                                 eDate,
                                                 endOffsetRatio,
                                                 endOffsetRatioMinimum,
                                                 negateEndOffsetRatio);
        }
        bDate = bDate.add(bInterval);
        eDate = eDate.add(eInterval);
        synchronized(this) {
            prevOriginLoc = origin.getLocation();
            prevSiteLoc = new Location(channel);
            prevOriginTime = origin.getOriginTime();
            prevRequestFilter = new RequestFilter(channel.get_id(),
                                                  bDate,
                                                  eDate);
        }
        logger.debug("Generated request from "
                + bDate
                + " to "
                + eDate
                + " for "
                + StationIdUtil.toStringNoDates(channel.getStation().get_id()));
        return prevRequestFilter;
    }

    private double getArrivalTime(String phase, Channel chan, OriginImpl origin)
            throws TauModelException {
        if(phase.equals(ORIGIN)) {
            return 0;
        }
        String[] phases = {phase};
        List<Arrival> arrivals = util.calcTravelTimes(new Location(chan),
                                                  origin,
                                                  phases);
        if(arrivals.size() == 0) {
            return -1;
        }
        // round to milliseconds
        return Math.rint(1000 * arrivals.get(0).getTime()) / 1000;
    }

    public static TimeInterval getTimeIntervalFromRatio(MicroSecondDate startPhaseTime,
                                                        MicroSecondDate endPhaseTime,
                                                        double ratio,
                                                        TimeInterval minimumTime,
                                                        boolean negate) {
        TimeInterval interval = new TimeInterval(endPhaseTime.difference(startPhaseTime)
                .multiplyBy(ratio));
        if(interval.lessThan(minimumTime)) {
            return negateIfTrue(minimumTime, negate);
        }
        return negateIfTrue(interval, negate);
    }

    public static TimeInterval negateIfTrue(TimeInterval interval,
                                            boolean negate) {
        if(negate) {
            double value = interval.getValue();
            return new TimeInterval(-value, interval.getUnit());
        }
        return interval;
    }
    
    public String getBeginPhase() {
        return beginPhase;
    }
    
    public String getEndPhase() {
        return endPhase;
    }
    
    public TimeInterval getBeginOffset() {
        return beginOffset;
    }
    
    public TimeInterval getEndOffset() {
        return endOffset;
    }
    
    public double getBeginOffsetRatio() {
        return beginOffsetRatio;
    }
    
    public double getEndOffsetRatio() {
        return endOffsetRatio;
    }
    
    public TimeInterval getBeginOffsetRatioMinimum() {
        return beginOffsetRatioMinimum;
    }
    
    public TimeInterval getEndOffsetRatioMinimum() {
        return endOffsetRatioMinimum;
    }
    
    public boolean isNegateBeginOffsetRatio() {
        return negateBeginOffsetRatio;
    }
    
    public boolean isNegateEndOffsetRatio() {
        return negateEndOffsetRatio;
    }
    
    private String beginPhase, endPhase;

    private TimeInterval beginOffset, endOffset;

    private double beginOffsetRatio, endOffsetRatio;

    private TimeInterval beginOffsetRatioMinimum, endOffsetRatioMinimum;

    private boolean negateBeginOffsetRatio = false,
            negateEndOffsetRatio = false;

    private TauPUtil util;

    private RequestFilter prevRequestFilter;

    private Location prevOriginLoc, prevSiteLoc;
    
    private MicroSecondDate prevOriginTime;

    private static Logger logger = LoggerFactory.getLogger(PhaseRequest.class);

    private static final String ORIGIN = "origin";
}// PhaseRequest
