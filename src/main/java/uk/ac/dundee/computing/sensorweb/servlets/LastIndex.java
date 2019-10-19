/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.sensorweb.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import uk.ac.dundee.computing.aec.sensorweb.lib.Convertors;
import uk.ac.dundee.computing.aec.sensorweb.lib.Dbutils;
import uk.ac.dundee.computing.aec.sensorweb.models.ReadingsModel;

/**
 *
 * @author andyc
 */
@WebServlet(name = "LastIndex", urlPatterns = {"/LastIndex","/LastIndex/*"},
initParams = {
            @WebInitParam(name = "data-source", value = "jdbc/Sensordb")
        }
)
public class LastIndex extends HttpServlet {

    private DataSource _ds = null;

    private  class LastIndexRecord{
        long LastIndex;
        String Name;
         
        LastIndexRecord(String Name,Long LastIndex){
            this.LastIndex=LastIndex;
            this.Name=Name;
        }
        
        public String getName(){return Name;};
        public Long getLastIndex(){return LastIndex;};
        
        
    }
    
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        Dbutils db = new Dbutils();

        _ds = db.assemble(config);
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
       String args[] = Convertors.SplitRequestPath(request);
       String Name=args[2];
       ReadingsModel rd = new ReadingsModel();
       long LastIndex=rd.getLastIndex(Name,_ds);
       LastIndexRecord ltr=new LastIndexRecord(Name,LastIndex);
       request.setAttribute("Data", ltr);
        RequestDispatcher rdjson = request.getRequestDispatcher("/RenderJson");
        rdjson.forward(request, response);
       
    }

   
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Get the last Index";
    }// </editor-fold>

}
