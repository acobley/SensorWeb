/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.lib;

import com.eclipsesource.json.Json;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.Base64;

import uk.ac.dundee.computing.aec.sensorweb.stores.B64Data;

/**
 *
 * @author andy
 */
public final class Utils {

    public Utils() {
    }

    public static Date getLastWeek(int Days) {
        GregorianCalendar dayBeforeThisWeek = new GregorianCalendar();
        //int dayFromMonday = (dayBeforeThisWeek.get(Calendar.DAY_OF_WEEK) + 7 - Calendar.MONDAY) % 7;
        dayBeforeThisWeek.add(Calendar.DAY_OF_YEAR, -1 * Days);
        return dayBeforeThisWeek.getTime();
    }

    public static int Uint16(byte high, byte low) {
        int Result = (int) (Byte.toUnsignedLong(high) * 0x100 + Byte.toUnsignedLong(low));
        return Result;
    }

    public static long Uint32(byte byte4, byte byte3, byte byte2, byte byte1) {
        System.out.println(byte4 + "  " + byte3 + "  " + byte2 + "  " + byte1 + "  ");
        Long Result = (Byte.toUnsignedLong(byte4) * 0x1000000 + Byte.toUnsignedLong(byte3) * 0x10000 + Byte.toUnsignedLong(byte2) * 0x100 + Byte.toUnsignedLong(byte1));
        return Result;
    }

    public static void mkLogDir() {
        File file = new File("/var/log/GROW");
        //Creating the directory
        boolean bool = file.mkdir();
    }
    static boolean logwritable = true;

    public static void WriteLog(String name, LocalDateTime mDate, String log) {
        String fileName = "/var/log/GROW/" + name +"-"+ mDate+".log";
        fileName=fileName.replaceAll("\\s","");
        if (logwritable == true) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
                writer.write(log+"\r\n");

                writer.close();

            } catch (Exception et) {
                System.out.println("Can't write log file "+et);
                logwritable = false;
            }
        }
    }
    
    public static void WriteJSONLog(String name, LocalDateTime mDate, String log) {
        String fileName = "/var/log/GROW/" + name +"-"+ mDate+".JSON";
        fileName=fileName.replaceAll("\\s","");
        if (logwritable == true) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
                writer.write(log+"\r\n");

                writer.close();

            } catch (Exception et) {
                System.out.println("Can't write log file");
                logwritable = false;
            }
        }
    }

}
