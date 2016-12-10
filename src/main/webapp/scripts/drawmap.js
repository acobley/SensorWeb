var map;
function initializeMap() {

var mapOptions = {
         
          zoom: 15,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        };
map=new google.maps.Map(document.getElementById('map'),mapOptions);

} ; 



function drawmap(p1,p2){
map.setCenter(new google.maps.LatLng(p1,p2));

}
