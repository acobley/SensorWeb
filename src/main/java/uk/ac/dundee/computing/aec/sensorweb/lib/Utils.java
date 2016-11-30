/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.lib;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author andy
 */
public final class Utils {
    public Utils(){
    }
    
    public static Date getLastWeek(int Days) {
    GregorianCalendar dayBeforeThisWeek = new GregorianCalendar();
    //int dayFromMonday = (dayBeforeThisWeek.get(Calendar.DAY_OF_WEEK) + 7 - Calendar.MONDAY) % 7;
    dayBeforeThisWeek.add(Calendar.DAY_OF_YEAR, -1*Days);
    return dayBeforeThisWeek.getTime();
  }
}
