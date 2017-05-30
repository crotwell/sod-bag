package edu.sc.seis.sod.bag;

import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import edu.sc.seis.sod.model.common.MicroSecondDate;
import edu.sc.seis.sod.model.common.TimeRange;
import edu.sc.seis.sod.model.common.UnitImpl;
import edu.sc.seis.sod.model.station.ChannelId;
import edu.sc.seis.sod.model.station.CoefficientFilter;
import edu.sc.seis.sod.model.station.Filter;
import edu.sc.seis.sod.model.station.FilterType;
import edu.sc.seis.sod.model.station.Instrumentation;
import edu.sc.seis.sod.model.station.ListFilter;
import edu.sc.seis.sod.model.station.PoleZeroFilter;
import edu.sc.seis.sod.model.station.Response;
import edu.sc.seis.sod.model.station.Sensitivity;
import edu.sc.seis.sod.model.station.Stage;
import edu.sc.seis.sod.model.station.TransferType;

/**
 * ResponsePrint.java
 * 
 * 
 * Created: Wed Mar 7 19:15:59 2001
 * 
 * @author K. Sue Schoch
 * @version
 * 
 * This class prints a channel response in evalresp format.
 */
public class ResponsePrint {

    public ResponsePrint() {}

    /**
     * Prints an instrumentation object in resp format. Declared statically so
     * can just be called with edu.iris.dmc.Client.printResponse( ChannelId,
     * Instrumentation );
     * 
     * @param chanId
     *            channel id for the response
     * @param inst
     *            object containing the entire response
     */
    public static String printResponse(ChannelId chanId, Instrumentation inst) {
        TimeRange effective_time = inst.effective_time;
        Response response = inst.the_response;
        return printResponse(chanId, response, effective_time);
    }

    public static String printResponse(ChannelId chanId,
                                       Response response,
                                       TimeRange effective_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy,DDD,HH:mm:ss");
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        sdf.setTimeZone(gmt);
        MicroSecondDate stime = effective_time.getBeginTime();
        MicroSecondDate etime = effective_time.getEndTime();
        Sensitivity sensitivity = response.the_sensitivity;
        StringBuffer r = new StringBuffer();
        r.append("\n#");
        r.append("\n###################################################################################");
        r.append("\n#");
        r.append("\nB050F03     Station:       " + chanId.station_code);
        r.append("\nB050F16     Network:       "
                + chanId.network_id.network_code);
        r.append("\nB052F03     Location:      " + chanId.site_code);
        r.append("\nB052F04     Channel:       " + chanId.channel_code);
        r.append("\nB052F22     Start date:    " + sdf.format(stime));
        r.append("\nB052F23     End date:      " + sdf.format(etime));
        for(int i = 0; i < response.stages.length; i++) {
            int stageNum = i + 1;
            Stage stage = response.stages[i];
            if(stage.filters.length > 0) {
                r.append(printB53(chanId, effective_time, stageNum, stage));
                r.append(printB54(chanId, effective_time, stageNum, stage));
                r.append(printB55(chanId, effective_time, stageNum, stage));
                r.append(printB57(chanId, effective_time, stageNum, stage));
                r.append(printB58(chanId, effective_time, stageNum, stage));
            }
        }
        r.append(printSensitivity(chanId, sensitivity, effective_time));
        // System.out.println( r.toString() );
        return r.toString();
    }

    /**
     * Used to print a channel ID and the effective times.
     * 
     * @param id
     *            channel id for the response
     * @param effective_time
     *            effective time of the channel
     */
    public static String printHeader(ChannelId id, TimeRange effective_time) {
        StringBuffer s = new StringBuffer("");
        s.append("#                   |        " + id.network_id.network_code
                + "  ");
        if(id.station_code.length() == 5)
            s.append(id.station_code + " ");
        if(id.station_code.length() == 4)
            s.append(id.station_code + "  ");
        if(id.station_code.length() == 3)
            s.append(id.station_code + "   ");
        if(id.station_code.length() == 2)
            s.append(id.station_code + "    ");
        if(id.site_code.length() == 1)
            s.append(id.site_code + "  ");
        if(id.site_code.length() == 2)
            s.append(id.site_code + " ");
        s.append(id.channel_code + "           |\n");
        MicroSecondDate stime = effective_time.getBeginTime();
        MicroSecondDate etime = effective_time.getEndTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        sdf.setTimeZone(gmt);
        s.append("#                   |     " + sdf.format(stime));
        s.append(" to " + sdf.format(etime) + "      |\n");
        s.append("#                   +-----------------------------------+\n");
        s.append("#\n");
        return s.toString();
    }

