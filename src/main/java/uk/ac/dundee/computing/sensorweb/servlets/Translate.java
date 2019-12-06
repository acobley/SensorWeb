/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.sensorweb.servlets;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
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
import java.util.TimeZone;
import javax.servlet.RequestDispatcher;
import uk.ac.dundee.computing.aec.sensorweb.lib.B64Util;
import uk.ac.dundee.computing.aec.sensorweb.lib.Convertors;
import uk.ac.dundee.computing.aec.sensorweb.lib.Dbutils;
import uk.ac.dundee.computing.aec.sensorweb.lib.Utils;
import uk.ac.dundee.computing.aec.sensorweb.lib.Web;
import uk.ac.dundee.computing.aec.sensorweb.models.ReadingsModel;
import uk.ac.dundee.computing.aec.sensorweb.models.WriteToKafka;
import uk.ac.dundee.computing.aec.sensorweb.stores.B64Data;
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

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        Dbutils db = new Dbutils();

        _ds = db.assemble(config);

        kf = new WriteToKafka();

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
    private void  AddRecord(String name, double value, JSONArray jsonSensors){
            JSONObject Record = null;
            Record = new JSONObject();
            Record.put("name", name);
            Record.put("fValue", value);
            jsonSensors.put(Record);
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
        String ip = "127.0.0.1";
        long Millis = 1;
        String b64History = request.getParameter("b64History");
        String Name = request.getParameter("name");
        String lat = request.getParameter("latitude");
        String Longitude = request.getParameter("longitude");
        String UserId = request.getParameter("UserId");
        String Meta = request.getParameter("Meta");
        
        
        JsonObject jMeta = Json.parse(Meta).asObject();
        JsonValue jSerial = jMeta.get("Serial");
        if (jSerial == null){
            jSerial = jMeta.get("SerialNumber");
        }
        String Serial=jSerial.asString();
       
        System.out.println(Name);
        System.out.println(lat);
        System.out.println(Longitude);
        System.out.println(UserId);
        System.out.println(Meta);
        ReadingsModel rd = new ReadingsModel();
        rd.StoreReading(Name, b64History, Meta, _ds);
        B64Data b64 = B64Util.HeaderB64(b64History);

        b64 = B64Util.PayloadB64(b64History, b64);
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
            AddRecord("Air temperature", Airtemperature,jsonSensors);
            AddRecord("Soil EC",SoilEC,jsonSensors);
            AddRecord("Soil temperature",Soiltemperature,jsonSensors);
            AddRecord("Soil VWC",SoilVWC,jsonSensors);
            AddRecord("Battery level",Batterylevel,jsonSensors);
            AddRecord("Light Level",dLight,jsonSensors);
            AddRecord("Raw Air temperature",sd.airTemp,jsonSensors);
            AddRecord("Raw Soil EC",sd.soilEC,jsonSensors);
            AddRecord("Raw Soil temperature",sd.soilTemp,jsonSensors);
            AddRecord("Raw Soil VWC",sd.soilVWC,jsonSensors);
            AddRecord("Raw Battery level",sd.batteryLevel,jsonSensors);
            AddRecord("Raw Light Level",sd.light,jsonSensors);
            
            JSONObject jsonDevice = new JSONObject();
            jsonDevice.put("Name", Name);
            
            DateTimeFormatter sdf = DateTimeFormatter
                    .ofPattern("EEE MMM d HH:mm:ss zzz yyyy");

            try {

                String sDate = sdf.withZone(tz.toZoneId()).format(dd);
                LocalDateTime formatDateTime = LocalDateTime.parse(sDate, sdf);
                jsonDevice.put("insertion_time", sDate);
            } catch (Exception et) {
                System.out.println("Can't format date for sending to Sensor " + et);

            }
            jsonDevice.put("TimeZone", tz.getDisplayName());
            JSONObject jsonMeta = new JSONObject();
            jsonMeta.put("Latitude", lat);
            jsonMeta.put("Longitude", Longitude);
            jsonMeta.put("UserId", UserId);
            jsonMeta.put("Serial",Serial);
            JSONObject json = new JSONObject();
            json.put("sensors", jsonSensors);
            json.put("SensorData", jsonDevice);
            json.put("meta", jsonMeta);
            Socket sc = null;
            boolean sent = false;
            while (sent == false) {
                try {
                    kf.sendMessage(json.toString());
                } catch (Exception et) {
                    System.out.println("Can't send to kafka");
                }
                sent=true;
            }
            try {
                //System.out.println("Sleeping");
                Thread.sleep(Millis);
                Thread.yield();
            } catch (Exception et) {
                System.out.println("Sleep went wrong");
            }

        }

        rd.StoreLastEntryIndex(Name, b64.getLastIndex(), _ds);
        request.setAttribute("Data", sensorData);
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
