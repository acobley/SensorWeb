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
import static uk.ac.dundee.computing.aec.sensorweb.lib.Utils.Uint16;
import static uk.ac.dundee.computing.aec.sensorweb.lib.Utils.Uint32;
import uk.ac.dundee.computing.aec.sensorweb.stores.B64Data;

/**
 *
 * @author andyc
 */
public class B64Util {
    public static byte[] Convert64(String B64) {
        JsonObject Record = null;
        try {
            Record = Json.parse(B64).asObject();
        } catch (Exception et) {
            System.out.println("Can't parse string into JSON");
            System.exit(-1);
        }
        

        String B64History = Record.get("b64History").asString();
        
        byte[] nb = null;
        byte[] Decoded = null;
        try {
            nb = B64History.getBytes("utf-8");
            Decoded = Base64.getMimeDecoder().decode(nb);

        } catch (Exception et) {
            System.out.println("Can't do Base64");
            return null;
        }
        return Decoded;
    }

    public static LocalDate B64StartDate(String B64) {
        JsonObject Record = null;
        try {
            Record = Json.parse(B64).asObject();
        } catch (Exception et) {
            System.out.println("Can't parse string into JSON");
            System.exit(-1);
        }
        JsonValue jB64 = Record.get("startupTime");
        String B64Date = Record.get("startupTime").asString();
        LocalDate dd = null;
        try {
            dd = Convertors.StringToLocalDate(B64Date);
        } catch (Exception et) {
            System.out.println("can't convert Start date String to date");
            return null;
        }
        return dd;
    }

    public static LocalDateTime B64StartDateTime(String B64) {
        JsonObject Record = null;
        try {
            Record = Json.parse(B64).asObject();
        } catch (Exception et) {
            System.out.println("Can't parse string into JSON");
            System.exit(-1);
        }
        JsonValue jB64 = Record.get("startupTime");
        String B64Date = Record.get("startupTime").asString();
        LocalDateTime dd = null;
        try {
            dd = Convertors.StringToLocalDateTime(B64Date);
        } catch (Exception et) {
            System.out.println("can't convert Start date String to date");
            return null;
        }
        return dd;
    }

    public static B64Data HeaderB64(String B64) {

        byte[] Decoded = Convert64(B64);
        B64Data data = new B64Data();
        int nbEntries = Utils.Uint16(Decoded[2], Decoded[3]);
        long LastInextime = Utils.Uint32(Decoded[4], Decoded[5], Decoded[6], Decoded[7]);
        long FirstIndex = Utils.Uint16(Decoded[8], Decoded[9]);
        long LastIndex = Utils.Uint16(Decoded[10], Decoded[11]);
        long SessionId = Utils.Uint16(Decoded[12], Decoded[13]);
        long Period = Utils.Uint16(Decoded[14], Decoded[15]);
        data.SetNbEntries(nbEntries);
        data.LastIndex(LastIndex);
        data.FirstIndex(FirstIndex);
        data.LastIndextime(LastInextime);
        data.SessionId(SessionId);
        data.Period(Period);
        System.out.println("NbEntries " + nbEntries);
        return (data);
    }

    public static B64Data PayloadB64(String B64, B64Data data) {
        int HEADER_SIZE = 0x10;
        byte[] Decoded = Convert64(B64);
        
        LocalDateTime StartDate = B64StartDateTime(B64);
        LocalDateTime ReadingDate = null;
        try {
            ReadingDate = StartDate.plusSeconds(data.getLastIndexTime());
        } catch (Exception et) {
            System.out.println(et);
            return null;
        }
        int nbEntries = data.nbEntries;

        for (int i = 0; i < nbEntries; i++) {
            LocalDateTime dt = ReadingDate.minusSeconds(i * 900);
            if (dt.isBefore(StartDate)) {
                break;
            }
            int offset = HEADER_SIZE + (i * 12);
            int airTemp = Uint16(Decoded[offset], Decoded[offset + 1]);
            int light = Uint16(Decoded[offset + 0x2], Decoded[offset + 0x2 + 1]);
            int soilEC = Uint16(Decoded[offset + 0x4], Decoded[offset + 0x4 + 1]);
            int soilTemp = Uint16(Decoded[offset + 0x6], Decoded[offset + 0x6 + 1]);
            int soilVWC = Uint16(Decoded[offset + 0x8], Decoded[offset + 0x8 + 1]);
            int batteryLevel = Uint16(Decoded[offset + 0xa], Decoded[offset + 0xa + 1]);

            double fAirTemp = Convertors.convertTemperatureData((double) airTemp);
            double fSoilTemp = Convertors.convertTemperatureData((double) soilTemp);
            data.addSensorData(airTemp, light, soilEC, soilTemp, soilVWC, batteryLevel,
                    fAirTemp, fSoilTemp,dt);

        }

        return data;
    }
}
