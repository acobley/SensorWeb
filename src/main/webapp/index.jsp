

<%@page import="com.datastax.driver.core.UserType"%>
<%@page import="com.datastax.driver.core.UDTValue"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
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
        <h1><a href="/SensorWeb/Devices">Sensors</a></h1>
    </header>
    <article>

        <%
            DeviceStore Device = (DeviceStore) request.getAttribute("Device");

            if (Device != null) {
        %>
        <h2>Device <a href="/SensorWeb/Device/<%=Device.getName()%>"><%=Device.getName()%></a></h2>
        <%
            Map<String, String> meta = Device.getMeta();
            if (meta != null) {
                for (Map.Entry<String, String> entry : meta.entrySet()) {
        %><%=entry.getKey()%>, <%=entry.getValue()%><br><%
                }
            }

            List<Date> dates = Device.getDates();
            if (dates != null) {
                Iterator<Date> it = dates.iterator();
                while (it.hasNext()) {
                    Date dd = it.next();
        %>
        <a href="/SensorWeb/Device/<%=Device.getName()%>/<%=dd%>"><%=dd%></a><br>
        <%}
                }
            Device.getSensors();
            Map<String, UDTValue> sensorMap = Device.getSensors();
            if (sensorMap != null){
                //UserType SensorReadingType=Device.getreadingType();
                for (Map.Entry<String, UDTValue> entry : sensorMap.entrySet()) {
        %><%=entry.getKey()%>, 
        <%
             UDTValue sensor= entry.getValue();%>
             <%=sensor.getFloat("fValue")%>,
             <%=sensor.getInt("iValue")%>,
             <%=sensor.getString("sValue")%><br>
             <%   }
            }
            }
            java.util.LinkedList<DeviceStore> Devices = (java.util.LinkedList<DeviceStore>) request.getAttribute("Devices");

            if (Devices == null) {
        %>
        
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
