package uk.ac.dundee.computing.aec.sensorweb.lib;

import com.datastax.driver.core.*;

import java.util.Iterator;
import java.util.Set;

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

    private static Cluster cluster;
    //static String Host = "0.0.0.0";  //at least one starting point to talk to
    static String Host = "node1";  //at least one starting point to talk to

    public CassandraHosts() {

    }

    public static String getHost() {
        return (Host);
    }

    public static String[] getHosts(Cluster cluster) {

        if (cluster == null) {
            System.out.println("Creating cluster connection");
            cluster = Cluster.builder().addContactPoint(Host).build();
        }
        System.out.println("Cluster Name " + cluster.getClusterName());
        Metadata mdata = cluster.getMetadata();
        Set<Host> hosts = mdata.getAllHosts();
        String sHosts[] = new String[hosts.size()];

        Iterator<Host> it = hosts.iterator();
        int i = 0;
        while (it.hasNext()) {
            Host ch = it.next();
            sHosts[i] = (String) ch.getAddress().toString();

            System.out.println("Hosts" + ch.getAddress().toString());
            i++;
        }

        return sHosts;
    }

    public static Cluster getCluster() {
        System.out.println("getCluster");
        //cluster = Cluster.builder().addContactPoint(Host).withPort(8081).build();
        cluster = Cluster.builder().addContactPoint(Host).build();
        getHosts(cluster);
        //Keyspaces.SetUpKeySpaces(cluster); Not needed, data is alreay in place

        return cluster;

    }

    public void close() {
        cluster.close();
    }

}
