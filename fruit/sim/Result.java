package fruit.sim;

public class Result implements Comparable<Result> {
	private String distr;
	private String[] playerNames;
	private int[] totalscores;
	private double avgScore;
	private int bowlsize, numplayers, repeats;
	
	public Result(String distr, String[] playerNames, int[] totalscores, int repeats) {
		this.distr = distr;
		this.playerNames = playerNames;
		this.totalscores = totalscores;
		this.repeats = repeats;
	}
	
	public String distr()				{return distr;}
	public double avgScore()			{return avgScore;}
	public int bowlsize()				{return bowlsize;}
	public int numplayers()				{return numplayers;}
	
	public int compareTo(Result compRes) {
		return (int) (this.avgScore - compRes.avgScore());
	}
	
	public String toString() {
		String format = "%40s %10s";
		String s = String.format(format, "Distribution: " + distr, "  ----  "); 
		 for (int p = 0; p < playerNames.length; ++p) {
	            s += String.format("%10s %.2f", playerNames[p] + ":",  1.0 * totalscores[p] / repeats);
	        }
		 
		 
		return s;
	}
}
