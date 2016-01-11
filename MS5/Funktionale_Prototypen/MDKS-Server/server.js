// Requires
var express = require('express');
var path = require("path");
var amqp = require('amqp');
var bodyParser = require("body-parser");

// Middleware Config

var app = express();
var connection = amqp.createConnection({host: 'localhost'});
var exchange;

app.use(express.static(__dirname + '/public'));
app.use(bodyParser.urlencoded({extended: false}));

connection.on('ready', function () {
    exchange = connection.exchange('amq.topic');
    exchange.on('open', function () {
        console.log('exchange ready');
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

var server = app.listen(3000, function () {
    console.log('Server läuft auf localhost:3000');
});