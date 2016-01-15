$(document).ready(function () {
    $("#btn_login").click(function () {
        var data = {
            user: $("#user").val(),
            pass: $("#pass").val()
        };
        console.log(data.user);
        
        
        $.ajax({
            type: 'POST',
            url: 'http://localhost:3000/login',
            data: JSON.stringify(data),
            contentType: 'application/json'
        }).done(function (data) {
            console.log(data);
        }).fail(function (e) {
            // hat nicht geklappt
            console.log('Fail');
        });
    });
});


