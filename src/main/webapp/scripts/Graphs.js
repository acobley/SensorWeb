/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function drawGraph(Data,Title) {
    Width = 300;
    Height = 200;
    var Datalength = Data.length;
    svg = d3.select("body").append("svg").attr("width", Width).attr("Height", Height);
    
    var ymin = d3.min(Data, function (d) {
        return parseFloat(d.value, 10);
    });
    var ymax = d3.max(Data, function (d) {
        return parseFloat(d.value, 10);
    });
    var padding=30;
    var yscale = d3.scale.linear()
            .domain([ymin,ymax])
            .range([ Height-padding,padding]);
    var xscale = d3.scale.linear()
            .domain([0, Datalength])
            .range([padding, Width-padding]);
    var circles = svg.selectAll("circle").data(Data).enter()
            .append("circle");
    var xAxis=d3.svg.axis().scale(xscale).orient("bottom").ticks(5);
    var yAxis=d3.svg.axis().scale(yscale).orient("left").ticks(5);
    circles.attr("cx", function (d, i) {
        return xscale(i);
    }).attr("cy", function (d, i) {
        cy=parseFloat(d.value, 10);
        return yscale(cy);
    }).attr("r", function (d, i) {
        return 2;
    });
     svg.append("g").attr("class","axis")
            //.attr("transform", "translate(0," + (Height - padding) + ")")
            .call(xAxis);
     svg.append("g").attr("class","axis")
            .attr("transform","translate("+1.5*padding+",0)")
            .call(yAxis);
    svg.append("text")
        .attr("x", (Width / 2))             
        .attr("y", (padding / 2))
        .attr("text-anchor", "middle")  
        .style("font-size", "16px") 
        .style("text-decoration", "underline")  
        .text(Title);
    
}

function getGraphsData() {
    d3.json(path + "/JSON", function (error, data) {
        if (error) {
            console.log(error);
        } else {
            var readings = data["D3Readings"];



            for (var i in readings) {
                var reading = readings[i];
                drawGraph(reading,i);
                //console.log(i);
            }

        }
    });

}


window.onload = getGraphsData;