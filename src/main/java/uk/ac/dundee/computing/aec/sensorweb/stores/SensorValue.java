/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.stores;

import java.util.Date;

/**
 *
 * @author Administrator
 */
public class SensorValue {

    private Date dd;
    private String Value;

    public SensorValue() {

    }

    public void create(Date dd, String Value) {
        this.dd = dd;
        this.Value = Value;

    }

    public Date getDate() {
        return dd;
    }

    public String getValue() {
        return Value;

    }
}
