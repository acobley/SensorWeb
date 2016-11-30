/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function drawGraph(Data, Title) {
    Width = 800;
    Height = 400;
    var padding = 50;

    var Datalength = Data.length;
    svg = d3.select("#Graphs").append("svg").attr("width", Width).attr("Height", Height);

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

    var yscale = d3.scale.linear()
            .domain([ymin - 10, ymax])
            .range([Height - padding, padding]);
    var xscale = d3.time.scale()
            .domain([mindate, maxdate])
            .range([padding, Width - (2 * padding)]);
    var circles = svg.selectAll("circle").data(Data).enter()
            .append("circle");
    var xAxis = d3.svg.axis().orient("bottom").scale(xscale);
    var yAxis = d3.svg.axis().orient("left").scale(yscale).ticks(5);
    circles.attr("cx", function (d, i) {
        return xscale(new Date(d.date));
    }).attr("cy", function (d, i) {
        cy = parseFloat(d.value, 10);
        return yscale(cy);
    }).attr("r", function (d, i) {
        return 1;
    });
    svg.append("g").attr("class", "axis")
            .attr("transform", "translate(0," + (2 * padding) + ")")
            .call(xAxis);

    svg.append("g").attr("class", "axis")
            .attr("transform", "translate(" + padding + ",0)")
            .call(yAxis);

    svg.append("text")
            .attr("x", (Width / 2))
            .attr("y", (padding / 2))
            .attr("text-anchor", "middle")
            .style("font-size", "16px")
            .style("text-decoration", "underline")
            .text(Title);
    svg.selectAll(".xaxis text")  // select all the text elements for the xaxis
            .attr("transform", function (d) {
                return "translate(" + this.getBBox().height * -2 + "," + this.getBBox().height + ")rotate(-45)";
            });
}

function getGraphsData() {
    var Route=null;
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
                drawGraph(reading, i);
                //console.log(i);
            }

        }
    });

}


window.onload = getGraphsData;