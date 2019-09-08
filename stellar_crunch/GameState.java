import java.util.*;
import java.awt.*;

public class GameState {
    // Class representing the game state and implementing main game loop update step.

    //private static Collection<GameObject> objects;
	public static HashMap<Integer, GameObject> objectMap = new HashMap<Integer, GameObject>();
    public static HashMap<Integer, PlayerObject> playerMap = new HashMap<Integer, PlayerObject>();
	//private final PlayerObject player;
	
	private static boolean pressedEscape = false;
	
	private static boolean pressedSpace = false;
	
	public enum states{
		MENU,
		PLAYING,
		PAUSE,
		LEVELUP,
		WINNER,
		GAMEOVER,
	}
	private static states state = states.MENU;
	
	private static boolean initialised = false;
	
	private static final double borderAmount = 0.3;
	
	public static int playerKey;
	
	private static double minimapSize = 0.3;		//fraction of screen size (on the bottom left)
	
	private static int level = 1;
	public static int levelPos = 1;
	public static int maxLevel = 25;
	
	
	
    static void update(double deltaTime) {
		//System.out.println(deltaTime);
		
		//menu:
		switch (state){
			case MENU:
				initialised = false;
				level = 1;
				mainMenu();
				if (StdDraw.isKeyPressed(27) == true && pressedEscape == false){		//escape key is pressed:
					pressedEscape = true;
					System.exit(0);														//exits program
				}
				
				if (StdDraw.isKeyPressed(32) == true && pressedSpace == false){			//spacebar is pressed:
					pressedSpace = true;
					state = states.PLAYING;												//switches to playing
				}
			break;
			
			//game:
			case PLAYING:
				// Main game loop update step
				//System.out.println("GameState.update");
				
				if (initialised == false){
					GameObject.numberOfAsteroids = 0;
					objectMap.clear();
					playerMap.clear();
					initialised = true;
					initialise();
					
				}
				
				//Check if player has lost... or alive...				
				if (objectMap.containsKey(playerKey) == false || (playerMap.get(0).getMass() <= GameObjectLibrary.getPlayerMinMass())){
					//System.out.println("game over");
					state = states.GAMEOVER;
					pressedSpace = true;
				}
				
				//Or if the player has won...
				else if (objectMap.size() == 1){					//the else if here as opposed to a if is quite important... to prevent false wins...
					//System.out.println("winner winner chicken dinner");
					
					if (level < maxLevel){	//level up
						level++;
						levelPos++;
						state = states.LEVELUP;
					}
					else{					//or you finished the game entirely...
						levelPos++;
						state = states.WINNER;
					}
					pressedSpace = true;
				}
				
				
				StdDraw.clear(StdDraw.BLACK);
				
				playerMap.get(0).drawStats();
				displayHandler();
				StdDraw.show();
				playerMap.get(0).processCommand();
				GameStats.updateLive(deltaTime);
				updatePhysics(deltaTime);
				
				
				if (StdDraw.isKeyPressed(27) == true && pressedEscape == false){	//escape key is pressed:
					pressedEscape = true;
					state = states.PAUSE;											//pause menu
				}
				//restart game with r...
				if (StdDraw.isKeyPressed(82) == true && pressedEscape == false){	//r key is pressed:
					initialised = false;
					state = states.PLAYING;											//restart
				}
			break;
			
			//gameover:
			case GAMEOVER:
				StdDraw.clear(StdDraw.BLACK);
				topDownDisplay();
				//displayHandler();
				gameOverScreen();
				StdDraw.show();
				updatePhysics(deltaTime);
				
				if (StdDraw.isKeyPressed(32) == true && pressedSpace == false){		//spacebar is pressed:
					pressedSpace = true;
					initialised = false;
					state = states.PLAYING;											//switches to playing
				}
				
				if (StdDraw.isKeyPressed(27) == true && pressedEscape == false){	//escape key is pressed:
					pressedEscape = true;
					state = states.MENU;											//exits to menu
				}
				if (StdDraw.isKeyPressed(82) == true && pressedEscape == false){	//r key is pressed:
					initialised = false;
					state = states.PLAYING;											//restart
				}
			break;
			
			case LEVELUP:
				GameStats.updateHighscores();
				StdDraw.clear(StdDraw.BLACK);
				displayHandler();
				levelUpScreen();
				playerMap.get(0).processCommand();
				//updatePhysics(deltaTime);
				StdDraw.show();
				
				if (StdDraw.isKeyPressed(32) == true){								//spacebar is pressed:
					initialised = false;
					state = states.PLAYING;											//switches to playing
				}
				if (StdDraw.isKeyPressed(27) == true && pressedEscape == false){	//escape key is pressed:
					pressedEscape = true;
					state = states.MENU;											//exits to menu
				}
				if (StdDraw.isKeyPressed(82) == true && pressedEscape == false){	//r key is pressed:
					initialised = false;
					state = states.PLAYING;											//restart
				}
				
				
			break;
			
			//winner:
			case WINNER:
				GameStats.updateHighscores();
				StdDraw.clear(StdDraw.BLACK);
				displayHandler();
				winnerScreen();
				playerMap.get(0).processCommand();
				//updatePhysics(deltaTime);
				StdDraw.show();
				
				
				if (StdDraw.isKeyPressed(27) == true && pressedEscape == false){	//escape key is pressed:
					pressedEscape = true;
					state = states.MENU;											//exits to menu
				}
				if (StdDraw.isKeyPressed(82) == true && pressedEscape == false){	//r key is pressed:
					initialised = false;
					state = states.PLAYING;											//restart
				}
			break;
			
			//pause:
			case PAUSE:
				StdDraw.clear(StdDraw.BLACK);
				displayHandler();
				pauseScreen();
				StdDraw.show();
				
				if (StdDraw.isKeyPressed(32) == true && pressedSpace == false){		//space is pressed:
					state = states.PLAYING;											//switches to playing
					pressedSpace = true;
					playerMap.get(0).clicked = true;
				}
				if (StdDraw.isKeyPressed(27) == true && pressedEscape == false){	//escape is pressed:
					state = states.MENU;											//exits to menu
					pressedEscape = true;
				}
				if (StdDraw.isKeyPressed(82) == true && pressedEscape == false){	//r key is pressed:
					initialised = false;
					state = states.PLAYING;											//restart
				}
				
			break;
			
			//unknown
			default:
				System.out.println("Error entered unknown state...");
				System.exit(0);
			break;
		}
		
		
		
		if (StdDraw.isKeyPressed(27) == false && pressedEscape == true){	//reset oneshot
			pressedEscape = false;
		}
		if (StdDraw.isKeyPressed(32) == false && pressedSpace == true){	//reset oneshot
			pressedSpace = false;
		}
		
        
    }

