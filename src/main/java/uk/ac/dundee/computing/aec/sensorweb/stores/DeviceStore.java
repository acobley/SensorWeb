/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.stores;

import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 *
 * @author andycobley
 */
public class DeviceStore {
    private UUID DeviceName=null;
    private Map<String,String> meta =null;
    private List<Date> dates = null;
    Map<String, UDTValue> sensorMap = null;
    UserType SensorReadingType = null;
    public void Device() {

    }
    
   
    public void setName(UUID Name){
        DeviceName=Name;
    }
    
    public UUID getName(){
        return DeviceName;
    }
    
    public void setMeta(Map meta){
        this.meta=meta;
    }
    
    public void setSensors(Map Sensors){
        sensorMap=Sensors; 
        
    }
    
    
    public void setReadingType(UserType SensorReadingType){
        this.SensorReadingType=SensorReadingType;
    }
    
    public UserType getreadingType(){
        return SensorReadingType;
    }
    public Map getMeta(){
        return meta;
    }
    
    public Map getSensors(){
        return sensorMap;
    }
    
    

    public void addDate(Date dd){
        if (dates == null){
            dates = new LinkedList<Date>();
        }
        dates.add(dd);
    }
    
    public List<Date> getDates(){
        return dates;
    }

}
