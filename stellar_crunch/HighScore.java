public class HighScore {
	//Instance variables:
	
	
	private double levelTime = 0;
	private double gameTime = 0;
	private String playerName;
	
	
	public void setLevelTime(double time){
		levelTime = time;
	}
	public double getLevelTime(){
		return levelTime;
	}
	
	
	
	public void setGameTime(double time){
		gameTime = time;
	}
	public double getGameTime(){
		return gameTime;
	}
	
	
	
	
	public void setName(String input){
		playerName = input;
		//System.out.println(playerName);
	}
	public String getName(){
		return playerName;
	}
	
}
