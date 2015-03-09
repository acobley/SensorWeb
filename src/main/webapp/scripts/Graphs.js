/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function drawGraph(Data) {
    Width = 300;
    Height = 200;
    var Datalength = Data.length;
    svg = d3.select("body").append("svg").attr("width", Width).attr("Height", Height);
    var ymin = d3.min(Data, function (d) {
        return parseInt(d.value, 10);
    });
    var ymax = d3.max(Data, function (d) {
        return parseInt(d.value, 10);
    });

    var yscale = d3.scale.linear()
            .domain([d3.min(Data, function (d) {
                    return parseInt(d.value, 10);
                })], [d3.max(Data, function (d) {
                    return parseInt(d.value, 10);
                })])
            .range([0, Height]);
    var xscale = d3.scale.linear()
            .domain([0, Datalength])
            .range([0, Width]);
    var circles = svg.selectAll("circle").data(Data).enter()
            .append("circle");

    circles.attr("cx", function (d, i) {
        return xscale(i);
    }).attr("cy", function (d, i) {
        return yscale(parseInt(d.value, 10));
    }).attr("r", function (d, i) {
        return 2;
    });
}

function getGraphsData() {
    d3.json(path + "/JSON", function (error, data) {
        if (error) {
            console.log(error);
        } else {
            var readings = data["D3Readings"];



            for (var i in readings) {
                var reading = readings[i];
                drawGraph(reading);
                console.log(i);
            }

        }
    });

}


window.onload = getGraphsData;