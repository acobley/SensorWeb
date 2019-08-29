/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.stores;

import com.datastax.oss.driver.api.core.*;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import java.util.Date;
import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

/**
 *
 * @author andycobley
 */
public class DeviceStore {

    private String DeviceName = null;
    private Map<String, String> meta = null;
    private List<LocalDate> dates = null;
    Map<String, UdtValue> sensorMap = null;
    UserDefinedType SensorReadingType = null;
    Map<String, String> SensorReading;
    Map<String, Map<String, String>> Sensor;

    Map<LocalDate, Map<String, UdtValue>> readings = null;
    private int Aggregation=1;

   public DeviceStore(){
       
   }

    public void setName(String Name) {
        DeviceName = Name;
    }

    public String getName() {
        return DeviceName;
    }

    public void setMeta(Map meta) {
        this.meta = meta;
    }

    //Stores the latest values only

    public void setSensors(Map Sensors) {
        sensorMap = Sensors;

    }

    public Map getMeta() {
        return meta;
    }

    public Map gtSensors() {
        return sensorMap;
    }

    public List<Map<String, Map<String, String>>> getSensorList() {
        // List of <Sensor Name, <Type, Value>>
        List<Map<String, Map<String, String>>> lst = new LinkedList<Map<String, Map<String, String>>>();
        for (Map.Entry<String, UdtValue> entry : sensorMap.entrySet()) {
            String SensorName = entry.getKey();
            UdtValue sensor = entry.getValue();
            Map<String, String> SensorReading = new HashMap<String, String>();
            float fValue = sensor.getFloat("fValue");
            String sfValue = Float.toString(fValue);

            int iValue = sensor.getInt("iValue");
            String siValue = Integer.toString(iValue);
            String sValue = sensor.getString("sValue");
            if (fValue != 0) {
                SensorReading.put("fValue", sfValue);
            }
            if (iValue != 0) {
                SensorReading.put("iValue", siValue);
            }
            if (sValue != null) {
                SensorReading.put("sValue", sValue);
            }
            Map<String, Map<String, String>> Sensor = new HashMap<String, Map<String, String>>();
            Sensor.put(SensorName, SensorReading);
            lst.add(Sensor);

        }

        return lst;
    }

    //get readings map of <Date, <Sensor Name, <Type, Value>>>
    public Map<LocalDate, List<Map<String, Map<String, String>>>> getReadings() {
        Map<LocalDate, List<Map<String, Map<String, String>>>> reading = new HashMap<LocalDate, List<Map<String, Map<String, String>>>>();
        for (Map.Entry<LocalDate, Map<String, UdtValue>> entry : readings.entrySet()) {
            LocalDate InsertionDate = entry.getKey();
            List<Map<String, Map<String, String>>> lst = new LinkedList<Map<String, Map<String, String>>>();
            Map<String, UdtValue> sensorMap = entry.getValue();
            for (Map.Entry<String, UdtValue> sensorentry : sensorMap.entrySet()) {
                String SensorName = sensorentry.getKey();
                UdtValue sensor = sensorentry.getValue();
                Map<String, String> SensorReading = new HashMap<String, String>();
                float fValue = sensor.getFloat("fValue");
                String sfValue = Float.toString(fValue);

                int iValue = sensor.getInt("iValue");
                String siValue = Integer.toString(iValue);
                String sValue = sensor.getString("sValue");
                if (fValue != 0) {
                    SensorReading.put("fValue", sfValue);
                }
                if (iValue != 0) {
                    SensorReading.put("iValue", siValue);
                }
                if (sValue != null) {
                    SensorReading.put("sValue", sValue);
                }
                Map<String, Map<String, String>> Sensor = new HashMap<String, Map<String, String>>();
                Sensor.put(SensorName, SensorReading);
                lst.add(Sensor);

            }
            reading.put(InsertionDate, lst);
        }
        return reading;
    }

    
    
    


    public void addReading(LocalDate insertDate, Map<String, UdtValue> Sensors) {
        if (readings == null) {
            readings = new HashMap<LocalDate, Map<String, UdtValue>>();
        }

        readings.put(insertDate, Sensors);
    }

    public void addDate(LocalDate dd) {
        if (dates == null) {
            dates = new LinkedList<LocalDate>();
        }
        dates.add(dd);
    }

    public List<LocalDate> getDates() {
        return dates;
    }
    
    public void setAggregation(int Aggregation){
        this.Aggregation=Aggregation;
    }
    
    public int getAggregation(){
        return Aggregation;
    }

}
