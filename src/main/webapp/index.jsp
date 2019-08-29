<%@page import="uk.ac.dundee.computing.aec.sensorweb.lib.Convertors"%>
<%@page import="java.net.URL"%>
<%@page import="com.datastax.oss.driver.api.core.type.UserDefinedType"%>
<%@page import="com.datastax.oss.driver.api.core.data.UdtValue"%>
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
    <style type="text/css">
        html { height: 100% }
        body { height: 100%; margin: 0; padding: 0 }
        #map { height: 200px; width: 300px; border: 2ps;border-color: black }
    </style>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js" type="text/javascript"></script>
    <script type="text/javascript"
            src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBRqkq8vYqlDHjL0-HvxN4OWKk3PVJ-rCg">
    </script>
    <script src="/SensorWeb/scripts/drawmap.js" type="text/javascript"></script>
    <script type="text/javascript" src="/SensorWeb/scripts/preview.js" ></script>
    <script type="text/javascript" src="/SensorWeb/scripts/Graphs.js" ></script>
    <script type="text/javascript" src="/SensorWeb/scripts/d3.v3.min.js" charset="utf-8" ></script>
    <link href="/SensorWeb/bootstrap-3.3.7-dist/css/bootstrap.min.css" rel="stylesheet">

</head>
<body>
    <header>
        <h1><a href="/SensorWeb/Devices" onmouseover="OnHeadingIn(this)">Sensors</a></h1>
        <h2>FlowerPower Sensors</h2>
        <div id="map"></div><br>
    </header>
    <nav>
        <%
            String Colours[] = {
                "blue", "blueviolet", "burlywood", "cadetblue", "chartreuse", "chocolate", "coral", "cornflowerblue", "cornsilk", "crimson", "cyan", "darkblue", "darkcyan", "darkgoldenrod", "darkgray", "darkgreen", "darkkhaki", "darkmagenta", "darkolivegreen", "darkorange", "darkorchid", "darkred", "darksalmon", "darkseagreen", "darkslateblue", "darkslategray", "darkturquoise", "darkviolet", "deeppink", "deepskyblue", "dimgray", "dodgerblue", "firebrick", "floralwhite", "forestgreen", "fuchsia", "gainsboro", "ghostwhite", "gold", "goldenrod", "gray", "green", "greenyellow", "honeydew", "hotpink", "indianred", "indigo", "ivory", "khaki", "lavender", "lavenderblush", "lawngreen", "lemonchiffon", "lightblue", "lightcoral", "lightcyan", "lightgoldenrodyellow", "lightgreen", "lightgrey", "lightpink", "lightsalmon", "lightseagreen", "lightskyblue", "lightslategray", "lightsteelblue", "lightyellow", "lime", "limegreen", "linen", "magenta", "maroon", "mediumaquamarine", "mediumblue", "mediumorchid", "mediumpurple", "mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise", "mediumvioletred", "midnightblue", "mintcream", "mistyrose", "moccasin", "navajowhite", "navy", "oldlace", "olive", "olivedrab", "orange", "orangered", "orchid", "palegoldenrod", "palegreen", "paleturquoise", "palevioletred", "papayawhip", "peachpuff", "peru", "pink", "plum", "powderblue", "purple", "red", "rosybrown", "royalblue", "saddlebrown", "salmon", "sandybrown", "seagreen", "seashell", "sienna", "silver", "skyblue", "slateblue", "slategray", "snow", "springgreen", "steelblue", "tan", "teal", "thistle", "tomato", "turquoise", "violet", "wheat", "white", "whitesmoke", "yellow", "yellowgreen"
            };
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
            if (PATH != null) {

                DeviceStore Device = (DeviceStore) request.getAttribute("Device");
                if (Device != null) {%>

        <a href="<%=PATH%>/JSON">Get json for this page</a><br>
        <button id="days30" >Draw 30 Days Graph</button><br>
        <button id="days14">Draw 14 Days Graph</button><br>
        <button id="days7">Draw 7 Days Graph</button><br>
        <button id="days3">Draw 3 Days Graph</button><br>
        <button id="days1">Draw 1 Day Graph</button><br>
        <%}%>
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
            <%
                Map<String, String> meta = Device.getMeta();
                if (meta != null) {
                    for (Map.Entry<String, String> entry : meta.entrySet()) {
                        if (entry.getKey().compareTo("Avatar") == 0) {
            %><img src="<%=entry.getValue()%>" width="200"><br><%
                             } else {
        %><%=entry.getKey()%>, <%=entry.getValue()%><br><%
                        }
                    }
                }
        %>

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
                int Colour = 0;
                while (iterator.hasNext()) {
                    DeviceStore p = (DeviceStore) iterator.next();

            %>
            <button id="Device" onclick="getDeviceGraphsData('<%=p.getName()%>', '<%=Colours[Colour]%>')">Draw</button><a href="/SensorWeb/Device/<%=p.getName()%>" onmouseover="OnMouseIn(this)" onmouseout="OnMouseOut(this)"><%=p.getName()%></a><br/><%
                        Colour++;
                    }
                }
            %>
            <div id="Graphs">
            </div>
        </div>
    </article>
</body>
</html>
