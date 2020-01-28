package uk.ac.dundee.computing.aec.sensorweb.lib;

import java.net.URLDecoder;
import java.util.StringTokenizer;
//import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import com.eaio.uuid.UUID;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public final class Convertors {

    public static int DISPLAY_IMAGE = 0;
    public static int DISPLAY_THUMB = 1;
    public static int DISPLAY_PROCESSED = 2;

    public void Convertors() {

    }

    public static java.util.UUID getTimeUUID() {
        return java.util.UUID.fromString(new com.eaio.uuid.UUID().toString());
    }

    public static byte[] asByteArray(java.util.UUID uuid) {

        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }

        return buffer;
    }

    public static byte[] longToByteArray(long value) {
        byte[] buffer = new byte[8]; //longs are 8 bytes I believe
        for (int i = 7; i >= 0; i--) { //fill from the right
            buffer[i] = (byte) (value & 0x00000000000000ff); //get the bottom byte

            //System.out.print(""+Integer.toHexString((int)buffer[i])+",");
            value = value >>> 8; //Shift the value right 8 bits
        }
        return buffer;
    }

    public static long byteArrayToLong(byte[] buffer) {
        long value = 0;
        long multiplier = 1;
        for (int i = 7; i >= 0; i--) { //get from the right

            //System.out.println(Long.toHexString(multiplier)+"\t"+Integer.toHexString((int)buffer[i]));
            value = value + (buffer[i] & 0xff) * multiplier; // add the value * the hex mulitplier
            multiplier = multiplier << 8;
        }
        return value;
    }

    public static void displayByteArrayAsHex(byte[] buffer) {
        int byteArrayLength = buffer.length;
        for (int i = 0; i < byteArrayLength; i++) {
            int val = (int) buffer[i];
            // System.out.print(Integer.toHexString(val)+",");
        }

        //System.out.println();
    }

