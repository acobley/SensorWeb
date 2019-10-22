package uk.ac.dundee.computing.aec.sensorweb.lib;

import com.datastax.oss.driver.api.core.*;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import java.net.InetSocketAddress;

import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import uk.ac.dundee.computing.aec.sensorweb.stores.DeviceStore;

/**
 * ********************************************************
 *
 *
 * @author administrator
 *
 * Hosts are 192.168.2.10 Seed for Vagrant hosts
 *
 *
 *
 *
 */
public final class CassandraHosts {

    //static String Host = "node1";  //at least one starting point to talk to
    static String Host = "172.17.0.2";  //Docker container
    //static String Host = "127.0.0.1"; // Local host
    public CassandraHosts() {

    }

    public static String getHost() {
        return (Host);
    }

    

    public static CqlSession getCluster() {
       
 
        System.out.println("getCluster");
        CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(Host,9042))
                .withLocalDatacenter("datacenter1")
                .build();
       
     
        
        SimpleStatement statement= SimpleStatement.newInstance("select cluster_name from system.local;");
        ResultSet rs = null;
        
        try {
        rs = session.execute(statement);
        }catch(Exception et){
            System.out.println("can't execute statement select distinct name from sensorsync.sensors"+et);
        }
        if (rs.getAvailableWithoutFetching()==0) {
            System.out.println("cluster_name");
            return null;
        } else {
            for (Row row : rs) {
                
                String peer=row.getString("cluster_name");
                System.out.println(peer);
               
            }
        }
        return session;

    }

  

}
