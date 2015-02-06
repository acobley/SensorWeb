/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.stores;

import java.util.UUID;



/**
 *
 * @author andycobley
 */
public class DeviceStore {
    private UUID DeviceName=null;
    public void Device() {

    }
    
    public void setName(UUID Name){
        DeviceName=Name;
    }
    
    public UUID getName(){
        return DeviceName;
    }

}
