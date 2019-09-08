import java.util.*;
import java.lang.Math;
import java.io.*;

public class GameStats {
	public static double gameDuration = 0;		//milliseconds irl time
	public static double totalDuration = 0;
	public static double playerMass = 0;
	private static int currentLevel = 0;
	
	private static final String FILENAME = "highscores.txt";
	
	private static HashMap<Integer, HighScore> highscores = new HashMap<Integer, HighScore>();
	
	private static boolean newRecord = false;
	
	private static final boolean totGameTimeMode = false;
	
	
	public static void updateLive(double deltaTime){
		gameDuration += deltaTime;
		totalDuration += deltaTime;
		playerMass = GameState.playerMap.get(0).getMass();
		currentLevel = GameState.levelPos;
		//System.out.println(currentLevel);
		//updateHighscores();
		//System.out.println(highestLevel);
		//readHighscores();
	}
	
	public static void resetLive(){
		gameDuration = 0;
	}
	
	
	
	public static void readHighscores(){
		//https://www.mkyong.com/java/how-to-read-file-from-java-bufferedreader-example/
		
		
		try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {

			String sCurrentLine;
			int i = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				HighScore thisScore= new HighScore();
				
				
				String[] words=sCurrentLine.split(",");
				
				String levelTime = "";
				String gameTime = "";
				String playerName = "";
				
				if (words.length >= 2){
					levelTime = words[1];
				}
				
				if (words.length >= 3){
					gameTime = words[2];
				}
				if (words.length >= 4){
					playerName = words[3];
				}
				
				try{
					thisScore.setLevelTime(Double.parseDouble(levelTime));
					thisScore.setGameTime(Double.parseDouble(gameTime));
					thisScore.setName(playerName);
					//System.out.println(playerName);
				}catch(NumberFormatException e){
					System.out.println("error reading highscores.txt");
				}
				
				i++;
				highscores.put(i, thisScore);
			}
			
			//System.out.println("Done reading");

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void writeHighscores(){
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {

			String content = "";
			
			
			for(int i = 0; i<(GameState.maxLevel+1); i++){
				if (highscores.containsKey(i)){
					if (i!= 1){
						//content += "\n";
					}
					content += String.valueOf(i);
					content += ",";
					content += String.valueOf(highscores.get(i).getLevelTime());
					content += ",";
					content += String.valueOf(highscores.get(i).getGameTime());
					content += ",";
					//content += highscores.get(i).getName();
					String lines[] = highscores.get(i).getName().split("\\r?\\n");
					content += lines[0];
					
					//content += "default";
					content += "\n";
				}
				
			}

			bw.write(content);

			// no need to close it.
			//bw.close();

			//System.out.println("Done writing");

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
	
	
	
	public static void updateHighscores(){
		//orderHighScores();
		
		currentLevel -= 1;
		
		if (totGameTimeMode){
			if (highscores.containsKey(currentLevel)){
				if(highscores.get(currentLevel).getGameTime() > totalDuration){
					HighScore myHighScore = new HighScore();
					myHighScore.setLevelTime(gameDuration);
					myHighScore.setGameTime(totalDuration);
					myHighScore.setName(StellarCrunch.username);
					highscores.remove(currentLevel);
					highscores.put(currentLevel, myHighScore);
					writeHighscores();
					newRecord = true;
				}
			}
			else{
				HighScore myHighScore = new HighScore();
				myHighScore.setLevelTime(gameDuration);
				myHighScore.setGameTime(totalDuration);
				myHighScore.setName(StellarCrunch.username);
				highscores.put(currentLevel, myHighScore);
				writeHighscores();
				newRecord = true;
			}
		}
		else{
			if (highscores.containsKey(currentLevel)){
				if(highscores.get(currentLevel).getLevelTime() > gameDuration){
					HighScore myHighScore = new HighScore();
					myHighScore.setLevelTime(gameDuration);
					myHighScore.setGameTime(totalDuration);
					myHighScore.setName(StellarCrunch.username);
					highscores.remove(currentLevel);
					highscores.put(currentLevel, myHighScore);
					writeHighscores();
					newRecord = true;
				}
			}
			else{
				HighScore myHighScore = new HighScore();
				myHighScore.setLevelTime(gameDuration);
				myHighScore.setGameTime(totalDuration);
				myHighScore.setName(StellarCrunch.username);
				highscores.put(currentLevel, myHighScore);
				writeHighscores();
				newRecord = true;
			}
		}
		
		currentLevel += 1;
		
		
		//orderHighScores();
	}
	
	private static void orderHighScores(){
		if (highscores.containsKey(0)){
			highscores.remove(0);
		}
	}
	
	
	public static double getGameTime(){
		double ans = 0;
		
		if (totGameTimeMode){ans = totalDuration;}
		else{ans = gameDuration;}
		
		return ans;
	}
	
	public static boolean levelHasTime(){
		boolean ans = false;
		if (highscores.containsKey(currentLevel) == true){
			ans = true;
		}
		return ans;
	}
	
	public static double timeForLevel(){
		double ans = 0;
		
		if (totGameTimeMode){ans = highscores.get(currentLevel).getGameTime();}
		else{ans = highscores.get(currentLevel).getLevelTime();}
		
		return ans;
	}
	
	public static double timeForPrevLevel(){
		double ans = 0;
		
		if (totGameTimeMode){ans = highscores.get(currentLevel-1).getGameTime();}
		else{ans = highscores.get(currentLevel-1).getLevelTime();}
		
		return ans;
	}
	
	public static String highscoreName(){
		String ans = "";
		if (highscores.containsKey(currentLevel) == true){
			ans = highscores.get(currentLevel).getName();
		}
		return ans;
	}
	
	
	public static boolean isHighscore(){
		
		return newRecord;
	}
	
}
