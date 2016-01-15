$(document).ready(function () {
    $("#btn_login").click(function () {
        var data = {
            user: $("#user").val(),
            pass: $("#pass").val()
        };   
        
        $.ajax({
            type: 'POST',
            url: '/login',
            data: JSON.stringify(data),
            contentType: 'application/json'
        }).done(function (data) {
            if(data.length!=0){
                console.log(data[0].personalid);
                window.location.replace("suche.html");
            }else{
                console.log("Benutzername oder Password falsch!");
            }
            
        }).fail(function (e) {
            // hat nicht geklappt
            console.log('Fail');
        }).always(function() {
            alert("complete"); 
        });
    });
});


