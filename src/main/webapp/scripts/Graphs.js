/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function drawGraph(Data, Title) {
    Width = 500;
    Height = 200;
    var padding = 50;

    var Datalength = Data.length;
    svg = d3.select("#Graphs").append("svg").attr("width", Width).attr("height", Height);

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
            .domain([ymin - 1, ymax])
            .range([Height - padding, padding]);
    var xscale = d3.time.scale()
            .domain([mindate, maxdate])
            .range([padding, Width - (2 * padding)]);
    var circles = svg.selectAll("circle").data(Data).enter()
            .append("circle");
    var xAxis = d3.svg.axis().orient("bottom").scale(xscale).ticks(5);
    var yAxis = d3.svg.axis().orient("left").scale(yscale).ticks(5);

    circles.attr("cx", function (d, i) {
        return xscale(new Date(d.date));
    }).attr("cy", function (d, i) {
        cy = parseFloat(d.value, 10);
        return yscale(cy);
    }).attr("r", function (d, i) {
        return 1;
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
                drawGraph(reading, i);
                //console.log(i);
            }

        }
    });

}


window.onload = getGraphsData;