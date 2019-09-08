import java.util.*;
import java.awt.*;	
public class PlayerObject extends GameObject implements IViewPort {
//public class PlayerObject extends GameObject{

	//help from http://stackoverflow.com/questions/10839131/implements-vs-extends-when-to-use-whats-the-difference
	
	
	
    private static final Color DEFAULT_COLOR = StdDraw.LIGHT_GRAY;
    private static final Color DEFAULT_FACING_COLOR = StdDraw.BLACK;
    private static final double DEFAULT_FOV = 1.5*Math.PI/2; 			// field of view of player's viewport
    private static final double FOV_INCREMENT = 1.2*Math.PI/180; 		// rotation speed  Math.PI/36;

	public static boolean clicked = false;
	
	public static double aimDirection = Math.PI/2;
	
	private double lastX = 0;
	private double lastY = 0;
	
	private static HashMap<Integer, Integer> rOrder = new HashMap<Integer, Integer>();
	
	private boolean turnLeft = false;
	private boolean turnRight = false;
	private double deltaX = 0;
	
	
	private static final double borderAmount = 0.3;			//!!!!!!!!!!!!!!!!! also found in gamestate,   should put a common in a render settings library:
    //private Camera cam;
	
	//Constructor
	public PlayerObject(){
		//http://docs.oracle.com/javase/tutorial/java/IandI/super.html
		
		double scale = StellarCrunch.getScale();
		
		turnLeft = false;
		turnRight = false;
		aimDirection = Math.PI/2;
		clicked = true;
		
		mass = GameObjectLibrary.getPLAYER_MASS();
		rgb = StdDraw.GREEN;
		xPos = 0.5*scale;
		yPos = 0.5*scale;
		
		fixSize();
		
		numberOfAsteroids ++;
		id = numberOfAsteroids;
	
		//colour = 'g';
		//System.out.println("hi");
		
	}
	
	
	//@Override
    public void processCommand() {
		//System.out.println("well, we got here");
        // Process keys applying to the player
		//http://monkeyfighter.com/asteroidkeyboard.html
		//37 - left
		//38 - up
		//39 - right
		//40 - down
		//87 - w
		//65 - a
		//83 - s
		//68 - d
		//32 - space
		
		double forceMax = 15e23;
		double force = forceMax;
		if (StdDraw.isKeyPressed(16) == false){											//shift
			force *= mass/1E26;
		}
		force = Math.min(forceMax, force);
		
		/*
		if (false){	//arrow keys: else{wasd}
		
			if (StdDraw.isKeyPressed(37) == true && StdDraw.isKeyPressed(39) == false){			//left
				//System.out.println("left");
				applX = -force;
			}
			else if (StdDraw.isKeyPressed(39) == true && StdDraw.isKeyPressed(37) == false){	//right
				//System.out.println("right");
				applX = force;
			}
			else{
				applX = 0;
			}
			if (StdDraw.isKeyPressed(38) == true && StdDraw.isKeyPressed(40) == false){			//forward
				//System.out.println("forward");
				applY = force;
			}
			else if (StdDraw.isKeyPressed(40) == true && StdDraw.isKeyPressed(38) == false){	//back
				//System.out.println("back");
				applY = -force;
			}
			else{
				applY = 0;
			}
		}
		else{
			if (StdDraw.isKeyPressed(65) == true && StdDraw.isKeyPressed(68) == false){			//left
				//System.out.println("left");
				applX = -force;
			}
			else if (StdDraw.isKeyPressed(68) == true && StdDraw.isKeyPressed(65) == false){	//right
				//System.out.println("right");
				applX = force;
			}
			else{
				applX = 0;
			}
			if (StdDraw.isKeyPressed(87) == true && StdDraw.isKeyPressed(83) == false){			//forward
				//System.out.println("forward");
				applY = force;
			}
			else if (StdDraw.isKeyPressed(83) == true && StdDraw.isKeyPressed(87) == false){	//back
				//System.out.println("back");
				applY = -force;
			}
			else{
				applY = 0;
			}
		}
		*/
		
		
		if (StdDraw.isKeyPressed(38) == true && StdDraw.isKeyPressed(40) == false){			//forward
			applyForward(force);
		}
		else if (StdDraw.isKeyPressed(40) == true && StdDraw.isKeyPressed(38) == false){	//back
			//System.out.println("back");
			applyReverse(force);
		}
		else{
			applX = 0;
			applY = 0;
		}
		keyboardAim();
		//mouseAim();
		
		if ((StdDraw.isKeyPressed(32) == true) && (clicked == false)){						//32 for spacebar
			//Check if player is alive...
			if (GameState.objectMap.containsKey(GameState.playerKey) == true){
			
				clicked = true;
				double scale = StellarCrunch.getScale();
				StdDraw.setScale(0, scale);
				double xClick = StdDraw.mouseX();
				double yClick = StdDraw.mouseY();
				//StdDraw.filledCircle(StdDraw.mouseX(), StdDraw.mouseY(), border);
				
				double theta = Math.atan2(yClick-yPos, xClick-xPos);
				
				//System.out.println(theta);
				shoot(aimDirection);
			}
		}
		
		if (StdDraw.isKeyPressed(32)== false){
			clicked = false;
		}
		
		//
		
		/*
		// Retrieve 
        if (cam != null) {
            // No commands if no draw canvas to retrieve them from!
            Draw dr = cam.getDraw();
            if (dr != null) {
				// Example code
                if (dr.isKeyPressed(KeyEvent.VK_UP)) up = true;
                if (dr.isKeyPressed(KeyEvent.VK_DOWN)) down = true;
            }
        }
		*/
    }
	
