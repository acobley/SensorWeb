/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var path = "/SensorWeb/Devices";



function setPath(PATH) {
    path = PATH;
}

function OnHeadingIn(elem) {
    $("#preview").empty();

}

function OnMouseOut (elem) {
            elem.style.border = "";
        }

function OnMouseIn(elem) {
    elem.style.border = "2px solid blue";
    var selem = elem.toString() + "/JSON";
    $("#preview").empty();
    $("#preview").append("<h2>Preview</h2>");
    $.getJSON(selem, function (data)
    {
        $("#preview").append("<h3>Meta Data</h3>");
        var meta = data["Meta"];
        for (i in meta) {

            k = meta[i];
            $("#preview").append(i + " : " + k + "<br>");

        }
         $("#preview").append("<h3>Available Dates</h3>");
        var dates=data["Dates"];
        for (i in dates){
            k=dates[i];
            $("#preview").append( k + "<br>");
        }
        
        var sensors=data["SensorList"];
        if (sensors!=null){
            for (i in sensors){
                for (k in sensors[i]){
                    j=sensors[i][k];
                    for (l in j){
                        t=j[l];
                    }
                    $("#preview").append(k + " : " + j + "<br>");
                }
               
            

        
            }
        }
    });

}