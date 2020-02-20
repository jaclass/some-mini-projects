/*
 * Author: Jerome Renaux
 * E-mail: jerome.renaux@gmail.com
 */

var Game = {};

var ball;
var paddle;
var bricks;
var player1;
var player2;
var ballOnPaddle = true;

var score = 0;

var scoreText;
var livesText;
var introText;
var scoreText;
var s;
Game.playerMap = [];
Game.init = function(){
    game.stage.disableVisibilityChange = true;
};

Game.preload = function() {
	game.load.image('paddle', 'assets/image/paddle.png');
    game.load.image('ball', 'assets/image/ball.png');
    game.load.image('starfield', 'assets/image/starfield.jpg');
};

Game.create = function(){
	game.physics.startSystem(Phaser.Physics.ARCADE);

    //  We check bounds collisions against all walls other than the bottom one
    game.physics.arcade.checkCollision.down = false;
    game.physics.arcade.checkCollision.up = false;
    s = game.add.tileSprite(0, 0, 800, 600, 'starfield');
    
    
    player1 = game.add.sprite(400, 500, 'paddle');
    player1.anchor.setTo(0.5, 0.5);
    game.physics.enable(player1, Phaser.Physics.ARCADE);
    player1.body.collideWorldBounds = true;
    player1.body.bounce.set(1);
    player1.body.immovable = true;
    
    
    player2 = game.add.sprite(400, 100, 'paddle');
    player2.anchor.setTo(0.5, 0.5);
    game.physics.enable(player2, Phaser.Physics.ARCADE);
    player2.body.collideWorldBounds = true;
    player2.body.bounce.set(1);
    player2.body.immovable = true;
    
    ball = game.add.sprite(400, 300, 'ball');
    ball.anchor.set(0.5);
    ball.checkWorldBounds = true;
    game.physics.enable(ball, Phaser.Physics.ARCADE);


    ball.body.collideWorldBounds = true;
    ball.body.bounce.set(1);
    
    
    Client.askNewPlayer();
    directions = this.input.keyboard.createCursorKeys();
};

Game.update = function(){
	if(directions.right.isDown){
		Client.sendKey(5);
	}
	
	if(directions.left.isDown){
		Client.sendKey(-5);
	}

	//game.physics.arcade.collide(ball, player1, ballHitPaddle, null, this);
	//game.physics.arcade.collide(ball, player2, ballHitPaddle, null, this);
	
	// Client.sendClick(pointer.worldX,pointer.worldY);
};

function ballHitPaddle (_ball, _paddle) {return;};

Game.addNewPlayer = function(id){
	console.log(Game.playerMap.length);
	console.log(id);
	if(id==1){
		Game.playerMap[id] = player1;
	}else if(id==2){
		Game.playerMap[id] = player2;
		Client.usersReady();
		//var vx = game.rnd.integerInRange(30, 80)
	    //ball.body.velocity.set(vx, 80);
	}else{
		console.log("No more position!");
		return;
	}
	
};

Game.movePlayer = function(id, dis){
    var player = Game.playerMap[id];
    player.x += dis;
    //console.log(player.x);
};

Game.removePlayer = function(id){
    Game.playerMap[id].destroy();
    delete Game.playerMap[id];
};

Game.moveBall = function(data){
	/*if(data.y==100 && data.y==500){
		console.log("ball:"+data.x);
	}*/
	ball.x = data.x;
	ball.y = data.y;
};