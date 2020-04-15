/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.lib;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

import java.util.TimeZone;
import static uk.ac.dundee.computing.aec.sensorweb.lib.Utils.Uint16;
import static uk.ac.dundee.computing.aec.sensorweb.lib.Utils.Uint32;
import uk.ac.dundee.computing.aec.sensorweb.stores.B64Data;

/**
 *
 * @author andyc
 */
public class B64Util {
    
    public static String RemovePadding(String B64){
        
        char lastChar = B64.charAt(B64.length() - 1);
        while (lastChar =='='){
            B64 = B64.substring(0, B64.length() - 1);
            lastChar = B64.charAt(B64.length() - 1);
        }
        return B64;
    }
    
    public static byte[] Convert64(String B64,String Name, LocalDateTime mobiletime)  {
        JsonObject Record = null;
        try {
            Record = Json.parse(B64).asObject();
        } catch (Exception et) {
            Utils.WriteLog(Name, mobiletime,"Can't parse string into JSON "+B64);
            return null;
        }

        String B64History = Record.get("b64History").asString();
        if (B64History == null){
            Utils.WriteLog(Name, mobiletime," B64History is null in Convert64");
            return null;
        }
        B64History=RemovePadding(B64History);
        byte[] nb = null;
        byte[] Decoded = null;
        try {
            nb = B64History.getBytes("utf-8");
            Decoded = Base64.getMimeDecoder().decode(nb);
            Utils.WriteLog(Name, mobiletime," B64History decoded as Base64.getMimeDecoder ");
        } catch (Exception et) {
            Utils.WriteLog(Name, mobiletime,"Can't do Base64 as getMimeDecoder UTF-8 ");
           
            try{
                 Base64.Decoder dc= Base64.getDecoder();
                
                 Decoded= dc.decode(B64History.trim());
                 Utils.WriteLog(Name, mobiletime,"B64History Decoded as dc.decode(B64History.trim()");
            }catch (Exception et2){
                 Utils.WriteLog(Name, mobiletime,"Can't do Base64 as dc.decode(B64History.trim()) ");
                return null;
            }
            Utils.WriteLog(Name, mobiletime,"Not sure We should be here");
            return null;
        }
        if (Decoded==null){
           Utils.WriteLog(Name, mobiletime,"Decoded is null");
        }
        return Decoded;
    }

    public static LocalDate B64StartDate(String B64,String Name, LocalDateTime mobiletime) {
        JsonObject Record = null;
        try {
            Record = Json.parse(B64).asObject();
        } catch (Exception et) {
            Utils.WriteLog(Name, mobiletime,"Can't parse string into JSON");
            System.exit(-1);
        }
        JsonValue jB64 = Record.get("startupTime");
        String B64Date = Record.get("startupTime").asString();
        B64Date=B64Date.replace("+", " ");  //If this + has been inseeted in error
        LocalDate dd = null;
        try {
            dd = Convertors.StringToLocalDate(B64Date);
        } catch (Exception et) {
            Utils.WriteLog(Name, mobiletime,"can't convert Start date String to date "+B64Date);
            return null;
        }
        return dd;
    }
    
    private static class TimeWithTZ{
        public LocalDateTime dd = null;
        public TimeZone tz =null;
        
        TimeWithTZ( LocalDateTime dd,
                    TimeZone tz){
            this.dd=dd;
            this.tz=tz;
                   
        }
    }

    public static TimeWithTZ B64StartDateTime(String B64,String Name, LocalDateTime mobiletime) {
        JsonObject Record = null;
        try {
            Record = Json.parse(B64).asObject();
        } catch (Exception et) {
            Utils.WriteLog(Name, mobiletime,"Can't parse string into JSON");
            System.exit(-1);
        }
       
        String B64Date = Record.get("startupTime").asString();
        B64Date=B64Date.replace("+", " ");  //If this + has been inseeted in error
        LocalDateTime dd = null;
        TimeZone tz =null;
        
        try {
             
            dd = Convertors.StringToLocalDateTime(B64Date);
            tz=  Convertors.getTimeZone(B64Date);
            TimeWithTZ ttz=new TimeWithTZ(dd,tz);
            return ttz;
        } catch (Exception et) {
            Utils.WriteLog(Name, mobiletime,"can't convert Start date String to date");
            try{
                dd = Convertors.AndroidStringToLocalDateTime(B64Date);
                tz=  Convertors.getAndroidTimeZone(B64Date);
            TimeWithTZ ttz=new TimeWithTZ(dd,tz);
            return ttz;
            }catch(Exception et2){
                Utils.WriteLog(Name, mobiletime,"can't convert javascript Start date String to date");
                return null;
            }
            
           
        }
        
    }

