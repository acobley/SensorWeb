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
import java.util.Arrays;
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
import uk.ac.dundee.computing.aec.sensorweb.stores.DeviceStore;

/**
 *
 * @author andycobley
 */
@WebServlet(name = "Device", urlPatterns = {"/Device", "/Device/*"})
public class Device extends HttpServlet {

    private Cluster cluster = null;
    private Session session = null;
    private HashMap CommandsMap = new HashMap();

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
        session = cluster.newSession();
        CommandsMap.put("JSON", 1);
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
