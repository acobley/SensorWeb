/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.stores;

import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 *
 * @author andyc
 */
public class Sensordata {

    public int airTemp = 0;
    public int light = 0;
    public int soilEC = 0;
    public int soilTemp = 0;
    public int soilVWC = 0;
    public int batteryLevel = 0;
    public double fAirTemp = 0;
    public double fSoilTemp = 0;
    public double dBatteryLevel = 0;
    public double dsoilEC = 0;
    public double dsoilVWC = 0;
    public double dlight = 0;
    public LocalDateTime ReadingTime = null;
    public TimeZone tz=null;

    
    public double getfAirTemp(){return fAirTemp; };
    public double getfSoilTemp(){return fSoilTemp; };
    public double getdBatteryLevel(){return dBatteryLevel ; };
    public double getdsoilEC(){return dsoilEC;};
    public double getdsoilVWC(){return dsoilVWC; };
    public double getdlight(){return dlight;};
    public LocalDateTime getReadingTime(){return ReadingTime;};
    public String getTZ(){return tz.getDisplayName();};
    
    Sensordata(int airTemp,
            int light,
            int soilEC,
            int soilTemp,
            int soilVWC,
            int batteryLevel,
            double fAirTemp,
            double fSoilTemp,
            double dBatteryLevel,
            double dsoilEC,
            double dsoilVWC,
            double dlight,
            LocalDateTime ReadingTime,
            TimeZone tz
    ) {
        this.light = light;
        this.batteryLevel = batteryLevel;
        this.airTemp = airTemp;
        this.soilEC = soilEC;
        this.soilTemp = soilTemp;
        this.soilVWC = soilVWC;
        this.fAirTemp = fAirTemp;
        this.fSoilTemp = fSoilTemp;
        this.dBatteryLevel = dBatteryLevel;
        this.dsoilEC = dsoilEC;
        this.dsoilVWC = dsoilVWC;
        this.dlight = dlight;
        this.ReadingTime = ReadingTime;
        this.tz=tz;
    }
}
