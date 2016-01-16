// Requires
var express = require('express');
var url = require('url');
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
function mysqlconnectWEBSERVICE() {
    return mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: '',
        database: 'mdks'
    });
}

var app = express();

var connection_rpc_queue = amqp.createConnection({host: 'localhost'});
var exchange;

app.use(express.static(__dirname + '/public'));
app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());
app.use(express.static(__dirname + '/client/'));

app.use(function (req, res, next) {

    // Website you wish to allow to connect
    res.setHeader('Access-Control-Allow-Origin', '*');

    // Request methods you wish to allow
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');

    // Request headers you wish to allow
    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');

    // Set to true if you need the website to include cookies in the requests sent
    // to the API (e.g. in case you use sessions)
    res.setHeader('Access-Control-Allow-Credentials', true);

    // Pass to next layer of middleware
    next();
});

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
    res.sendFile(path.join(__dirname + '/client/login.html'));
});


// Login Arzt-Client
app.post('/login', function (req, res) {
    var user = req.body.user;
    var pass = req.body.pass;
    console.log(req.body);

    mysqlconnection = mysqlconnectMDKS();
    var query = 'SELECT * from user WHERE name="' + user + '" AND pass="' + pass + '"';
    mysqlconnection.query(query, function (err, rows) {
        if (!err) {
            console.log(rows);
            res.writeHead(200, {"Content-Type": 'application/json'});
            res.end(JSON.stringify(rows));
        } else {
            console.log(err);
        }
    });
    mysqlconnection.end();
});
app.get('/patient/:id', function (req, res) {
    var id = req.params.id;
    mysqlconnection = mysqlconnectMDKS();
    var query = 'SELECT * from patient NATURAL JOIN verordnung NATURAL JOIN medikament WHERE patient_id="' + id + '"';
    mysqlconnection.query(query, function (err, rows) {
        if (!err) {
            console.log(rows);
            res.writeHead(200, {"Content-Type": 'application/json'});
            res.end(JSON.stringify(rows));
        } else {
            console.log(err);
        }
    });
    mysqlconnection.end();
});

app.get('/medikamente', function (req, res) {
    mysqlconnection = mysqlconnectMDKS();
    var query = 'SELECT * from medikament';
    mysqlconnection.query(query, function (err, rows) {
        if (!err) {
            console.log(rows);
            res.writeHead(200, {"Content-Type": 'application/json'});
            res.end(JSON.stringify(rows));
        } else {
            console.log(err);
        }
    });
    mysqlconnection.end();
});

app.post('/verordnung', function (req, res) {
    //todo:stringify
    mysqlconnection = mysqlconnectMDKS();
    var query = 'INSERT INTO verordnung SET medikament_id = '+req.body.medikament_id+', personal_id = '+req.body.personal_id+', patient_id = '+req.body.patient_id+', station_id = '+req.body.station_id+', applikationszeitpunkt = "'+req.body.applikationszeit+'", selbstverordnung = 0';
    console.log(query);
    mysqlconnection.query(query, function (err, rows) {
        if (!err) {
            console.log(rows);
            res.writeHead(200, {"Content-Type": 'application/json'});
            res.end(JSON.stringify(rows));
        } else {
            console.log(err);
        }
    });
    mysqlconnection.end();
});

app.post('/checkInteraction', function (req, res) {
    var medikament = req.body.medikamentname;
    var patientid = req.body.patientid;

    console.log(medikament);
    console.log(patientid);
    webserviceconnection = mysqlconnectWEBSERVICE();
    var query = 'SELECT medikamentname FROM verordnung NATURAL JOIN medikament WHERE patient_id=' + patientid + "";
    webserviceconnection.query(query, function (err, rows) {
        if (!err) {
            console.log(rows);
            var medikamentliste = rows;
            var query = 'SELECT interaction,medikamentname FROM medikament WHERE medikamentname="' + medikament + '"';
            webserviceconnection.query(query, function (err, rows) {
                if (!err) {
                    console.log(rows);
                    var result = checkInteractions(medikamentliste, rows);
                    console.log(result);
                    res.writeHead(200, {"Content-Type": 'application/json'});
                    res.end(JSON.stringify(result));
                } else {
                    console.log(err);
                }

            });
            webserviceconnection.end();
        } else {
            console.log('Error while performing Query.');
        }

    });
});

app.post('/patient', function (req, res) {
    var station = req.body.station;
    var zimmer = req.body.zimmer;
    var name = req.body.name;

    console.log(req.query.pateint_id);

    mysqlconnection = mysqlconnectMDKS();
    var query = 'SELECT * from patient WHERE station_id="' + station + '"';
    mysqlconnection.query(query, function (err, rows) {
        if (!err) {
            //console.log(rows);
            res.writeHead(200, {"Content-Type": 'application/json'});
            res.end(JSON.stringify(rows));
        } else {
            console.log(err);
        }
    });
    mysqlconnection.end();

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

function responseGET(data, queue) {
    console.log(queue + ".get");
    console.log(data);
    exchange.publish(queue + ".get", data);
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

// Überprüft Wechselwirkung
function checkInteractions(medikamentListe, interactionListe) {
    var result = [];
    for (var i = 0; i < medikamentListe.length; i++) {
        console.log("Interaktion: " + medikamentListe[i].medikamentname + " + " + interactionListe[0].interaction);
        if (medikamentListe[i].medikamentname == interactionListe[0].interaction) {
            result.push({"grund": "Bei den Medikamenten " + medikamentListe[i].medikamentname + " und " + interactionListe[0].medikamentname + " liegt eine Wechselwirkung vor!"});
        }
        console.log("Gleich: " + medikamentListe[i].medikamentname + " + " + interactionListe[0].medikamentname);
        if (medikamentListe[i].medikamentname == interactionListe[0].medikamentname) {
            result.push({"grund": "Das Medikamenten " + interactionListe[0].medikamentname + " wurde bereits verordnet!"});
            break;
        }
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
//getMedikationsplanForStation(1, "responsequeue");
function getMedikationsplanForStation(station_id, responsequeue) {
    var medikationsPlan;
    mysqlconnection = mysqlconnectMDKS();
    var query = 'SELECT * FROM verordnung NATURAL JOIN medikament NATURAL JOIN patient WHERE verordnung.station_id=' + station_id +
            "";
    mysqlconnection.query(query, function (err, rows) {
        if (!err) {
            medikationsPlan = rows;
            console.log("JSON : " + JSON.stringify(rows));
            responseGET(medikationsPlan, responsequeue);
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



