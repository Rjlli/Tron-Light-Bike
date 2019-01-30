/*Light_Bike
  Ricky Li
  
  This game will be able to play tron.There are 3 classes in this, they
  are main Light Bike, GamePanel, and Bike. There wll be a special ability
  called speed boost that speeds up the bike. There will be 2 power ups
  that are shield and invincibility. There is also a screen that will
  tell you the controls.
*/
/*things to do
 - levels
 - score
 - keep high score using text file
 - add picture of light bike
 */

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.sound.sampled.AudioSystem;


//Main clas that controls main components
public class Light_Bike extends JFrame implements ActionListener{
	
	javax.swing.Timer myTimer;   
	GamePanel game;
	AudioClip song;
	
	public Light_Bike(){
		super("Classical Tron");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900,625);
		myTimer = new javax.swing.Timer(15, this);
		game = new GamePanel(this);
		add(game);
		song = Applet.newAudioClip(getClass().getResource("Action_Song.wav"));
		song.loop();
		setResizable(false);
		setVisible(true);
	}
	
	public void start(){
		myTimer.start();
	}
	
	public void actionPerformed(ActionEvent evt){
		game.repaint();
		game.move();
	}

    public static void main(String[] arguments) {
		Light_Bike frame = new Light_Bike();		
    }
	
}

//GamePanel class
class GamePanel extends JPanel implements KeyListener{
	private Image background;
	private Image main;
	private Image arrows;
	private boolean []keys;
	private Light_Bike mainFrame;
	private String current_screen = "main";
	private static Random rand = new Random();
	
	//This will see which options they are on 
	//in the game over screen
	private boolean cont = true;
	
	//This will see which options they are on
	//in the start menu
	private boolean start = true;
	
	//This will make sure that it does not
	//start the game right when you exit the
	//game over screen
	private boolean ready_main = true;
	
	//This will allow power ups to pop up from time to time
	private int power_timer = 0;
	private boolean shield = false;
	private int sx,sy,ix,iy;
	private boolean invincible = false;
	//This will say when the invincible runs out
	private int invincible_timer1 = 0;
	private int invincible_timer2 = 0;
	
	//This will let them move after counter is 3
	private int counter1 = 0;
	private int counter2 = 0;
	
	//This will keep track for how long the special in on
	private int time1 = 0;
	private int time2 = 0;
	
	//Keeps tracks for how much wins
	private int p1_wins = 0;
	private int p2_wins = 0;
	
	//This will say who loss the round or draw
	private boolean p1_loss = false;
	private boolean p2_loss = false;
	
	Bike p1;
	Bike p2;
	
