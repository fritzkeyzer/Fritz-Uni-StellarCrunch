import java.util.*;
import java.lang.Math;
import java.awt.*;

public class GameObject {
    // Default implementation of a game object
	
	//Help from http://introcs.cs.princeton.edu/java/32class/
	//And from https://docs.oracle.com/javase/tutorial/java/javaOO/classvars.html
	
	
	
	//Instance variables
	public double mass;
	public double size;
	public double xPos;
	public double yPos;
	public double totXForce = 0;
	public double totYForce = 0;
	public double xNextFramePos;
	public double yNextFramePos;
	public double xVel = 0;
	public double yVel = 0;
	
	public double applX = 0;
	public double applY = 0;
	
	public Color rgb;
	
	public int id;
	
	public static int numberOfAsteroids = 0;
	
	public boolean collision = false;
	
	public static final int maxAsteroids = 50;
	
	
	
	//Constructor
	public GameObject(){
		//System.out.println("New GameObject Spawning");
		
		double scale = StellarCrunch.getScale();
		
		mass = GameObjectLibrary.getASTEROID_MASS();
		
		//Creating the colour - code from http://stackoverflow.com/questions/4246351/creating-random-colour-in-java from user: Komplot, not the top answer
		int R = (int)(Math.random()*256);
		int G = (int)(Math.random()*256);
		int B= (int)(Math.random()*256);
		Color color = new Color(R, G, B); //random color, but can be bright or dull

		//to get rainbow, pastel colors
		Random random = new Random();
		final float hue = random.nextFloat();
		final float saturation = 0.7f;//1.0 for brilliant, 0.0 for dull
		final float luminance = 0.8f; //1.0 for brighter, 0.0 for black
		color = Color.getHSBColor(hue, saturation, luminance);
		
		rgb = color;
		/*
		if (Math.random()<0.5){
			rgb = StdDraw.RED;
		}
		else{
			rgb = StdDraw.BLUE;
		}
		*/
		
		double theta;
		double r;
		theta = Math.random()*2*Math.PI;
		r = ((GameObjectLibrary.getASTEROID_RADIUS()) - (0.5*(GameObjectLibrary.getASTEROID_WIDTH()))) + Math.random()*(GameObjectLibrary.getASTEROID_WIDTH());
		
		xPos =  (((Math.cos(theta))*r) + 0.5)*scale;
		yPos =  (((Math.sin(theta))*r) + 0.5)*scale;

		fixSize();
		
		
		/*
		if (numberOfAsteroids == 0){
			//the first asteroid will spawn in the center:
			
			xPos = 0.5*scale;
			yPos = 0.5*scale;
			
			mass = 1*GameObjectLibrary.getASTEROID_MAX_MASS();
			fixSize();
		}
		*/
		
		numberOfAsteroids ++;
		id = numberOfAsteroids;
		
		//System.out.println("New GameObject finished Spawning");
	}
	
	public int getID(){
		return id;
    }
	
	public static int getNumberOfAsteroids(){
		return numberOfAsteroids;
    }
	
	public void setValues(double mass0, double xPos0, double yPos0, double xVel0, double yVel0){
		//System.out.println("setting values");
		//System.out.print("from: "+ mass);
		mass = mass0;
		fixSize();
		//System.out.println("to: "+ mass);
		xPos = xPos0;
		yPos = yPos0;
		xVel = xVel0;
		yVel = yVel0;
	}
	
	public double getMass(){
		return mass;
    }
	
	public double getSize(){
		return size;
    }
	
	public void fixSize(){
		if (GameObjectLibrary.getASTEROID_SIZE_CUBIC_SCALE() == true){
			size = (Math.cbrt(mass))*GameObjectLibrary.getASTEROID_SIZE_SCALE();
		}
		else{
			size = (mass/5E17)*GameObjectLibrary.getASTEROID_SIZE_SCALE();
		}
		
		//size = 5e7;
		
	}
	
	public double getXPos(){
		return xPos;
    }
	
	public double getYPos(){
		return yPos;
    }
	
	public double getXVel(){
		return xVel;
    }
	
	public double getYVel(){
		return yVel;
    }
	
	
	
