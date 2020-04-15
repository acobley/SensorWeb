/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.stores;

import java.time.LocalDateTime;

/**
 *
 * @author andyc
 */
public class FileNameStore {
    private String name="";
    private LocalDateTime dd;
    
    public FileNameStore(String Name, LocalDateTime dd){
        this.name=Name;
        this.dd =dd;
        
    }
    
    public String getName(){
        return this.name;
    }
    
    public LocalDateTime getDd(){
        return this.dd;
    }
    
}
