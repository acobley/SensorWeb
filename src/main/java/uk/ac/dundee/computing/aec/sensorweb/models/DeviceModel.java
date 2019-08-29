/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.models;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;

import uk.ac.dundee.computing.aec.sensorweb.lib.Convertors;
import uk.ac.dundee.computing.aec.sensorweb.stores.D3Store;
import uk.ac.dundee.computing.aec.sensorweb.stores.DeviceStore;

/**
 *
 * @author andycobley
 */
public class DeviceModel {

    
    CqlSession session = null;
    UserDefinedType SensorReadingType = null;

    public void Device() {
        SensorReadingType = session.getMetadata().getKeyspace("sensorsync").flatMap(sensorsync -> sensorsync.getUserDefinedType("SensorReading")).orElseThrow(() -> new IllegalArgumentException("Missing UDT definition"));;
     }

    public void setSession(CqlSession session) {

        this.session = session;
    }

    public List<DeviceStore> getDevices() {
        List<DeviceStore> devices = new LinkedList<DeviceStore>();
        String DeviceQuery = "select distinct name from sensorsync.sensors";
        
        
        SimpleStatement statement =
                   SimpleStatement.newInstance(DeviceQuery);
        System.out.println(statement.getQuery());
        
        ResultSet rs = null;
        
        try {
        rs = session.execute(statement);
        }catch(Exception et){
            System.out.println("can't execute statement select distinct name from sensorsync.sensors"+et);
        }
        if (rs.getAvailableWithoutFetching()==0) {
            System.out.println("No Devices");
            return null;
        } else {
            for (Row row : rs) {
                DeviceStore dd = new DeviceStore();
                dd.setName(row.getString("name"));
                //System.out.println("Device " + dd.getName());
                devices.add(dd);
            }
        }
        return devices;
    }