	public void applyPlanarRotation(){
		
		double omega = GameObjectLibrary.getPLANAR_ROTATIONAL_VELOCITY();
		double scale = StellarCrunch.getScale();
		double center = 0.5*scale;
		
		double theta = Math.atan2(yPos-center, xPos-center);		//polar angle from center to asteroid
		
		//System.out.println(theta);
		
		//anticlockwise : + 90 = pi/2
		//clockwise : -90 = -pi/2
		
		double alpha;				//polar angle for velocity direction
		
		
		//clockwise
		alpha = theta - (Math.PI/2);
		
		
		double r = Math.hypot(xPos - center, yPos - center);
		
		double speed = omega*r;		//tangental velocity
		
		
		xVel += speed*(Math.cos(alpha));
		yVel += speed*(Math.sin(alpha));
		
		
	}
	
	
	
	public void calculate(double deltaTime){
		//System.out.println("calculate()");
		
		
		calculateForces();
		
		//Collision Detection
		if (GameObjectLibrary.getInterAsteroidalCollisionDetection() == true){
			collisionDetection();
		}
		
		
		
		//Bounce off the edges of the screen:
		if(GameObjectLibrary.getBorderBounce() == true){
			borderBounce();
		}
		
		//Edge scrolling
		else if (GameObjectLibrary.getBorderScroll() == true){
			borderScroll();
		}
		
		calculateNextFramePos(deltaTime);
		
		//System.out.println(numberOfAsteroids);
		//System.out.println("finished calculate()");
	}
	
	public void move(){
		//System.out.println("move()");
		xPos = xNextFramePos;
		yPos = yNextFramePos;
	}
	
	
	
	private void calculateForces(){
		//calculate all the forces based on the postions of all other gameobjects
		
		totXForce = applX;
		totYForce = applY;
		
		int i = 0;
		do{	//Calculates the forces between this gameobject and every other gameobject
			//System.out.println("id: " + id);
			if (i+1!= id){
				//Not itself
				if (GameState.objectMap.containsKey(i) == true){	//not an empty location in the hashmap
					double dx = (GameState.objectMap.get(i).getXPos() - xPos);
					double dy = (GameState.objectMap.get(i).getYPos() - yPos);
					double r = Math.hypot(dx, dy);			//https://docs.oracle.com/javase/7/docs/api/java/lang/Math.html
					
					//System.out.println("i: " + i);
					
					
					
					double iMass = GameState.objectMap.get(i).getMass();
					double G = StellarCrunch.getG();
					double softE = StellarCrunch.getSoftE();
					
					//To prevent issues... if the asteroids collide, we disable the gravitational attraction between those bodies in that particular simulation frame
					double i2size = GameState.objectMap.get(i).getSize();
					if (r<(size + i2size)){
						//G = 0;
						//G *= -1;
					}
					
					
					//xforce
					double xForce = (dx/(r+softE))*((G*mass*iMass)/(r*r + softE));
					//System.out.println("xForce: " + xForce);
					
					totXForce += xForce;
					
					//yforce
					double yForce = (dy/(r+softE))*((G*mass*iMass)/(r*r + softE));
					//System.out.println("yForce: " + yForce);
					
					totYForce += yForce;
					
					//"air drag" to counteract the build up of energy in the system (that is caused by simulation inacuraccy)
					double A = Math.PI*size*size;
					double pCd = GameObjectLibrary.getDragCoefficient();
					if (xVel > 0){
						totXForce -= (xVel*xVel*A*pCd)/2;
					}
					else{
						totXForce += (xVel*xVel*A*pCd)/2;
					}
					if (yVel > 0){
						totYForce -= (yVel*yVel*A*pCd)/2;
					}
					else{
						totYForce += (yVel*yVel*A*pCd)/2;
					}
					
					
				}
			}
			i ++;
		}while(i < numberOfAsteroids);
		
		
	}
	
