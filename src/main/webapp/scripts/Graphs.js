/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function drawGraph(Data) {
    Width = 300;
    Height = 200;
    if ((typeof svg == 'undefined')) {
        svg = d3.select("body").append("svg").attr("width", Width).attr(
                "Height", Height);
    }
    var ymin=d3.min(Data, function(d){
        return d.value;});
    var ymax=d3.max(Data, function(d){
        return d.value;});
    var yscale= d3.scale.linear()
            .domain ([d3.min(Data, function(d){return d.value;})],[d3.max(Data, function(d){return d.value;})])
            .range([0,Height]);
    var xscale=d3.scale.linear()
            .domain ([0,Data.length()])
            .range([0,Width]);
    var circles = svg.selectAll("circle").data(Data).enter()
			.append("circle");

	circles.attr("cx", function(d,i) {
                
		return xscale(i);
	}).attr("cy", function(d,i) {
		return yscale(d.value);
	}).attr("r", function(d,i) {
		return 2;
	});
}

function getGraphsData(){
    d3.json(path+"/JSON", function(error, data){
        if (error){
            console.log(error);
        } else{
            var readings=data["D3Readings"];
            var keys=readings.Sensor0;
            var temperature=readings["Sensor0"];
            
            drawGraph(temperature);
        }
    });

}


window.onload= getGraphsData;