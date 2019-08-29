/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.stores;

import com.datastax.oss.driver.api.core.*;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 *
 * @author andycobley
 */
public class D3Store {

    private String DeviceName = null;
    private Map<String, String> meta = null;
    private List<LocalDate> dates = null;
    Map<String, UdtValue> sensorMap = null;
    UserDefinedType SensorReadingType = null;
    Map<String, String> SensorReading;
    Map<String, Map<String, String>> Sensor;

    Map<LocalDate, Map<String, UdtValue>> readings = null;
    private int Aggregation = 1;

    private String Error = "";

    public D3Store() {

    }

    public int AverageMinutesBetweenReadings() {
        int Average = 1;
        int SumMinutes = 0;
        int NumMinutes = 0;
        int LastTime=0;
        Iterator<LocalDate> it = dates.iterator();
        Calendar cl = Calendar.getInstance();
        while (it.hasNext()) {
            LocalDate d=it.next();
           
            Date date = Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
            cl.setTime(date);
            int diff=0;
            int minute = cl.get(Calendar.MINUTE);
            if (minute>LastTime){
                diff = minute-LastTime;
            }else{
                diff=60-minute+LastTime;
            }
            
            SumMinutes = SumMinutes + diff;
            NumMinutes++;
            LastTime=minute;
        }
        Average = (int) SumMinutes / NumMinutes;
        if (Average < 1) {
            Average = 1;
        }
        return Average;

    }

    public void setError(String Error) {
        this.Error = this.Error + " : " + Error;
    }

    public String getError() {
        return Error;
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

    //I think this was removed on purpose (gt replaced get)
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
    //in a form for D3 to use, miss out Strings
    //Map of Sensor:[{Date:D,Value:V}]
    public Map<String, List<SensorValue>> getD3Readings() {
        int numMinutes = Aggregation;
        String SensorName = null;
        int Average = AverageMinutesBetweenReadings();
        Map<String, List<SensorValue>> d3Readings = new HashMap<String, List<SensorValue>>();
        try {

            for (Map.Entry<String, UdtValue> sensornameentry : sensorMap.entrySet()) {
                SensorName = sensornameentry.getKey();
                List<SensorValue> Values = new LinkedList<SensorValue>();
                int currentMin = -1; //This is the current minute, not minimum
                float fTotal = (float) 0.0;
                int num = 0;
                int minCount = 0;
                for (Map.Entry<LocalDate, Map<String, UdtValue>> entry : readings.entrySet()) {
                    if (Aggregation > Average) {
                        LocalDate InsertionDate = entry.getKey();
                        Date date = Date.from(InsertionDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        Calendar cl = Calendar.getInstance();
                        cl.setTime(date);
                        int minute = cl.get(Calendar.MINUTE);
                        Map<String, UdtValue> sensorMap = entry.getValue();
                        UdtValue sensor = sensorMap.get(SensorName);
                        float fValue = sensor.getFloat("fValue");

                        if (fValue == 0) {
                            int iValue = sensor.getInt("iValue");
                            fValue = (float) iValue;
                        }
                        if (currentMin == -1) {
                            fTotal = fTotal + fValue;
                            num++;
                            currentMin = minute;
                        } else if (currentMin == minute) {
                            fTotal = fTotal + fValue;
                            num++;
                        } else {
                            minCount++;
                            if (minCount >= numMinutes) {
                                fValue = fTotal / num;
                                num = 0;
                                minCount = 0;
                                fTotal = (float) 0.0;
                                String sValue = Float.toString(fValue);
                                SensorValue sv = new SensorValue();
                                sv.create(InsertionDate, sValue);
                                Values.add(sv);
                            }
                        }
                    } else { //Don't do aggregation
                        LocalDate InsertionDate = entry.getKey();
                       
                        Map<String, UdtValue> sensorMap = entry.getValue();
                        UdtValue sensor = sensorMap.get(SensorName);
                        float fValue = sensor.getFloat("fValue");

                        if (fValue == 0) {
                            int iValue = sensor.getInt("iValue");
                            fValue = (float) iValue;
                        }
                        String sValue = Float.toString(fValue);
                        SensorValue sv = new SensorValue();
                        sv.create(InsertionDate, sValue);
                        Values.add(sv);
                    }
                }
                d3Readings.put(SensorName, Values);
            }
        } catch (Exception et) {
            setError(et.toString());
            System.out.println("Error getteing d3 readings " + et + " :" + SensorName);
            et.printStackTrace();
        }
        return d3Readings;
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

    public void setAggregation(int Aggregation) {
        this.Aggregation = Aggregation;
    }

    public int getAggregation() {
        return Aggregation;
    }
}
