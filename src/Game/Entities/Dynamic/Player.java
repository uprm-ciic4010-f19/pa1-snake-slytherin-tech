package Game.Entities.Dynamic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import Game.GameStates.State;
import Main.Handler;

/**
 * Created by AlexVR on 7/2/2018.
 */
public class Player {

	public int lenght;
	public boolean justAte;
	private Handler handler;

	public int xCoord;
	public int yCoord;

	public int steps = 0;

	public int moveCounter;
	public int speed = 5; //initialize speed variable to manipulate the speed of the snake
	public double score = 0;

	public String direction;//is your first name one?

	public Player(Handler handler){
		this.handler = handler;
		xCoord = 30;
		yCoord = 30;
		moveCounter = 0;
		direction= "Right";
		justAte = false;
		lenght= 1;

	}

	public void tick(){
		moveCounter++;
		steps++;
		handler.getWorld().isGood();
		if(moveCounter >= speed ) {
			checkCollisionAndMove();
			moveCounter=0;
		}
		//Controls for the speed with Equals and Minus keys
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_EQUALS)){
			speed -= 1; 
			if (speed < 0) {
				speed = 0;
			}
		}

		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_MINUS)){
			speed += 1; 
			if ( speed > 15) {
				speed = 15;
			}

		}

		//Implemented WASD controls for actual gamers
		//Also made it so the snake can't go back on itself
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_W)&&direction!="Down"){
			direction="Up";
		}if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_S)&&direction!="Up"){
			direction="Down";
		}if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_A)&&direction!="Right"){
			direction="Left";
		}if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_D)&&direction!="Left"){
			direction="Right";
		}

		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_UP)&&direction!="Down"){
			direction="Up";
		}if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_DOWN)&&direction!="Up"){
			direction="Down";
		}if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_LEFT)&&direction!="Right"){
			direction="Left";
		}if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_RIGHT)&&direction!="Left"){
			direction="Right";
		}if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_N)){
			Eat();
			handler.getWorld().appleOnBoard=true; //This is to make it so another apple doesn't spawn in when N is pressed
		}
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_ESCAPE)){
			State.setState(handler.getGame().pauseState); //Pauses game whenever the Escape key is pressed
		}
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_R)){
			State.setState(handler.getGame().menuState); //Resets the game (starts from the main menu)
		}
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_BACK_SPACE)){
			handler.getGame().reStart(); //Restarts the game (from the moment the start button is pressed)
		}
	}

	public void checkCollisionAndMove(){
		handler.getWorld().playerLocation[xCoord][yCoord]=false;
		int x = xCoord;
		int y = yCoord;
		switch (direction){
		case "Left":
			if(xCoord==0){
				xCoord = handler.getWorld().GridWidthHeightPixelCount-1;
			}else{
				xCoord--;
			}
			break;
		case "Right":
			if(xCoord==handler.getWorld().GridWidthHeightPixelCount-1){
				xCoord = 0;
			}else{
				xCoord++;
			}
			break;
		case "Up":
			if(yCoord==0){
				yCoord = handler.getWorld().GridWidthHeightPixelCount-1 ;
			}else{
				yCoord--;
			}
			break;
		case "Down":
			if(yCoord==handler.getWorld().GridWidthHeightPixelCount-1){
				yCoord = 0;
			}else{
				yCoord++;
			}
			break;
		}
		handler.getWorld().playerLocation[xCoord][yCoord]=true;

		if(handler.getWorld().appleLocation[xCoord][yCoord]){
			Eat();
			score += Math.sqrt(2*score+1);
		} //Made it so the player only gets points when they actually eat an apple, so as to prevent cheating with N key

		if(!handler.getWorld().body.isEmpty()) {
			handler.getWorld().playerLocation[handler.getWorld().body.getLast().x][handler.getWorld().body.getLast().y] = false;
			handler.getWorld().body.removeLast();
			handler.getWorld().body.addFirst(new Tail(x, y,handler));
		}

		for (int i = 0; i < handler.getWorld().body.size(); i++) {
			if(xCoord == handler.getWorld().body.get(i).x &&
					yCoord == handler.getWorld().body.get(i).y) {
				State.setState(handler.getGame().gameOverState); //Implemented the game over state
			}
		}

	}

	public void render(Graphics g,Boolean[][] playeLocation){
		Color Green = new Color (0,128,0); //Initialized color variables for the snake and the apple, respectively
		Color Red = new Color(250,0,0);
		for (int i = 0; i < handler.getWorld().GridWidthHeightPixelCount; i++) {
			for (int j = 0; j < handler.getWorld().GridWidthHeightPixelCount; j++) {
				g.setColor(Green);

				if(playeLocation[i][j]){
					g.fillRect((i*handler.getWorld().GridPixelsize),
							(j*handler.getWorld().GridPixelsize),
							handler.getWorld().GridPixelsize,
							handler.getWorld().GridPixelsize);
				}
				if(handler.getWorld().appleLocation[i][j]){
					if(handler.getWorld().isRotten) {
						g.setColor(Green);
					}
					else {
						g.setColor(Red);
					}

					g.fillRect((i*handler.getWorld().GridPixelsize),
							(j*handler.getWorld().GridPixelsize),
							handler.getWorld().GridPixelsize,
							handler.getWorld().GridPixelsize);
					g.setColor(new Color(250,250,250));
					g.setFont(new Font("OCR A Extended",Font.BOLD,20));
					DecimalFormat scoreRounding = new DecimalFormat("#.000");
					g.drawString("Score: "+scoreRounding.format(score), 10, 30);
					//Implemented score display with .drawString() method
					//Changed the score font to be more game-y and made it so only three decimal places are displayed
				}
			} 
		}
	}

	public void Eat(){
		lenght++;
		speed =-5; //Made it so the speed changes by the Student ID of Alanis + 1, as per the specs
		Tail tail= null; //If the speed is too fast, it can be changed to "--" to make it more manageable
		handler.getWorld().appleLocation[xCoord][yCoord]=false;
		handler.getWorld().appleOnBoard=false;
		switch (direction){
		case "Left":
			if( handler.getWorld().body.isEmpty()){
				if(this.xCoord!=handler.getWorld().GridWidthHeightPixelCount-1){
					tail = new Tail(this.xCoord+1,this.yCoord,handler);
				}else{
					if(this.yCoord!=0){
						tail = new Tail(this.xCoord,this.yCoord-1,handler);
					}else{
						tail =new Tail(this.xCoord,this.yCoord+1,handler);
					}
				}
			}else{
				if(handler.getWorld().body.getLast().x!=handler.getWorld().GridWidthHeightPixelCount-1){
					tail=new Tail(handler.getWorld().body.getLast().x+1,this.yCoord,handler);
				}else{
					if(handler.getWorld().body.getLast().y!=0){
						tail=new Tail(handler.getWorld().body.getLast().x,this.yCoord-1,handler);
					}else{
						tail=new Tail(handler.getWorld().body.getLast().x,this.yCoord+1,handler);

					}
				}

			}
			break;
		case "Right":
			if( handler.getWorld().body.isEmpty()){
				if(this.xCoord!=0){
					tail=new Tail(this.xCoord-1,this.yCoord,handler);
				}else{
					if(this.yCoord!=0){
						tail=new Tail(this.xCoord,this.yCoord-1,handler);
					}else{
						tail=new Tail(this.xCoord,this.yCoord+1,handler);
					}
				}
			}else{
				if(handler.getWorld().body.getLast().x!=0){
					tail=(new Tail(handler.getWorld().body.getLast().x-1,this.yCoord,handler));
				}else{
					if(handler.getWorld().body.getLast().y!=0){
						tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord-1,handler));
					}else{
						tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord+1,handler));
					}
				}

			}
			break;
		case "Up":
			if( handler.getWorld().body.isEmpty()){
				if(this.yCoord!=handler.getWorld().GridWidthHeightPixelCount-1){
					tail=(new Tail(this.xCoord,this.yCoord+1,handler));
				}else{
					if(this.xCoord!=0){
						tail=(new Tail(this.xCoord-1,this.yCoord,handler));
					}else{
						tail=(new Tail(this.xCoord+1,this.yCoord,handler));
					}
				}
			}else{
				if(handler.getWorld().body.getLast().y!=handler.getWorld().GridWidthHeightPixelCount-1){
					tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord+1,handler));
				}else{
					if(handler.getWorld().body.getLast().x!=0){
						tail=(new Tail(handler.getWorld().body.getLast().x-1,this.yCoord,handler));
					}else{
						tail=(new Tail(handler.getWorld().body.getLast().x+1,this.yCoord,handler));
					}
				}

			}
			break;
		case "Down":
			if( handler.getWorld().body.isEmpty()){
				if(this.yCoord!=0){
					tail=(new Tail(this.xCoord,this.yCoord-1,handler));
				}else{
					if(this.xCoord!=0){
						tail=(new Tail(this.xCoord-1,this.yCoord,handler));
					}else{
						tail=(new Tail(this.xCoord+1,this.yCoord,handler));
					} System.out.println("Tu biscochito");
				}
			}else{
				if(handler.getWorld().body.getLast().y!=0){
					tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord-1,handler));
				}else{
					if(handler.getWorld().body.getLast().x!=0){
						tail=(new Tail(handler.getWorld().body.getLast().x-1,this.yCoord,handler));
					}else{
						tail=(new Tail(handler.getWorld().body.getLast().x+1,this.yCoord,handler));
					}
				}

			}
			break;
		}
		if(handler.getWorld().isRotten) {
			score -= Math.sqrt(2*score+1);

			if(handler.getWorld().body.size()>1) {
				handler.getWorld().body.removeLast();
				kill();
			}
			else {
				State.setState(handler.getGame().gameOverState);
			}

		}
		else {
			handler.getWorld().body.addLast(tail);
			handler.getWorld().playerLocation[tail.x][tail.y] = true;
		}

	}

	public void kill(){
		lenght = 0;
		for (int i = 0; i < handler.getWorld().GridWidthHeightPixelCount; i++) {
			for (int j = 0; j < handler.getWorld().GridWidthHeightPixelCount; j++) {

				handler.getWorld().playerLocation[i][j]=false;

			}
		}
	}

	public boolean getJustAte() {
		return justAte;
	}

	public void setJustAte(boolean justAte) {
		this.justAte = justAte;
	}
}