    /**
     * Prints poles and zeros response blockette 53
     * 
     * @param id
     *            channel id for the response
     * @param effective_time
     *            effective time of the channel
     * @param stageNum
     *            the stage number
     * @param stage
     *            the entire stage object
     */
    public static String printB53(ChannelId id,
                                  TimeRange effective_time,
                                  int stageNum,
                                  Stage stage) {
        // Not a poles and zeros filter
        Filter filter = stage.filters[0];
        if(filter.discriminator().value() != FilterType._POLEZERO)
            return "";
        String transferType = "";
        if(stage.type == TransferType.LAPLACE)
            transferType = "A";
        else if(stage.type == TransferType.ANALOG)
            transferType = "B";
        else if(stage.type == TransferType.COMPOSITE)
            transferType = "C";
        else if(stage.type == TransferType.DIGITAL)
            transferType = "D";
        PoleZeroFilter pz = filter.pole_zero_filter();
        java.text.DecimalFormat f = new java.text.DecimalFormat("+0.00000E00;-0.00000E00", new DecimalFormatSymbols(Locale.US));
        java.text.DecimalFormat ind = new java.text.DecimalFormat(" 0", new DecimalFormatSymbols(Locale.US));
        StringBuffer s = new StringBuffer("\n#");
        s.append("\n#                   +-----------------------------------+");
        s.append("\n#                   |     Response (Poles and Zeros)    |\n");
        s.append(printHeader(id, effective_time));
        s.append("B053F03     Transfer function type:            "
                + transferType);
        s.append("\nB053F04     Stage sequence number:             " + stageNum);
        s.append("\nB053F05     Response in units lookup:          "
                + formatUnit(stage.input_units));
        s.append("\nB053F06     Response out units lookup:         "
                + formatUnit(stage.output_units));
        // careful here as normalization is optional in stages,
        // and may be a zero length array
        s.append("\nB053F07     AO normalization factor:           ");
        if(stage.the_normalization.length == 1) {
            s.append(f.format(stage.the_normalization[0].ao_normalization_factor));
        } else {
            s.append("1.0");
        }
        s.append("\nB053F08     Normalization frequency:           ");
        if(stage.the_normalization.length == 1) {
            s.append(f.format(stage.the_normalization[0].normalization_freq));
        } else {
            s.append("1.0");
        }
        s.append("\nB053F09     Number of zeroes:                  "
                + pz.zeros.length);
        s.append("\nB053F14     Number of poles:                   "
                + pz.poles.length);
        if(pz.zeros.length > 0) {
            s.append("\n#              Complex zeroes:");
            s.append("\n#               i  real          imag          real_error    imag_error");
            for(int k = 0; k < pz.zeros.length; k++) {
                if(k < 10)
                    s.append("\nB053F10-13      " + k + "  "
                            + f.format(pz.zeros[k].real) + "  "
                            + f.format(pz.zeros[k].imaginary) + "  "
                            + f.format(pz.zeros[k].real_error) + "  "
                            + f.format(pz.zeros[k].imaginary_error));
                else
                    s.append("\nB053F10-13     " + k + "  "
                            + f.format(pz.zeros[k].real) + "  "
                            + f.format(pz.zeros[k].imaginary) + "  "
                            + f.format(pz.zeros[k].real_error) + "  "
                            + f.format(pz.zeros[k].imaginary_error));
            }
        }
        if(pz.poles.length > 0) {
            s.append("\n#              Complex poles:");
            s.append("\n#               i  real          imag          real_error    imag_error");
            for(int k = 0; k < pz.poles.length; k++) {
                if(k < 10)
                    s.append("\nB053F15-18      " + k + "  "
                            + f.format(pz.poles[k].real) + "  "
                            + f.format(pz.poles[k].imaginary) + "  "
                            + f.format(pz.poles[k].real_error) + "  "
                            + f.format(pz.poles[k].imaginary_error));
                else
                    s.append("\nB053F15-18     " + k + "  "
                            + f.format(pz.poles[k].real) + "  "
                            + f.format(pz.poles[k].imaginary) + "  "
                            + f.format(pz.poles[k].real_error) + "  "
                            + f.format(pz.poles[k].imaginary_error));
            }
        }
        s.append("\n#");
        return s.toString();
    }

