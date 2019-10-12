/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.stores;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author andyc
 */
public class B64Data {
    public int nbEntries;
    long LastIndextime;
      long FirstIndex;
      long LastIndex;
       long SessionId;
       long Period;
       ArrayList<Sensordata> sensorData;
       
    private class Sensordata{
       int airTemp=0;
        int light=0;
        int soilEC=0;
        int soilTemp=0;
        int soilVWC=0;
        int batteryLevel=0;
        double fAirTemp=0;
        double fSoilTemp=0;
        LocalDateTime ReadingTime=null;
        Sensordata(int airTemp,
        int light,
        int soilEC,
        int soilTemp,
        int soilVWC,
        int batteryLevel,
        double fAirTemp,
        double fSoilTemp,
        LocalDateTime ReadingTime
        ){
            this.light=light;
            this.batteryLevel=batteryLevel;
            this.airTemp=airTemp;
            this.soilEC=soilEC;
            this.soilTemp=soilTemp;
            this.soilVWC=soilVWC;
            this.fAirTemp=fAirTemp;
            this.fSoilTemp=fSoilTemp; 
            this.ReadingTime=ReadingTime;
        }
    }   
    
    public B64Data(){
        sensorData = new ArrayList<Sensordata>();
    }
    
    public  void SetNbEntries(int nbEntries){        
        this.nbEntries=nbEntries;
    }
    public  void LastIndextime(long LastIndextime){        
        this.LastIndextime=LastIndextime;
    }
    public  void FirstIndex(long FirstIndex){        
        this.FirstIndex=FirstIndex;
    }
    public  void LastIndex(long LastIndex){        
        this.LastIndex=LastIndex;
    }
    public  void SessionId(long SessionId){        
        this.SessionId=SessionId;
    }
    public  void Period(long Period){        
        this.Period=Period;
    }
    
    public void addSensorData(int airTemp,
        int light,
        int soilEC,
        int soilTemp,
        int soilVWC,
        int batteryLevel,
         double fAirTemp,
        double fSoilTemp,
        LocalDateTime ReadingTime){
        sensorData.add(new Sensordata(airTemp,
        light,
        soilEC,
        soilTemp,
        soilVWC,
        batteryLevel,
        fAirTemp,
        fSoilTemp,
        ReadingTime));
        
    }
    
    public int getNbEntries(){
        return this.nbEntries;
    }
    
    public long getLastIndexTime(){
        return this.LastIndextime;
    }
    
}