	private static void initialise(){
		
		GameStats.resetLive();
		//spawn in planets
		//System.out.println("Game world initialising!");
		
		//get numer of ateroids to spawn:
		int number = levelHandler(level);
		//System.out.println("no: " + number);
		
		//Spawn and add each asteroid to the collection
		

		//Set<GameObject> gameSet = Collections.emptySet();
		
		for (int i = 0; i < number; i++)
		{
			//creating
			GameObject myObject = new GameObject();
			
			//apply planar rotation (if enabled)
			if (GameObjectLibrary.getPLANAR_ROTATION() == true){
				myObject.applyPlanarRotation();
			}
			
			//adding object to hashmap
			//System.out.println("adding gameobject:");
			objectMap.put(i, myObject);
			//System.out.println("finished adding gameobject: " + objectMap.get(i).getID());
		}
		//add the player
		PlayerObject player = new PlayerObject();
		playerKey = number + 1;
		objectMap.put(playerKey, player);
		playerMap.put(0, player);
		
		
		
	}
	
	private static void updatePhysics(double deltaTime){	//physics timestep
		//Player control
		
		
		int i = 0;
		do{
			if (objectMap.containsKey(i) == true){objectMap.get(i).calculate(deltaTime);}
			i ++;
		}while(i < GameObject.getNumberOfAsteroids());
		
		int i2 = 0;
		do{
			if (objectMap.containsKey(i2) == true){objectMap.get(i2).move();}
			i2 ++;
		}while(i2 < GameObject.getNumberOfAsteroids());
		
	}
	
	
	private static int levelHandler(int currLevel){
		int numberToBeSpawned = currLevel;
		
		
		return numberToBeSpawned;
	}
	
	
	public static int getLevel(){
		return level;
	}
	
	
	private static void displayHandler(){
		
		playerMap.get(0).renderPOV();
		//topDownDisplay();
		miniMap();
		drawCompass();
	}
	
