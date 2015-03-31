/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import uk.ac.dundee.computing.aec.sensorweb.lib.Convertors;
import uk.ac.dundee.computing.aec.sensorweb.stores.D3Store;
import uk.ac.dundee.computing.aec.sensorweb.stores.DeviceStore;

/**
 *
 * @author andycobley
 */
public class DeviceModel {

    Cluster cluster = null;
    Session session = null;
    UserType SensorReadingType = null;

    public void Device() {
        SensorReadingType = cluster.getMetadata().getKeyspace("sensorsync").getUserType("SensorReading");
    }

    public void setSession(Session session) {

        this.session = session;
    }

    public List<DeviceStore> getDevices() {
        List<DeviceStore> devices = new LinkedList<DeviceStore>();
        String DeviceQuery = "select distinct name from sensorsync.sensors";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind());
        if (rs.isExhausted()) {
            System.out.println("No Devices");
            return null;
        } else {
            for (Row row : rs) {
                DeviceStore dd = new DeviceStore();
                dd.setName(row.getUUID("name"));
                //System.out.println("Device " + dd.getName());
                devices.add(dd);
            }
        }
        return devices;
    }

    public DeviceStore getDevice(String DeviceName) {
        DeviceStore dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? order by insertion_time desc limit 100";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind(java.util.UUID.fromString(DeviceName)));
        if (rs.isExhausted()) {
            System.out.println("No Devices");
            return null;
        } else {
            dd = new DeviceStore();
            for (Row row : rs) {
                dd.setName(row.getUUID("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                dd.addDate(row.getDate("insertion_time"));
            }
        }
        return dd;
    }

    public DeviceStore getDevice(String DeviceName, String InsertionTime) throws ParseException {
        DeviceStore dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? and insertion_time=?";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);

        Date dt = Convertors.StringToDate(InsertionTime);
        rs = session.execute(boundStatement.bind(java.util.UUID.fromString(DeviceName), dt));
        if (rs.isExhausted()) {
            System.out.println("No Devices");
            return null;
        } else {

            dd = new DeviceStore();
            //dd.setReadingType(SensorReadingType);
            for (Row row : rs) {
                dd.setName(row.getUUID("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                dd.addDate(row.getDate("insertion_time"));
                //http://www.datastax.com/documentation/developer/java-driver/2.1/java-driver/reference/udtApi.html
                dd.setSensors(row.getMap("reading", String.class, UDTValue.class));
            }
        }
        return dd;
    }

    //get readings for a date > than Insertion Time
    public DeviceStore getDeviceRange(String DeviceName, Date InsertionTime) {
        DeviceStore dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? and insertion_time>=? order by insertion_time desc";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        boundStatement.setFetchSize(1000);

        rs = session.execute(boundStatement.bind(java.util.UUID.fromString(DeviceName), InsertionTime));
        if (rs.isExhausted()) {
            System.out.println("No Devices");
            return null;
        } else {

            dd = new DeviceStore();
            //dd.setReadingType(SensorReadingType);
            for (Row row : rs) {
                dd.setName(row.getUUID("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                dd.addDate(row.getDate("insertion_time"));
                //http://www.datastax.com/documentation/developer/java-driver/2.1/java-driver/reference/udtApi.html
                dd.setSensors(row.getMap("reading", String.class, UDTValue.class));
                dd.addReading(row.getDate("insertion_time"), row.getMap("reading", String.class, UDTValue.class));

            }
        }
        return dd;
    }

    public DeviceStore getDeviceRange(String DeviceName, Date StartDate, Date EndDate) {
        DeviceStore dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? and insertion_time>? and insertion_time<? order by insertion_time desc";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        boundStatement.setFetchSize(1000);

        rs = session.execute(boundStatement.bind(java.util.UUID.fromString(DeviceName), StartDate, EndDate));
        if (rs.isExhausted()) {
            System.out.println("No Devices");
            return null;
        } else {

            dd = new DeviceStore();
            rs.getAvailableWithoutFetching();
            //dd.setReadingType(SensorReadingType);
            for (Row row : rs) {
                dd.setName(row.getUUID("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                dd.addDate(row.getDate("insertion_time"));
                //http://www.datastax.com/documentation/developer/java-driver/2.1/java-driver/reference/udtApi.html
                dd.setSensors(row.getMap("reading", String.class, UDTValue.class)); //Name of sensor and reading
                dd.addReading(row.getDate("insertion_time"), row.getMap("reading", String.class, UDTValue.class));
            }
        }
        return dd;
    }

    //get readings for a date > than Insertion Time
    public D3Store getD3Range(String DeviceName, Date InsertionTime) {
        D3Store dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? and insertion_time>=? order by insertion_time desc";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        boundStatement.setFetchSize(1000);

        rs = session.execute(boundStatement.bind(java.util.UUID.fromString(DeviceName), InsertionTime));
        if (rs.isExhausted()) {
            System.out.println("No Devices");
            return null;
        } else {

            dd = new D3Store();
            //dd.setReadingType(SensorReadingType);
            for (Row row : rs) {
                dd.setName(row.getUUID("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                dd.addDate(row.getDate("insertion_time"));
                //http://www.datastax.com/documentation/developer/java-driver/2.1/java-driver/reference/udtApi.html
                dd.setSensors(row.getMap("reading", String.class, UDTValue.class));
                dd.addReading(row.getDate("insertion_time"), row.getMap("reading", String.class, UDTValue.class));

            }
        }
        return dd;
    }

    public D3Store getD3Range(String DeviceName, Date StartDate, Date EndDate) {
        D3Store dd = null;
        String DeviceQuery = "select * from sensorsync.sensors where name=? and insertion_time>? and insertion_time<? order by insertion_time desc";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        boundStatement.setFetchSize(1000);

        rs = session.execute(boundStatement.bind(java.util.UUID.fromString(DeviceName), StartDate, EndDate));
        if (rs.isExhausted()) {
            System.out.println("No Devices");
            return null;
        } else {

            dd = new D3Store();
            rs.getAvailableWithoutFetching();
            //dd.setReadingType(SensorReadingType);
            for (Row row : rs) {
                dd.setName(row.getUUID("name"));
                dd.setMeta(row.getMap("metadata", String.class, String.class));
                //http://www.datastax.com/documentation/developer/java-driver/2.1/java-driver/reference/udtApi.html
                dd.setSensors(row.getMap("reading", String.class, UDTValue.class)); //Name of sensor and reading
                dd.addReading(row.getDate("insertion_time"), row.getMap("reading", String.class, UDTValue.class));
            }
        }
        return dd;
    }
}
