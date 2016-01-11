var mysql = require('mysql');

var express = require('express');

var bodyParser = require("body-parser");

var path = require("path");

function mysqlconnect() {

    return mysql.createConnection({
        
        host: 'localhost',
        user: 'root',
        password: '',
        database: 'EIS'

    });

}


var app = express();

app.use(express.static(__dirname + '/public'));

app.use(bodyParser.urlencoded({extended: false}));

app.get('/verordnungen', function (req, res) {

    res.sendFile(path.join(__dirname + '/public/verordnungen.html'));

});

app.get('/verordnung', function (req, res) {

    mysqlconnection = mysqlconnect();

    mysqlconnection.query('SELECT * from Verordnungen', function (err, rows, fields) {

        if (!err) {

            console.log('The solution is: ', rows);
            res.json(rows);

        }
        else {

            console.log('Error while performing Query.');

        }

    });

    mysqlconnection.end();

});

app.get("/test", function (req, res) {

    console.log("get auf test");

    res.writeHead(200, {'content-type': 'text/plain'});

    res.write("hallo");

    res.end();

});

app.post('/verordnung', function (req, res) {

    console.log("post");

    mysqlconnection = mysqlconnect();

    mysqlconnection.query('INSERT INTO verordnungen SET ?', req.body, function (err, res) {

        if (err) {

            throw err;

        }

    });

    mysqlconnection.end();

});



var server = app.listen(3001, function () {

    console.log('Server läuft auf localhost:3001');

});