	private void calculateNextFramePos(double deltaTime){
		double max = StellarCrunch.getScale();
		double simDeltaTime = ((deltaTime/1000)*StellarCrunch.getTIME_PER_MS());		//How many milliseconds pass in the simulation
		double e = GameObjectLibrary.getCoefficientOfRestitution();
		
		//xVel
		xVel += (totXForce/mass)*simDeltaTime;		//note conservation of linear momentum here
		//xVel = xVel*(1-GameObjectLibrary.friction);
		
		//yVel
		yVel += (totYForce/mass)*simDeltaTime;		//note conservation of linear momentum here
		//yVel = yVel*(1-GameObjectLibrary.friction);
		
		
		//xNextFramePos
		xNextFramePos = xPos + 0.5*xVel*simDeltaTime;
		/*
		if (xNextFramePos < 0){
			xNextFramePos = size;
			xVel *= e;
		}
		else if (xNextFramePos > max){
			xNextFramePos = max-size;
			xVel *= e;
		}
		*/
		//yNextFramePos
		yNextFramePos = yPos + 0.5*yVel*simDeltaTime;
		/*
		if (yNextFramePos < 0){
			yNextFramePos = size;
			yVel *= e;
		}
		else if (yNextFramePos > max){
			yNextFramePos = max-size;
			yVel *= e;
		}
		*/
	}
	
	private void collisionDetection(){
		int collisions = 0;
		int i2 = 0;
		do{	//Collision detection
			//System.out.println("id: " + id);
			if ((i2+1!= id)&&(GameState.objectMap.containsKey(i2) == true)){	//Not itself and not an empty location in the hashmap
				
				
				double xPosB = GameState.objectMap.get(i2).getXPos();
				double yPosB = GameState.objectMap.get(i2).getYPos();
				double dx = (xPosB - xPos);
				double dy = (yPosB - yPos);
				double r = Math.hypot(dx, dy);
				
				double i2size = GameState.objectMap.get(i2).getSize();
				
				//Merge conditions:
				//Center of smaller asteroid reaches the radius of the larger:
				if (r<size){
					double sizeA = size;
					double sizeB = i2size;
					
					if ((sizeA >= sizeB)){
						merge(i2);
					}
				}
				
				//Shatter conditions:
				//The radii of the two asteroids meet
				else if (r<(size + i2size) && (GameState.objectMap.size() < maxAsteroids)){
					
					//!!!! we have a collision!!!!
					//System.out.print("collision detected between: " + id);
					//System.out.println(" and: " + i2+1);
					
					double xVelB = GameState.objectMap.get(i2).getXVel();
					double yVelB = GameState.objectMap.get(i2).getYVel();
					double sizeA = size;
					double sizeB = i2size;
					
					double massA = mass;
					double massB = GameState.objectMap.get(i2).getMass();
					
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
					//shatterThresh = 150;
					
					//smaller body handles shattering
						//but the smaller object cannot be the player...
					if ((sizeA <= sizeB) && (id != GameState.playerKey+1)){
						if (aNormVel-bNormVel > shatterThresh){
							//bounce(i2);
							shatter(i2);
						}
					}
					
				}
				
			}
			i2 ++;
		}while(i2 < numberOfAsteroids);
		
		if (collisions == 0){
			collision = false;
		}
		
	}
	