    /**
     * Prints Coefficient responses blockette 53
     * 
     * @param id
     *            channel id for the response
     * @param effective_time
     *            effective time of the channel
     * @param stageNum
     *            the stage number
     * @param stage
     *            the entire stage object
     */
    public static String printB54(ChannelId id,
                                  TimeRange effective_time,
                                  int stageNum,
                                  Stage stage) {
        // Not a poles and zeros filter
        Filter filter = stage.filters[0];
        if(filter.discriminator().value() != FilterType._COEFFICIENT)
            return "";
        StringBuffer s = new StringBuffer("\n#");
        String transferType = "";
        if(stage.type == TransferType.ANALOG)
            transferType = "A";
        else if(stage.type == TransferType.COMPOSITE)
            transferType = "C";
        else if(stage.type == TransferType.DIGITAL)
            transferType = "D";
        CoefficientFilter c = filter.coeff_filter();
        s.append("\n#                   +-----------------------------------+");
        s.append("\n#                   |       Response (Coefficients)     |\n");
        s.append(printHeader(id, effective_time));
        s.append("B054F03     Transfer function type:            "
                + transferType);
        s.append("\nB054F04     Stage sequence number:             " + stageNum);
        s.append("\nB054F05     Response in units lookup:          "
                + formatUnit(stage.input_units));
        s.append("\nB054F06     Response out units lookup:         "
                + formatUnit(stage.output_units));
        s.append("\nB054F07     Number of numerators:              "
                + c.numerator.length);
        s.append("\nB054F10     Number of denominators:            "
                + c.denominator.length);
        s.append("\n#");
        java.text.DecimalFormat f = new java.text.DecimalFormat("+0.00000E00;-0.00000E00", new DecimalFormatSymbols(Locale.US));
        if(c.numerator.length > 0) {
            s.append("\n#              Numerator coefficients:");
            s.append("\n#               i  coefficient   error");
            for(int k = 0; k < c.numerator.length; k++) {
                if(k < 10)
                    s.append("\nB054F08-09      " + k + "  "
                            + f.format(c.numerator[k].value) + "  "
                            + f.format(c.numerator[k].error));
                else
                    s.append("\nB054F08-09     " + k + "  "
                            + f.format(c.numerator[k].value) + "  "
                            + f.format(c.numerator[k].error));
            }
        }
        if(c.denominator.length > 0) {
            s.append("\n#              Denominator coefficients:");
            s.append("\n#               i  coefficient   error");
            for(int k = 0; k < c.denominator.length; k++) {
                if(k < 10)
                    s.append("\nB054F11-12      " + k + "  "
                            + f.format(c.denominator[k].value) + "  "
                            + f.format(c.denominator[k].error));
                else
                    s.append("\nB054F11-12     " + k + "  "
                            + f.format(c.denominator[k].value) + "  "
                            + f.format(c.denominator[k].error));
            }
        }
        s.append("\n#");
        return s.toString();
    }

