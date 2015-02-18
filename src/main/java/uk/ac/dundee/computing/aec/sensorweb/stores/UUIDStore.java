/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.stores;

import java.util.UUID;

/**
 *
 * @author Administrator
 */
public class UUIDStore {
    private UUID uuid=null;
    public UUIDStore(){
        
    }
    public void setUUID(UUID uuid){
        this.uuid=uuid;
    }
    
    public UUID getUUID(){
        return uuid;
    }
}
