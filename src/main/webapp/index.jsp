<%@page import="java.util.Iterator"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="uk.ac.dundee.computing.aec.sensorweb.stores.*" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sensor Web</title>
    </head>
    <body>
    <header>
        <h1>Sensors</h1>
    </header>
    <article>
        <%
            java.util.LinkedList<DeviceStore> Devices = (java.util.LinkedList<DeviceStore>) request.getAttribute("Devices");
            if (Devices == null) {
        %>
        <p>No Devices found</p>
        <%
        } else {
            Iterator<DeviceStore> iterator;
            iterator = Devices.iterator();
            while (iterator.hasNext()) {
                DeviceStore p = (DeviceStore) iterator.next();

        %>
         <a href="/SensorWeb/Device/<%=p.getName()%>" ><%=p.getName()%></a><br/><%

            }
            }
        %>
    </article>
    </body>
</html>