	private static void topDownDisplay(){		//for when you die...
		//System.out.println("display()");
		double scale = StellarCrunch.getScale();
		
		double systemMass = 0;
		
		
		
		StdDraw.setScale(0, 1);
		//StdDraw.picture(0.5, 0.5, "galaxy.jpg");
		
		StdDraw.setScale(0, scale);
		
		//To correctly zoom:
		//Find the most extreme values:
		
		double maxx = scale;
		double minx = 0;
		double maxy = scale;
		double miny = 0;
		
		for (int i2 = 0; i2 < GameObject.getNumberOfAsteroids(); i2++){
			if (objectMap.containsKey(i2) == true){
				if (objectMap.get(i2).getXPos() > maxx){
					maxx = objectMap.get(i2).getXPos();
				}
				else if (objectMap.get(i2).getXPos() < minx){
					minx = objectMap.get(i2).getXPos();
				}
				if (objectMap.get(i2).getYPos() > maxy){
					maxy = objectMap.get(i2).getYPos();
				}
				else if (objectMap.get(i2).getYPos() < miny){
					miny = objectMap.get(i2).getYPos();
				}
			}
		}
		
		/* System.out.println("X: "+maxx);
		System.out.println("x: "+minx);
		System.out.println("Y: "+maxy);
		System.out.println("y: "+miny); */
		
		double zoomScale = Math.max((maxx-minx),(maxy-miny));
		
		StdDraw.setXscale(minx, (minx+zoomScale));
		StdDraw.setYscale(miny, (miny+zoomScale));
		
		
		double trans = Math.min(scale/zoomScale, 1);
		trans = 0.75;
		
		
		
		
		
		int i = 0;
		do{
			if (objectMap.containsKey(i) == true){
				//System.out.println(i);
				
				double size = objectMap.get(i).getSize();
				
				
				if (i == playerKey){
					//This is you:
					
					StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
					
					StdDraw.setPenRadius(0.0001);
					
					//X line:
					StdDraw.line(objectMap.get(i).getXPos(), scale, objectMap.get(i).getXPos(), 0);
					//Y line:
					StdDraw.line(scale, objectMap.get(i).getYPos(), 0, objectMap.get(i).getYPos());
					
					//FOV lines:
					double rFOV = 5E9;
					
					double xEnd = playerMap.get(0).getXPos() + rFOV*Math.cos(playerMap.get(0).aimDirection);
					double yEnd = playerMap.get(0).getYPos() + rFOV*Math.sin(playerMap.get(0).aimDirection);
					
					StdDraw.line(playerMap.get(0).getXPos(), playerMap.get(0).getYPos(), xEnd, yEnd);
				}
				
				if (objectMap.containsKey(playerKey) == true){
					if (size > objectMap.get(playerKey).getSize()){
						StdDraw.setPenColor(StdDraw.RED);
					}
					else{
						StdDraw.setPenColor(StdDraw.GREEN);
					}
				}
				else{
					StdDraw.setPenColor(StdDraw.RED);
				}
				double border = size + borderAmount*(Math.cbrt(GameObjectLibrary.getASTEROID_MIN_MASS()))*GameObjectLibrary.getASTEROID_SIZE_SCALE();
				if (objectMap.containsKey(playerKey)){
					//only draw border if player is alive:
					StdDraw.filledCircle(objectMap.get(i).getXPos(), objectMap.get(i).getYPos(), border);
				}
				
			
				border = size + 0.5*borderAmount*(Math.cbrt(GameObjectLibrary.getASTEROID_MIN_MASS()))*GameObjectLibrary.getASTEROID_SIZE_SCALE();
			
				StdDraw.setPenColor(StdDraw.GRAY);
				StdDraw.setPenRadius(0.0001);
				StdDraw.filledCircle(objectMap.get(i).getXPos(), objectMap.get(i).getYPos(), border*trans);
				
				StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
				StdDraw.setPenRadius(0.001);
				//StdDraw.circle(objectMap.get(i).getXPos(), objectMap.get(i).getYPos(), (border/scale)*zoomScale);
				
				
				StdDraw.setPenColor(objectMap.get(i).rgb);
				StdDraw.setPenRadius(0.00001);
				StdDraw.filledCircle(objectMap.get(i).getXPos(), objectMap.get(i).getYPos(), size*trans);
				if (i == playerKey){
					//StdDraw.text(objectMap.get(i).getXPos()*, objectMap.get(i).getYPos()+size+1e8, "this is you :)");
				}
			}
			i ++;
		}while(i < GameObject.getNumberOfAsteroids());
		StdDraw.setPenColor(StdDraw.GRAY);
		double xPlayer = playerMap.get(0).getXPos();
		double yPlayer = playerMap.get(0).getYPos();
		double rPlayer = playerMap.get(0).getSize();
		StdDraw.filledCircle(xPlayer, yPlayer, rPlayer*trans);
		
		int textSize = (int)(playerMap.get(0).getMass()/1E24);
		
		Font font = new Font("Comic Sans MS", Font.PLAIN, 10);
		StdDraw.setFont(font);
		StdDraw.text(xPlayer, yPlayer+1.5*rPlayer, "Rest In Pieces");
		
	}
	
