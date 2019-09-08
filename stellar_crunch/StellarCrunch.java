/* Acknowledgements/notes:
 - Some of this code based on code for Rubrica by Steve Kroon
 - Original inspiration idea for this project was IntelliVision's AstroSmash, hence the name
*/

/* Ideas for extensions/improvements:
PRESENTATION:
-theme your game
-hall of fame/high score screen
-modifiable field of view, rear-view mirror, enhance first-person display by showing extra information on screen
-mouse control
-autoscaling universe to keep all universe objects on screen (or making the edge of the universe repel objects)
-better rendering in camera (better handling of objects on edges, and more accurate location rendering
-improved gameplay graphics, including pictures/sprites/textures for game objects
-add sounds for for various game events/music: Warning: adding both sounds and music will likely lead to major
 headaches and frustration, due to the way the StdAudio library works.  If you go down this route, you choose
 to walk the road alone...
-full 3D graphics with 3D universe (no libraries)

MECHANICS/GAMEPLAY CHANGES:
-avoid certain other game objects rather than/in addition to riding into them
-more interactions - missiles, auras, bombs, explosions, shields, etc.
-more realistic physics for thrusters, inertia, friction, momentum, relativity?
-multiple levels/lives
-energy and hit points/health for game objects and players
-multi-player mode (competitive/collaborative)
-checking for impacts continuously during moves, rather than at end of each time step
-Optimize your code to be able to deal with more objects (e.g. with a quad-tree) - document the improvement you get
--QuadTree implementation with some of what you may want at : http://algs4.cs.princeton.edu/92search/QuadTree.java.html
--https://github.com/phishman3579/java-algorithms-implementation/blob/master/src/com/jwetherell/algorithms/data_structures/QuadTree.java may also be useful - look at the Point Region Quadtree
*/
import java.awt.*;				//FK can i import this?

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;
public class StellarCrunch {
    
    // Main game class

    // CONSTANTS TUNED FOR GAMEPLAY EXPERIENCE
    private static final int GAME_DELAY_TIME = 5000; // in-game time units between frame updates FK:i changed this to public
    private static int TIME_PER_MS = 1000000; 			// how long in-game time corresponds to a real-time millisecond
    private static final double G = 6.67e-11; 		// gravitational constant FK: made this public
    private static final double softE = 0.001; 				// softening factor to avoid division by zero calculating force for co-located objects
    private static final double scale = 5e10; 						// plotted universe size
	
	private static final int maxFPS = 200;
	private static final int screenRes = 900;
	
	
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private static double lastTime = System.nanoTime();;
	
	public static String username = "";
	private static boolean gotName = false;
	
	public static int getTIME_PER_MS(){
		return TIME_PER_MS;
	}
	
	public static double getG(){
		return G;
	}
	
	public static double getSoftE(){
		return softE;
	}
	
	public static double getScale(){
		return scale;
	}

	public static void main(String[] args) {
		
		//http://stackoverflow.com/questions/11871520/how-can-i-read-input-from-the-console-using-the-scanner-class-in-java
		//System.out.println("Enter your username: ");
		Scanner scanner = new Scanner(System.in);
		//username = scanner.nextLine();
		//System.out.println("Your username is " + username);
		
		StdDraw.setCanvasSize(screenRes, screenRes);
		
		while(gotName == false){
			getUserNameScreen();
		}
		
		
		GameStats.readHighscores();
		/*
		try{
			while (true) {
				Thread.sleep(50);
				stateIteration();
			}
		}
		catch (Exception e){}
		*/
		/*
		try
		{
			StellarCrunch obj = new StellarCrunch ();
			obj.run (args);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		*/
		
		int frameTime = (int)((1000000000/maxFPS));
		
		int TIME_PER_MS_P = TIME_PER_MS;
		
		
		
		final Runnable frame = new Runnable() {
			public void run() {
				//calculate delta time i got help from : https://gamedev.stackexchange.com/questions/111741/calculating-delta-time
				
				double time = (double)System.nanoTime();
				double deltaTime = (double)((time - lastTime)/1000000);
				lastTime = time;
				
				if (StdDraw.isKeyPressed(70) == true){
					TIME_PER_MS = 10*TIME_PER_MS_P;
				}
				else{
					TIME_PER_MS = TIME_PER_MS_P;
				}
				
				GameState.update(deltaTime);	//milliseconds between frames
			}
		};
		
		
		
		final ScheduledFuture<?> frameHandle = scheduler.scheduleAtFixedRate(frame, 0, frameTime, TimeUnit.NANOSECONDS);
		
	}
	/*
	public void run (String[] args) throws Exception{
		
		gameFrame();
	}
	public void gameFrame() {		//Beeper control example helped me with this section: https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ScheduledExecutorService.html
		
		
		//scheduler.schedule(new Runnable() {public void run() {frameHandle.cancel(true);}}, 1, TimeUnit.SECONDS);	//To kill the schedule after a given time...
	}
	*/

	
	private static void getUserNameScreen(){
		StdDraw.enableDoubleBuffering();
		StdDraw.setScale(0, 1);
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
		double y1 = 0.7;
		double y2 = 0.65;
		
		StdDraw.line(x1, y1, x2, y1);
		StdDraw.line(x1, y2, x2, y2);
		StdDraw.line(x1, y1, x1, y2);
		StdDraw.line(x2, y1, x2, y2);
		
		//Instructions:
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		Font font = new Font("Comic Sans MS", Font.PLAIN, 20);		//require "import java.awt.*;"
        StdDraw.setFont(font);
		StdDraw.text(0.5, 0.75, "please enter your name");
		
		if(StdDraw.isKeyPressed(10)){
			String lines[] = username.split("\\r?\\n");
			username = lines[0];
			
			gotName = true;
		}
		
		
		
		getUserName();
		StdDraw.text(0.5, 0.675, username);
		
		
		
		StdDraw.show();
	}
	
	private static void getUserName(){
		
		if(StdDraw.isKeyPressed(8)){
			username = removeLast(username, 1);
		}
		if (StdDraw.hasNextKeyTyped()){
			username += StdDraw.nextKeyTyped();
		}
		
	}
	
	private static String removeLast(String s, int n) {
    if (null != s && !s.isEmpty()) {
        s = s.substring(0, s.length()-n);
    }
    return s;
}
	
	
}
