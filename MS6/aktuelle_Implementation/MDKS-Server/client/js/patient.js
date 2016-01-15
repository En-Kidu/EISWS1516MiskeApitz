$(document).ready(function () {
    loadPatientDaten();
});

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function loadPatientDaten() {
    var id = getParameterByName('patient_id');
    console.log(id);
    $.ajax({
        type: 'GET',
        url: '/patient/' + id,
        contentType: 'application/json'
    }).done(function (data) {
        buildPanel(data);
        console.log("done");
    }).fail(function (e) {
        // hat nicht geklappt
        console.log('Fail');
    });
}

function buildPanel(data) {
    $("#patient_daten_name").html(data[0].vorname + ' ' + data[0].name);
    $("#patient_daten_station").html(data[0].station_id);
    $("#patient_daten_zimmer").html(data[0].zimmer);



    for (var i = 0; i < data.length; i++) {
        var selbstverordnung;
        if (data[i].selbstverordnung == 0) {
            selbstverordnung = "Nein";
        } else {
            selbstverordnung = "Ja";
        }
        $("#table_verordnungen").append('<tr>\n\
<td>' + data[i].medikamentname + '</td>\n\
<td>' + data[i].dosierung + '</td>\n\
<td>' + data[i].einheit + '</td>\n\
<td>' + data[i].darreichungsform + '</td>\n\
<td>' + selbstverordnung + '</td>\n\
<td><button id="'+data[i].verordnung_id+'">bearbeiten</button></td>\n\
\n\</tr>');
    }
}


