$(document).ready(function (e) {
    loadPatientDaten();
    $("#btn_verordnung_anlegen").click(function () {
        verordnungAnlegen();
    });
    e.preventDefault();
});

var patientID;
var personalID = 2;
var statioID;
var medikamentID;
var verordnungStatus = true;
var pregnant;
var medikamente;

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
    patientID = data[0].patient_id;
    stationID = data[0].station_id;

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
<td><button id="' + data[i].verordnung_id + '">bearbeiten</button></td>\n\
\n\</tr>');
    }
}

function verordnungAnlegen() {
    verordnungStatus = true;
    showPopup("verordnungAnlegen.html");
}

// füllt den popup mit daten 
function fillVerordnungData() {
    $("#td_patientid").html(patientID);
    $("#td_personalid").html(personalID);

    $.ajax({
        type: 'GET',
        url: '/medikamente',
        contentType: 'application/json'
    }).done(function (data) {
        var medikamente = data;
        var tags = [data.length];
        for (var i = 0; i < data.length; i++) {
            tags[i] = data[i].medikamentname;
        }
        $("#verordnung_anlegen_medikament").autocomplete({
            source: tags
        });

        // getestet mit chrome
        $("#verordnung_anlegen_medikament").focusout(function () {
            console.log("medikament change");
            for (var i = 0; i < medikamente.length; i++) {
                if ($("#verordnung_anlegen_medikament").val() == medikamente[i].medikamentname) {
                    $("#td_einheit").html(medikamente[i].einheit);
                    medikamentID = medikamente[i].medikament_id;
                }
            }

        });
        console.log("done");
    }).fail(function (e) {
        // hat nicht geklappt
        console.log('Fail');
    });

}

function saveVerordnung() {
    var sample = [
        {
            "tag": "Montag", "zeiten":
                    [
                        {"zeit": "08:00"},
                        {"zeit": "12:00"},
                        {"zeit": "16:00"},
                        {"zeit": "18:00"}
                    ]
        }
    ];
    var data = {
        patient_id: patientID,
        personal_id:personalID,
        station_id: stationID,
        medikament_id: medikamentID,
        applikationszeit: []
    };

    var days = [];
    $(".input_verordnung_day").each(function () {
        if ($(this).is(':checked')) {
            switch ($(this).attr("id").split("_")[2]) {
                case "montag":
                    days.push("Montag");
                    break;
                case "dienstag":
                    days.push("Dienstag");
                    break;
                case "mittwoch":
                    days.push("Mittwoch");
                    break;
                case "donnerstag":
                    days.push("Donnerstag");
                    break;
                case "freitag":
                    days.push("Freitag");
                    break;
                case "samstag":
                    days.push("Samstag");
                    break;
                case "sonntag":
                    days.push("Sonntag");
                    break;
                }
        }
    });
    console.log(days);

    var times = [];
    $(".verordnung_anlegen_appzeit").each(function () {
        times.push($(this).val());
    });
    console.log(times);

    for (var i = 0; i < days.length; i++) {
        data.applikationszeit.push({tag: days[i], zeiten: []});
        for (var j = 0; j < times.length; j++) {
            data.applikationszeit[i].zeiten.push({zeit: times[j]});
        }
        JSON.stringify(data.applikationszeit[i].zeiten);
    }
    JSON.stringify(data.applikationszeit);
    console.log(JSON.stringify(data).toString());
    
    $.ajax({
        type: 'POST',
        url: '/verordnung',
        data: JSON.stringify(data),
        contentType: 'application/json'
    }).done(function (data) {
        console.log("verordnung gespeichert");
        hidePopup2();
        hidePopup();
        alert("Verordnung gespeichert");
    }).fail(function (e) {
        // hat nicht geklappt
        console.log('Fail');
    });
    
}

function checkVerordnung() {
    var data = {
        medikamentname: $("#verordnung_anlegen_medikament").val(),
        patientid: patientID
    };

    $.ajax({
        type: 'POST',
        url: '/checkInteraction',
        data: JSON.stringify(data),
        contentType: 'application/json'
    }).done(function (data) {
        setRisiko(data);
    }).fail(function (e) {
        // hat nicht geklappt
        console.log('Fail');
    });
}

function setRisiko(data) {
    if (data.length != 0) {
        $("#td_risiko").empty();
        for (var i = 0; i < data.length; i++) {
            $("#td_risiko").append('Risiko:' + data[i].grund + '<br>');
        }
        // TODO: Felder validieren
    } else {
        $("#td_risiko").empty();
        $("#td_risiko").append('Ok');

        $("#popup2").css({"border": "5px solid black"});
        $("#popup2").load("verordnungSpeichern.html");
    }
}

function showPopup(html, callback) {
    $("#popup").load(html, function () {
        fillVerordnungData();
    });
    $("#popup").css({"border": "5px solid black"});
}

function hidePopup() {
    $("#popup").empty();
    $("#popup").css({"border": "0px solid black"});
}

function hidePopup2() {
    $("#popup2").empty();
    $("#popup2").css({"border": "0px solid black"});
}

function addAppZeit() {
    console.log("aa");
    $("#table_applikationszeit").append('<tr><td><input class="verordnung_anlegen_appzeit" type="text"></td><td><img src="icons/ic_add_to_photos_black_24dp.png" class="btn_img btn_appzeit_anlegen" onclick="addAppZeit()"></td></tr>');
}


