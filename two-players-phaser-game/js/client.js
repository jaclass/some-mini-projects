/**
 * Created by Jerome on 03-03-17.
 */

var Client = {};
Client.socket = io.connect();

Client.sendTest = function(){
    console.log("test sent");
    Client.socket.emit('test');
};

Client.askNewPlayer = function(){
    Client.socket.emit('newplayer');
};
/*
Client.sendClick = function(x,y){
  Client.socket.emit('click',{x:x,y:y});
};*/

Client.sendKey = function(dis){
	Client.socket.emit('keyPress',dis);
}

Client.usersReady = function(){
	Client.socket.emit('usersReady');
}

Client.socket.on('newplayer',function(data){
    Game.addNewPlayer(data.id);
});

Client.socket.on('ballmove',function(data){
    Game.moveBall(data);
    //console.log(data);
});

Client.socket.on('allplayers',function(data){
    for(var i = 0; i < data.length; i++){
        Game.addNewPlayer(data[i].id);
    }

    Client.socket.on('move',function(data){
        Game.movePlayer(data.id,data.dis);
    });

    Client.socket.on('remove',function(id){
        Game.removePlayer(id);
    });
});


