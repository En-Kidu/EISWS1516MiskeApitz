$(document).ready(function () {

    refreshTabVerordnung();
    
    $("#btn_verordnung_add").click(function () {
        name = $("#input_verordnung_name").val();
        $.ajax({
            type: "POST",
            url: "/verordnung",
            data: {Name: name},
            success: function (data) {
            },
            dataType: "json"
        });
        
        refreshTabVerordnung();

    });

});

function refreshTabVerordnung(){
    $.ajax({
        type: "GET",
        url: "/verordnung",
        success: function (data) {
            $("#tab_verordnungen").find("tr:gt(0)").remove();  
            data.forEach(function(obj){
              $('#tab_verordnungen').append('<tr><td>' + obj.id + '</td><td>' + obj.Name + '</td></tr>');  
            });
            
        },
        dataType: "json"
    });
}


