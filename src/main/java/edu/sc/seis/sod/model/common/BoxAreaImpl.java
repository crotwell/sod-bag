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
package edu.sc.seis.sod.model.common;

import java.io.Serializable;

//
// IDL:iris.edu/Fissures/BoxArea:1.0
//
/***/
public class BoxAreaImpl implements Area {

    public BoxAreaImpl(float min_latitude,
                       float max_latitude,
                       float min_longitude,
                       float max_longitude) {
        this.min_latitude = min_latitude;
        this.max_latitude = max_latitude;
        this.min_longitude = sanitize(min_longitude);
        this.max_longitude = sanitize(max_longitude);
    }
    
    public static float sanitize(float longitude){
        if(longitude > 180){
            longitude -= 360;
        }
        return longitude;
    }
    
    public static double sanitize(double longitude){
        if(longitude > 180 && longitude <= 360){
            longitude -= 360;
        }
        return longitude;
    }

    protected BoxAreaImpl() {}

    public static Serializable createEmpty() {
        return new BoxAreaImpl();
    }

    public String toString() {
        return "BoxArea (" + min_latitude + "," + min_longitude + ") ("
                + max_latitude + "," + max_longitude + ")";
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(o instanceof BoxAreaImpl) {
            BoxAreaImpl ba = (BoxAreaImpl)o;
            return ba.min_latitude == min_latitude
                    && ba.max_latitude == max_latitude
                    && ba.min_longitude == min_longitude
                    && ba.max_longitude == max_longitude;
        }
        return false;
    }

    public int hashCode() {
        int result = 7;
        result = result * 37 + Float.floatToIntBits(min_latitude);
        result = result * 37 + Float.floatToIntBits(max_latitude);
        result = result * 37 + Float.floatToIntBits(min_longitude);
        return result * 37 + Float.floatToIntBits(max_longitude);
    }
    

    public float min_latitude;

    //
    // IDL:iris.edu/Fissures/BoxArea/max_latitude:1.0
    //
    /***/

    public float max_latitude;

    //
    // IDL:iris.edu/Fissures/BoxArea/min_longitude:1.0
    //
    /***/

    public float min_longitude;

    //
    // IDL:iris.edu/Fissures/BoxArea/max_longitude:1.0
    //
    /***/

    public float max_longitude;
}
