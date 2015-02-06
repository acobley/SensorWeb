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
import java.util.LinkedList;
import java.util.List;
import uk.ac.dundee.computing.aec.sensorweb.stores.DeviceStore;

/**
 *
 * @author andycobley
 */
public class DeviceModel {
    Cluster cluster=null;
    Session session=null;
    public void Device() {

    }

    public void setSession(Session session) {
        
        this.session=session;
    }
    
    public List<DeviceStore> getDevices(){
        List<DeviceStore> devices= new LinkedList<DeviceStore>();
        String DeviceQuery="select distinct name from sensorsync.sensors";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind());
        if (rs.isExhausted()) {
            System.out.println("No Devices");
            return null;
        } else {
        for (Row row : rs) {
                DeviceStore dd= new DeviceStore();
                dd.setName(row.getUUID("name"));
                System.out.println("Device "+dd.getName());
                devices.add(dd);
            }
        }
        return devices;
    }
    
    public DeviceStore getDevice(String DeviceName){
        DeviceStore dd=null;
        String DeviceQuery="select * from sensorsync.sensors where name=?";
        PreparedStatement ps = session.prepare(DeviceQuery);
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind(java.util.UUID.fromString(DeviceName)));
        if (rs.isExhausted()) {
            System.out.println("No Devices");
            return null;
        } else {
            dd= new DeviceStore();
        for (Row row : rs) {
             dd.setName(row.getUUID("name"));
             dd.setMeta(row.getMap("metadata", String.class, String.class));
             dd.addDate(row.getDate("insertion_time"));
        }
        }
        return dd;
    }
}