	private void mouseAim(){
		double scale = StellarCrunch.getScale();
		
		double currX = StdDraw.mouseX();
		double currY = StdDraw.mouseY();
		
		double deltaX = currX-lastX;
		double deltaY = currY-lastY;
		
		lastX = currX;
		lastY = currY;
		
		double sens = 20/scale;
		
		aimDirection += sens*deltaX;
		
		while (aimDirection > 2*Math.PI){
			aimDirection -= 2*Math.PI;
		}
		while (aimDirection < 0){
			aimDirection += 2*Math.PI;
		}
		
		System.out.println(aimDirection);
		
		
	}
	
	private void keyboardAim(){
		
		double deltaAim = 0;
		
		if (turnLeft == true){
			deltaX ++;
			deltaAim = deltaX/50;
			deltaAim = Math.min(deltaAim, 1);
		}
		else if (turnRight == true){
			deltaX ++;
			deltaAim = deltaX/50;
			deltaAim = Math.min(deltaAim, 1);
		}
		else{
			deltaX = 5;
			deltaAim = 5/50;
		}
		
		
		if (StdDraw.isKeyPressed(37) == true && StdDraw.isKeyPressed(39) == false){			//left
			aimDirection += deltaAim*FOV_INCREMENT;
			turnLeft = true;
			turnRight = false;
		}
		else if (StdDraw.isKeyPressed(39) == true && StdDraw.isKeyPressed(37) == false){	//right
			aimDirection -= deltaAim*FOV_INCREMENT;
			turnRight = true;
			turnLeft = false;
		}
		else{
			turnLeft = false;
			turnRight = false;
		}
		
		while (aimDirection > 2*Math.PI){
			aimDirection -= 2*Math.PI;
		}
		while (aimDirection < 0){
			aimDirection += 2*Math.PI;
		}
		
		//System.out.println(aimDirection);
	}
	
	
	private void orderHashMap(){
		//Order the r values in descending fashion
		//rOrder: <i.this, i.GameState.objectMap>
		//for each i in rOrder:
		//System.out.println("in");
		rOrder.clear();
		
		int i = 0;
		do{
			//this is for each i in the rOrder:
			double maxTemp = 0;
			int iObjTemp = 0;
			int i2 = 0;
			boolean found = false;
			do{
				
				if ((GameState.objectMap.containsKey(i2) == true) && (i2 != id-1)){
					//go through each gameobject and find the max r:
					//calc r:
					double xPosB = GameState.objectMap.get(i2).getXPos();
					double yPosB = GameState.objectMap.get(i2).getYPos();
					double dx = (xPosB - xPos);
					double dy = (yPosB - yPos);
					double r = Math.hypot(dx, dy);
					
					//ALSO check that we dont take the same location twice:
					if ((r>maxTemp) && (rOrder.containsValue(i2) == false)){
						maxTemp = r;
						iObjTemp = i2;
						found = true;
					}
					
					
				}
				
				
				i2++;
			}while(i2<numberOfAsteroids);
			//finished going through each REMAINING gameobject:
			
			//put the keylocation of the max r
			//System.out.println(maxTemp);
			if (found == true){
				rOrder.put(i, iObjTemp);
			}
			
			
			
			i++;
		}while(i<GameState.objectMap.size()-1);
		//System.out.println(rOrder.size());
		
	}
	
	
	
