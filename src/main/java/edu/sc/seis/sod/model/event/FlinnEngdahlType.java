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

// Version: 4.0.5

package edu.sc.seis.sod.model.event;

//
// IDL:iris.edu/Fissures/FlinnEngdahlType:1.0
//
/***/

final public class FlinnEngdahlType 
{
    private static FlinnEngdahlType [] values_ = new FlinnEngdahlType[2];
    private int value_;

    public final static int _SEISMIC_REGION = 0;
    public final static FlinnEngdahlType SEISMIC_REGION = new FlinnEngdahlType(_SEISMIC_REGION);
    public final static int _GEOGRAPHIC_REGION = 1;
    public final static FlinnEngdahlType GEOGRAPHIC_REGION = new FlinnEngdahlType(_GEOGRAPHIC_REGION);

    protected
    FlinnEngdahlType(int value)
    {
        values_[value] = this;
        value_ = value;
    }

    public int
    value()
    {
        return value_;
    }

    public static FlinnEngdahlType
    from_int(int value)
    {
        return values_[value];
    }
}
