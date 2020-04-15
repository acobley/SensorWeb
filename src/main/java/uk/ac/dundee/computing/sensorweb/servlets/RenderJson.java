package uk.ac.dundee.computing.sensorweb.servlets;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.lang.reflect.*;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import javax.servlet.annotation.WebServlet;
import uk.ac.dundee.computing.aec.sensorweb.lib.Utils;
import uk.ac.dundee.computing.aec.sensorweb.stores.FileNameStore;

/**
 * Servlet implementation class RenderJson
 */
@WebServlet(name = "RenderJson", urlPatterns = {"/RenderJson"})

public class RenderJson extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RenderJson() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        Object temp = request.getAttribute("Data");
        FileNameStore fs = (FileNameStore)request.getAttribute("FileName");
        LocalDateTime dd=fs.getDd();
        String Name=(String)fs.getName();
        Class c = temp.getClass();
        String className = c.getName();
        if (className.compareTo("java.util.LinkedList") == 0) { //Deal with a linked list
            List Data = (List) request.getAttribute("Data");
            Iterator iterator;
            JSONObject JSONObj = new JSONObject();
            JSONArray Parts = new JSONArray();
            iterator = Data.iterator();
            while (iterator.hasNext()) {
                Object Value = iterator.next();
                JSONObject obj = ProcessObject(Value);
                try {
                    Parts.put(obj);
                } catch (Exception JSONet) {
                    System.out.println("JSON Fault" + JSONet);
                }
            }
            try {
                JSONObj.put("Data", Parts);
            } catch (Exception JSONet) {
                System.out.println("JSON Fault" + JSONet);
            }
            if (JSONObj != null) {
                Utils.WriteJSONLog(Name, dd, JSONObj.toString(4));
                PrintWriter out = response.getWriter();
                System.out.println("JSON "+JSONObj);
                out.print(JSONObj);
            }

        } else {
            Object Data = request.getAttribute("Data");
            JSONObject obj = ProcessObject(Data);
            if (obj != null) {
                Utils.WriteJSONLog(Name, dd, obj.toString(4));
                PrintWriter out = response.getWriter();
                System.out.println("JSON "+obj);
                out.print(obj);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        Object temp = request.getAttribute("Data");
        FileNameStore fs = (FileNameStore)request.getAttribute("FileName");
        LocalDateTime dd=fs.getDd();
        String Name=(String)fs.getName();
        Class c = temp.getClass();
        String className = c.getName();
        if ((className.compareTo("java.util.LinkedList") == 0) || (className.compareTo("java.util.ArrayList") == 0)) { //Deal with a linked list

            if (className.compareTo("java.util.LinkedList") == 0) {
                List Data = (List) request.getAttribute("Data");
                Iterator iterator;
                JSONObject JSONObj = new JSONObject();
                JSONArray Parts = new JSONArray();
                iterator = Data.iterator();
                while (iterator.hasNext()) {
                    Object Value = iterator.next();
                    JSONObject obj = ProcessObject(Value);
                    try {
                        Parts.put(obj);
                    } catch (Exception JSONet) {
                        System.out.println("JSON Fault" + JSONet);
                    }
                }
                try {
                    JSONObj.put("Data", Parts);
                } catch (Exception JSONet) {
                    System.out.println("JSON Fault" + JSONet);
                }
                if (JSONObj != null) {
                    Utils.WriteJSONLog(Name, dd, JSONObj.toString(4));
                    PrintWriter out = response.getWriter();
                    System.out.println(JSONObj);
                    out.print(JSONObj);
                }
            }
            if (className.compareTo("java.util.ArrayList") == 0) {
                ArrayList Data = (ArrayList) request.getAttribute("Data");
                Iterator iterator;
                JSONObject JSONObj = new JSONObject();
                JSONArray Parts = new JSONArray();
                iterator = Data.iterator();
                while (iterator.hasNext()) {
                    Object Value = iterator.next();
                    JSONObject obj = ProcessObject(Value);
                    try {
                        Parts.put(obj);
                    } catch (Exception JSONet) {
                        System.out.println("JSON Fault" + JSONet);
                    }
                }
                try {
                    JSONObj.put("Data", Parts);
                } catch (Exception JSONet) {
                    System.out.println("JSON Fault" + JSONet);
                }
                if (JSONObj != null) {
                    Utils.WriteJSONLog(Name, dd, JSONObj.toString(4));
                    PrintWriter out = response.getWriter();
                    System.out.println(JSONObj);
                    out.print(JSONObj);
                }
            }

        } else {
            Object Data = request.getAttribute("Data");
            JSONObject obj = ProcessObject(Data);
            if (obj != null) {
                Utils.WriteJSONLog(Name, dd, obj.toString(4));
                PrintWriter out = response.getWriter();
                System.out.println(obj);
                out.print(obj);
            }
        }
    }

    private JSONObject ProcessObject(Object Value) {
        JSONObject Record = new JSONObject();

        try {
            Class c = Value.getClass();
            Method methlist[] = c.getDeclaredMethods();
            for (int i = 0; i < methlist.length; i++) {
                Method m = methlist[i];
                //System.out.println(m.toString());
                String mName = m.getName();

                if (mName.startsWith("get") == true) {
                    String Name = mName.replaceFirst("get", "");
                    //Class pvec[] = m.getParameterTypes(); //Get the Parameter types
                    //for (int j = 0; j < pvec.length; j++)
                    //   System.out.println("param #" + j + " " + pvec[j]);
                    //System.out.println(mName+" return type = " +  m.getReturnType());
                    Class partypes[] = new Class[0];
                    Method meth = c.getMethod(mName, partypes);
                    Object rt = null;
                    try {
                        rt = meth.invoke(Value);
                    } catch (Exception et) {
                        System.out.println("Method " + Name);
                        System.out.println("Cat't process this reflection invocation" + et);
                        System.out.println("Cause " + et.getCause());

                        continue;
                    }
                    Class cl = rt.getClass();
                    String className = cl.getName();
                    if (rt != null) {
                        //System.out.println(Name+" Return "+ rt);
                        try {
                            Record.put(Name, rt);
                        } catch (Exception JSONet) {
                            System.out.println("Warning JSON Fault rt out of range " + JSONet);
                            Record.put(Name,Double.MAX_VALUE);
                
                        }

                    }
                }
            }

        } catch (Throwable e) {
            System.err.println(e);
        }
        return Record;
    }

}