//From: http://www.captain.at/howto-java-convert-binary-data.php
    public static long arr2long(byte[] arr, int start) {
        int i = 0;
        int len = 4;
        int cnt = 0;
        byte[] tmp = new byte[len];
        for (i = start; i < (start + len); i++) {
            tmp[cnt] = arr[i];
            cnt++;
        }
        long accum = 0;
        i = 0;
        for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
            accum |= ((long) (tmp[i] & 0xff)) << shiftBy;
            i++;
        }
        return accum;
    }

    public static String[] SplitTags(String Tags) {
        String args[] = null;

        StringTokenizer st = Convertors.SplitTagString(Tags);
        args = new String[st.countTokens() + 1];  //+1 for _No_Tag_
        //Lets assume the number is the last argument

        int argv = 0;
        while (st.hasMoreTokens()) {;
            args[argv] = new String();
            args[argv] = st.nextToken();
            argv++;
        }
        args[argv] = "_No-Tag_";
        return args;
    }

    private static StringTokenizer SplitTagString(String str) {
        return new StringTokenizer(str, ",");

    }

    public static String[] SplitFiletype(String type) {
        String args[] = null;

        StringTokenizer st = SplitString(type);
        args = new String[st.countTokens()];
        //Lets assume the number is the last argument

        int argv = 0;
        while (st.hasMoreTokens()) {;
            args[argv] = new String();

            args[argv] = st.nextToken();
            try {
                //System.out.println("String was "+URLDecoder.decode(args[argv],"UTF-8"));
                args[argv] = URLDecoder.decode(args[argv], "UTF-8");

            } catch (Exception et) {
                System.out.println("Bad URL Encoding" + args[argv]);
            }
            argv++;
        }

        //so now they'll be in the args array.  
        // argv[0] should be the user directory
        return args;
    }

    public static String[] SplitRequestPath(HttpServletRequest request) {
        String args[] = null;

        StringTokenizer st = SplitString(request.getRequestURI());
        args = new String[st.countTokens()];
        //Lets assume the number is the last argument

        int argv = 0;
        while (st.hasMoreTokens()) {;
            args[argv] = new String();

            args[argv] = st.nextToken();
            try {
                //System.out.println("String was "+URLDecoder.decode(args[argv],"UTF-8"));
                args[argv] = URLDecoder.decode(args[argv], "UTF-8");

            } catch (Exception et) {
                System.out.println("Bad URL Encoding" + args[argv]);
            }
            argv++;
        }

        //so now they'll be in the args array.  
        // argv[0] should be the user directory
        return args;
    }

    private static StringTokenizer SplitString(String str) {
        return new StringTokenizer(str, "/");

    }

    public static Date JavaScriptStringToDate(String dd) throws ParseException {
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EE MMM d y H:m:s 'GMT'Z (zz)");
        try {
            cl.setTime(sdf.parse(dd));
        } catch (ParseException et) {
            System.out.println("Can't convert date" + et);
            throw et;
        }
        Date dt = cl.getTime();
        return (dt);
    }

    public static Date StringToDate(String dd) throws ParseException {
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
        try {
            cl.setTime(sdf.parse(dd));
        } catch (ParseException et) {
            System.out.println("Can't convert date in StringtoDate, trying android format" + et);
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                cl.setTime(sdf.parse(dd));
            } catch (ParseException et2) {
                //Try Mon Oct 7 13:04:36 BST 2019

                System.out.println("Can't convert date in StringToDate after trying Android" + et2);
                throw et;
            }
            
        }
        Date dt = cl.getTime();
        return (dt);
    }

    public static LocalDate StringToLocalDate(String dd) throws ParseException {
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
        try {
            cl.setTime(sdf.parse(dd));
        } catch (ParseException et) {
            System.out.println("Can't convert date" + et);
            throw et;
        }
        Date dt = cl.getTime();
        LocalDate date = DateToLocalDate(dt);
        return (date);
    }

    public static LocalDateTime StringToLocalDateTime(String dd) throws ParseException {
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
        try {
            cl.setTime(sdf.parse(dd));
        } catch (ParseException et) {
            //Try Mon Oct 7 13:04:36 BST 2019

            System.out.println("Can't convert date" + et);
            throw et;
        }
        
        TimeZone tz=cl.getTimeZone();
        Date dt = cl.getTime();
       
        LocalDateTime date = DateToLocalDateTime(dt,tz);
        return (date);
    }
    
    public static LocalDateTime AndroidStringToLocalDateTime(String dd) throws ParseException {
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            cl.setTime(sdf.parse(dd));
        } catch (ParseException et) {
            //Try Mon Oct 7 13:04:36 BST 2019

            System.out.println("Can't convert date" + et);
            throw et;
        }
        
        TimeZone tz=cl.getTimeZone();
        Date dt = cl.getTime();
       
        LocalDateTime date = DateToLocalDateTime(dt,tz);
        return (date);
    }

    public static TimeZone getTimeZone(String dd)throws ParseException {
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
        try {
            cl.setTime(sdf.parse(dd));
        } catch (ParseException et) {
            //Try Mon Oct 7 13:04:36 BST 2019

            System.out.println("Can't convert date" + et);
            throw et;
        }
        
        TimeZone tz=cl.getTimeZone();
        return tz;
    }
    
    public static TimeZone getAndroidTimeZone(String dd)throws ParseException {
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss");
        try {
            cl.setTime(sdf.parse(dd));
        } catch (ParseException et) {
            //Try Mon Oct 7 13:04:36 BST 2019

            System.out.println("Can't convert date" + et);
            throw et;
        }
        
        TimeZone tz=cl.getTimeZone();
        return tz;
    }
    
    public static Instant LocalDateToInstant(LocalDate dt) {
        Instant i = dt.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return i;
    }

    public static LocalDate DateToLocalDate(Date dt) {
        LocalDate date = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return (date);
    }

    public static LocalDateTime DateToLocalDateTime(Date dt,TimeZone tz) {
        LocalDateTime date = dt.toInstant().atZone(tz.toZoneId()).toLocalDateTime();
        return (date);
    }

    public static java.util.UUID UUIDFromString(String sUUID) throws IllegalArgumentException {

        return java.util.UUID.fromString(sUUID);
    }

    public static double minmax(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public static double convertTemperatureData(double value) {
        return 0.0473711045 * value + -11.19891627;
    }

    public static double convertBatteryData(double value) {
        return minmax(0.2865304553 * value + -177.3583506, 0, 100);
    }

    public static double convertMoistureData(double value) {
        return minmax(0.179814297 * value + -40.76741498, 0, 100);
    }

    public static double convertLightData(double value) {
        return (10981.31391 * Math.exp(1 / value) + -10981.3812);
    }

}