    public static void DumpDecoded(byte[] Decoded,String Name, LocalDateTime mobiletime){
        Utils.WriteLog(Name, mobiletime,"Decoded");
        int Length=Decoded.length;
        Utils.WriteLog(Name, mobiletime,"Length "+Length);
        int Rows=(int)(Length/8);
        int Remainder = Length % 8;
        Utils.WriteLog(Name, mobiletime,"Rows "+Rows+" "+Remainder);
        String Row="";
        for (int i=0;i<Rows;i++){
           Row="";
           for (int j=0; j<8;j++){
              Row=Row+Byte.toUnsignedLong(Decoded[i*8+j])+"\t";
           }
           Utils.WriteLog(Name, mobiletime,Row);
        }
    }
    
    
    /*
    These entries where from 
    https://github.com/BuBuaBu/flower-power-history
    Investigate sugggets that the first four bytes are 1 0 30 0
    
        int nbEntries = Utils.Uint16(Decoded[2], Decoded[3]);
        int nbEntries = Utils.Uint16(Decoded[4], Decoded[5]);
        long LastInextime = Utils.Uint32(Decoded[4], Decoded[5], Decoded[6], Decoded[7]);
        long FirstIndex = Utils.Uint16(Decoded[8], Decoded[9]);
        long LastIndex = Utils.Uint16(Decoded[10], Decoded[11]);
        long SessionId = Utils.Uint16(Decoded[12], Decoded[13]);
        long Period = Utils.Uint16(Decoded[14], Decoded[15]);
    
    */
    public static byte FindOffset( byte[] Decoded ,String Name, LocalDateTime mobiletime) {

       
        byte offset=4;
        Utils.WriteLog(Name, mobiletime,"offset " +Decoded[offset+12]+"   "+Decoded[offset+13]);
        if ((Byte.toUnsignedLong(Decoded[offset+12])==3) &&(Byte.toUnsignedLong(Decoded[offset+13])==132)){
            
            return 4;
        }
        return 2;
    }
    public static B64Data HeaderB64(String B64,String Name, LocalDateTime mobiletime) {

        byte[] Decoded = Convert64(B64,Name,mobiletime);
        byte offset=FindOffset(Decoded,Name,mobiletime);
        
        Utils.WriteLog(Name, mobiletime,"Offset "+offset);
       
        B64Data data = new B64Data();
        //int nbEntries = Utils.Uint16(Decoded[2], Decoded[3]);
        int nbEntries = Utils.Uint16(Decoded[offset+0], Decoded[offset+1]);
        long LastInextime = Utils.Uint32(Decoded[offset+2], Decoded[offset+3], Decoded[offset+4], Decoded[offset+5]);
        long FirstIndex = Utils.Uint16(Decoded[offset+6], Decoded[offset+7]);
        long LastIndex = Utils.Uint16(Decoded[offset+8], Decoded[offset+9]);
        long SessionId = Utils.Uint16(Decoded[offset+10], Decoded[offset+11]);
        long Period = Utils.Uint16(Decoded[offset+12], Decoded[offset+13]);
        data.SetNbEntries(nbEntries);
        data.LastIndex(LastIndex);
        data.FirstIndex(FirstIndex);
        data.LastIndextime(LastInextime);
        data.SessionId(SessionId);
        data.Period(Period);
        Utils.WriteLog(Name, mobiletime,"NbEntries " + nbEntries+"   "+Decoded[4]+" "+ Decoded[5]);
        Utils.WriteLog(Name, mobiletime,"LastInextime " + LastInextime);
        Utils.WriteLog(Name, mobiletime,"FirstIndex " + FirstIndex);
        Utils.WriteLog(Name, mobiletime,"LastIndex " +LastIndex);
        Utils.WriteLog(Name, mobiletime,"SessionId " +SessionId);
        Utils.WriteLog(Name, mobiletime,"Period " +Period);
        return (data);
    }