	public GamePanel(Light_Bike m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		
		background = new ImageIcon("GameBackground.png").getImage();
		background = background.getScaledInstance(900, 550, Image.SCALE_DEFAULT);
		
		main = new ImageIcon("light_bike_background.jpg").getImage();
		main = main.getScaledInstance(900, 625, Image.SCALE_DEFAULT);
		
		arrows = new ImageIcon("Arrow_Keys.jpg").getImage();
		
		mainFrame = m;
		
		p1 = new Bike(this,100,300,"right");
		p2 = new Bike(this,800,300,"left");
		
        addKeyListener(this);
	}
	
    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
	
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        //This will make sure it does not start
        //new game when holding enter to exit
        if(!cont){
        	ready_main = false;
        }
    }
    
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        //You are allowed to start game after letting
        //go of enter
    	ready_main = true;
    }
    
    //This will move the game
   	public void move(){
   		
   		//If the current screen is play, it will move the bike
   		//to the direction the player clicked. They cant go
   		//in the oposite direction
		if(current_screen.equals("play")){
			
			//This will randomly drop a power up and randomly drop shield or invicibility
			if(power_timer == 300){
				if(rand.nextInt(2) == 0){
					shield = true;
					while(true){
						sx =  rand.nextInt(900);
						sy =  rand.nextInt(550);
						
						//Makes sure you dont spawn it over the trail
						if(p1.power_collide(sx,sy) == false && p2.power_collide(sx,sy) == false){
							break;
						}
					}
				}
				else{
					invincible = true;
					while(true){
						ix =  rand.nextInt(900);
						iy =  rand.nextInt(550);
						//Makes sure you dont spawn it over the trail
						if(p1.power_collide(ix,iy) == false && p2.power_collide(ix,iy) == false){
							break;
						}
					}
				}
				power_timer = 0;	
			}
			power_timer+=1;
			
			//This will see if player one collected 
			//shield power up
			if(p1.collide(sx,sy)){
				p1.shield_on();
				shield = false;
			}
			
			//This will see if player one collected 
			//invincible power up
			if(p1.collide(ix,iy)){
				p1.invincible_on();
				invincible = false;
			}
			
			//This will see if player two collected 
			//shield power up
			if(p2.collide(sx,sy)){
				p2.shield_on();
				shield = false;
			}
			
			//This will see if player two collected 
			//invincible power up
			if(p2.collide(ix,iy)){
				p2.invincible_on();
				invincible = false;
			}
			
			//Player one can stay invincible for a 
			//certain time limit
			if(p1.get_invincible()){
				invincible_timer1 += 1;
				if(invincible_timer1 == 200){
					p1.invincible_off();
					invincible_timer1 = 0;
				}
			}
			
			//Player two can stay invincible for a 
			//certain time limit
			if(p2.get_invincible()){
				invincible_timer2 += 1;
				if(invincible_timer2 == 200){
					p2.invincible_off();
					invincible_timer2 = 0;
				}
			}
			
			//This will check if they can use boost or have any boost left
			if(keys[KeyEvent.VK_G]){
				if(p1.get_boost_left()>0 && p1.get_boost() == false){
					p1.go_boost();
					p1.set_boost_left(); 
				}
			}
			
			//This will check if they can use boost or have any boost left
			if(keys[KeyEvent.VK_NUMPAD0]){
				if(p2.get_boost_left()>0 && p2.get_boost() == false){
					p2.go_boost();
					p2.set_boost_left(); 
				}
			}
			
			
			//When the boost is true it will
			//allow them to use it for a 
			// certain amount of time
			if(p1.get_boost()){
				counter1 = 5;
				time1 += 1;
				if(time1 == 30){
					time1 = 0; 
					p1.stop_boost();
				}
			}
			
			//When the boost is true it will
			//allow them to use it for a 
			// certain amount of time
			if(p2.get_boost()){
				counter2 = 5;
				time2 += 1;
				if(time2 == 30){
					time2 = 0; 
					p2.stop_boost();
				}
			}
						
			if(keys[KeyEvent.VK_D]){
				if(!p1.getd().equals("left")){
					p1.setd("right");
				}
			}
			else if(keys[KeyEvent.VK_A]){
				if(!p1.getd().equals("right")){
					p1.setd("left");
				}
			}
			else if(keys[KeyEvent.VK_W]){
				if(!p1.getd().equals("down")){
					p1.setd("up");
				}
			}
			else if(keys[KeyEvent.VK_S]){
				if(!p1.getd().equals("up")){
					p1.setd("down");
				}
			}
			if(keys[KeyEvent.VK_RIGHT]){
				if(!p2.getd().equals("left")){
					p2.setd("right");
				}
			}
			else if(keys[KeyEvent.VK_LEFT] ){
				if(!p2.getd().equals("right")){
					p2.setd("left");
				}
			}
			else if(keys[KeyEvent.VK_UP] ){
				if(!p2.getd().equals("down")){
					p2.setd("up");
				}
			}
			else if(keys[KeyEvent.VK_DOWN] ){
				if(!p2.getd().equals("up")){
					p2.setd("down");
				}
			}
			if(counter1 == 5){
				if(p1.getd().equals("right")){
					p1.setx(5);
				}
				else if(p1.getd().equals("left")){
					p1.setx(-5);
				}
				else if(p1.getd().equals("up")){
					p1.sety(-5);
				}
				else if(p1.getd().equals("down")){
					p1.sety(5);
				}
			}
			if(counter2 == 5){
				if(p2.getd().equals("right")){
					p2.setx(5);
				}
				else if(p2.getd().equals("left")){
					p2.setx(-5);
				}
				else if(p2.getd().equals("up")){
					p2.sety(-5);
				}
				else if(p2.getd().equals("down")){
					p2.sety(5);
				}
			}

			if(p1.hit_wall()){
				current_screen = "end";
				p1_loss = true;
			}
			
			if(p2.hit_wall()){
				current_screen = "end";
				p2_loss = true;
			}
			
			//This will check if the player collides into the trails
			for(int i=0; i<p1.trail_length(); i++){
				if(counter1 == 5){
					
					//Can't get hit if you are invincible
					if(!p1.get_invincible()){
						if(p1.collide(p1.get_trailx(i),p1.get_traily(i))){
					
							//Block a hit if you go over a trail
							if(!p1.get_shield()){
								current_screen = "end";
								p1_loss = true;
							}
							else{
								p1.shield_off();
							}
						}
					}
					
					//Can't get hit if you are invincible
					if(!p2.get_invincible()){
						if(p2.collide(p1.get_trailx(i),p1.get_traily(i))){
							
							//Block a hit if you go over a trail
							if(!p2.get_shield()){
								current_screen = "end";
								p2_loss = true;
							}
							else{
								p2.shield_off();
							}
						}
					}
				}
			}
			
			//This will check if the player collides into the trails
			for(int i=0; i<p2.trail_length(); i++){
				if(counter2 == 5){
					
					//Can't get hit if you are invincible
					if(!p1.get_invincible()){
						if(p1.collide(p2.get_trailx(i),p2.get_traily(i))){
							
							//Block a hit if you go over a trail
							if(!p1.get_shield()){
								current_screen = "end";
								p1_loss = true;
							}
							else{
								p1.shield_off();
							}
						}
					}
					
					//Can't get hit if you are invincible
					if(!p2.get_invincible()){
						if(p2.collide(p2.get_trailx(i),p2.get_traily(i))){
							
							//Block a hit if you go over a trail
							if(!p2.get_shield()){
								current_screen = "end";
								p2_loss = true;
							}
							else{
								p2.shield_off();
							}
						}
					}
				}
			}
			
			//They will add the trail when moving
			if(counter1 == 5){
				counter1 = 0;
				p1.add_trailx(p1.getx());
				p1.add_traily(p1.gety());
			}
			counter1 += 1;

			//They will add the trail when moving
			if(counter2 == 5){
				counter2 = 0;
				p2.add_trailx(p2.getx());
				p2.add_traily(p2.gety());

			}
			counter2 += 1;
		}
		
		//This is when it is the game over screen
		else if(current_screen.equals("end")){
			
			//This will show which option they are on
			if(keys[KeyEvent.VK_UP]){
				cont = true;
			}
			if(keys[KeyEvent.VK_DOWN]){
				cont = false;
			}
			
			//This will check if the choose that option
			if(cont && keys[KeyEvent.VK_ENTER]){
				//reset the game
				current_screen = "play";
				p1 = new Bike(this,100,300,"right");
				p2 = new Bike(this,800,300,"left");
				shield = false;
				invincible = false;
				p1.shield_off();
				p1.invincible_off();
				p2.shield_off();
				p2.invincible_off();
			}
			else if(!cont && keys[KeyEvent.VK_ENTER]){
				//reset the game
				current_screen = "main";
				p1 = new Bike(this,100,300,"right");
				p2 = new Bike(this,800,300,"left");
				shield = false;
				invincible = false;
				p1.shield_off();
				p1.invincible_off();
				p2.shield_off();
				p2.invincible_off();
				p1_wins = 0;
				p2_wins = 0;
				cont = true;
			}
		}
		
		//This is the main menu screen
		else if(current_screen.equals("main")){
			
			//This will show which option they are on
			if(keys[KeyEvent.VK_UP]){
				start = true;
			}
			if(keys[KeyEvent.VK_DOWN]){
				start = false;
			}
			
			//This will see if they choose the option
			if(start && keys[KeyEvent.VK_ENTER] && ready_main == true){
				current_screen = "play";
			}
			else if(!start && keys[KeyEvent.VK_ENTER]){
				current_screen = "howtoplay";
				start = true;
			}
		}
		
		//This will let you go back to the main
		else if(current_screen.equals("howtoplay")){
			if(keys[KeyEvent.VK_BACK_SPACE]){
				current_screen = "main";
			}
		}
	}
    
    public void paintComponent(Graphics g){
    	
    	//This is for other graphics such as text and renders them
    	Graphics2D g2 = (Graphics2D) g;
    	RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    	
    	//When someone loses it shows this screen
    	if(current_screen.equals("end")){
    		g.setColor(Color.green);
    		g.fillRect(350,200,200,200);
	        g2.setColor(Color.black);
    		g2.setFont(new Font("Footlight MT Light", Font.PLAIN, 40));
    		
    		g2.drawString("AGAIN?", 385, 280);
    		g2.drawString("EXIT", 415, 350);
    		
    		if(p1_loss == true && p2_loss == true){
    			p1_loss = false;
    			p2_loss = false;
    		}
    		else if(p1_loss){
    			p2_wins += 1;
    			p1_loss = false;
    		}
    		else if(p2_loss){
    			p1_wins += 1;
    			p2_loss = false;
    		}
    		
    		if(p1_wins == 3){
    			g2.drawString("P1 Win", 385, 315);
    		}
    		if(p2_wins == 3){
    			g2.drawString("P2 Win", 385, 315);
    		}
    		
    		//This is for showing which is your current option
    		//to continue or exit to main menu 
    		if(cont){
    			g.setColor(Color.red);
    			g.fillRect(370,260,10,10);
    			g.fillRect(525,260,10,10);	
    		}
    		else{
    			g.setColor(Color.red);
    			g.fillRect(390,330,10,10);
    			g.fillRect(500,330,10,10);	
    		}
    	}
    	
    	//When they are playing it draws this screen
    	else if(current_screen.equals("play")){
	    	g.drawImage(background,0,0,this);
	    	
	    	//draws a little section on the bottom of the screen
	    	g.setColor(Color.white);
	    	g.fillRect(0,550,900,75);
	        g2.setColor(Color.black);
    		g2.setFont(new Font("Footlight MT Light", Font.PLAIN, 40));
    		g2.drawString("Boost Left", 20, 590);
    		if(p1.get_boost_left() == 3){
    			g2.drawString("3", 200, 590);
    		}
     		else if(p1.get_boost_left() == 2){
    			g2.drawString("2", 200, 590);
    		}
     		else if(p1.get_boost_left() == 1){
    			g2.drawString("1", 200, 590);
    		}
     		else if(p1.get_boost_left() == 0){
    			g2.drawString("0", 200, 590);
    		}
    		g2.drawString("Boost Left", 710, 590);
    		if(p2.get_boost_left() == 3){
    			g2.drawString("3", 670, 590);
    		}
     		else if(p2.get_boost_left() == 2){
    			g2.drawString("2", 670, 590);
    		}
     		else if(p2.get_boost_left() == 1){
    			g2.drawString("1", 670, 590);
    		}
     		else if(p2.get_boost_left() == 0){
    			g2.drawString("0", 670, 590);
    		}
    		
    		//Shows how many wins you have
    		if(p1_wins == 0){
    			g.setColor(Color.black);
    			g.fillRect(250,555,40,40);
    			g.fillRect(300,555,40,40);
    			g.fillRect(350,555,40,40);
    		}
    		else if(p1_wins == 1){
    			g.setColor(Color.red);
    			g.fillRect(250,555,40,40);
    			g.setColor(Color.black);
    			g.fillRect(300,555,40,40);
    			g.fillRect(350,555,40,40);
    		}
    		else if(p1_wins == 2){
    			g.setColor(Color.red);
    			g.fillRect(250,555,40,40);
    			g.fillRect(300,555,40,40);
    			g.setColor(Color.black);
    			g.fillRect(350,555,40,40);
    		}
    		else if(p1_wins == 3){
    			g.setColor(Color.red);
    			g.fillRect(250,555,40,40);
    			g.fillRect(300,555,40,40);
    			g.fillRect(350,555,40,40);
    			p1_wins = 0;
    			p2_wins = 0;
    		}
    		
    		if(p2_wins == 0){
    			g.setColor(Color.black);
    			g.fillRect(620,555,40,40);
    			g.fillRect(570,555,40,40);
    			g.fillRect(520,555,40,40);
    		}
    		else if(p2_wins == 1){
    			g.setColor(Color.red);
    			g.fillRect(620,555,40,40);
    			g.setColor(Color.black);
    			g.fillRect(570,555,40,40);
    			g.fillRect(520,555,40,40);
    		}
    		else if(p2_wins == 2){
    			g.setColor(Color.red);
    			g.fillRect(620,555,40,40);
    			g.fillRect(570,555,40,40);
    			g.setColor(Color.black);
    			g.fillRect(520,555,40,40);
    		}
    		else if(p2_wins == 3){
    			g.setColor(Color.red);
    			g.fillRect(620,555,40,40);
    			g.fillRect(570,555,40,40);
    			g.fillRect(520,555,40,40);
    			p1_wins = 0;
				p2_wins = 0;
    		}
    		
			g.setColor(Color.red);
			//Draws the trail for player 1
			for(int i=0; i<p1.trail_length(); i++){
				g.fillRect(p1.get_trailx(i),p1.get_traily(i),5,5);
			}
			g.fillRect(p1.getx(),p1.gety(),5,5);
			g.setColor(Color.gray);
			
			//Draws the trail for player 2
			for(int i=0; i<p2.trail_length(); i++){
				g.fillRect(p2.get_trailx(i),p2.get_traily(i),5,5);
			}
			g.fillRect(p2.getx(),p2.gety(),5,5);
			
			//Draws the shield power up on the screen
			if(shield){
				g.setColor(Color.yellow);
				g.fillRect(sx,sy,5,5);
			}
			
			//Draws the invincible power up on the screen
			if(invincible){
				g.setColor(Color.green);
				g.fillRect(ix,iy,5,5);
			}
    	}
    	
    	//This draws the start screen
    	else if(current_screen.equals("main")){
	        g2.setRenderingHints(rh);
	        g2.drawImage(main,0,0,this);
	        g2.setColor(Color.black);
    		g2.setFont(new Font("Footlight MT Light", Font.PLAIN, 40));
    		g2.drawString("Start", 405, 480);
    		g2.drawString("How To Play", 350, 550);
    		
    		//This is for showing which is your current option
    		//to play or how to play to main menu 
    		if(start){
    			g.setColor(Color.red);
    			g.fillRect(380,465,10,10);
    			g.fillRect(495,465,10,10);	
    		}
    		else{
    			g.setColor(Color.red);
    			g.fillRect(335,540,10,10);
    			g.fillRect(560,540,10,10);	
    		}
    	}
    	
    	//This draws the screen for instructions on how to play
    	else if(current_screen.equals("howtoplay")){
    		g.setColor(new Color(8,188,217));
    		g.fillRect(0,0,900,600);
	        g2.setColor(Color.black);
    		g2.setFont(new Font("Footlight MT Light", Font.PLAIN, 40));
    		g2.drawString("Move", 330, 80);
    		g2.drawString("Speed Boost", 510, 80);
    		g2.drawString("Player One", 50, 200);
    		g2.drawString("w", 370, 180);
    		g2.drawString("a", 330, 220);
    		g2.drawString("s", 370, 220);
    		g2.drawString("d", 410, 220);
    		g2.drawString("g", 600, 220);
    		g2.drawString("Player Two", 50, 400);
    		g2.drawImage(arrows,310,370,this);
    		g2.drawString("num pad 0", 510, 420);
    		g2.drawString("BackSpace", 0, 40);
    		g2.drawString("Power Ups", 50, 520);
    		g2.drawString("Shield", 310, 520);
    		g2.drawString("Invincible", 610, 520);
    		g.setColor(Color.yellow);
    		g.fillRect(300,510,5,5);
    		g.setColor(Color.green);
    		g.fillRect(600,510,5,5);
    	}
    }

}