	private void bounce(int i2){
		double xPosB = GameState.objectMap.get(i2).getXPos();
		double yPosB = GameState.objectMap.get(i2).getYPos();
		double dx = (xPosB - xPos);
		double dy = (yPosB - yPos);
		double r = Math.hypot(dx, dy);
		
		double i2size = GameState.objectMap.get(i2).getSize();
		
		double massA = mass;
		double massB = GameState.objectMap.get(i2).getMass();
		double xVelB = GameState.objectMap.get(i2).getXVel();
		double yVelB = GameState.objectMap.get(i2).getYVel();
		double sizeA = size;
		double sizeB = i2size;
		
		double e = GameObjectLibrary.getCoefficientOfRestitution();
		
		//bounce:
		//collision point:
		double ra = sizeA;
		double cX = ra*(dx/r);
		double cY = ra*(dy/r);
		
		double normTheta = Math.atan2(dy, dx);
		
		
		//System.out.println(normTheta);
		
		double aTheta;
		aTheta = Math.atan2(yVel, xVel);
		double bTheta;
		bTheta = Math.atan2(yVelB, xVelB);
		
		//System.out.println(bTheta);
		
		//calculate the collision conservation of momentum:
		//mock head on collision:
		
		double aSpeed = Math.abs(Math.hypot(xVel, yVel));
		double bSpeed = Math.abs(Math.hypot(xVelB, yVelB));
		
		double aNormVel =  aSpeed*(Math.cos(aTheta-normTheta));
		double bNormVel =  bSpeed*(Math.cos(bTheta-normTheta));		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		double aTangentVel = aSpeed*(Math.cos(aTheta-normTheta+(Math.PI/2)));
		double bTangentVel = bSpeed*(Math.cos(bTheta-normTheta+(Math.PI/2)));
		
		//double aNormVelFinal = (aNormVel*((massA-massB)/(massA+massB)))+(bNormVel*((2*massB)/(massA+massB)));
		//double bNormVelFinal = (aNormVel*((2*massA)/(massA+massB)))+(bNormVel*((massB-massA)/(massA+massB)));
		
		//aNormVelFinal = 10000;
		//bNormVelFinal = -10000;
		
		double aNormVelFinal = ((-e*(aNormVel-bNormVel))+(aNormVel*(massA/massB))+bNormVel)/(1+(massA/massB));
		double bNormVelFinal = e*(aNormVel-bNormVel) + aNormVelFinal;
		
		aNormVelFinal = ((massA-massB)/(massA+massB))*aNormVel + ((2*massB)/(massA+massB))*bNormVel;
		bNormVelFinal = ((2*massA)/(massA+massB))*aNormVel + ((massB-massA)/(massA+massB))*bNormVel;
		//double bNormVelFinal = ((-e*(aNormVel-bNormVel))+(aNormVel*(massA/massB))+bNormVel)/(1+(massA/massB));
		
		//aNormVelFinal = -bNormVel*(massB/massA);
		//bNormVelFinal = aNormVel*(massA/massB);
		
		
		xVel = aNormVelFinal*Math.cos(normTheta) + aTangentVel*Math.cos(normTheta+(Math.PI/2));
		yVel = aNormVelFinal*Math.sin(normTheta) + aTangentVel*Math.sin(normTheta+(Math.PI/2));
		
		xVelB = -bNormVelFinal*Math.cos(normTheta) + bTangentVel*Math.cos(normTheta+(Math.PI/2));
		yVelB = -bNormVelFinal*Math.sin(normTheta) + bTangentVel*Math.sin(normTheta+(Math.PI/2));
		
		//xVelB = 0;
		//yVelB = 0;
		
		
		
		GameState.objectMap.get(i2).setValues(massB, xPosB, yPosB, xVelB, yVelB);
		
	}
	