	private static void miniMap(){
		//System.out.println("display()");
		double scale = StellarCrunch.getScale();
		
		double systemMass = 0;
		
		for (int i = 0; i<GameObject.getNumberOfAsteroids(); i++){
			if (objectMap.containsKey(i) == true){
				systemMass += objectMap.get(i).getMass();
			}
		}
		//System.out.println(systemMass);
		
		StdDraw.setScale(0, 1);
		//StdDraw.picture(0.5, 0.5, "galaxy.jpg");
		
		StdDraw.setScale(0, scale);
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		StdDraw.filledCircle(0.5*minimapSize*scale, 0.5*minimapSize*scale, 0.505*minimapSize*scale);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledCircle(0.5*minimapSize*scale, 0.5*minimapSize*scale, 0.5*minimapSize*scale);
		
		
		
		int i = 0;
		do{
			if (objectMap.containsKey(i) == true){
				//System.out.println(i);
				
				double size = objectMap.get(i).getSize();
				
				
				if (i == playerKey){
					//This is you:
					
					StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
					
					StdDraw.setPenRadius(0.0001);
					
					//X line:
					//StdDraw.line(objectMap.get(i).getXPos()*minimapSize, minimapSize*scale, objectMap.get(i).getXPos()*minimapSize, 0);
					//Y line:
					//StdDraw.line(minimapSize*scale, objectMap.get(i).getYPos()*minimapSize, 0, objectMap.get(i).getYPos()*minimapSize);
					
					//FOV lines:
					double rFOV = 5E9;
					
					double xEnd = playerMap.get(0).getXPos() + rFOV*Math.cos(playerMap.get(0).aimDirection);
					double yEnd = playerMap.get(0).getYPos() + rFOV*Math.sin(playerMap.get(0).aimDirection);
					
					//StdDraw.line(playerMap.get(0).getXPos()*minimapSize, playerMap.get(0).getYPos()*minimapSize, xEnd*minimapSize, yEnd*minimapSize);
				}
				
				double border = 0;
				
				//Get the color of the border:
				if (objectMap.containsKey(playerKey) == true){
					if (objectMap.get(i).getSize() <= playerMap.get(0).getSize()){	//It is smaller than you:
						
						double xPos = playerMap.get(0).getXPos();
						double yPos = playerMap.get(0).getYPos();
						
						double xVel= playerMap.get(0).getXVel();
						double yVel= playerMap.get(0).getYVel();
						
						double xPosB = objectMap.get(i).getXPos();
						double yPosB = objectMap.get(i).getYPos();
						double dx = (xPosB - xPos);
						double dy = (yPosB - yPos);
						double xVelB = objectMap.get(i).getXVel();
						double yVelB = objectMap.get(i).getYVel();
						
						double normTheta = Math.atan2(dy, dx);
						
						double aTheta;
						aTheta = Math.atan2(yVel, xVel);
						double bTheta;
						bTheta = Math.atan2(yVelB, xVelB);
						
						double aSpeed = Math.hypot(xVel, yVel);
						double bSpeed = Math.hypot(xVelB, yVelB);
						
						double aNormVel =  aSpeed*(Math.cos(aTheta-normTheta));
						double bNormVel =  bSpeed*(Math.cos(bTheta-normTheta));
						
						double shatterThresh = GameObjectLibrary.getShatterThreshold();
						
						if (aNormVel-bNormVel > shatterThresh){
							//Too fast:
							StdDraw.setPenColor(StdDraw.YELLOW);
						}
						else {
							//This is aaight:
							StdDraw.setPenColor(StdDraw.GREEN);
						}
					}
					else{																		//Bigger
						StdDraw.setPenColor(StdDraw.RED);
					}
					
					border = size*minimapSize + borderAmount*(Math.cbrt(GameObjectLibrary.getASTEROID_MIN_MASS()))*GameObjectLibrary.getASTEROID_SIZE_SCALE();
					
					if (miniMapShape(i, scale)){
						StdDraw.filledCircle(miniMapPos(i, 'x', minimapSize, scale), miniMapPos(i, 'y', minimapSize, scale), border);
					}
					else{
						drawMiniMapTriangles(i, minimapSize, scale);
					}
					
					
				}
				
			
				border = size*minimapSize + 0.5*borderAmount*(Math.cbrt(GameObjectLibrary.getASTEROID_MIN_MASS()))*GameObjectLibrary.getASTEROID_SIZE_SCALE();
			
				StdDraw.setPenColor(StdDraw.GRAY);
				StdDraw.setPenRadius(0.0001);
				if (miniMapShape(i, scale)){
					StdDraw.filledCircle(miniMapPos(i, 'x', minimapSize, scale), miniMapPos(i, 'y', minimapSize, scale), border);
				}
				
				
				StdDraw.setPenColor(objectMap.get(i).rgb);
				StdDraw.setPenRadius(0.00001);
				
				if (miniMapShape(i, scale)){
					StdDraw.filledCircle(miniMapPos(i, 'x', minimapSize, scale), miniMapPos(i, 'y', minimapSize, scale), size*minimapSize);
				}
				if (i == playerKey){
					//StdDraw.text(objectMap.get(i).getXPos()*minimapSize, objectMap.get(i).getYPos()*minimapSize+size+1e8, "this is you :)");
				}
			}
			i ++;
		}while(i < GameObject.getNumberOfAsteroids());
	}
	
