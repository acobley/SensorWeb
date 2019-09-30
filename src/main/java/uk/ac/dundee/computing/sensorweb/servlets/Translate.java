/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.sensorweb.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.dundee.computing.aec.sensorweb.lib.Convertors;
import uk.ac.dundee.computing.aec.sensorweb.lib.Web;

/**
 *
 * @author andyc
 */
@WebServlet(name = "Translate", urlPatterns = {"/Translate"})
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
        String ip = "172.17.0.4";
        //String ip = "127.0.0.1";
        long Millis = 1;
        String b64History = request.getParameter("b64History");
        String Name = request.getParameter("name");
        String lat = request.getParameter("latitude");
        String Longitude = request.getParameter("longitude");
        String UserId = request.getParameter("UserId");
        String Meta = request.getParameter("Meta");
        System.out.println(Name);
        System.out.println(lat);
        System.out.println(Longitude);
        System.out.println(UserId);
        System.out.println(Meta);
        //String  startupTime = request.getParameter("startupTime");
        //String Data="{\"b64History\":\""+b64Hist+"\",\"startupTime\":\""+startupTime+"\"}";
        //System.out.print("Recived b64 History ");
        //System.out.println(b64History);

        String Response = Web.GetJson("https://us-central1-devops-2018-218513.cloudfunctions.net/DataDecode", b64History);
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println(Response);
        }
        Scanner scanner = new Scanner(Response);
        if (scanner.hasNextLine()){
            String line = scanner.nextLine();
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // process the line
            String[] items = line.split(",");
            String dDate = items[0];
            String Airtemperature = items[1];
            String SoilEC = items[2];
            String Soiltemperature = items[3];
            String SoilVWC = items[4];
            String Batterylevel = items[5];
            
            try{
                Date dd=Convertors.JavaScriptStringToDate(dDate);
                dDate=dd.toString();
            }catch(Exception et){
                    System.out.println("Can't parse Python Date " + et);
                    }
            JSONArray jsonSensors = new JSONArray();
            JSONObject Record = null;
            Record = new JSONObject();
            Record.put("name", "Air temperature");
            Record.put("fValue", Airtemperature);
            jsonSensors.put(Record);
            Record = new JSONObject();
            Record.put("name", "Soil EC");
            Record.put("fValue", SoilEC);
            jsonSensors.put(Record);
            Record = new JSONObject();
            Record.put("name", "Soil temperature");
            Record.put("fValue", Soiltemperature);
            jsonSensors.put(Record);
            Record = new JSONObject();
            Record.put("name", "Soil VWC");
            Record.put("fValue", SoilVWC);
            jsonSensors.put(Record);
            Record = new JSONObject();
            Record.put("name", "Battery level");
            Record.put("fValue", Batterylevel);
            jsonSensors.put(Record);
            JSONObject jsonDevice = new JSONObject();
            jsonDevice.put("device", Name);
            jsonDevice.put("insertion_time", dDate);
            JSONObject jsonMeta = new JSONObject();
            jsonMeta.put("Latitude", lat);
            jsonMeta.put("Longitude", Longitude);
            jsonMeta.put("UserId", UserId);
            JSONObject json = new JSONObject();
            json.put("sensors", jsonSensors);
            json.put("SensorData", jsonDevice);
            json.put("meta", jsonMeta);
            Socket sc = null;
            boolean sent = false;
            while (sent == false) {
                try {
                    sc = new Socket(ip, 19877);
                    //sc = new Socket(ip, 80);
                    OutputStream os = sc.getOutputStream();
                    PrintWriter out = new PrintWriter(os);
                    out.print(json);
                    out.print("\r\n");
                    out.close();
                    sc.close();
                    sent = true;
                    System.out.println(json);
                } catch (Exception et) {
                    System.out.println("No Host " + Name + " : " + ip);
                    try {
                        Thread.sleep((long) 1000);
                    } catch (Exception et1) {
                        System.out.println("Cant sleep " + et);
                    }
                }
            }
            try {
                //System.out.println("Sleeping");
                Thread.sleep(Millis);
                Thread.yield();
            } catch (Exception et) {
                System.out.println("Sleep went wrong");
            }

        }
        scanner.close();

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
