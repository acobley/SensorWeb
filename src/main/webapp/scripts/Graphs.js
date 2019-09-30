/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var svg;
var axScale = [];
var ayScale = [];

function drawGraph(Data, Title, ID,Colour,ReCalcXScale) {
    Width = 500;
    Height = 200;
    var padding = 50;

    var Datalength = Data.length;
    var first = false;
    svg = d3.select("#" + ID);
    if (svg.empty()) {
        svg[ID] = d3.select("#Graphs").append("svg").attr("id", ID).attr("width", Width).attr("height", Height);
        svg = svg[ID];
        first = true;

    } else {
        xscale = axScale[ID];
        yscale = ayScale[ID];
    }

    var ymin = d3.min(Data, function (d) {
        return parseFloat(d.value, 10);
    });
    var ymax = d3.max(Data, function (d) {
        return parseFloat(d.value, 10);
    });

    var mindate = d3.min(Data, function (d) {
        return new Date(d.date);
    });
    var maxdate = d3.max(Data, function (d) {
        return new Date(d.date);
    });

    if (ReCalcXScale==true){
        xscale = d3.time.scale()
                .domain([mindate, maxdate])
                .range([padding, Width - (2 * padding)]);
        axScale[ID] = xscale;
    }
    if (first == true) {
        yscale = d3.scale.linear()
                .domain([ymin - 1, ymax+ymax/10])
                .range([Height - padding, padding]);
        xscale = d3.time.scale()
                .domain([mindate, maxdate])
                .range([padding, Width - (2 * padding)]);
        axScale[ID] = xscale;
        ayScale[ID] = yscale
    }
    var circles = svg.selectAll("circle").data(Data).enter()
            .append("circle");
    var xAxis = d3.svg.axis().orient("bottom").scale(xscale).ticks(5);
    var yAxis = d3.svg.axis().orient("left").scale(yscale).ticks(5);

    var lineFunction = d3.svg.line()
            .x(function (d) {
                return xscale(new Date(d.date))
            })
            .y(function (d) {
                cy = parseFloat(d.value, 10);
                return yscale(cy);
            })
            .interpolate("monotone");

    var lineGraph = svg.append("path")
            .attr("d", lineFunction(Data))
            .attr("stroke", Colour)
            .attr("stroke-width", 1)
            .attr("fill", "none");


    circles
            .append("svg:title")
            .text(function (d) {
                return d.value;
            });
    svg.append("g").attr("class", "xaxis")
            .attr("transform", "translate(0," + (Height - padding) + ")")
            .style("font-size", "10px")
            .style("fill", "cornflowerblue")
            .call(xAxis);

    svg.append("g").attr("class", "yaxis")
            .attr("transform", "translate(" + padding + ",0)")
            .style("font-size", "10px")
            .style("fill", "cornflowerblue")

            .call(yAxis);
    svg.selectAll(".xaxis text")  // select all the text elements for the xaxis

            .attr("transform", function (d) {
                return "translate(" + this.getBBox().height * -2 + "," + this.getBBox().height + ")rotate(-45)";
            });

    svg.append("text")
            .attr("x", (Width / 2))
            .attr("y", (padding / 2))
            .attr("text-anchor", "middle")
            .style("font-size", "16px")
            .style("text-decoration", "underline")
            .text(Title);



}

function sortByDateAscending(a, b) {
    // Dates will be cast to numbers automagically:
    var diff = new Date(a.date) - new Date(b.date);
    return diff;
}

function getGraphsData() {
    var Route = null;
    if (command === "Device") {
        path = path.replace("Device", "Days");
        Route = path + "/30/JSON/D3";
    } else {
        Route = path + "/1/JSON/D3";
    }
    d3.json(Route, function (error, data) {
        if (error) {
            console.log(error);
        } else {
            var readings = data["D3Readings"];
            for (var i in readings) {
                var reading = readings[i];
                reading = reading.sort(sortByDateAscending);
                drawGraph(reading, i + " (30 Days)", i,"cornflowerblue");
                //console.log(i);
            }

        }
    });

}

function getGraphsPeriodData(Period) {
    if (typeof Period == 'undefined') {
        return;
    }

    var Route = null;
    if (command === "Device") {
        path = path.replace("Device", "Days");
        Route = path + "/" + Period + "/JSON/D3";
    } else {
        Route = path + "/1/JSON/D3";
    }
    d3.json(Route, function (error, data) {
        if (error) {
            console.log(error);
        } else {
            var readings = data["D3Readings"];
            for (var i in readings) {
                 svg = d3.select("#" + i);
                 
                if (!svg.empty()) {
                    svg.selectAll("*").remove();
                }
                var reading = readings[i];
                reading = reading.sort(sortByDateAscending);
                drawGraph(reading, i + " " + Period + " Days", i,"cornflowerblue",true);
                //console.log(i);
            }
        }
    });

}

window.onload = function () {
    d3.select("#days180").on('click', function () {
        getGraphsPeriodData(180);
    });
    d3.select("#days30").on('click', function () {
        getGraphsPeriodData(30);
    });
    d3.select("#days14").on('click', function () {
        getGraphsPeriodData(14);
    });
    d3.select("#days7").on('click', function () {
        getGraphsPeriodData(7);
    });
    d3.select("#days3").on('click', function () {
        getGraphsPeriodData(3);
    });
    d3.select("#days1").on('click', function () {
        getGraphsPeriodData(1);
    });
    getGraphsData();
}

function getDeviceGraphsData(Device,Colour) {
    var Route = null;

    path = path.replace("Devices", "Days");
    Route = path + "/" + Device + "/30/JSON/D3";

    d3.json(Route, function (error, data) {
        if (error) {
            console.log(error);
        } else {
            var readings = data["D3Readings"];
            for (var i in readings) {
                var reading = readings[i];
                reading = reading.sort(sortByDateAscending);
                drawGraph(reading, i + " (30 Days)", i,Colour);
                //console.log(i);
            }

        }
    });

}