	public void renderPOV(){
		orderHashMap();
		
		double scale = StellarCrunch.getScale();
		
		int i = 0;
		do{
			
			if (rOrder.containsKey(i) == true){
				int i0 = rOrder.get(i);
				//System.out.println(i);
				//rendering object i:
				
				
				
				//Only render objects that will actually appear: (optimisation...)
				if((returnCirclePos(i0) > -(0.5+returnCircleSize(i0))) && (returnCirclePos(i0) < (0.5+returnCircleSize(i0)))){
				
					StdDraw.setScale(0, scale);
					StdDraw.setPenRadius(0.0001);
					
					//Get render locations and sizes of the circles:
					double xPos0 = returnCirclePos(i0);
					double sizeOnDisplay = returnCircleSize(i0);
					double yOnDisplay = returnCircleHeight(i0);
					
					xPos0 = xPos0*scale+0.5*scale;
					double yPos0 = yOnDisplay*scale+0.5*scale;
					double size0 = sizeOnDisplay*scale;
					
					
					
					//Draw color rings around asteroid:
					
					//Draw danger ring if the collision would cause shattering and a red rign if the asteroid is bigger than you:
					//Also draw a line between your center and the center of the asteroid:
					
					if (GameState.objectMap.containsKey(GameState.playerKey) == true){
						if (GameState.objectMap.get(i0).getSize() <= GameState.playerMap.get(0).getSize()){	//It is smaller than you:
							
							//System.out.println("we got here:");
							double xPosB = GameState.objectMap.get(i0).getXPos();
							double yPosB = GameState.objectMap.get(i0).getYPos();
							double dx = (xPosB - xPos);
							double dy = (yPosB - yPos);
							double xVelB = GameState.objectMap.get(i0).getXVel();
							double yVelB = GameState.objectMap.get(i0).getYVel();
							
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
								//StdDraw.line(0.5*scale, 0, xPos0, yPos0);
							}
							else {
								//This is aaight:
								StdDraw.setPenColor(StdDraw.GREEN);
								//StdDraw.line(0.5*scale, 0, xPos0, yPos0);
							}
						}
						else{																		//Bigger
							StdDraw.setPenColor(StdDraw.RED);
							//StdDraw.line(0.5*scale, 0, xPos0, yPos0);
						}
					}
					double border = size0 + borderAmount*(Math.cbrt(GameObjectLibrary.getASTEROID_MIN_MASS()))*GameObjectLibrary.getASTEROID_SIZE_SCALE();
					StdDraw.filledCircle(xPos0, yPos0, border);
				
					border = size0 + 0.5*borderAmount*(Math.cbrt(GameObjectLibrary.getASTEROID_MIN_MASS()))*GameObjectLibrary.getASTEROID_SIZE_SCALE();
				
					StdDraw.setPenColor(StdDraw.GRAY);
					StdDraw.filledCircle(xPos0, yPos0, border);
					
					
					//Draw asteroid:
					StdDraw.setPenColor(fog(GameState.objectMap.get(i0).rgb, i0));
					//render3Dsphere(xPos0, yPos0, size0, i0);
					StdDraw.filledCircle(xPos0, yPos0, size0);
					
					
					//Text above asteroid:
					String asteroidMass = String.valueOf((int)(GameState.objectMap.get(i0).getMass()/1E24));
					String asteroidDist = String.valueOf((int)(returnR(i0)/1E9));
					
					double textSizeD = (Math.max(30*Math.atan2(2*size0, returnR(i0)), 10));
					int textSizeI = Integer.valueOf((int) Math.round(textSizeD));		//http://stackoverflow.com/a/24816336/1450294 helped me:
					
					Font font = new Font("Comic Sans MS", Font.PLAIN, textSizeI);
					StdDraw.setFont(font);
					double yPosT = returnTextHeight(i0)*scale+0.5*scale;
					
					StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
					
					StdDraw.line(xPos0, yPosT, xPos0, yPos0+border);
					StdDraw.text(xPos0, yPosT, asteroidMass + " ZT, " + asteroidDist + " million kms");
					
					
				}
			}
			i ++;
			
		}while(i < GameObject.getNumberOfAsteroids()-1);
		