	private static double miniMapPos(int i, char xORy, double minimapSize, double scale){
		//from cartesian
		//to polar
		//rotate polar by playerAngle
		//convert back to cartesian
		double playerX = playerMap.get(0).getXPos();
		double playerY = playerMap.get(0).getYPos();
		double playerDirection = playerMap.get(0).aimDirection;
		
		double xPosB = objectMap.get(i).getXPos();
		double yPosB = objectMap.get(i).getYPos();
		double dx = (xPosB - playerX);
		double dy = (yPosB - playerY);
		
		double r = Math.hypot(dx, dy);
		
		r = Math.min(r, 0.5*scale);
		
		double theta = Math.atan2(dy, dx) - playerDirection + Math.PI/2;
		
		double ans = 0;
		
		
		if (xORy == 'x'){
			ans = r*Math.cos(theta);
		}
		else if (xORy == 'y'){
			ans = r*Math.sin(theta);
		}
		else{
			System.out.println("Error: rotateAboutPlayer received invalid argument: "+xORy);
		}
		ans = ans*minimapSize + 0.5*minimapSize*scale;
		
		
		return ans;
	}
	
	private static boolean miniMapShape(int i, double scale){	//returns true if the circle is within the map size:
		boolean ans = false;
		
		double playerX = playerMap.get(0).getXPos();
		double playerY = playerMap.get(0).getYPos();
		double playerDirection = playerMap.get(0).aimDirection;
		
		double xPosB = objectMap.get(i).getXPos();
		double yPosB = objectMap.get(i).getYPos();
		double dx = (xPosB - playerX);
		double dy = (yPosB - playerY);
		
		double r = Math.hypot(dx, dy);
		
		if (r > 0.5*scale){
			ans = false;
		}
		else{
			ans = true;
		}
		
		return ans;
	}
	
	private static void drawMiniMapTriangles(int i, double minimapSize, double scale){
		
		double playerX = playerMap.get(0).getXPos();
		double playerY = playerMap.get(0).getYPos();
		double playerDirection = playerMap.get(0).aimDirection;
		
		double sizeB = objectMap.get(i).getSize();
		
		double xPosB = objectMap.get(i).getXPos();
		double yPosB = objectMap.get(i).getYPos();
		double dx = (xPosB - playerX);
		double dy = (yPosB - playerY);
		
		double r = Math.hypot(dx, dy);
		double rReal = (scale)/r;
		r = Math.min(r, 0.5*scale);
		
		double theta = Math.atan2(dy, dx) - playerDirection + Math.PI/2;
		double phi = Math.atan2(sizeB, r);
		
		double x1 = r*Math.cos(theta)*minimapSize + 0.5*minimapSize*scale;
		double x2 = 0.95*r*Math.cos(theta-phi)*minimapSize + 0.5*minimapSize*scale;
		double x3 = 0.95*r*Math.cos(theta+phi)*minimapSize + 0.5*minimapSize*scale;
		
		double y1 = r*Math.sin(theta)*minimapSize + 0.5*minimapSize*scale;
		double y2 = 0.95*r*Math.sin(theta-phi)*minimapSize + 0.5*minimapSize*scale;
		double y3 = 0.95*r*Math.sin(theta+phi)*minimapSize + 0.5*minimapSize*scale;
		
		
		double[] x = {x1, x2, x3};
		double[] y = {y1, y2, y3};
		
		StdDraw.filledPolygon(x, y);
		
		//StdDraw.filledCircle(x3, y3, 0.005*scale);
	}
	
