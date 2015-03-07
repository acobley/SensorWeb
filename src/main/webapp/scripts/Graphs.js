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
    var circles = svg.selectAll("circle").data(Data).enter()
			.append("circle");

	circles.attr("cx", function(d,i) {
                var dd=d;
		return i;
	}).attr("cy", function(d,i) {
		return i;
	}).attr("r", function(d,i) {
		return 20;
	});
}

function getGraphsData(){
    d3.json(path+"/JSON", function(error, data){
        if (error){
            console.log(error);
        } else{
            var readings=data["Readings"];
            drawGraph(readings);
        }
    });

}


window.onload= getGraphsData;