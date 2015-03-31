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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.sensorweb.lib.Convertors;
import uk.ac.dundee.computing.aec.sensorweb.models.DeviceModel;
import uk.ac.dundee.computing.aec.sensorweb.stores.D3Store;
import uk.ac.dundee.computing.aec.sensorweb.stores.DeviceStore;

/**
 *
 * @author andycobley
 */
@WebServlet(name = "Range", urlPatterns = {"/Range/*"})
public class Range extends HttpServlet {

    private Cluster cluster = null;
    private Session session = null;
    private HashMap CommandsMap = new HashMap();

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
        session = cluster.newSession();
        CommandsMap.put("JSON", 1);
        CommandsMap.put("D3", 2);
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
        boolean RenderJSON = false;
        boolean D3 = false;
        Date Dates[] = new Date[2];
        int dateCount = 0;
        String Device = null;
        int Aggregation = 1;
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
                Dates[dateCount] = Convertors.StringToDate(args[i]);
                dateCount++;
            } catch (ParseException pEt) {
                //We actually don't care, it wasn't a date
            }
            try {
                Aggregation = Integer.parseInt(args[i]);
            } catch (Exception et2) {
                //We actually don't care,  it wasn't a number
            }

        }

        if (Device != null) {
            DeviceStore dd = null;
            D3Store d3S = null;
            if (D3 == false) {
                switch (dateCount) {
                    case 1:
                        dd = dm.getDeviceRange(Device, Dates[0]);
                        break;
                    case 2:
                        dd = dm.getDeviceRange(Device, Dates[0], Dates[1]);
                        break;
                    default:
                        break;
                }
            } else {
                switch (dateCount) {
                    case 1:
                        d3S = dm.getD3Range(Device, Dates[0]);
                        break;
                    case 2:
                        d3S = dm.getD3Range(Device, Dates[0], Dates[1]);
                        break;
                    default:
                        break;
                }
            }

            if (RenderJSON == true) {

                if (D3 == false) {
                    request.setAttribute("Data", dd);
                } else {
                    d3S.setAggregation(Aggregation);
                    request.setAttribute("Data", d3S);
                }
                RequestDispatcher rdjson = request.getRequestDispatcher("/RenderJson");
                rdjson.forward(request, response);
            } else {

                RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
                request.setAttribute("Device", dd);
                request.setAttribute("Path", request.getRequestURI());
                rd.forward(request, response);
            }
        } else { //Shouldn't get here, but if it does, go back to the index.
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