	private static void drawCompass(){
		//System.out.println("display()");
		double scale = StellarCrunch.getScale();
		
		double systemMass = 0;
		/*
		for (int i = 0; i<GameObject.getNumberOfAsteroids(); i++){
			if (objectMap.containsKey(i) == true){
				systemMass += objectMap.get(i).getMass();
			}
		}
		*/
		//System.out.println(systemMass);
		
		//StdDraw.picture(0.5, 0.5, "galaxy.jpg");
		
		
		
		double width = 0.45;
		double height = 0.008;
		
		
		double x1 = 0.5-0.5*width;
		double x2 = 0.5+0.5*width;
		double y1 = 0.98-0.5*height;
		double y2 = 0.98+0.5*height;
		
		/* StdDraw.line(x1, y1, x2, y1);
		StdDraw.line(x1, y2, x2, y2);
		StdDraw.line(x1, y1, x1, y2);
		StdDraw.line(x2, y1, x2, y2); */
		
		StdDraw.setScale(0, scale);
		
		int i = 0;
		do{
			if (objectMap.containsKey(i) == true && i != playerKey){
				//System.out.println(i);
				
				double size = objectMap.get(i).getSize();
				
				double xPos = playerMap.get(0).getXPos();
				double yPos = playerMap.get(0).getYPos();
				
				double xVel= playerMap.get(0).getXVel();
				double yVel= playerMap.get(0).getYVel();
				
				double xPosB = objectMap.get(i).getXPos();
				double yPosB = objectMap.get(i).getYPos();
				double dx = (xPosB - xPos);
				double dy = (yPosB - yPos);
				double r = Math.hypot(dy, dx);
				
				//Get the color of the stripe:
				if (objectMap.containsKey(playerKey) == true){
					if (objectMap.get(i).getSize() <= playerMap.get(0).getSize()){	//It is smaller than you:
						
						
						double xVelB = objectMap.get(i).getXVel();
						double yVelB = objectMap.get(i).getYVel();
						
						double normTheta = Math.atan2(dy, dx);
						
						double aTheta;
						aTheta = Math.atan2(yVel, xVel);
						double bTheta;
						bTheta = Math.atan2(yVelB, xVelB);
						
						double aSpeed = Math.hypot(xVel, yVel);
						double bSpeed = Math.hypot(xVelB, yVelB);
						
						double aNormVel =  aSpeed*(Math.cos(aTheta-normTheta));
						double bNormVel =  bSpeed*(Math.cos(bTheta-normTheta));
						
						double shatterThresh = GameObjectLibrary.getShatterThreshold();
						
						if (aNormVel-bNormVel > shatterThresh){
							//Too fast:
							StdDraw.setPenColor(StdDraw.YELLOW);
						}
						else {
							//This is aaight:
							StdDraw.setPenColor(StdDraw.GREEN);
						}
					}
					else{																		//Bigger
						StdDraw.setPenColor(StdDraw.RED);
					}
					
					
					double yPos0 = ((y1+y2)/2)*scale;
					
					double thetaPlayer = playerMap.get(0).aimDirection;
					double deltaAngle = -(Math.atan2(dy, dx) - thetaPlayer);
					
					while (deltaAngle > 1*Math.PI){
						deltaAngle -= 2*Math.PI;
					}
					while (deltaAngle < -1*Math.PI){
						deltaAngle += 2*Math.PI;
					}
					
					double xPos0 = (deltaAngle/(Math.PI/2))*scale + 0.5*scale;
					
					double width0 = 0.1*width*scale*(Math.atan2(size, r));
					
					if (xPos0 <= (x1*scale -width0)){
						xPos0 = x1*scale +width0;
					}
					else if (xPos0 >= (x2*scale +width0)){
						xPos0 = x2*scale -width0;
					}
					
					StdDraw.filledRectangle(xPos0, yPos0, width0, 0.5*height*scale);
					
					double aiming = playerMap.get(0).aimDirection-Math.PI/2;
					
					while(aiming<-Math.PI/2){
						aiming += 2*Math.PI;
					}
					while(aiming>3*Math.PI/4){
						aiming -= 2*Math.PI;
					}
					
					double northPos = scale*(aiming)+0.5*scale;
					double northYPos = (0.98-2*height)*scale;
					
					double southPos = scale*(aiming+Math.PI)+0.5*scale;
					
					double eastPos = scale*(aiming+Math.PI/2)+0.5*scale;
					
					double westPos = scale*(aiming-Math.PI/2)+0.5*scale;
					
					StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
					Font font = new Font("Comic Sans MS", Font.PLAIN, 10);
					StdDraw.setFont(font);
					
					if ((northPos>x1*scale)&&(northPos<x2*scale)){
						StdDraw.text(northPos, northYPos, "N");
					}
					if ((southPos>x1*scale)&&(southPos<x2*scale)){
						StdDraw.text(southPos, northYPos, "S");
					}
					if ((eastPos>x1*scale)&&(eastPos<x2*scale)){
						StdDraw.text(eastPos, northYPos, "E");
					}
					if ((westPos>x1*scale)&&(westPos<x2*scale)){
						StdDraw.text(westPos, northYPos, "W");
					}
					
					double topDash = y1*scale;
					double bottomMajor = (y1-2*height)*scale;
					double bottomMinor = (y1-height)*scale;
					double bottomSubMinor = (y1-0.5*height)*scale;
					
					//Major
					for (int z = 0; z<12; z++){
						double dashPos = (aiming + z*((Math.PI*2)/12));
						
						while(dashPos<-Math.PI/2){
							dashPos += 2*Math.PI;
						}
						while(dashPos>3*Math.PI/4){
							dashPos -= 2*Math.PI;
						}
						dashPos *= scale;
						dashPos += 0.5*scale;
						
						if ((dashPos>x1*scale)&&(dashPos<x2*scale)){
							StdDraw.line(dashPos, topDash, dashPos, bottomMajor);
						}
						
					}
					//Minor
					for (int z = 0; z<36; z++){
						double dashPos = (aiming + z*((Math.PI*2)/36));
						
						while(dashPos<-Math.PI/2){
							dashPos += 2*Math.PI;
						}
						while(dashPos>3*Math.PI/4){
							dashPos -= 2*Math.PI;
						}
						dashPos *= scale;
						dashPos += 0.5*scale;
						
						if ((dashPos>x1*scale)&&(dashPos<x2*scale)){
							StdDraw.line(dashPos, topDash, dashPos, bottomMinor);
						}
						
					}
					//Subminor
					for (int z = 0; z<108; z++){
						double dashPos = (aiming + z*((Math.PI*2)/108));
						
						while(dashPos<-Math.PI/2){
							dashPos += 2*Math.PI;
						}
						while(dashPos>3*Math.PI/4){
							dashPos -= 2*Math.PI;
						}
						dashPos *= scale;
						dashPos += 0.5*scale;
						
						if ((dashPos>x1*scale)&&(dashPos<x2*scale)){
							StdDraw.line(dashPos, topDash, dashPos, bottomSubMinor);
						}
						
					}
					
					double xs[] = {0.5*scale, (0.5-0.5*height)*scale, (0.5+0.5*height)*scale};
					double ys[] = {bottomMajor, bottomMajor-(height*scale), bottomMajor-(height*scale)};
					
					StdDraw.filledPolygon(xs, ys);
					
				}
			}
			i ++;
		}while(i < GameObject.getNumberOfAsteroids());
		
		StdDraw.setScale(0, 1);
		StdDraw.setPenRadius(0.0015);
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		StdDraw.line(x1, y1, x2, y1);
		StdDraw.line(x1, y2, x2, y2);
		StdDraw.line(x1, y1, x1, y2);
		StdDraw.line(x2, y1, x2, y2);
		
		
	}
	
	
	
	
	private static void gameOverScreen(){
		StdDraw.setScale(0, 1);
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		Font font = new Font("Comic Sans MS", Font.PLAIN, 30);
		StdDraw.setFont(font);
		StdDraw.text(0.5, 0.6, "you died");
		font = new Font("Comic Sans MS", Font.PLAIN, 20);
		StdDraw.setFont(font);
		StdDraw.text(0.5, 0.4, "space or r to restart level");
		StdDraw.text(0.5, 0.3, "escape to exit to menu");
	}
	
