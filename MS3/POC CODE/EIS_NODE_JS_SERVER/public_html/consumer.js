var amqp = require('amqp');

var connection = amqp.createConnection({host: 'localhost'});

var queueToReceiveFrom = "testMessageQueue";

connection.on('ready', function () {
    
    connection.queue(queueToReceiveFrom, {autoDelete: false, durable:true}, function (queue) {
        
        queue.bind('amq.topic','verordnung');
        
        console.log('Waiting messages...');
        
        queue.subscribe(function (messageReceived) {
            
            console.log("Received message: " + messageReceived.data.toString());
            
        });
        
    });
    
});



