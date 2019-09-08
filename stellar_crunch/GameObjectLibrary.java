public class GameObjectLibrary {
// Class for defining various game objects, and putting them together to create content
// for the game world.  Default assumption is objects face in the direction of their velocity, and are spherical.

    // UNIVERSE CONSTANTS - TUNED BY HAND FOR RANDOM GENERATION
    private static final double ASTEROID_RADIUS = 0.35; // Location of asteroid belt for random initialization
    private static final double ASTEROID_WIDTH = 0.05; // Width of asteroid belt
	private static final boolean PLANAR_ROTATION = true;		
	private static final double PLANAR_ROTATIONAL_VELOCITY = 0.0000001;			//in radians per second
	private static final double ASTEROID_MIN_MASS = 6.5E25;
    private static final double ASTEROID_MAX_MASS = 6.5E25;
	private static final boolean ASTEROID_SIZE_CUBIC_SCALE = true;
	private static final double ASTEROID_SIZE_SCALE = 1.4;	//Coefficient of mass to size
    private static final double PLAYER_MASS = 6E25;
	private static final int MIN_NUMBER_ASTEROIDS = 15;		//besides the player...
	private static final int MAX_NUMBER_ASTEROIDS = 25;
	
	
	private static final double playerMinMass = 5E23;						//go below this and you die...
	private static final double blobMass = 0.2;								//Amount of player mass lost per shot
	private static final double shotVelocity = 30000;						//Velocity the your blobMass gets shot at
	private static final double shatterThreshold = 20000;					//Delta normal velocity required to cause shattering
	private static final boolean interAsteroidalCollisionDetection = true;	//Asteroids will collide and merge or shatter	!!!!!!!!!!!!!!!!public
	private static final double coefficientOfRestitution = 0.5;				//1: bouncy, 0: sticky
	private static final double dragCoefficient = 0.0002;					//drag coefficient 
	private static final double shatterCoef = 1.5;							//4000
	private static final int shatterPieces = 10;								//Maximum number of pieces that asteroids can shatter into
	
	private static final boolean borderBounce = false;						//Asteroids will bounce of the edge of the window
	private static final boolean borderScroll = false;						//Asteroids will enter the screen on the opposite side to the side they exited
	
	
	//https://docs.oracle.com/javase/tutorial/java/javaOO/variables.html (got the idea to return the private values via public access methods)
	
	public static double getASTEROID_RADIUS(){
		return ASTEROID_RADIUS;
	}
	
	public static double getASTEROID_WIDTH(){
		return ASTEROID_WIDTH;
	}
	
	public static boolean getPLANAR_ROTATION(){
		return PLANAR_ROTATION;
	}
	
	public static double getPLANAR_ROTATIONAL_VELOCITY(){
		return PLANAR_ROTATIONAL_VELOCITY;
	}
	
	public static double getASTEROID_MAX_MASS(){
		return ASTEROID_MAX_MASS;
	}
	
	public static double getASTEROID_MIN_MASS(){
		return ASTEROID_MIN_MASS;
	}
	
	public static boolean getASTEROID_SIZE_CUBIC_SCALE(){
		return ASTEROID_SIZE_CUBIC_SCALE;
	}
	
	public static double getASTEROID_SIZE_SCALE(){
		return ASTEROID_SIZE_SCALE;
	}
	
	public static double getASTEROID_MASS(){
		double mass = (Math.random()*(ASTEROID_MAX_MASS-ASTEROID_MIN_MASS)) + ASTEROID_MIN_MASS;
		return mass;
	}
	
	public static double getPLAYER_MASS(){
		return PLAYER_MASS;
	}
	
	public static int getNUMBER_ASTEROIDS(){
		int number = (int)(Math.random()*(MAX_NUMBER_ASTEROIDS-MIN_NUMBER_ASTEROIDS)) + MIN_NUMBER_ASTEROIDS;
		return number;
	}
	
	public static boolean getBorderBounce(){
		return borderBounce;
	}
	
	public static boolean getBorderScroll(){
		return borderScroll;
	}
	
	public static double getShatterThreshold(){
		return shatterThreshold;
	}
	
	public static boolean getInterAsteroidalCollisionDetection(){
		return interAsteroidalCollisionDetection;
	}
	
	public static double getCoefficientOfRestitution(){
		return coefficientOfRestitution;
	}
	
	public static double getDragCoefficient(){
		return dragCoefficient;
	}
	
	public static double getShatterCoef(){
		return shatterCoef;
	}
	
	public static double getBlobMass(){
		return blobMass;
	}
	
	public static double getShotVelocity(){
		return shotVelocity;
	}

	public static double getPlayerMinMass(){
		return playerMinMass;
	}
	
	}
