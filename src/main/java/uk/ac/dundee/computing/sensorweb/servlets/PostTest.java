/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.sensorweb.servlets;
import uk.ac.dundee.computing.aec.sensorweb.lib.Web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author andyc
 */
@WebServlet(name = "PostTest", urlPatterns = {"/PostTest"})
public class PostTest extends HttpServlet {

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
        String url="http://34.73.199.125/SensorWeb/Device";
        String Data="{\n" +
"  \"SensorData\": {\n" +
"    \"device\": \"Data from Java Post\",\n" +
"    \"insertion_time\": \"2019-08-14 08:37:31\"\n" +
"  },\n" +
"  \"sensors\": [\n" +
"    {\n" +
"      \"name\": \"fertilizer_level\",\n" +
"      \"fValue\": 0.26\n" +
"    },\n" +
"    {\n" +
"      \"name\": \"soil_moisture_percent\",\n" +
"      \"fValue\": 25.0\n" +
"    },\n" +
"    {\n" +
"      \"name\": \"air_temperature_celsius\",\n" +
"      \"fValue\": 28.0\n" +
"    },\n" +
"    {\n" +
"      \"name\": \"light\",\n" +
"      \"fValue\": 0.3\n" +
"    }\n" +
"  ],\n" +
"  \"meta\": {\n" +
"    \"latitude\": 56.4549073,\n" +
"    \"Type\": \"FlowerPower\",\n" +
"    \"Avatar\": \"https://s3.amazonaws.com/dev-plant-library/FP_CS3000_GWI.jpg\",\n" +
"    \"longitude\": -2.9933931,\n" +
"    \"Is_indoor\": true\n" +
"  }\n" +
"}";
        Web.PostJson(url, Data);
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet PostTest</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PostTest at " + request.getContextPath() + "</h1>");
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
