/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.sensorweb.servlets;

import com.datastax.oss.driver.api.core.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.aec.sensorweb.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.sensorweb.lib.Convertors;
import uk.ac.dundee.computing.aec.sensorweb.models.DeviceModel;
import uk.ac.dundee.computing.aec.sensorweb.stores.DeviceStore;

/**
 *
 * @author andycobley
 */
@WebServlet(name = "Device", urlPatterns = {"/Device", "/Device/*"})
public class Device extends HttpServlet {


    private CqlSession session = null;
    private HashMap CommandsMap = new HashMap();
    private HashMap putMap = new HashMap();
    public void init(ServletConfig config) throws ServletException {
       
        session = CassandraHosts.getCluster();
        CommandsMap.put("JSON", 1);
        putMap.put("Data",1);
        putMap.put("json",2);
        putMap.put("JSON",3);
    }

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
        String args[] = Convertors.SplitRequestPath(request);
        //check for JSON request  which must be the last in the path
        boolean RenderJSON = false;

        if (CommandsMap.containsKey(args[args.length - 1])) {
            if ((Integer) CommandsMap.get(args[args.length - 1]) == 1) {
                //Remove the JSON
                args = Arrays.copyOf(args, args.length - 1);
                RenderJSON = true;
            }
        }

        
        String Device = args[2];
        if (Device != null) {
            DeviceModel dm = new DeviceModel();
            dm.setSession(session);
            DeviceStore dd = null;
            int la = args.length;
            //This really needs rewritten !
            if (la == 4) {
                try{
                    dd = dm.getDevice(Device, args[3]);
                    }catch(Exception et3){
                           System.out.println("Date parse errror"+et3);
                       }

            } else {

                dd = dm.getDevice(Device);
            }

            if (RenderJSON == true) {
                request.setAttribute("Data", dd);
                RequestDispatcher rdjson = request.getRequestDispatcher("/RenderJson");
                rdjson.forward(request, response);
            } else {

                RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
                request.setAttribute("Device", dd);
                request.setAttribute("Path", request.getRequestURI());
                rd.forward(request, response);
            }
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
request.setAttribute("Path", request.getRequestURI());
            rd.forward(request, response);
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
        System.out.println("Device doPost");
        String Key = null;
        String sJSON=null;
        Map<String, String[]> map = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            System.out.println("Key = " + entry.getKey()
                    + ", Value = " + entry.getValue());
            Key = entry.getKey();
            byte[] data = Key.getBytes("ASCII");
            sJSON = new String(data);
            System.out.println(sJSON);
            String value[]=entry.getValue();
            if (putMap.containsKey(Key)) {
                Integer i=(Integer) putMap.get(Key);
                switch(i){
                    case 1 : sJSON=value[0];  //text
                             break;
                    case 2 : data = value[0].getBytes("ASCII");
                             sJSON=new String(data);
                             
                             break;
                    case 3 :data = value[0].getBytes("ASCII");
                             sJSON=new String(data);
                             //sJSON=value[0];
                            
                             break;
                    default:
                             break;
        } }
            
            
             sJSON=sJSON.replace("\r", "");
             sJSON=sJSON.replace("\n", "");
            Socket sc = null;
            boolean sent = false;
            String ip = "172.17.0.4";
            //String ip = "127.0.0.1";  //Change for deployment
            while (sent == false) {
                try {
                    sc = new Socket(ip, 19877);
                    //sc = new Socket(ip, 80);
                    OutputStream os = sc.getOutputStream();
                    PrintWriter out = new PrintWriter(os);
                    out.print(sJSON);
                    out.print("\r\n");
                    out.close();
                    sc.close();
                    sent = true;
                    
                } catch (Exception et) {
                    System.out.println("No Host " + " : " + ip);
                    
                }
            }
 
        }
        response.sendRedirect("/SensorWeb/Devices");
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