    public static B64Data PayloadB64(String B64, B64Data data,String Name, LocalDateTime mobiletime) {
        // int HEADER_SIZE = 0x10; // From https://github.com/BuBuaBu/flower-power-history
        
        byte[] Decoded = Convert64(B64,Name,mobiletime);
        int HEADER_SIZE = 0x10+FindOffset(Decoded,Name,mobiletime)-2;
        DumpDecoded(Decoded,Name,mobiletime);
        int bSize =Decoded.length-HEADER_SIZE;
        int rows=(int)bSize/12;
        Utils.WriteLog(Name, mobiletime,"Rows "+rows);
        TimeWithTZ ttz=B64StartDateTime(B64,Name,mobiletime);
        LocalDateTime StartDate = ttz.dd;
        TimeZone tz=ttz.tz;
        LocalDateTime ReadingDate = null;
        try {
            Utils.WriteLog(Name, mobiletime,"Start Date "+StartDate);
            Utils.WriteLog(Name, mobiletime,"LastIndexTime "+data.getLastIndexTime());
            Utils.WriteLog(Name, mobiletime,"LastIndex "+data.getLastIndex());
            Utils.WriteLog(Name, mobiletime,"Period " +data.Period);
            ReadingDate = StartDate.plusSeconds(data.getLastIndexTime());
            
        } catch (Exception et) {
            Utils.WriteLog(Name, mobiletime,et.toString());
            return null;
        }
        int nbEntries = data.nbEntries;
        Utils.WriteLog(Name, mobiletime,"Start Date:" +StartDate);
        Utils.WriteLog(Name, mobiletime,"Readign Date:" +ReadingDate);
        for (int i = 0; i < nbEntries; i++) {
            LocalDateTime dt = ReadingDate.minusSeconds(((nbEntries-1)-i) * 900);
            Utils.WriteLog(Name, mobiletime,"Calculated Date "+dt+"   "+i);   
            if (dt.isBefore(StartDate)) {
                Utils.WriteLog(Name, mobiletime,"Before Start Date "+i);
                
            }else{
            
            int offset = HEADER_SIZE + (i * 12);
            if (offset+12 > Decoded.length){ //This is a guard function
                Utils.WriteLog(Name, mobiletime,"Run out of file "+(offset+12)+"  "+Decoded.length);
                break;
            }
            int airTemp = Uint16(Decoded[offset], Decoded[offset + 1]);
            int light = Uint16(Decoded[offset + 0x2], Decoded[offset + 0x2 + 1]);
            int soilEC = Uint16(Decoded[offset + 0x4], Decoded[offset + 0x4 + 1]);
            int soilTemp = Uint16(Decoded[offset + 0x6], Decoded[offset + 0x6 + 1]);
            int soilVWC = Uint16(Decoded[offset + 0x8], Decoded[offset + 0x8 + 1]);
            int batteryLevel = Uint16(Decoded[offset + 0xa], Decoded[offset + 0xa + 1]);

            double dAirTemp = Convertors.convertTemperatureData((double) airTemp);
            double dSoilTemp = Convertors.convertTemperatureData((double) soilTemp);
            double dBatteryLevel = Convertors.convertBatteryData((double) batteryLevel);
            double dsoilEC = Convertors.convertMoistureData((double) soilEC);
            double dsoilVWC = Convertors.convertMoistureData((double) soilVWC);
            double dlight = Convertors.convertLightData((double) light);
            data.addSensorData(airTemp, light, soilEC, soilTemp, soilVWC, batteryLevel,
                    dAirTemp, dSoilTemp,
                    dBatteryLevel,
                    dsoilEC,
                    dsoilVWC,
                    dlight, dt,tz);
            }

        }

        return data;
    }
}
