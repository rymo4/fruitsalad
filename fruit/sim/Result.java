package fruit.sim;

public class Result implements Comparable<Result> {
	private String distr;
	private double avgScore;
	private int bowlsize, numplayers;
	
	public Result(String distr, double score, int bowlsize, int numplayers) {
		this.distr = distr;
		this.avgScore = score;
		this.bowlsize = bowlsize;
		this.numplayers = numplayers;
	}
	
	public String distr()				{return distr;}
	public double avgScore()			{return avgScore;}
	public int bowlsize()				{return bowlsize;}
	public int numplayers()				{return numplayers;}
	
	public int compareTo(Result compRes) {
		return (int) (this.avgScore - compRes.avgScore());
	}
	
	public String toString() {
		return "Distribution: " + distr + ", score: " + avgScore + ", bowlsize: " + bowlsize + ", numplayers: " + numplayers;
	}
}