	private static void levelUpScreen(){
		StdDraw.setScale(0, 1);
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		Font font = new Font("Comic Sans MS", Font.PLAIN, 30);
		StdDraw.setFont(font);
		String completedLevel = String.valueOf(level-1);
		StdDraw.text(0.5, 0.6, "you completed level "+completedLevel+"!");
		font = new Font("Comic Sans MS", Font.PLAIN, 20);
		StdDraw.setFont(font);
		StdDraw.text(0.5, 0.4, "space to go to the next level");
		StdDraw.text(0.5, 0.3, "escape exit to menu");
		StdDraw.text(0.5, 0.2, "r to restart level");
		
		if (GameStats.isHighscore()){
			StdDraw.text(0.5, 0.9, "new highscore!");
			String time = String.valueOf((int)(GameStats.timeForPrevLevel()/1000));
			StdDraw.text(0.5, 0.85, time + " seconds");
		}
	}
	
	
	private static void winnerScreen(){
		StdDraw.setScale(0, 1);
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		Font font = new Font("Comic Sans MS", Font.PLAIN, 30);
		StdDraw.setFont(font);
		StdDraw.text(0.5, 0.6, "you completed the game");
		font = new Font("Comic Sans MS", Font.PLAIN, 20);
		StdDraw.setFont(font);
		StdDraw.text(0.5, 0.3, "escape to exit to menu");
		StdDraw.text(0.5, 0.2, "r to restart level");
	}
	
