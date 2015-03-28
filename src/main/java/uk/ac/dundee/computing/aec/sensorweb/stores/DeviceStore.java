/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.stores;

import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import java.util.Calendar;
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

    private UUID DeviceName = null;
    private Map<String, String> meta = null;
    private List<Date> dates = null;
    Map<String, UDTValue> sensorMap = null;
    UserType SensorReadingType = null;
    Map<String, String> SensorReading;
    Map<String, Map<String, String>> Sensor;

    Map<Date, Map<String, UDTValue>> readings = null;

    public void Device() {

    }

    public void setName(UUID Name) {
        DeviceName = Name;
    }

    public UUID getName() {
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
        for (Map.Entry<String, UDTValue> entry : sensorMap.entrySet()) {
            String SensorName = entry.getKey();
            UDTValue sensor = entry.getValue();
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
    public Map<Date, List<Map<String, Map<String, String>>>> gtReadings() {
        Map<Date, List<Map<String, Map<String, String>>>> reading = new HashMap<Date, List<Map<String, Map<String, String>>>>();
        for (Map.Entry<Date, Map<String, UDTValue>> entry : readings.entrySet()) {
            Date InsertionDate = entry.getKey();
            List<Map<String, Map<String, String>>> lst = new LinkedList<Map<String, Map<String, String>>>();
            Map<String, UDTValue> sensorMap = entry.getValue();
            for (Map.Entry<String, UDTValue> sensorentry : sensorMap.entrySet()) {
                String SensorName = sensorentry.getKey();
                UDTValue sensor = sensorentry.getValue();
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

    //get readings map of <Date, <Sensor Name, <Type, Value>>>
    //in a form for D3 to use, miss out Strings
    //Map of Sensor:[{Date:D,Value:V}]
    public Map<String, List<SensorValue>> getD3Readings() {
        Map<String, List<SensorValue>> d3Readings = new HashMap<String, List<SensorValue>>();

        for (Map.Entry<String, UDTValue> sensornameentry : sensorMap.entrySet()) {
            String SensorName = sensornameentry.getKey();
            List<SensorValue> Values = new LinkedList<SensorValue>();
            int currentMin=-1;
            float fTotal=(float)0.0;
            int num=0;
            for (Map.Entry<Date, Map<String, UDTValue>> entry : readings.entrySet()) {
                Date InsertionDate = entry.getKey();
                Calendar cl = Calendar.getInstance();
                cl.setTime(InsertionDate);
                int minute     = cl.get(Calendar.MINUTE);
                Map<String, UDTValue> sensorMap = entry.getValue();
                UDTValue sensor = sensorMap.get(SensorName);
                float fValue = sensor.getFloat("fValue");
                
                if (fValue == 0) {
                    int iValue = sensor.getInt("iValue");
                    fValue=(float)iValue;
                }
                if (currentMin==-1){
                    fTotal=fTotal+fValue;
                    num++;
                    currentMin=minute;
                }else if (currentMin==minute){
                    fTotal=fTotal+fValue;
                    num++;
                }else{
                    fValue=fTotal/num;
                    num=0;
                    fTotal=(float)0.0;
                    String sValue = Float.toString(fValue);
                SensorValue sv = new SensorValue();
                sv.create(InsertionDate, sValue);
                Values.add(sv);
                }
                
                

            }
            d3Readings.put(SensorName, Values);
        }

        return d3Readings;
    }

    public void addReading(Date insertDate, Map<String, UDTValue> Sensors) {
        if (readings == null) {
            readings = new HashMap<Date, Map<String, UDTValue>>();
        }

        readings.put(insertDate, Sensors);
    }

    public void addDate(Date dd) {
        if (dates == null) {
            dates = new LinkedList<Date>();
        }
        dates.add(dd);
    }

    public List<Date> getDates() {
        return dates;
    }

}
