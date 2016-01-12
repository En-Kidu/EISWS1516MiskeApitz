// Requires
var express = require('express');
var path = require("path");
var amqp = require('amqp');
var bodyParser = require("body-parser");
var mysql = require('mysql');

// Verbundungsaufbau zur MySQL MDKS Datenbank
function mysqlconnectMDKS() {
    return mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: '',
        database: 'mdks'
    });
}

// Verbundungsaufbau zur MySQL WEBSERVICE Datenbank
function mysqlconnectWEBSERVICES() {
    return mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: '',
        database: 'webservice'
    });
}

var app = express();
var connection_rpc_queue = amqp.createConnection({host: 'localhost'});
var exchange;

app.use(express.static(__dirname + '/public'));
app.use(bodyParser.urlencoded({extended: false}));

connection_rpc_queue.on('ready', function () {
    exchange = connection_rpc_queue.exchange('amq.topic');
    connection_rpc_queue.queue("rpc_queue", {autoDelete: false, durable: true}, function (queue) {
        console.log('Connected to rpc_queue. Waiting for messages');
        queue.subscribe(function (message, deliveryInfo) {
            console.log("Received message: " + message.data.toString());
            var json = JSON.parse(message.data.toString());
            if (json.routingKey == "get") {
                handleGET(json);
            } else if (json.routingKey == "post") {

            }

        });

    });

});

app.get('/', function (req, res) {
    res.sendFile(path.join(__dirname + '/public/index.html'));
});

app.post('/patient', function (req, res) {
    var message = req.body.message;
    var routing_key = "verordnung";

    exchange.publish(routing_key, message);
    console.log("Sent message: " + message);

});

//******************** //
//**** RPC *********** //
//******************** //

function handleGET(jsonGet) {
    var responseData;
    switch (jsonGet.methode) {
        case "getMedikationsplanForStation":
            getMedikationsplanForStation(jsonGet.parameters[0].station, jsonGet.absender);
            //console.log(JSON.parse(responseData)[0]);
            
            break;
    }
}

function responseGET(data, queue){
    console.log(queue+".get");
    console.log(data);
    exchange.publish(queue+".get", data);
}
//getMedikationsplanForStation(1);

function handlePOST() {

}

//******************** //
//**** Webservice **** //
//******************** //

// Liefert ein JSON mit allen Allergien zurück, die unverträglich mit dem Medikament sind
function getAllergieFromWebservice(medikament) {
    var allergieList;
    webserviceconnection = mysqlconnectWEBSERVICE();
    webserviceconnection.query('SELECT allergie from medikament WHERE medikament=?', [medikament], function (err, rows) {
        if (!err) {
            allergieList = rows;
        } else {
            console.log('Error while performing Query.');
        }
    });
    webserviceconnection.end();

    return allergieList;
}

// Liefert ein JSON mit Nahrungsmitteln zurück, die unverträglich mit der Verordnung ist
function getNahrungsmittelFromWebservice(medikament) {
    var nahrungsmittelList;
    webserviceconnection = mysqlconnectWEBSERVICE();
    webserviceconnection.query('SELECT nahrung from medikament WHERE medikament=?', [medikament], function (err, rows) {
        if (!err) {
            allergieList = rows;
        } else {
            console.log('Error while performing Query.');
        }
    });
    webserviceconnection.end();

    return nahrungsmittelList;
}

// Überprüft die Wirkstoffe zweier Medikamente auf Wechselwirkung
function checkInteractions(medikament1, medikament2) {
    var medikament1Data;
    var medikament2Data;
    var result = {
        "medikament1": medikament1,
        "medikament2": medikament2
    };

    webserviceconnection = mysqlconnectWEBSERVICE();
    webserviceconnection.query('SELECT * from medikament WHERE medikament=?', [medikament1], function (err, rows) {
        if (!err) {
            medikament1Data = rows;
        } else {
            console.log('Error while performing Query.');
        }
    });
    webserviceconnection.query('SELECT * from medikament WHERE medikament=?', [medikament2], function (err, rows) {
        if (!err) {
            medikament1Data = rows;
        } else {
            console.log('Error while performing Query.');
        }
    });
    webserviceconnection.end();

    //Liste der Wirkstoffe, die nicht mit diesem Medikament zusammen eingenommen werden dürfen
    interactionMedikament1Liste = JSON.parse(medikament1Data.interaction);

    for (var i = 0; i < interactionMedikament1Liste.length; i++) {
        if (interactionMedikament1Liste[i].name == medikament2Data.wirkstoff) {
            // Medikament1 inkompatibel mit Medikament2
            result.push({"inkompatibel": medikament2Data.wirkstoff});
        }
    }

    // Überprüfen, ob die Medikamente die selben Wirkstoffe haben
    if (medikament1Data.wirkstoff == medikament2Data.wirkstoff) {
        result.push({"risiko": "Gleicher Wirkstoff: " + medikament2Data.wirkstoff});
    }

    return result;

}