    public DeviceStore getDevice(String DeviceName) {
        DeviceStore dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? order by insertion_time asc ";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
       BoundStatement bound = ps.bind(DeviceName);
        rs = session.execute(bound);
        if (rs.getAvailableWithoutFetching()==0) {
            System.out.println("No Devices");
            return null;
        } else {
            dd = new DeviceStore();
            for (Row row : rs) {
                dd.setName(row.getString("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                //LocalDate cdate=row.getDate("insertion_time");

                 //LocalDate ldate=row.getLocalDate("insertion_time");
                 Instant i=row.getInstant("insertion_time");
                 LocalDate ldate=i.atZone(ZoneId.systemDefault()).toLocalDate();
                 //LocalDate ldate= LocalDate.from( row.getInstant("insertion_time"));
                //dd.addDate(new Date(cdate.getMillisSinceEpoch()));
                dd.addDate(ldate);
            }
        }
        return dd;
    }

    public DeviceStore getDevice(String DeviceName, String InsertionTime) throws ParseException {
        DeviceStore dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? and insertion_time=?";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        Date dt = Convertors.StringToDate(InsertionTime);
        BoundStatement bound = ps.bind(DeviceName, dt);
        rs = session.execute(bound);
        if (rs.isFullyFetched()) {
            System.out.println("No Devices");
            return null;
        } else {

            dd = new DeviceStore();
            //dd.setReadingType(SensorReadingType);
            for (Row row : rs) {
                dd.setName(row.getString("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                //LocalDate cdate=row.getDate("insertion_time");
                LocalDate ldate=row.getLocalDate("insertion_time");
                //dd.addDate(new Date(cdate.getMillisSinceEpoch()));
                dd.addDate(ldate);
                //http://www.datastax.com/documentation/developer/java-driver/2.1/java-driver/reference/udtApi.html
                dd.setSensors(row.getMap("reading", String.class, UdtValue.class));
            }
        }
        return dd;
    }

    //get readings for a date > than Insertion Time
    public DeviceStore getDeviceRange(String DeviceName, LocalDate InsertionTime) {
        DeviceStore dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? and insertion_time>=? order by insertion_time desc";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
         BoundStatement bound = ps.bind(java.util.UUID.fromString(DeviceName), Convertors.LocalDateToInstant(InsertionTime));
         //bound.setFetchSize(1000);
         
        rs = session.execute(bound);
        

        if (rs.isFullyFetched()) {
            System.out.println("No Devices");
            return null;
        } else {

            dd = new DeviceStore();
            //dd.setReadingType(SensorReadingType);
            for (Row row : rs) {
                dd.setName(row.getString("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                 //LocalDate cdate=row.getDate("insertion_time");
                 Instant i=row.getInstant("insertion_time");
                 LocalDate ldate=i.atZone(ZoneId.systemDefault()).toLocalDate();
                //LocalDate ldate=row.getLocalDate("insertion_time");
                dd.addDate(ldate);
                //dd.addDate(new Date(cdate.getMillisSinceEpoch()));
                //dd.addDate(cdate);
                //http://www.datastax.com/documentation/developer/java-driver/2.1/java-driver/reference/udtApi.html
                dd.setSensors(row.getMap("reading", String.class, UdtValue.class));
                dd.addReading(ldate, row.getMap("reading", String.class, UdtValue.class));

            }
        }
        return dd;
    }

    public DeviceStore getDeviceRange(String DeviceName, LocalDate StartDate, LocalDate EndDate) {
        DeviceStore dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? and insertion_time>? and insertion_time<? order by insertion_time desc";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        
        //boundStatement.setFetchSize(1000);
       BoundStatement bound = ps.bind(DeviceName, Convertors.LocalDateToInstant(StartDate), Convertors.LocalDateToInstant(EndDate));
        rs = session.execute(bound);
 
        if (rs.isFullyFetched()) {
            System.out.println("No Devices");
            return null;
        } else {

            dd = new DeviceStore();
            rs.getAvailableWithoutFetching();
            //dd.setReadingType(SensorReadingType);
            for (Row row : rs) {
                dd.setName(row.getString("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                //LocalDate cdate=row.getDate("insertion_time");
                Instant i=row.getInstant("insertion_time");
                 LocalDate ldate=i.atZone(ZoneId.systemDefault()).toLocalDate();
                //LocalDate ldate=row.getLocalDate("insertion_time");
                //dd.addDate(new Date(cdate.getMillisSinceEpoch()));
                dd.addDate(ldate);
                //http://www.datastax.com/documentation/developer/java-driver/2.1/java-driver/reference/udtApi.html
                dd.setSensors(row.getMap("reading", String.class, UdtValue.class)); //Name of sensor and reading
                dd.addReading(ldate, row.getMap("reading", String.class, UdtValue.class));
            }
        }
        return dd;
    }

    //get readings for a date > than Insertion Time
    public D3Store getD3Range(String DeviceName, LocalDate InsertionTime) {
        D3Store dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? and insertion_time>=? order by insertion_time desc";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
          BoundStatement bound = ps.bind(DeviceName, Convertors.LocalDateToInstant(InsertionTime));
        rs = session.execute(bound);
 

        if (rs.isFullyFetched()) {
            System.out.println("No Devices");
            return null;
        } else {

            dd = new D3Store();
            //dd.setReadingType(SensorReadingType);
            for (Row row : rs) {
                dd.setName(row.getString("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                //LocalDate cdate=row.getDate("insertion_time");
                
                //LocalDate ldate=row.getLocalDate("insertion_time");
                Instant i=row.getInstant("insertion_time");
                 LocalDate ldate=i.atZone(ZoneId.systemDefault()).toLocalDate();
                //dd.addDate(new Date(cdate.getMillisSinceEpoch()));
                dd.addDate(ldate);
                //http://www.datastax.com/documentation/developer/java-driver/2.1/java-driver/reference/udtApi.html
                dd.setSensors(row.getMap("reading", String.class, UdtValue.class));
                dd.addReading(ldate, row.getMap("reading", String.class, UdtValue.class));

            }
        }
        return dd;
    }

    public D3Store getD3Range(String DeviceName, LocalDate StartDate, LocalDate EndDate) {
        D3Store dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? and insertion_time>? and insertion_time<? order by insertion_time desc";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        BoundStatement bound = ps.bind(DeviceName, Convertors.LocalDateToInstant(StartDate), Convertors.LocalDateToInstant(EndDate));
        System.out.println(bound.toString());
        rs = session.execute(bound);
        if (rs.getAvailableWithoutFetching()==0) {
            System.out.println("No Devices");
            return null;
        } else {

            dd = new D3Store();
           
            //dd.setReadingType(SensorReadingType);
            for (Row row : rs) {
                dd.setName(row.getString("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                //http://www.datastax.com/documentation/developer/java-driver/2.1/java-driver/reference/udtApi.html
                dd.setSensors(row.getMap("reading", String.class, UdtValue.class)); //Name of sensor and reading
               //LocalDate cdate=row.getDate("insertion_time");
               Instant i=row.getInstant("insertion_time");
                 LocalDate ldate=i.atZone(ZoneId.systemDefault()).toLocalDate();
                //LocalDate ldate=row.getLocalDate("insertion_time");
                //dd.addDate(new Date(cdate.getMillisSinceEpoch()));
                dd.addDate(ldate);
          
                dd.addReading(ldate, row.getMap("reading", String.class, UdtValue.class));
            }
        }
        return dd;
    }
}
