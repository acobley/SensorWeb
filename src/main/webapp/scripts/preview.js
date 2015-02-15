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
        var meta = data["Meta"];
        for (i in meta) {

            k = meta[i];
            $("#preview").append(i + " : " + k + "<br>");

        }
    });

}