    /**
     * Prints list response SEED blockette 55
     * 
     * @param id
     *            channel id for the response
     * @param effective_time
     *            effective time of the channel
     * @param stageNum
     *            the stage number
     * @param stage
     *            the entire stage object
     */
    public static String printB55(ChannelId id,
                                  TimeRange effective_time,
                                  int stageNum,
                                  Stage stage) {
        // Not a List filter
        Filter filter = stage.filters[0];
        if(filter.discriminator().value() != FilterType._LIST)
            return "";
        StringBuffer s = new StringBuffer("\n#");
        ListFilter c = filter.list_filter();
        s.append("\n#                   +-----------------------------------+");
        s.append("\n#                   |       Response List               |\n");
        s.append(printHeader(id, effective_time));
        s.append("\nB055F03     Stage sequence number:             " + stageNum);
        s.append("\nB055F04     Response in units lookup:          "
                + formatUnit(stage.input_units));
        s.append("\nB055F05     Response out units lookup:         "
                + formatUnit(stage.output_units));
        s.append("\nB055F06     Number of responses listed:        "
                + c.frequency.length);
        s.append("\n#");
        java.text.DecimalFormat f = new java.text.DecimalFormat("+0.00000E00;-0.00000E00", new DecimalFormatSymbols(Locale.US));
        java.text.DecimalFormat ind = new java.text.DecimalFormat(" 0", new DecimalFormatSymbols(Locale.US));
        if(c.frequency.length > 0) {
            s.append("\n#              i  frequency     amplitude     amplitude err phase angle   phase err");
            for(int k = 0; k < c.frequency.length; k++) {
                if(k < 10) {
                    s.append("\nB055F07-11      " + k);
                } else {
                    s.append("\nB055F07-11     " + k);
                }
                s.append("  " + f.format(c.frequency[k]));
                s.append("  " + f.format(c.amplitude[k]));
                s.append("  " + f.format(c.amplitude_error[k]));
                s.append("  " + f.format(c.phase[k]));
                s.append("  " + f.format(c.phase_error[k]));
            }
        }
        s.append("\n#");
        return s.toString();
    }

    /**
     * Prints deciamation response SEED blockette 57
     * 
     * @param id
     *            channel id for the response
     * @param effective_time
     *            effective time of the channel
     * @param stageNum
     *            the stage number
     * @param stage
     *            the entire stage object
     */
    public static String printB57(ChannelId id,
                                  TimeRange effective_time,
                                  int stageNum,
                                  Stage stage) {
        if(stage.the_decimation.length == 0)
            return "";
        java.text.DecimalFormat f = new java.text.DecimalFormat("+0.0000E00;-0.0000E00", new DecimalFormatSymbols(Locale.US));
        StringBuffer s = new StringBuffer("#\n");
        s.append("#                   +-----------------------------------+\n");
        s.append("#                   |             Decimation            |\n");
        s.append(printHeader(id, effective_time));
        s.append("B057F03     Stage sequence number:             " + stageNum);
        s.append("\nB057F04     Input sample rate (HZ):            "
                + f.format(stage.the_decimation[0].input_rate.numPoints));
        s.append("\nB057F05     Decimation factor:                 "
                + stage.the_decimation[0].factor);
        s.append("\nB057F06     Decimation offset:                 "
                + stage.the_decimation[0].offset);
        s.append("\nB057F07     Estimated delay (seconds):         "
                + f.format(stage.the_decimation[0].estimated_delay.getValue()));
        s.append("\nB057F08     Correction applied (seconds):      "
                + f.format(stage.the_decimation[0].correction_applied.getValue()));
        s.append("\n#");
        return s.toString();
    }

    /**
     * Prints sensitivity/gain response SEED blockette 58
     * 
     * @param id
     *            channel id for the response
     * @param effective_time
     *            effective time of the channel
     * @param stageNum
     *            the stage number
     * @param stage
     *            the entire stage object
     */
    public static String printB58(ChannelId id,
                                  TimeRange effective_time,
                                  int stageNum,
                                  Stage stage) {
        java.text.DecimalFormat f = new java.text.DecimalFormat("+0.00000E00;-0.00000E00", new DecimalFormatSymbols(Locale.US));
        StringBuffer s = new StringBuffer("#\n");
        s.append("#                   +-----------------------------------+\n");
        s.append("#                   |      Channel Sensitivity/Gain     |\n");
        s.append(printHeader(id, effective_time));
        s.append("B058F03     Stage sequence number:             " + stageNum);
        s.append("\nB058F04     Sensitivity:                       "
                + f.format(stage.the_gain.gain_factor));
        s.append("\nB058F05     Frequency of sensitivity:          "
                + f.format(stage.the_gain.frequency));
        s.append("\nB058F06     Number of calibrations:            0\n");
        return s.toString();
    }