		//Crosshair
		StdDraw.setScale(0, 1);
		
		double xHairLength = 0.01;						//!!!!!!!!!!!!!!game setting
		double xHairThickness = 0.005;					//!!!!!!!!
		Color xHairColour = StdDraw.LIGHT_GRAY;			//!!!!!!!
		
		StdDraw.setPenRadius(xHairThickness);
		StdDraw.setPenColor(xHairColour);
		StdDraw.line(0.5-xHairLength, 0.5, 0.5+xHairLength, 0.5);
		StdDraw.line(0.5, 0.5-xHairLength, 0.5, 0.5+xHairLength);
		
		//drawStats();
		
	}
	
	
	
	private void render3Dsphere(double xPos0, double yPos0, double size0, int i){
		
		double xPosB = GameState.objectMap.get(i).getXPos();
		double yPosB = GameState.objectMap.get(i).getYPos();
		double dx = (xPosB - xPos);
		double dy = (yPosB - yPos);
		double r = Math.hypot(dx, dy);
		
		
		double viewingAngle = Math.atan2(dy, dx);
		
		while (viewingAngle > 2*Math.PI){
			viewingAngle -= 2*Math.PI;
		}
		while (viewingAngle < 0){
			viewingAngle += 2*Math.PI;
		}
		//System.out.println(viewingAngle);
		
		double shineSpotAngle = Math.PI-viewingAngle;
		
		double shineSpotCenter = -size0*Math.sin(-shineSpotAngle);
		
		StdDraw.filledCircle(xPos0, yPos0, size0);
		
		Color prevColor = StdDraw.getPenColor();
		
		double divisions = 20;
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.setPenRadius(0.002);
		/*
		for (int i2 = 0; i2 <= divisions; i2++){
			double theta = ((2*Math.PI)/divisions)*i2;
		
			double x1 = xPos0 + 0.9*size0*Math.cos(theta);
			double y1 = yPos0 + 0.9*size0*Math.sin(theta);
			
			double x2 = xPos0 + 0.9*size0*Math.cos(Math.PI-theta);
			double y2 = yPos0 + 0.9*size0*Math.sin(Math.PI-theta);
			
			StdDraw.line(x1, y1, x2, y2);
		}
		*/
		
		//Spot is viewable:
		if (viewingAngle > Math.PI/2 && viewingAngle < Math.PI*1.5){
		//if (shineSpotAngle > 0 && shineSpotAngle < 2*Math.PI){
		//if (true){
			StdDraw.setPenColor(shadow(prevColor, 1.1));
			double minor = 0.85*Math.abs(size0-Math.abs(shineSpotCenter));
			double major = 0.85*Math.abs(size0*Math.sin(Math.acos(shineSpotCenter/size0)));
			StdDraw.filledEllipse(xPos0+shineSpotCenter, yPos0, minor, major);
			
			StdDraw.setPenColor(shadow(prevColor, 1.2));
			minor = 0.6*Math.abs(size0-Math.abs(shineSpotCenter));
			major = 0.6*Math.abs(size0*Math.sin(Math.acos(shineSpotCenter/size0)));
			StdDraw.filledEllipse(xPos0+shineSpotCenter, yPos0, minor, major);
			
			StdDraw.setPenColor(shadow(prevColor, 1.3));
			minor = 0.45*Math.abs(size0-Math.abs(shineSpotCenter));
			major = 0.45*Math.abs(size0*Math.sin(Math.acos(shineSpotCenter/size0)));
			StdDraw.filledEllipse(xPos0+shineSpotCenter, yPos0, minor, major);
			
			StdDraw.setPenColor(shadow(prevColor, 1.4));
			minor = 0.3*Math.abs(size0-Math.abs(shineSpotCenter));
			major = 0.3*Math.abs(size0*Math.sin(Math.acos(shineSpotCenter/size0)));
			StdDraw.filledEllipse(xPos0+shineSpotCenter, yPos0, minor, major);
			
			//StdDraw.filledCircle(shineSpotCenter, yPos0, 0.5*size0);
		}
		else{	//this is the dark side:
			//welcome:
			shineSpotCenter *= -1;
			
			StdDraw.setPenColor(shadow(prevColor, 0.9));
			double minor = 0.85*Math.abs(size0-Math.abs(shineSpotCenter));
			double major = 0.85*Math.abs(size0*Math.sin(Math.acos(shineSpotCenter/size0)));
			StdDraw.filledEllipse(xPos0+shineSpotCenter, yPos0, minor, major);
			
			StdDraw.setPenColor(shadow(prevColor, 0.8));
			minor = 0.6*Math.abs(size0-Math.abs(shineSpotCenter));
			major = 0.6*Math.abs(size0*Math.sin(Math.acos(shineSpotCenter/size0)));
			StdDraw.filledEllipse(xPos0+shineSpotCenter, yPos0, minor, major);
			
			StdDraw.setPenColor(shadow(prevColor, 0.7));
			minor = 0.45*Math.abs(size0-Math.abs(shineSpotCenter));
			major = 0.45*Math.abs(size0*Math.sin(Math.acos(shineSpotCenter/size0)));
			StdDraw.filledEllipse(xPos0+shineSpotCenter, yPos0, minor, major);
			
			StdDraw.setPenColor(shadow(prevColor, 0.6));
			minor = 0.3*Math.abs(size0-Math.abs(shineSpotCenter));
			major = 0.3*Math.abs(size0*Math.sin(Math.acos(shineSpotCenter/size0)));
			StdDraw.filledEllipse(xPos0+shineSpotCenter, yPos0, minor, major);
			
			
			
			
		}
		
		
		
		
		
		
	}
	
	private Color fog(Color inputRGB, int i){
		//https://docs.oracle.com/javase/7/docs/api/java/awt/Color.html
		
		double scale = StellarCrunch.getScale();
		
		int R = (int)(inputRGB.getRed());
		int G = (int)(inputRGB.getGreen());
		int B= (int)(inputRGB.getBlue());
		
		int ROrig = R;
		int GOrig = G;
		int BOrig = B;
		
		double r = returnR(i);
		r /= scale;
		r *= 20;
		
		R = (int)Math.min((R/r), ROrig);
		G = (int)Math.min((G/r), GOrig);
		B = (int)Math.min((B/r), BOrig);
		
		
		
		Color output = new Color(R, G, B);
		
		
		return output;
	}
	
	private Color shadow (Color inputRGB, double factor){
		int R = (int)(inputRGB.getRed());
		int G = (int)(inputRGB.getGreen());
		int B= (int)(inputRGB.getBlue());
		
		int ROrig = R;
		int GOrig = G;
		int BOrig = B;
		
		R = (int)Math.min((R*factor), 255);
		G = (int)Math.min((G*factor), 255);
		B = (int)Math.min((B*factor), 255);
		
		Color output = new Color(R, G, B);
		
		
		return output;
	}
	
	private double returnCirclePos(int i){
		double xPosB = GameState.objectMap.get(i).getXPos();
		double yPosB = GameState.objectMap.get(i).getYPos();
		double dx = (xPosB - xPos);
		double dy = (yPosB - yPos);
		double r = Math.hypot(dx, dy);
		
		double thetaPlayer = aimDirection;
		double deltaAngle = -(Math.atan2(dy, dx) - thetaPlayer);
		
		while (deltaAngle > 1*DEFAULT_FOV){
			deltaAngle -= 2*Math.PI;
		}
		while (deltaAngle < -1*DEFAULT_FOV){
			deltaAngle += 2*Math.PI;
		}
		
		double xPos0 = deltaAngle/DEFAULT_FOV;
		
		return xPos0;
	}
	
	private double returnTextHeight(int i){
		double r = returnR(i);
		
		double deltaCenterHeight = 4*GameState.objectMap.get(i).getSize() - size;
		
		double yOnDisplay = (Math.atan2(deltaCenterHeight, r))/DEFAULT_FOV;
		//positve for bigger objects:
		//negative for smaller:
		return yOnDisplay;
	}
	
	private double returnR(int i){
		double xPosB = GameState.objectMap.get(i).getXPos();
		double yPosB = GameState.objectMap.get(i).getYPos();
		double dx = (xPosB - xPos);
		double dy = (yPosB - yPos);
		double r = Math.hypot(dx, dy);
		return r;
	}
	
	private double returnCircleSize(int i){
		double r = returnR(i);
		double sizeB = GameState.objectMap.get(i).getSize();
		
		double sizeOnDisplay = (2*Math.atan2(0.5*sizeB, r))/DEFAULT_FOV;
		
		return sizeOnDisplay;
	}
	
	private double returnCircleHeight(int i){
		//the concept is that all the asteroids are sliding around on an imaginary floor... this is not realistic, but should help in making first person more immersive:
		
		double r = returnR(i);
		
		double deltaCenterHeight = GameState.objectMap.get(i).getSize() - size;
		
		double yOnDisplay = (Math.atan2(deltaCenterHeight, r))/DEFAULT_FOV;
		//positve for bigger objects:
		//negative for smaller:
		return yOnDisplay;
	}
	
	
	
	public double getFacingDirection(){
		return 10;
	}
	
	public double getCameraPosition(){
		return 10;
	}
	
	
	
	private void applyForward(double force){
		applX = force*Math.cos(aimDirection);
		applY = force*Math.sin(aimDirection);
	}
	
	private void applyReverse(double force){
		//Stops the player
		double vTheta = Math.atan2(yVel, xVel);
		//Apply force opposite to the drection of velocity:
		applX = -force*Math.cos(vTheta);
		applY = -force*Math.sin(vTheta);
	}
	
	
	public void drawStats(){
		StdDraw.setScale(0, 1);
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		
		Font font = new Font("Comic Sans MS", Font.PLAIN, 40);		//require "import java.awt.*;"
        StdDraw.setFont(font);
		
		StdDraw.text(0.7, 0.1, StellarCrunch.username);
		
		
		font = new Font("Comic Sans MS", Font.PLAIN, 15);		//require "import java.awt.*;"
        StdDraw.setFont(font);
		
		//http://stackoverflow.com/questions/5766318/converting-double-to-string
		//Time passed:
		
		double x1 = 0.15;
		double x2 = 0.85;
		double yTop = 0.95;
		double yBottom = 0.3;
		double ySpace = 0.05;
		
		double gameTimeD = (GameStats.gameDuration*StellarCrunch.getTIME_PER_MS())/3600000;
		String gameTime = String.valueOf((int)gameTimeD);
		String timeUnit = "hours";
		
		if (gameTimeD>24){
			gameTimeD /= 24;
			gameTime = String.valueOf((int)gameTimeD);
			//now in days...
			if (gameTimeD < 7){
				timeUnit = "days";
			}
			else{
				gameTimeD /= 7;
				gameTime = String.valueOf((int)gameTimeD);
				
				if (gameTimeD < 52){
					timeUnit = "weeks";
				}
				else{
					gameTimeD /= 52;
					gameTime = String.valueOf((int)gameTimeD);
					
					if (gameTimeD < 2){
						timeUnit = "year";
					}
					else{
						timeUnit = "years";
					}
				}
			}
		}
		
		
		
		String gameDuration = String.valueOf((int)(((GameStats.getGameTime())/1000)));
		String totalDuration = String.valueOf((int)(((GameStats.totalDuration)/1000)));
		
		//player mass:
		String playerMass = String.valueOf((int)(mass/1E23));		//10s of zettatonnes
		char playerMassPoint = playerMass.charAt(playerMass.length()-1);
		playerMass = String.valueOf((int)(mass/1E24));
		
		
		String playerVel = String.valueOf((int)(Math.hypot(xVel, yVel)/1000));
		
		
		String currentLevel = String.valueOf(GameState.getLevel());
		
		
		
		double xD;
		double yD;
		
		
		
		
		xD = x1;			
		//Left
		////////////////////////////////////////////////////////
		yD = yTop;			
		//Top
		StdDraw.text(xD, yD, "duration "+gameDuration+" seconds");
		
		////////////////////////////////////////////////////////
		yD = yTop-1*ySpace;	
		//Top-1
		if (GameStats.levelHasTime()){
			String fastestTime = String.valueOf((int)(GameStats.timeForLevel()/1000));
			StdDraw.text(xD, yD, "level record "+fastestTime+" seconds");
		}
		////////////////////////////////////////////////////////
		yD = yTop-2*ySpace;	
		//Top-2
		if (GameStats.levelHasTime()){
			StdDraw.text(xD, yD, "by "+GameStats.highscoreName());
		}
		////////////////////////////////////////////////////////
		
		
		
		//Middle
		xD =0.5;
		yD = yTop-1*ySpace;	
		StdDraw.text(xD, yD, "current level "+currentLevel);
		////////////////////////////////////////////////////////
		
		
		
		
		xD = x2;
		//Right
		////////////////////////////////////////////////////////
		yD = yTop;			
		//Top
		StdDraw.text(xD, yD, "mass "+playerMass+","+playerMassPoint+" zettatonnes");
		////////////////////////////////////////////////////////
		yD = yTop-1*ySpace;	
		//Top-1
		StdDraw.text(xD, yD, "velocity "+playerVel+" km/s");
		////////////////////////////////////////////////////////
		yD = yTop-2*ySpace;	
		//Top-2
		StdDraw.text(xD, yD, "game time "+gameTime+" "+timeUnit);
		////////////////////////////////////////////////////////
		yD = yBottom;	
		//Bottom top:
		
		////////////////////////////////////////////////////////
		yD = yBottom-1*ySpace;	
		//Bottom top-1:
		
		////////////////////////////////////////////////////////
		yD = yBottom-2*ySpace;	
		//Bottom top-2:
		
		yD = yBottom-3*ySpace;	
		//Bottom top-3:
		
		//StdDraw.text(xD, yD, "duration "+gameDuration+" seconds");
		
		
	}
}
