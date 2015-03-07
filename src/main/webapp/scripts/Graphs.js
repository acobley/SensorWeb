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
}

function getGraphsData(){
    d3.json(path, function(error, data){
        if (error){
            console.log(error);
        } else{
            drawGraph(data);
        }
    });

}


window.onload= loadData;