//This is the bike class
class Bike{
	private ArrayList<Integer> trailx = new ArrayList<Integer>();
	private ArrayList<Integer> traily = new ArrayList<Integer>();
	private int bikex,bikey;
	private String direction;
	private boolean boost = false;
	private boolean ready_boost = true;
	private boolean shield = false;
	private boolean invincible = false;
	private int boost_left = 3;
	
	public Bike(GamePanel m, int x, int y, String d){
		bikex = x;
        bikey = y;
        direction = d;
        trailx.add(x);
        traily.add(y);
	}
	
	//This will check if the power can be placed there with out overlapping
	public boolean power_collide(int x, int y){
		boolean overlap = false;
		for(int i = 0; i<trailx.size(); i++){
			if(trailx.get(i)<x+5 && trailx.get(i)+5>x && traily.get(i)<y+5 && traily.get(i)+5>y){
				overlap = true;
				break;
			}	
		}
		return overlap;
	}
	
	//This will check if the current player hit one of the trails
	public boolean collide(int x, int y){
		return bikex<x+5 && bikex+5>x && bikey<y+5 && bikey+5>y;
	}
	
	//This will make sure you don't go off the screen
	public boolean hit_wall(){
		return bikex<0 || bikex+5>=900 || bikey<0 || bikey+5>=553;
	}
	