    /**
     * Prints the overall sensitivity/gain response SEED blockette 58
     * 
     * @param id
     *            channel id for the response
     * @param inst
     *            the entire instrument response
     */
    public static String printSensitivity(ChannelId id, Instrumentation inst) {
        return printSensitivity(id,
                                inst.the_response.the_sensitivity,
                                inst.effective_time);
    }

    public static String printSensitivity(ChannelId id,
                                          Sensitivity sensitivity,
                                          TimeRange effective_time) {
        StringBuffer s = new StringBuffer("#\n");
        s.append("#                   +-----------------------------------+\n");
        s.append("#                   |      Channel Sensitivity/Gain     |\n");
        s.append(printHeader(id, effective_time));
        s.append("#\n");
        s.append("B058F03     Stage sequence number:             0");
        s.append("\nB058F04     Sensitivity:                       "
                + sensitivity.sensitivity_factor);
        s.append("\nB058F05     Frequency of sensitivity:          "
                + sensitivity.frequency);
        s.append("\nB058F06     Number of calibrations:            0\n");
        return s.toString();
    }

    public static String printInstEffectiveTime(Instrumentation inst) {
        MicroSecondDate stime = inst.effective_time.getBeginTime();
        MicroSecondDate etime = inst.effective_time.getEndTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSz");
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        sdf.setTimeZone(gmt);
        StringBuffer s = new StringBuffer("Instrument Effective Time:\n");
        s.append("  Starttime: " + sdf.format(stime) + "\n");
        s.append("  Endtime:   " + sdf.format(etime) + "\n");
        return s.toString();
    }

    public static String formatUnit(UnitImpl unit) {
        if(unit == null) {
            return "null";
        }
        UnitImpl impl = UnitImpl.createUnitImpl(unit);
        Iterator it = unitNames.keySet().iterator();
        while(it.hasNext()) {
            UnitImpl key = (UnitImpl)it.next();
            if (impl.equals(key)) {
                return (String)unitNames.get(key);
            }
        }
        // no configured name, auto-generate
        return unit.toString();
    }
    
    public static void addToNameMap(UnitImpl unit, String name) {
        unitNames.put(unit, name);
    }
    
    private static HashMap unitNames = new HashMap();
    
    static {
        addToNameMap(UnitImpl.VOLT, "V - Volts");
        addToNameMap(UnitImpl.COUNT, "COUNTS");
        addToNameMap(UnitImpl.METER_PER_SECOND, "M/S - Velocity in Meters/Second");
        addToNameMap(UnitImpl.NANOMETER, "NM");
        addToNameMap(UnitImpl.MILLIMETER, "MM");
        addToNameMap(UnitImpl.CENTIMETER, "CM");
        addToNameMap(UnitImpl.METER, "M");
        addToNameMap(UnitImpl.NANOMETER_PER_SECOND, "NM/S");
        addToNameMap(UnitImpl.MILLIMETER_PER_SECOND, "MM/S");
        addToNameMap(UnitImpl.CENTIMETER_PER_SECOND, "CM/S");
        addToNameMap(UnitImpl.NANOMETER_PER_SECOND_PER_SECOND, "NM/S**2");
        addToNameMap(UnitImpl.MILLIMETER_PER_SECOND_PER_SECOND, "MM/S**2");
        addToNameMap(UnitImpl.CENTIMETER_PER_SECOND_PER_SECOND, "CM/S**2");
        addToNameMap(UnitImpl.METER_PER_SECOND_PER_SECOND, "M/S**2");
        addToNameMap(UnitImpl.PASCAL, "P");
        addToNameMap(UnitImpl.TESLA, "T");
    }
}
