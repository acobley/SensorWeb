<%@page import="uk.ac.dundee.computing.aec.sensorweb.lib.Convertors"%>
<%@page import="java.net.URL"%>
<%@page import="com.datastax.driver.core.UserType"%>
<%@page import="com.datastax.driver.core.UDTValue"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>

<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.sensorweb.stores.*" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sensor Web</title>
        <link rel="stylesheet" type="text/css" href="/SensorWeb/Styles.css" ></script>
    <script type="text/javascript" src="/SensorWeb/scripts/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="/SensorWeb/scripts/preview.js" ></script>
    <script type="text/javascript" src="/SensorWeb/scripts/Graphs.js" ></script>
    <script type="text/javascript" src="/SensorWeb/scripts/d3.v3.min.js" charset="utf-8" ></script>

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
            String args[] = null;
            String ServerPath = new URL(request.getScheme(),
                    request.getServerName(),
                    request.getServerPort(), "").toString();
            int port = request.getServerPort();
            if (request.getAttribute("Path") != null) {
                PATH = request.getAttribute("Path").toString();
                args = Convertors.SplitFiletype(PATH);
            }
            if (PATH != null) {%>

        <a href="<%=PATH%>/JSON">Get json for this page</a>
        <script>
            $(function () {
                setPath("<%=ServerPath%><%=PATH%>");
                setCommand("<%=args[1]%>");
            });

        </script>
        <%
            }
        %>

        <div id="preview">
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
        <div id="Graphs">
        </div>
        <% } %>
        <div id="SensorDates">    
            <%
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
        </div>
    </article>
</body>
</html>