	public void shield_on(){
		shield = true;
	}
	
	public void shield_off(){
		shield = false;
	}
	
	public void invincible_on(){
		invincible = true;
	}
	
	public void invincible_off(){
		invincible = false;
	}
	
	public void go_boost(){
		boost = true;
	}
	
	public void stop_boost(){
		boost = false;
	}
	
	public void set_boost_left(){
		boost_left-=1;
	}
	
	public void add_trailx(int x){
		trailx.add(x);
	}
	
	public void add_traily(int y){
		traily.add(y);
	}
	
	public void setx(int x){
		bikex+=x;
	}
	
	public void sety(int y){
		bikey+=y;
	}

	public void setd(String d){
		direction = d;
	}
	
	public boolean get_shield(){
		return shield;
	}
	
	public boolean get_invincible(){
		return invincible;
	}
	
	public boolean get_boost(){
		return boost;
	}
	
	public int get_boost_left(){
		return boost_left;
	}
	
	public int get_trailx(int num){
		return trailx.get(num);
	}
	
	public int get_traily(int num){
		return traily.get(num);
	}
	
	public int trail_length(){
		return trailx.size();
	}
	
	public int getx(){
		return bikex;
	}
	
	public int gety(){
		return bikey;
	}

	public String getd(){
		return direction;
	}

}