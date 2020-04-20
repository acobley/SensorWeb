/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.models;


import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import uk.ac.dundee.computing.aec.sensorweb.lib.Convertors;


/**
 *
 * @author andyc
 */
public class ReadingsModel {

    private DataSource _ds = null;

    public int StoreReading(String Name, String B64History, String Meta, DataSource _ds,HttpServletRequest request) {
        this._ds = _ds;
        PreparedStatement pmst = null;
        Connection Conn;
        try {
            Conn = _ds.getConnection();
        } catch (Exception et) {
            return -1;
        }
        String MetaString = request.getParameter("Meta");
        JsonObject jMeta=null;
        try{
         jMeta= Json.parse(Meta).asObject();
        }catch (Exception et) {
                  jMeta = new JsonObject();
            }
        JsonValue jNb = jMeta.get("Nbentries");
        JsonValue jLastEntryIndex = jMeta.get("LastentryIndex");
        JsonValue jTransferStartIndex = jMeta.get("TransferStartIndex");
        JsonValue jCurrentSessionID = jMeta.get("CurrentSessionID");
        JsonValue jCurrentSessionStartIndex = jMeta.get("CurrentSessionStartIndex");
        JsonValue jCurrentSessionPeriod = jMeta.get("CurrentSessionPeriod");
        JsonValue jFlowerPowercurenttime = jMeta.get("FlowerPowercurrenttime");
        JsonValue jMobileTime = jMeta.get("MobileTime");
        String sqlQuery = "INSERT INTO `Readings` "
                + "(`name`,`LastEntryIndex`,`Nbentries`,`TransferStartIndex`,`CurrentSessionId`,"
                + "`CurrentSessionStartIndex`,`CurrentSessionPeriod`,`FlowerPowercurrenttime`,`MobileTime`,"
                + " `B64History`,`MetaString`) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?);";
        try {
            pmst = Conn.prepareStatement(sqlQuery);
            try{
            pmst.setString(1, Name);
            }catch (Exception et) {
                 pmst.setString(1,"NoName");
            }
            try {
            pmst.setInt(2, jLastEntryIndex.asInt());
            }catch (Exception et) {
                 pmst.setInt(2, -1);
            }
            try{
            pmst.setInt(3, jNb.asInt());
            }catch (Exception et) {
                  pmst.setInt(3, -1);
            }
            try{
            pmst.setInt(4, jTransferStartIndex.asInt());
            }catch (Exception et) {
                  pmst.setInt(4, -1);
            }
            try {
            pmst.setInt(5, jCurrentSessionID.asInt());
            }catch (Exception et) {
                  pmst.setInt(5, -1);
            }
            try{
            pmst.setInt(6, jCurrentSessionStartIndex.asInt());
            }catch (Exception et) {
                  pmst.setInt(6, -1);
            }
            try{
            pmst.setInt(7, jCurrentSessionPeriod.asInt());
            }catch (Exception et) {
                  pmst.setInt(7, -1);
            }
            try{
            pmst.setInt(8, jFlowerPowercurenttime.asInt());
            }catch (Exception et) {
                  pmst.setInt(8, -1);
            }
            Date dDate = null;

            try {
                dDate = Convertors.StringToDate(jMobileTime.asString());

            } catch (Exception et) {
                System.out.println("mySQl save Can't parse Date " + et);
            }
            try {
            pmst.setDate(9, new java.sql.Date(dDate.getTime()));
            }catch (Exception et) {
                  pmst.setDate(9, null);
            }
            int len = B64History.length();
            pmst.setString(10, B64History);
            pmst.setString(11,MetaString);
            
            pmst.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Can not insert data into Readings " + ex);
            return -1;
        }
        return 0;
    }

    public int StoreLastEntryIndex(String Name, long LastEntryIndex, DataSource _ds) {
        System.out.println("Saving last index "+LastEntryIndex);
        this._ds = _ds;
        PreparedStatement pmst = null;
        Connection Conn;
        try {
            Conn = _ds.getConnection();
        } catch (Exception et) {
            return -1;
        }

        String sqlQuery = "INSERT INTO `LastEntryIndex` "
                + "(`name`,`LastEntryIndex`) "
                + "VALUES (?,?);";
        try {
            pmst = Conn.prepareStatement(sqlQuery);
            pmst.setString(1, Name);
            pmst.setLong(2, LastEntryIndex);

            pmst.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Can not insert data into LastEntryIndex " + ex);
            return -1;
        }
        return 0;
    }

    public long getLastIndex(String Name, DataSource _ds) {

        this._ds = _ds;
        PreparedStatement pmst = null;
        Connection Conn;
        long LastIndex=0;
        try {
            Conn = _ds.getConnection();
        } catch (Exception et) {
            return -1;
        }
        String sqlQuery = "select * from LastEntryIndex where name=? ;";
        try {
            pmst = Conn.prepareStatement(sqlQuery);
            pmst.setString(1, Name);

            ResultSet rs = pmst.executeQuery();
            while (rs.next()) {
            
            LastIndex = rs.getLong("LastEntryIndex");
            }
        } catch (Exception ex) {
            System.out.println("Can not get data from LastEntryIndex " + ex);
            return 0;
        }
        return LastIndex;
    }
}