// Überprüft, ob das Medikament für schwangere Frauen geeignet ist
function checkPregnancy(medikament) {
    var pregnancyKompatibel;
    var result = {};
    webserviceconnection = mysqlconnectWEBSERVICE();
    webserviceconnection.query('SELECT pregnancy from medikament WHERE medikament=?', [medikament], function (err, rows) {
        if (!err) {
            pregnancyKompatibel = rows.pregnancy;
        } else {
            console.log('Error while performing Query.');
        }
    });
    webserviceconnection.end();

    if (pregnancyKompatibel != 1) {
        // Ist nicht mit Schwangerschaften kompatibel
        result.push({
            "medikament": medikament,
            "kompatibel": false,
            "info": "Das Medikament" + medikament + " darf nicht bei Schwangerschaften verabreicht werden."
        });
    }

    return result;
}

//*******************************//
//**** Medikationskontrolle **** //
//*******************************//

// Gibt ein JSON mit allen allergien des Patienten zurück
function getAllergie(patientID) {
    var allergieList;
    mysqlconnection = mysqlconnectMDKS();
    mysqlconnection.query('SELECT name from allergie WHERE patient_id=?', [patientID], function (err, rows) {
        if (!err) {
            allergieList = rows;
        } else {
            console.log('Error while performing Query.');
        }
    });
    mysqlconnection.end();

    return allergieList;
}

function getMedikationsplanForPatient(station_id, patient_id) {

}

function getMedikationsplanForStation(station_id,responsequeue) {
    var medikationsPlan;
    mysqlconnection = mysqlconnectMDKS();
    var query = 'SELECT * FROM verordnung NATURAL JOIN patient WHERE station_id=' + station_id;
    mysqlconnection.query(query, function (err, rows) {
        if (!err) {
            medikationsPlan =rows;
            console.log("JSON : "+JSON.stringify(rows));
            responseGET(medikationsPlan,responsequeue);
        } else {
            console.log('Error while performing Query.');
            console.log(err);
        }
    });
    mysqlconnection.end();
}

function getMedikamente(patientID) {
    var medikamenteList;
    mysqlconnection = mysqlconnectMDKS();
    mysqlconnection.query('SELECT name from verordnung NATURAL JOIN medikament WHERE patient_id=?', [patientID], function (err, rows) {
        if (!err) {
            allergieList = rows;
        } else {
            console.log('Error while performing Query.');
        }
    });
    mysqlconnection.end();

    return medikamenteList;
}

// Überprüft, ob die Allergien verträglich mit einem Medikament ist
// Liefert ein JSON mit Liste der unverträglichen Allergien zurück
function checkAllergie(medikament, patientID) {
    // Liste der Allergien, die mit dem Medikament nicht verträglich sind
    var allergieListFromMedikament = getAllergieFromWebservice(medikament);
    // Liste aller Allergien, die unter denen der Patient leidet
    var allergieListFromPatient = getAllergie(patientID);
    // Liste der Allergien, die sich mit nicht dem Medikament vertragen
    var gefundeneAllergien = {};
    // Suche nach gemeinsamen Allergien der Listen
    for (var i = 0; i < allergieListFromMedikament.length; i++) {
        for (var j = 0; j < allergieListFromPatient.length; j++) {
            if (allergieListFromMedikament[i] == allergieListFromPatient[j]) {
                // Füge Allergie zur Liste hinzu
                gefundeneAllergien.push(allergieListFromPatient[j]);
            }
        }
    }
    return gefundeneAllergien;
}

// Liefert eine vollständige Liste aller Nahrungsmittel zurück, die nicht unverträglich mit den Verordnungen des Patienten sind
function getNahrungsmittelListe(patientID) {
    var medikamente = getMedikamente(patientID);
    var nahrungsmittelListe = {};
    for (var i = 0; i < medikamente.length; i++) {
        nahrungsmittelListe.push(getNahrungsmittelFromWebservice(medikamente[0].name));
    }
    return nahrungsmittelListe;
}

var server = app.listen(3000, function () {
    console.log('Server läuft auf localhost:3000');
});



