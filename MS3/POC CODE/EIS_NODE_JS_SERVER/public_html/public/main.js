$(document).ready(function () {
    var message;
    $("#submit").click(function () {
        message = $("#message").val();
        $.ajax({
            type: "POST",
            url: "http://localhost:3000/patient",
            data: {message: message},
            success: function (data) {
            },
            dataType: "json"
        });

    });
});

