/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.sensorweb.servlets;

import com.datastax.oss.driver.api.core.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
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
@WebServlet(name = "Devices", urlPatterns = {"/Devices", "/Devices/*"})

public class Devices extends HttpServlet {

 
    CqlSession session;
    private HashMap CommandsMap = new HashMap();

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        session = CassandraHosts.getCluster();
        CommandsMap.put("JSON", 1);
    }

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean RenderJSON = false;
        String args[] = Convertors.SplitRequestPath(request);
        for (int i = 0; i < args.length; i++) {
            //System.out.println(i + " : " + args[i]);
            int Command = -1;
            if (CommandsMap.containsKey(args[i])) {
                if ((Integer) CommandsMap.get(args[i]) == 1) {
                    RenderJSON = true;
                }
            }
        }

        DeviceModel dd = new DeviceModel();
        dd.setSession(session);
        List<DeviceStore> devices = dd.getDevices();

        if (RenderJSON == true) {
            request.setAttribute("Data", devices);
            RequestDispatcher rdjson = request.getRequestDispatcher("/RenderJson");
            rdjson.forward(request, response);
        } else {
            request.setAttribute("Devices", devices);
            request.setAttribute("Path", request.getRequestURI());
            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");

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
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
