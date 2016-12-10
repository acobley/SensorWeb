/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var path = "/SensorWeb/Devices";
var command="Device"
function newUUID(){
    $("#UUID").empty();
     $.getJSON("/SensorWeb/UUID", function (data)
    {
        var uuid=data["UUID"];
        $("#UUID").append("<p>"+uuid+"</p>");
        $("#UUID").css(border,"2px solid blue");
    });
}

function setPath(PATH) {
    path = PATH;
}

function setCommand(COMMAND){
    command=COMMAND;
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
            
            if (i=='latitude')
                p1=k;
             if (i=='longitude')
                p2=k;
            if (i=='Is_indoor'){
                if (k=="false")
                    k="No";
                else
                    k="Yes"
            }
            if (i=='Avatar'){
                k = k.replace(/\/\s*$/, "");
                $("#preview").append("<img src="+k+" width='200'><br>")
            }else{
                $("#preview").append(i + " : " + k + "<br>");
            }

        }
        initializeMap();
        drawmap(p1,p2);
        
        
        var sensors=data["SensorList"];
        if (sensors!=null){
            $("#preview").append("<h3>Sensor Readings</h3>");
            for (i in sensors){
                for (k in sensors[i]){
                    j=sensors[i][k];
                    for (l in j){
                        t=j[l];
                    }
                    $("#preview").append(k+" : "+l + " : " + t + "<br>");
                }
               
            

        
            }
        }
    });

}