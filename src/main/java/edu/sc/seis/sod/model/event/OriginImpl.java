// **********************************************************************
//
// Generated by the ORBacus IDL to Java Translator
//
// Copyright (c) 2000
// Object Oriented Concepts, Inc.
// Billerica, MA, USA
//
// All Rights Reserved
//
// **********************************************************************
// Version: 4.0.3
package edu.sc.seis.sod.model.event;

import java.time.Duration;
import java.time.Instant;
//
// IDL:iris.edu/Fissures/IfEvent/Origin:1.0
//
/***/
import java.util.ArrayList;
import java.util.List;

import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.sod.model.common.Location;
import edu.sc.seis.sod.model.common.LocationUtil;
import edu.sc.seis.sod.model.common.ParameterRef;
import edu.sc.seis.sod.model.common.ParameterRefUtil;
import edu.sc.seis.sod.model.common.UnitImpl;


public class OriginImpl  {

    /** An id for this origin. The format is unspecified, but it
     *should be unique within a given service. */

    protected String id;

    //
    // IDL:iris.edu/Fissures/IfEvent/Origin/catalog:1.0
    //
    /** The catalog to which this origin belongs, for example
     *FINGER, ISCCD, MHDF */

    protected String catalog;

    //
    // IDL:iris.edu/Fissures/IfEvent/Origin/contributor:1.0
    //
    /** The contributor of this origin, such as NEIC, ISC. */

    protected String contributor;

    //
    // IDL:iris.edu/Fissures/IfEvent/Origin/origin_time:1.0
    //
    /** The estimate of when the event happened. */

    protected Instant originTime;

    //
    // IDL:iris.edu/Fissures/IfEvent/Origin/my_location:1.0
    //
    /** The estimate of where the event happened. */

    protected Location my_location;

    //
    // IDL:iris.edu/Fissures/IfEvent/Origin/magnitudes:1.0
    //
    /** The magnitude estimates for this event, associated with this
     *origin. */

    protected Magnitude[] magnitudes;

    //
    // IDL:iris.edu/Fissures/IfEvent/Origin/parm_ids:1.0
    //
    /** Parameters assiciated with this Origin. */

    protected ParameterRef[] parm_ids;

    //
    // IDL:iris.edu/Fissures/IfEvent/Origin/get_id:1.0
    //
    /** @return the id of this origin. The id is intended to be
     *a read-only immutible attribute of an Origin.*/
    public String get_id() {
        return id;
    }

    
    protected OriginImpl() {
        // make sure things are not null
        setParmIds(new ParameterRef[0]);
        set_id("");
    }

    /** Only for use for CORBA object serialization. */
    public static OriginImpl createEmpty() {
        return new OriginImpl();
    }

    public OriginImpl(String id, String catalog, String contributor,
                      Instant origin_time, Location my_location, Magnitude[] magnitudes,
            ParameterRef[] parm_ids) {
        this.id = id;
        this.setCatalog(catalog);
        this.setContributor(contributor);
        this.setOriginTime(origin_time);
        this.setLocation(my_location);
        this.setMagnitudes(magnitudes);
        this.setParmIds(parm_ids);
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        } else if(o instanceof OriginImpl) {
            OriginImpl oOrigin = (OriginImpl)o;
            if(equalsExceptTime(oOrigin) && oOrigin.getOriginTime().equals(getOriginTime())) { return true; }
        }
        return false;
    }

    public boolean equalsExceptTime(OriginImpl oOrigin) {
        if(oOrigin == this) {
            return true;
        } else if(LocationUtil.areEqual(oOrigin.getLocation(), getLocation())
                && oOrigin.getCatalog().equals(getCatalog())
                && oOrigin.getContributor().equals(getContributor())
                && MagnitudeUtil.areEqual(getMagnitudes(), oOrigin.getMagnitudes())
                && ParameterRefUtil.areEqual(getParmIds(), oOrigin.getParmIds())) { return true; }
        return false;
    }

    /**
     * does an equals, except origin times within 1 millisecond are judged to be
     * the same.
     */
    public boolean close(OriginImpl oOrigin) {
        if(oOrigin == this) {
            return true;
        } else if(equalsExceptTime(oOrigin)) {
            Instant myOTime = getOriginTime();
            Instant eventOTime = oOrigin.getOriginTime();
            if(TimeUtils.durationToDoubleSeconds(Duration.between(eventOTime, myOTime)) < 0.0001) { return true; }
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = 29;
        result += 89 * result + LocationUtil.hash(getLocation());
        result += 89 * result + getOriginTime().hashCode();
        result += 89 * result + getContributor().hashCode();
        result += 89 * result + getCatalog().hashCode();
        result += 89 * result + ParameterRefUtil.hash(getParmIds());
        result += 89 * result + MagnitudeUtil.hash(getMagnitudes());
        return result;
    }

    @Deprecated
    public Instant getFissuresTime() {
        return getOriginTime();
    }
    
    // for hibernate
    private int dbid;
    protected void setDbid(int dbid) {
        this.dbid = dbid;
    }
    public int getDbid() { return dbid;}
    
    protected void set_id(String id) {
        if(id == null) {
            this.id = ""+getDbid();
        } else {
            this.id = id;
        }
    }
    
    public List<Magnitude> getMagnitudeList() {
        // hibernate needs same collection returned from get as it put in via set for dirty checking
        if(hibernateMagList != null) {return hibernateMagList;}
        ArrayList<Magnitude> out = new ArrayList<Magnitude>();
        for(int i = 0; i < getMagnitudes().length; i++) {
            out.add(getMagnitudes()[i]);
        }
        return out;
    }
    
    protected void setMagnitudeList(List<Magnitude> list) {
        hibernateMagList = list;
        setMagnitudes(new Magnitude[list.size()]);
        setMagnitudes((Magnitude[])list.toArray(getMagnitudes()));
    }


    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getContributor() {
        return contributor;
    }

    public void setOriginTime(Instant origin_time) {
        this.originTime = origin_time;
    }

    public Instant getOriginTime() {
        return originTime;
    }

    public void setLocation(Location my_location) {
        this.my_location = my_location;
    }

    public Location getLocation() {
        return my_location;
    }

    public void setMagnitudes(Magnitude[] magnitudes) {
        this.magnitudes = magnitudes;
    }

    public Magnitude[] getMagnitudes() {
        return magnitudes;
    }

    public void setParmIds(ParameterRef[] parm_ids) {
        this.parm_ids = parm_ids;
    }

    public ParameterRef[] getParmIds() {
        return parm_ids;
    }
    
    protected List<Magnitude> hibernateMagList = null;
}