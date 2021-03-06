<%@page import="java.net.URL"%>
<%@page import="com.datastax.driver.core.UserType"%>
<%@page import="com.datastax.driver.core.UDTValue"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ page import="uk.ac.dundee.computing.aec.sensorweb.stores.*" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sensor Web</title>
        <link rel="stylesheet" type="text/css" href="/SensorWeb/Styles.css" />
        <script type="text/javascript" src="http://code.jquery.com/jquery-2.1.3.min.js"></script>
        <script type="text/javascript" src="/SensorWeb/scripts/preview.js"></script>
        <script type="text/javascript" src="/SensorWeb/scripts/Graphs.js"></script>
        <script src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>

    </head>
    <body>
        <header>
            <h1><a href="/SensorWeb/Devices" onmouseover="OnHeadingIn(this)">Sensors</a></h1>
            <h2>V1.0</h2>
            <h3>Range Slice with graphs and axis </h3>
        </header>
        <nav>
            <%
                String PATH = null;
                String ServerPath = new URL(request.getScheme(),
                        request.getServerName(),
                        request.getServerPort(), "").toString();
                int port = request.getServerPort();
                if (request.getAttribute("Path") != null) {
                    PATH = request.getAttribute("Path").toString();
            }
            if (PATH != null) {%>

            <a href="<%=PATH%>/JSON">Get json for this page</a>
            <script>
                $(function () {
                    setPath("<%=ServerPath%><%=PATH%>");
                });

            </script>
            <%
                }
            %>

            <div id="preview">
            </div>
            <div id="uuidbutton">
                <button name="newUUID"  onclick="newUUID()" >New UUID</button>
            </div>
            <div id="UUID">

            </div>
        </nav>
        <article>
            <%
                String Path = (String) request.getAttribute("Path");
                boolean isRange = false;
                if (Path != null) {
                    if (Path.contains("Range")) {
                        isRange = true;
                    }
                }
                DeviceStore Device = (DeviceStore) request.getAttribute("Device");

                if (Device != null) {
            %>
            <h2>Device <a href="/SensorWeb/Device/<%=Device.getName()%>"   ><%=Device.getName()%></a></h2>
                <%
                    Map<String, String> meta = Device.getMeta();
                    if (meta != null) {
                        for (Map.Entry<String, String> entry : meta.entrySet()) {
                %><%=entry.getKey()%>, <%=entry.getValue()%><br><%
                        }
                    }

                    List<Date> dates = Device.getDates();
                    int Numberofdates = dates.size();
                    int datestep = 1;
                    if (Numberofdates > 50) {
                        datestep = Numberofdates / 50;

                    }
                    if (dates != null) {
                        int DateCount = 0;
                        Iterator<Date> it = dates.iterator();
                        while (it.hasNext()) {
                            Date dd = null;
                            for (int i = 0; i < datestep; i++) {
                                if (it.hasNext()){
                                dd = it.next();
                                }
                            }
                            DateCount++;
                            if (isRange == false) {
                               
            %>

            <%=DateCount%> : <a href="/SensorWeb/Range/<%=Device.getName()%>/<%=dd%>" onmouseover="OnMouseIn(this)" onmouseout="OnMouseOut(this)">>> </a>
            <% } else {%>
            <%=DateCount%> :<a href="<%=Path%>/<%=dd%>" onmouseover="OnMouseIn(this)" onmouseout="OnMouseOut(this)"><<< </a>
            <% }%>
            <a href="/SensorWeb/Device/<%=Device.getName()%>/<%=dd%>" onmouseover="OnMouseIn(this)" onmouseout="OnMouseOut(this)"><%=dd%></a><br>
            <%}
                }
                    
                Map<String, UDTValue> sensorMap = Device.gtSensors();
                if (sensorMap != null) {
                    //UserType SensorReadingType=Device.getreadingType();
                    for (Map.Entry<String, UDTValue> entry : sensorMap.entrySet()) {
            %><%=entry.getKey()%>, 
            <%
                UDTValue sensor = entry.getValue();
                float fValue = sensor.getFloat("fValue");
                int iValue = sensor.getInt("iValue");
                String sValue = sensor.getString("sValue");
                if (fValue != 0) {%>
            <%=sensor.getFloat("fValue")%>,
            <%}
                if (iValue != 0) {%>
            <%=sensor.getInt("iValue")%>,
            <%}
                if (sValue != null) {%>
            <%=sensor.getString("sValue")%>
            <%}%>
            <br>
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
            <a href="/SensorWeb/Device/<%=p.getName()%>" onmouseover="OnMouseIn(this)" onmouseout="OnMouseOut(this)"><%=p.getName()%></a><br/><%

                    }
                }
            %>
        </article>
    </body>
</html>
