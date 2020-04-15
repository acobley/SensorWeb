/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.sensorweb.servlets;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.TimeZone;
import javax.servlet.RequestDispatcher;
import org.json.JSONException;
import uk.ac.dundee.computing.aec.sensorweb.lib.B64Util;
import uk.ac.dundee.computing.aec.sensorweb.lib.Convertors;
import uk.ac.dundee.computing.aec.sensorweb.lib.Dbutils;
import uk.ac.dundee.computing.aec.sensorweb.lib.Utils;
import uk.ac.dundee.computing.aec.sensorweb.lib.Web;
import uk.ac.dundee.computing.aec.sensorweb.models.ReadingsModel;
import uk.ac.dundee.computing.aec.sensorweb.models.WriteToKafka;
import uk.ac.dundee.computing.aec.sensorweb.stores.B64Data;
import uk.ac.dundee.computing.aec.sensorweb.stores.FileNameStore;
import uk.ac.dundee.computing.aec.sensorweb.stores.Sensordata;

/**
 *
 * @author andyc
 */
@WebServlet(name = "Translate", urlPatterns = {"/Translate"},
        initParams = {
            @WebInitParam(name = "data-source", value = "jdbc/Sensordb")
        }
)
public class Translate extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private DataSource _ds = null;
    private WriteToKafka kf = null;
    private String WaracleIOS="";
    private String WaracleAndroid="";
    
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        WaracleIOS=System.getenv("WaracleIOS");
        WaracleAndroid=System.getenv("WaracleAndroid");
        System.out.println("IOS Env "+WaracleIOS);
        System.out.println("Android Env "+WaracleAndroid);
        Dbutils db = new Dbutils();

        _ds = db.assemble(config);

        kf = new WriteToKafka();
        
        Utils.mkLogDir();

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Translate</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Translate at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private void AddRecord(String name, double value, JSONArray jsonSensors, String Name, LocalDateTime mobiletime) throws JSONException {
        JSONObject Record = null;
        Record = new JSONObject();
        try {
            Record.put("name", name);
            Record.put("fValue", value);
            jsonSensors.put(Record);
        } catch (JSONException et) {
            Utils.WriteLog(Name, mobiletime, "Warning: Translate can't add record" + name + "  " + value + "   " + et);
            Record.put("fValue", Double.MAX_VALUE);
            jsonSensors.put(Record);

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void SendUnauthorized(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Translate</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Your application is not allowed to send data</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //String ip = "172.17.0.5";
        System.out.println("Recieiving Data from " + request.getRemoteAddr());
        /*
        //This will dump the servers raw input
        InputStream requestBodyInput = request.getInputStream();
        InputStreamReader isReader = new InputStreamReader(requestBodyInput);
      //Creating a BufferedReader object
        BufferedReader reader = new BufferedReader(isReader);
        StringBuffer rawBody = new StringBuffer();
        String str;
        while((str = reader.readLine())!= null){
         rawBody.append(str);
        }
        System.out.println(rawBody);
        */
        String uub64History = request.getParameter("b64History");
        String b64History=uub64History.replace(" ","+");
        boolean Authorized = false; 
        String Mobile = "";
        String Auth = request.getHeader("authorization");
        if (Auth != null) {
            if (Auth.compareTo(WaracleAndroid) == 0) {

                Authorized = true;
                Mobile = "Waracle Android";
            }
            if (Auth.compareTo(WaracleIOS) == 0) {

                Authorized = true;
                Mobile = "Waracle IOS";
            }
        }
        String referrer = request.getHeader("referer");
        System.out.println("Referrer " + referrer);

        if (referrer != null) {
            if (referrer.compareTo("http://35.196.52.5/SensorWeb/TestTranslate.html") == 0) {
                Authorized = true;
                Mobile = "Test Page";
            }
        }
            if (Authorized != true) {
                System.out.println("Unauthorised posting" + Auth);
                SendUnauthorized(request, response);
                return;
            }
        
        long Millis = 1;
        LocalDateTime mobiletime = null;
        
        
        /*
        String ssMobile="Wed Apr 8 16:47:15 BST 2020";
        try {
            mobiletime = Convertors.StringToLocalDateTime(ssMobile);
        } catch (Exception et) {
            Utils.WriteLog("TestPost", mobiletime, "Can't convert mobile time tryning android format" + ssMobile + " " + et);
            try {
                mobiletime = Convertors.AndroidStringToLocalDateTime(ssMobile);
            } catch (Exception et2) {
                Utils.WriteLog("TestPost", mobiletime, "Can't convert mobile time " + ssMobile + " " + et);

            }
        }
        Utils.WriteLog("TestPost", mobiletime, b64History);
        */ 
        //String nb64History = new String(request.getParameter("b64History").getBytes("iso-8859-1"),"UTF-8");
        String Name = request.getParameter("name");
        String lat = request.getParameter("latitude");
        String Longitude = request.getParameter("longitude");
        String UserId = request.getParameter("UserId");
        String Meta = request.getParameter("Meta");
        System.out.println("Recieving " + Name);
        ReadingsModel rd = new ReadingsModel();
        rd.StoreReading(Name, b64History, Meta, _ds, request);
        JsonObject jMeta = null;
        try {
            jMeta = Json.parse(Meta).asObject();
        } catch (Exception et) {
            System.out.println("Can't parse Meta data ");
            System.out.println("String is \r\n" + Meta);
            System.out.println("Error is :\r\n" + et);

        }
        JsonValue jSerial = jMeta.get("Serial");
        if (jSerial == null) {
            jSerial = jMeta.get("SerialNumber");
        }
        String Serial = jSerial.asString();

        String sMobile = jMeta.get("mobileTime").asString();
        //LocalDateTime mobiletime = null;
        try {
            mobiletime = Convertors.StringToLocalDateTime(sMobile);
        } catch (Exception et) {
            Utils.WriteLog(Name, mobiletime, "Can't convert mobile time tryning android format" + sMobile + " " + et);
            try {
                mobiletime = Convertors.AndroidStringToLocalDateTime(sMobile);
            } catch (Exception et2) {
                Utils.WriteLog(Name, mobiletime, "Can't convert mobile time " + sMobile + " " + et);

            }
        }
       
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            Utils.WriteLog(Name, mobiletime,"Key "+key+" Value "+value);
        }
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        Utils.WriteLog(Name, mobiletime, Mobile);
        Utils.WriteLog(Name, mobiletime, "Mobile time " + dtf.format(mobiletime));
        Utils.WriteLog(Name, mobiletime, "Mobile time " + dtf.format(mobiletime));
        Utils.WriteLog(Name, mobiletime, dtf.format(now));
        Utils.WriteLog(Name, mobiletime, Name);
        Utils.WriteLog(Name, mobiletime, lat);
        Utils.WriteLog(Name, mobiletime, Longitude);
        Utils.WriteLog(Name, mobiletime, UserId);

        Utils.WriteLog(Name, mobiletime, Meta);
        Utils.WriteLog(Name, mobiletime, b64History);
      

        B64Data b64 = B64Util.HeaderB64(b64History, Name, mobiletime);

        b64 = B64Util.PayloadB64(b64History, b64, Name, mobiletime);
        ArrayList<Sensordata> sensorData = b64.getSensorData();

        Iterator<Sensordata> iter
                = sensorData.iterator();

        while (iter.hasNext()) {
            Sensordata sd = iter.next();

            LocalDateTime dd = sd.ReadingTime;
            TimeZone tz = sd.tz;
            double Airtemperature = sd.fAirTemp;
            double SoilEC = sd.dsoilEC;
            double Soiltemperature = sd.fSoilTemp;
            double SoilVWC = sd.dsoilVWC;
            double Batterylevel = sd.dBatteryLevel;
            double dLight = sd.dlight;

            JSONArray jsonSensors = new JSONArray();
            AddRecord("Air temperature", Airtemperature, jsonSensors, Name, mobiletime);
            AddRecord("Soil EC", SoilEC, jsonSensors, Name, mobiletime);
            AddRecord("Soil temperature", Soiltemperature, jsonSensors, Name, mobiletime);
            AddRecord("Soil VWC", SoilVWC, jsonSensors, Name, mobiletime);
            AddRecord("Battery level", Batterylevel, jsonSensors, Name, mobiletime);
            AddRecord("Light Level", dLight, jsonSensors, Name, mobiletime);
            AddRecord("Raw Air temperature", sd.airTemp, jsonSensors, Name, mobiletime);
            AddRecord("Raw Soil EC", sd.soilEC, jsonSensors, Name, mobiletime);
            AddRecord("Raw Soil temperature", sd.soilTemp, jsonSensors, Name, mobiletime);
            AddRecord("Raw Soil VWC", sd.soilVWC, jsonSensors, Name, mobiletime);
            AddRecord("Raw Battery level", sd.batteryLevel, jsonSensors, Name, mobiletime);
            AddRecord("Raw Light Level", sd.light, jsonSensors, Name, mobiletime);

            JSONObject jsonDevice = new JSONObject();
            jsonDevice.put("Name", Name);

            DateTimeFormatter sdf = DateTimeFormatter
                    .ofPattern("EEE MMM d HH:mm:ss zzz yyyy");

            try {

                String sDate = sdf.withZone(tz.toZoneId()).format(dd);
                LocalDateTime formatDateTime = LocalDateTime.parse(sDate, sdf);
                jsonDevice.put("insertion_time", sDate);
            } catch (Exception et) {
                Utils.WriteLog(Name, mobiletime, "Can't format date for sending to Sensor " + et);

            }
            jsonDevice.put("TimeZone", tz.getDisplayName());
            JSONObject jsonMeta = new JSONObject();
            jsonMeta.put("Latitude", lat);
            jsonMeta.put("Longitude", Longitude);
            jsonMeta.put("UserId", UserId);
            jsonMeta.put("Serial", Serial);
            jsonMeta.put("Agent", Mobile);
            JSONObject json = new JSONObject();
            json.put("sensors", jsonSensors);
            json.put("SensorData", jsonDevice);
            json.put("meta", jsonMeta);
            Socket sc = null;
            boolean sent = false;
            while (sent == false) {
                try {
                    kf.sendMessage(json.toString());
                    //Utils.WriteLog(Name, mobiletime, json.toString());
                } catch (Exception et) {
                    Utils.WriteLog(Name, mobiletime, "Can't send to kafka");
                }
                sent = true;
            }
            try {
                //Utils.WriteLog(Name, mobiletime,"Sleeping");
                //Thread.sleep(Millis);
                Thread.yield();
            } catch (Exception et) {
                Utils.WriteLog(Name, mobiletime, "Sleep went wrong");
            }

        }

        rd.StoreLastEntryIndex(Serial, b64.getLastIndex(), _ds);
        FileNameStore fs= new FileNameStore(Name, mobiletime);
        request.setAttribute("Data", sensorData);
        request.setAttribute("FileName",fs);
        RequestDispatcher rdjson = request.getRequestDispatcher("/RenderJson");
        rdjson.forward(request, response);
        //scanner.close();

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
