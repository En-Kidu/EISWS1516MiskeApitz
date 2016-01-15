$(document).ready(function () {
    $("#btn_patient_suchen").click(function () {
        var station = $("#input_patient_suchen_station").val();
        var zimmer = $("#input_patient_suchen_zimmer").val();
        var name = $("#input_patient_suchen_name").val();

        var data = {
            station: station,
            zimmer: zimmer,
            name: name
        };
        console.log(data.user);
        $.ajax({
            type: 'POST',
            url: '/patient',
            data: JSON.stringify(data),
            contentType: 'application/json'
        }).done(function (data) {
            if (data.length != 0) {
                buildTable(data);
            }
        }).fail(function (e) {
            // hat nicht geklappt
            console.log('Fail');
        });
    });
});

function buildTable(data) {
    for (var i = 0; i < data.length; i++) {
        $("#table_patient").append('<tr>\n\
<td>' + data[i].vorname + ' ' + data[i].name + '</td>\n\
<td>' + data[i].station_id + '</td>\n\
<td>' + data[i].zimmer + '</td>\n\
<td><button id=btn_patient_' + data[i].patient_id + '>Patientenakte öffnen</button></td>\n\
\n\</tr>');
        var id = data[i].patient_id;
        $('#btn_patient_' + data[i].patient_id).click(function () {
            var tmp = $(this).attr("id").split("_");
            var id = tmp[tmp.length-1];
            window.location.replace("/panel.html?patient_id=" + id);
        });
    }
}