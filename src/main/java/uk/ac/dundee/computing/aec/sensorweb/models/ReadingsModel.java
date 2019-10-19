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
import javax.sql.DataSource;
import uk.ac.dundee.computing.aec.sensorweb.lib.Convertors;

/**
 *
 * @author andyc
 */
public class ReadingsModel {

    private DataSource _ds = null;

    public int StoreReading(String Name, String B64History, String Meta, DataSource _ds) {
        this._ds = _ds;
        PreparedStatement pmst = null;
        Connection Conn;
        try {
            Conn = _ds.getConnection();
        } catch (Exception et) {
            return -1;
        }
        JsonObject jMeta = Json.parse(Meta).asObject();
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
                + " `B64History`) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?);";
        try {
            pmst = Conn.prepareStatement(sqlQuery);
            pmst.setString(1, Name);
            pmst.setInt(2, jLastEntryIndex.asInt());
            pmst.setInt(3, jNb.asInt());
            pmst.setInt(4, jTransferStartIndex.asInt());
            pmst.setInt(5, jCurrentSessionID.asInt());
            pmst.setInt(6, jCurrentSessionStartIndex.asInt());
            pmst.setInt(7, jCurrentSessionPeriod.asInt());
            pmst.setInt(8, jFlowerPowercurenttime.asInt());
            Date dDate = null;

            try {
                dDate = Convertors.StringToDate(jMobileTime.asString());

            } catch (Exception et) {
                System.out.println("Can't parse Python Date " + et);
            }
            pmst.setDate(9, new java.sql.Date(dDate.getTime()));
            int len=B64History.length();
            pmst.setString(10, B64History);
            pmst.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Can not insert data into Readings " + ex);
            return -1;
        }
        return 0;
    }
}