	private static void pauseScreen(){
		
		StdDraw.setScale(0, 1);
		//StdDraw.picture(0.5, 0.5, "doge.jpg");
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		Font font = new Font("Comic Sans MS", Font.PLAIN, 30);
		StdDraw.setFont(font);
		StdDraw.text(0.5, 0.6, "game paused");
		font = new Font("Comic Sans MS", Font.PLAIN, 20);
		StdDraw.setFont(font);
		StdDraw.text(0.5, 0.4, "space to resume");
		StdDraw.text(0.5, 0.3, "escape to exit to menu");
		StdDraw.text(0.5, 0.2, "r to restart level");
	}
	
	private static void mainMenu(){
		StdDraw.enableDoubleBuffering();
		StdDraw.setScale(0, 1);
		//StdDraw.setCanvasSize(1024, 1024);
		
		//clear canvas to black
		StdDraw.clear(StdDraw.BLACK);
		
		StdDraw.setScale(0, 1);
		//StdDraw.picture(0.5, 0.5, "galaxy.jpg");
		
		StdDraw.setPenRadius(0.01);
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        //StdDraw.line(0.37, 0.60, 0.37, 0.30);
		//StdDraw.line(0.37, 0.60, 0.62, 0.45);
		//StdDraw.line(0.62, 0.45, 0.37, 0.30);
		
		double x1 = 0.2;
		double x2 = 0.8;
		double y1 = 0.8;
		double y2 = 0.25;
		
		StdDraw.line(x1, y1, x2, y1);
		StdDraw.line(x1, y2, x2, y2);
		StdDraw.line(x1, y1, x1, y2);
		StdDraw.line(x2, y1, x2, y2);
		
		//Instructions:
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		Font font = new Font("Comic Sans MS", Font.PLAIN, 20);		//require "import java.awt.*;"
        StdDraw.setFont(font);
		StdDraw.text(0.5, 0.75, "instructions");
		
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		font = new Font("Comic Sans MS", Font.PLAIN, 15);		//require "import java.awt.*;"
        StdDraw.setFont(font);
		StdDraw.text(0.5, 0.70, "you are an asteroid in an astroid belt");
		StdDraw.text(0.5, 0.65, "to win you must be the last asteroid remaining");
		StdDraw.text(0.5, 0.60, "you can absorb other smaller asteroids by slowly colliding with them");
		StdDraw.text(0.5, 0.55, "smaller asteroids are highlighted in green");
		StdDraw.text(0.5, 0.50, "bigger asteroids are highlighted in red");
		StdDraw.text(0.5, 0.45, "fast moving aseroids are highlighted in yellow");
		StdDraw.text(0.5, 0.35, "arrow keys to navigate and aim");
		StdDraw.text(0.5, 0.30, "spacebar to shoot part of your mass");
		
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		StdDraw.setPenRadius(0.05);
		//Draw Big play button in center of canvas
		double[] xcoords = { 0.37, 0.37, 0.62};
		double[] ycoords = { 0.60, 0.30, 0.45};
		//StdDraw.filledPolygon(xcoords, ycoords);
        
		//Background Title
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		font = new Font("Comic Sans MS", Font.PLAIN, 60);		//require "import java.awt.*;"
        StdDraw.setFont(font);
        //StdDraw.text(0.5, 0.8, "stellar crunch");
		
		//Title
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		font = new Font("Comic Sans MS", Font.PLAIN, 60);		//require "import java.awt.*;"
        StdDraw.setFont(font);
		StdDraw.text(0.5, 0.9, "stellar crunch");
		
		//Press spacebar to play text
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		font = new Font("Comic Sans MS", Font.PLAIN, 20);		//require "import java.awt.*;"
        StdDraw.setFont(font);
		StdDraw.text(0.5, 0.18, "press spacebar to start a new game");
		
		//Press escape to exit text
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		font = new Font("Comic Sans MS", Font.PLAIN, 20);		//require "import java.awt.*;"
        StdDraw.setFont(font);
		StdDraw.text(0.5, 0.11, "press escape to exit game");
		
		StdDraw.show();
		
	}
	
	
}



