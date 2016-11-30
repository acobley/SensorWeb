/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.sensorweb.servlets;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.aec.sensorweb.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.sensorweb.lib.Convertors;
import uk.ac.dundee.computing.aec.sensorweb.lib.Utils;
import uk.ac.dundee.computing.aec.sensorweb.models.DeviceModel;
import uk.ac.dundee.computing.aec.sensorweb.stores.D3Store;
import uk.ac.dundee.computing.aec.sensorweb.stores.DeviceStore;

/**
 *
 * @author andy
 */
@WebServlet(name = "Days", urlPatterns = {"/Days", "/Days/*"})
public class Days extends HttpServlet {

    private Cluster cluster = null;
    private Session session = null;
    private HashMap CommandsMap = new HashMap();

    public void init(ServletConfig config) throws ServletException {

        cluster = CassandraHosts.getCluster();
        session = cluster.newSession();
        CommandsMap.put("JSON", 1);
        CommandsMap.put("D3", 2);
    }

    // /Days/UUID/Count
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean RenderJSON = false;
        boolean D3 = false;
        int Days = 7;  //DEfault to a week
        Date Dates[] = new Date[2];
        String Device = null;
        String args[] = Convertors.SplitRequestPath(request);
        DeviceModel dm = new DeviceModel();
        dm.setSession(session); //connect to Cassandra;
        for (int i = 0; i < args.length; i++) {
            if (CommandsMap.get(args[i]) != null) {
                switch ((Integer) CommandsMap.get(args[i])) {
                    case 1:
                        RenderJSON = true;
                        break;
                    case 2:
                        D3 = true;
                        break;
                    default:
                        break;

                }
            }
            try {
                java.util.UUID uuid = Convertors.UUIDFromString(args[i]);
                Device = args[i];
            } catch (IllegalArgumentException iLLet) {
                //We actually don't care, it wasn't a UUID !
            }

            try {
                Days = Integer.parseInt(args[i]);
            } catch (Exception et2) {
                //We actually don't care,  it wasn't a number
            }
        }
        Dates[1] = Calendar.getInstance().getTime();
        Utils uu = new Utils();
        Dates[0] = uu.getLastWeek(Days);

        if (Device != null) {
            DeviceStore dd = null;
            D3Store d3S = null;
            if (D3 == false) {

                dd = dm.getDeviceRange(Device, Dates[0], Dates[1]);

            } else {

                d3S = dm.getD3Range(Device, Dates[0], Dates[1]);

            }

            if (RenderJSON == true) {

                if (D3 == false) {
                    request.setAttribute("Data", dd);
                } else {

                    request.setAttribute("Data", d3S);
                }
                RequestDispatcher rdjson = request.getRequestDispatcher("/RenderJson");
                rdjson.forward(request, response);
            }
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
        protected void doGet
        (HttpServletRequest request, HttpServletResponse response)
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
        protected void doPost
        (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            processRequest(request, response);
        }

        /**
         * Returns a short description of the servlet.
         *
         * @return a String containing servlet description
         */
        @Override
        public String getServletInfo
        
            () {
        return "Short description";
        }// </editor-fold>

    }