	private void shatter(int i2){
		
		
		double xPosB = GameState.objectMap.get(i2).getXPos();
		double yPosB = GameState.objectMap.get(i2).getYPos();
		double dx = (xPosB - xPos);
		double dy = (yPosB - yPos);
		double r = Math.hypot(dx, dy);
		
		double i2size = GameState.objectMap.get(i2).getSize();
		
		double massA = mass;
		double massB = GameState.objectMap.get(i2).getMass();
		double xVelB = GameState.objectMap.get(i2).getXVel();
		double yVelB = GameState.objectMap.get(i2).getYVel();
		double sizeA = size;
		double sizeB = i2size;
		
		double e = GameObjectLibrary.getCoefficientOfRestitution();
		
		
		double normTheta = Math.atan2(dy, dx);
		
		
		//System.out.println(normTheta);
		
		double aTheta;
		aTheta = Math.atan2(yVel, xVel);
		double bTheta;
		bTheta = Math.atan2(yVelB, xVelB);
		
		//System.out.println(bTheta);
		
		//calculate the collision conservation of momentum:
		//mock head on collision:
		
		double aSpeed = Math.abs(Math.hypot(xVel, yVel));
		double bSpeed = Math.abs(Math.hypot(xVelB, yVelB));
		
		double aNormVel =  aSpeed*(Math.cos(aTheta-normTheta));
		double bNormVel =  bSpeed*(Math.cos(bTheta-normTheta));		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		double aTangentVel = aSpeed*(Math.cos(aTheta-normTheta+(Math.PI/2)));
		double bTangentVel = bSpeed*(Math.cos(bTheta-normTheta+(Math.PI/2)));
		
		//double aNormVelFinal = (aNormVel*((massA-massB)/(massA+massB)))+(bNormVel*((2*massB)/(massA+massB)));
		//double bNormVelFinal = (aNormVel*((2*massA)/(massA+massB)))+(bNormVel*((massB-massA)/(massA+massB)));
		
		//aNormVelFinal = 10000;
		//bNormVelFinal = -10000;
		
		double massSplit = massB/3;
		
		massB = massSplit;
		
		double aNormVelFinal = ((-e*(aNormVel-bNormVel))+(aNormVel*(massA/massB))+bNormVel)/(1+(massA/massB));
		double bNormVelFinal = e*(aNormVel-bNormVel) + aNormVelFinal;
		
		aNormVelFinal = e*((massA-massB)/(massA+massB))*aNormVel + ((2*massB)/(massA+massB))*bNormVel;
		bNormVelFinal = -e*((2*massA)/(massA+massB))*aNormVel + ((massB-massA)/(massA+massB))*bNormVel;
		//double bNormVelFinal = ((-e*(aNormVel-bNormVel))+(aNormVel*(massA/massB))+bNormVel)/(1+(massA/massB));
		
		xVel = aNormVelFinal*Math.cos(normTheta) + aTangentVel*Math.cos(normTheta+(Math.PI/2));
		yVel = aNormVelFinal*Math.sin(normTheta) + aTangentVel*Math.sin(normTheta+(Math.PI/2));
		
		xVelB = -bNormVelFinal*Math.cos(normTheta) + bTangentVel*Math.cos(normTheta+(Math.PI/2));
		yVelB = -bNormVelFinal*Math.sin(normTheta) + bTangentVel*Math.sin(normTheta+(Math.PI/2));
		
		bTheta = Math.atan2(yVelB, xVelB);
		
		double cTheta = bTheta + Math.PI/2;
		double dTheta = bTheta - Math.PI/2;
		
		bSpeed = Math.abs(Math.hypot(xVelB, yVelB));
		
		double explosion = GameObjectLibrary.getShatterCoef();
		
		double explodee = aNormVel-bNormVel;
		
		double xVelC = e*explodee*explosion*Math.cos(cTheta);
		double yVelC = e*explodee*explosion*Math.sin(cTheta);
		
		double xVelD = e*explodee*explosion*Math.cos(dTheta);
		double yVelD = e*explodee*explosion*Math.sin(dTheta);
		
		//xPosB = xPosB + sizeB*Math.cos(bTheta);
		//yPosB = yPosB + sizeB*Math.sin(bTheta);
		
		double simDeltaTime = ((5/1000)*StellarCrunch.getTIME_PER_MS());
		
		double xPosC = xPosB + (sizeB+sizeA)*Math.cos(cTheta);
		double yPosC = yPosB + (sizeB+sizeA)*Math.sin(cTheta);
		
		double xPosD = xPosB + (sizeB+sizeA)*Math.cos(dTheta);
		double yPosD = yPosB + (sizeB+sizeA)*Math.sin(dTheta);
		
		//xPosB = xPosB + 50*xVelB*simDeltaTime;
		//yPosB = yPosB + 50*yVelB*simDeltaTime;
		
		xPosB = xPosB + (sizeB+sizeA)*Math.cos(bTheta);
		yPosB = yPosB + (sizeB+sizeA)*Math.sin(bTheta);
		
		GameState.objectMap.get(i2).setValues(massB+200, xPosB, yPosB, xVelB, yVelB);
		
		GameState.objectMap.put(numberOfAsteroids, new GameObject());
		GameState.objectMap.get(numberOfAsteroids-1).setValues(massSplit-100, xPosC, yPosC, xVelC, yVelC);
		
		GameState.objectMap.get(numberOfAsteroids-1).collision = true;
		
		GameState.objectMap.put(numberOfAsteroids, new GameObject());
		GameState.objectMap.get(numberOfAsteroids-1).setValues(massSplit-100, xPosD, yPosD, xVelD, yVelD);
		
		GameState.objectMap.get(numberOfAsteroids-1).collision = true;
		
	}
	
