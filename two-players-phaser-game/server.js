var express = require('express');
var app = express();
var server = require('http').Server(app);
var io = require('socket.io').listen(server);
var ballx = 400;
var bally = 300;
var speedx = 2;
var speedy = -4;
var px = [400,400];

app.use('/css',express.static(__dirname + '/css'));
app.use('/js',express.static(__dirname + '/js'));
app.use('/assets',express.static(__dirname + '/assets'));

app.get('/',function(req,res){
    res.sendFile(__dirname+'/index.html');
});

server.lastPlayderID = 1;

server.listen(process.env.PORT || 8081,function(){
    console.log('Listening on '+server.address().port);
});



io.on('connection',function(socket){

    socket.on('newplayer',function(){
        socket.player = {
            id: server.lastPlayderID++,
        };
        socket.emit('allplayers',getAllPlayers());
        socket.broadcast.emit('newplayer',socket.player);

        socket.on('keyPress',function(dis){
        	if(px[socket.player.id-1]+dis>=800 || px[socket.player.id-1]+dis<=0){
        		return;
        	}
            socket.player.x += dis;
            px[socket.player.id-1] += dis;
            console.log("player"+socket.player.id+":"+px[socket.player.id-1]);
            io.emit('move',{id: socket.player.id, dis: dis});
        });

        socket.on('disconnect',function(){
            io.emit('remove',socket.player.id);
        });
    });
    
    socket.on('usersReady',function(){
    	ballPos = function(){
    		//console.log("x:"+data.x+", "+"y:"+data.y);
    		if(ballx<=10 || ballx>=790){
    			speedx = -speedx;
    		}
    		if((bally==100 && ballx>=px[1]-30 && ballx<=px[1]+30) || (bally==500 && ballx>=px[0]-30 && ballx<=px[0]+30)){
    			speedy = -speedy;
    		}
    		if(bally<=5 || bally>=595){
    			speedy = -speedy;
    		}
    		if(bally>=495 && bally<=500){
    			console.log("ball:"+ballx);
    		}
    		ballx += speedx;
    		bally += speedy;
    		io.emit('ballmove', {x:ballx,y:bally});
    	}
    	var int=setInterval(ballPos,50);
    })

    socket.on('test',function(){
        console.log('test received');
    });
});

function getAllPlayers(){
    var players = [];
    Object.keys(io.sockets.connected).forEach(function(socketID){
        var player = io.sockets.connected[socketID].player;
        if(player) players.push(player);
    });
    return players;
}

function randomInt (low, high) {
    return Math.floor(Math.random() * (high - low) + low);
}