	private void merge(int i2){
		double xPosB = GameState.objectMap.get(i2).getXPos();
		double yPosB = GameState.objectMap.get(i2).getYPos();
		double dx = (xPos - xPosB);
		double dy = (yPos - yPosB);
		double r = Math.hypot(dx, dy);
		
		double i2size = GameState.objectMap.get(i2).getSize();
		
		double massA = mass;
		double massB = GameState.objectMap.get(i2).getMass();
		double massTot = massA+massB;
		double xVelB = GameState.objectMap.get(i2).getXVel();
		double yVelB = GameState.objectMap.get(i2).getYVel();
		double sizeA = size;
		double sizeB = i2size;
		
		
		//The larger body will be the one to remain (this is all handled by the larger body), the other gameobject will be deleted
		//add x and y momentums:
		double xMom = (xVel*massA)+(xVelB*massB);
		double yMom = (yVel*massA)+(yVelB*massB);
		
		//Calculate new mass, size and new velocities
		mass = massTot;
		//colour = 'r';
		
		rgb = (colorBlend(rgb, GameState.objectMap.get(i2).rgb));
		
		fixSize();
		
		xVel = xMom/massTot;
		yVel = yMom/massTot;
		
		//calculate new center of mass to offset the movement from
		double xCent = xPos - ((dx*massB)/massTot);
		double yCent = yPos - ((dy*massB)/massTot);
		
		xPos =  xCent;
		yPos =  yCent;
		
		
		//remove the gameobject from the hash map
		GameState.objectMap.remove(i2);
		
	}
	
	
	public void shoot(double theta){
		double blobMass = GameObjectLibrary.getBlobMass();
		
		if ((GameState.objectMap.size() < maxAsteroids)&&(mass*blobMass>GameObjectLibrary.getPlayerMinMass())){
			//theta is the direction to shoot in:
			//theta = 0;
			double xMom = mass*xVel;
			double yMom = mass*yVel;
			
			double massB = mass*blobMass;
			
			double shotVel = GameObjectLibrary.getShotVelocity();
			
			double xVelB = shotVel*Math.cos(theta);
			xMom -= massB*xVelB;
			
			double yVelB = shotVel*Math.sin(theta);
			yMom -= massB*yVelB;
			
			mass -= massB;
			
			xVel = xMom/mass;
			yVel = yMom/mass;
			
			double xPosB = xPos + (4*size)*Math.cos(theta);
			double yPosB = yPos + (4*size)*Math.sin(theta);
			
			fixSize();
			
			GameState.objectMap.put(numberOfAsteroids, new GameObject());
			GameState.objectMap.get(numberOfAsteroids-1).setValues(massB, xPosB, yPosB, xVelB, yVelB);
		}
		
	}
	
	
	private void borderBounce(){
		double min = 0;
		double max = StellarCrunch.getScale();
		double softE = StellarCrunch.getSoftE();
		
		double e = GameObjectLibrary.getCoefficientOfRestitution();
		
		if (xPos - size <= min){
			xPos = size+(size-xPos);
			xPos = size;
			xVel = e*xVel*-1;
		}
		else if (xPos + size >= max){
			xPos = max-size-(xPos-(max-size));
			xPos = max-size;
			xVel = e*xVel*-1;
		}
		if (yPos - size <= min){
			yPos = size+(size-yPos);
			yPos = size;
			yVel = e*yVel*-1;
		}
		else if (yPos + size >= max){
			yPos = max-size-(yPos-(max-size));
			yPos = max-size;
			yVel = e*yVel*-1;
		}
	}
	
	private void borderScroll(){
		double min = 0;
		double max = StellarCrunch.getScale();
		
		while (xPos < min){
			xPos += max;
		}
		while (xPos > max){
			xPos -= max;
		}
		
		while (yPos < min){
			yPos += max;
		}
		while (yPos > max){
			yPos -= max;
		}
	}
	
	private static Color colorBlend(Color c0, Color c1) {		//http://www.java2s.com/Code/Java/2D-Graphics-GUI/Blendtwocolors.htm
		double totalAlpha = c0.getAlpha() + c1.getAlpha();
		double weight0 = c0.getAlpha() / totalAlpha;
		double weight1 = c1.getAlpha() / totalAlpha;

		double r = weight0 * c0.getRed() + weight1 * c1.getRed();
		double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
		double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
		double a = Math.max(c0.getAlpha(), c1.getAlpha());

		return new Color((int) r, (int) g, (int) b, (int) a);
	}	
	
}
