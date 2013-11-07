package fruit.g5b;
import java.util.*;
import java.io.*;

public class Player extends fruit.sim.Player
{

	int nplayers, index, choice = -1, choicesleft, numfruits = 0, length;
	int[] pref, transform, distribution, observedlist, distsum;
	int[][] choicelist;
	double[][][] fruitproblist;
	double[][] combinationlist;

	long time01, time02, time03, time11 = 0, time12 = 0, time13 = 0;

	boolean createLog = true, initflag = true;
	PrintWriter outfile;

	double adjfactor = 1.0;

	public void init(int nplayers, int[] pref)
	{
		this.nplayers = nplayers;
		this.index = getIndex();
		this.pref = pref;
		length = pref.length;

		transform = new int[length];
		distribution = new int[length];
		observedlist = new int[length];
		distsum = new int[length];
		choicelist = new int[nplayers + 1][length];

		for(int i = 0; i < length; i++)
			transform[length - pref[i]] = i;
		
		/*
		if(createLog)
		{
			try{
				FileWriter fstream = new FileWriter("fruit/g5/log.txt", true);
				outfile = new PrintWriter(fstream);
			} catch (IOException e){ }

			
			outfile.println("Players    : " + Integer.toString(nplayers));
			outfile.println("Index      : " + Integer.toString(index));
			outfile.println("Pref       : " + Arrays.toString(pref));
			outfile.println("Transform  : " + Arrays.toString(transform));
			outfile.flush();
			
		} */
	}


	public boolean pass(int[] bowl, int bowlId, int round, boolean canPick, boolean musTake)
	{

		int[] plate = transform(bowl);
		incrementVariables(round, plate);
		updateDistribution(plate);

		if(musTake)
		{
			//outfile.println(Integer.toString(choice + 1) + " " + Integer.toString(choicesleft));
			//outfile.flush();
			return true;
		}
		if(!canPick)
			return false;

		double expectedscore = expectedScore(distribution,plate);

		if(expectedscore < plateScore(plate) * adjfactor)
		{
				//outfile.println(Integer.toString(choice + 1) + " " + Integer.toString(choicesleft));
				//outfile.flush();
				return true;
		}

		return false;
	}

	private int[] transform(int[] bowl)
	{
		int plate[] = new int[length];
		for(int i = 0; i < length; i++)
			plate[i] = bowl[transform[i]];
		return plate;
	}

	private void incrementVariables(int round, int[] plate)
	{
		choice++;
		if(round == 0)
			choicesleft = (nplayers - index - choice) - 1; //Excluding the current plate
		else
			choicesleft = (nplayers + 1 - choice) - 1;

		if(initflag)
		{
			for(int i = 0; i < length; i++)
					numfruits = numfruits + plate[i];
			if(numfruits > 20)
			{
					adjfactor = 20.0 / numfruits;
					numfruits = 20;
			}
			fruitproblist = new double[length][numfruits + 1][numfruits * length + 1];
			combinationlist = new double[numfruits * nplayers + 1][numfruits + 1];

			for(int i = 0; i <= numfruits * nplayers; i++)
			{
				combinationlist[i][0] = 1;
				for(int j = 1; j <= numfruits && j <= i; j++)
					combinationlist[i][j] = combinationlist[i][j - 1] * (i - j + 1) / j;
			}
			initflag = false;
		}
	}

	private void updateDistribution(int[] plate)
	{

		int sum = 0, max = 0;
		double normalfactor = 1.0 * numfruits * (choice + 1) / length;
		double normalweight = 0.6 - (1.2 * (choice + 1) / nplayers);
		choicelist[choice] = plate.clone();
		for(int i = 0; i < length; i++)
		{
			observedlist[i] = observedlist[i] + plate[i];
			if((choice + 1) * 2.5 >= (choicesleft + 1))
				distribution[i] = (int) Math.round(adjfactor * observedlist[i] * choicesleft / (choice + 1));
			else
				distribution[i] = (int) Math.round((adjfactor * observedlist[i] * (1 - normalweight) + normalfactor * normalweight) * choicesleft / (choice + 1));
			sum = sum + distribution[i];
			distsum[i] = sum;
			if(distribution[i] > distribution[max])
				max = i;
		}
		if(distsum[length - 1] != numfruits * choicesleft)
		{
			distribution[max] = distribution[max] + ((int) Math.round(numfruits * choicesleft)) - distsum[length - 1];
			sum = 0;
			for(int i = 0; i < length; i++)
			{
				sum = sum + distribution[i];
				distsum[i] = sum;
			}

		}
	}

	private int plateScore(int[] plate)
	{
		int score = 0;
		for(int i = 0; i < length; i++)
			score = score + plate[i] * (length - i);
		return score;
	}


	private double expectedScore(int[] distribution,int[] plate)
	{
		double[] problist = new double[numfruits * length + 1];
		int minscore = numfruits * 1, maxscore = numfruits * length;



		time01 = System.nanoTime();
		initFruitProbs();

		time11 += System.nanoTime() - time01;
		time02 = System.nanoTime();

		double expectedscore = 0.0;

		int increment = 1;
		/*
		int increment = (int) Math.ceil((maxscore - minscore) / 55.0);
		if(numfruits % 2 == 1)
			minscore = minscore + 1;
		if(increment % 2 == 1 && increment != 1)
			increment = increment - 1;
		*/

		for(int i = minscore; i <= maxscore; i = i + increment)
		{
			problist[i] = problist[i - increment] + getFruitProbs(0,(int) Math.round(numfruits),i) * increment;
			expectedscore = expectedscore + i * (Math.pow(problist[i], choicesleft) - Math.pow(problist[i - increment], choicesleft));
//			outfile.println(Integer.toString(i) + "\t" + Double.toString(problist[i] - problist[i - 1]));
		}
//		outfile.println("");
//		outfile.println(Integer.toString((int) expectedscore) + " " + Integer.toString((int) (plateScore(distribution) / choicesleft)) + " " + Integer.toString(choicesleft));
//		outfile.flush();

		time12 += System.nanoTime() - time02;

		return Math.floor(expectedscore);
	}

	private void initFruitProbs()
	{
		for(int i = 0; i < length; i++)
			for(int j = 0; j <= numfruits; j++)
				for(int k = 0; k <= numfruits * length; k++)
					fruitproblist[i][j][k] = -1.0;

		for(int i = 0; i < length; i++)
		{
			for(int k = 1; k <= numfruits * length; k++)
				fruitproblist[i][0][k] = 0.0;
			for(int j = 1; j <= numfruits; j++)
				fruitproblist[i][j][0] = 0.0;
			fruitproblist[i][0][0] = 1.0;
		}
	}

	public double getFruitProbs(int pos, int fruits, int score)
	{
		double prob = 0.0, innerprob, curprob;

		if(pos == length - 1)
		{
			if(fruits <= distribution[pos] && score == fruits)
				prob = 1.0;
		}

		else
		{
			for(int i = 0, s = 0; (i <= fruits) && (s <= score) && (i <= distribution[pos]); i++, s = s + (length - pos))
			{
				if(fruits - i <= (numfruits * choicesleft) - distsum[pos])
				{
					if(fruitproblist[pos + 1][fruits - i][score - s] < -0.1)
					{
						innerprob = getFruitProbs(pos + 1, fruits - i, score - s);
						fruitproblist[pos + 1][fruits - i][score - s] = innerprob;
					}
					else
						innerprob = fruitproblist[pos + 1][fruits - i][score - s];

					curprob = combinationlist[distribution[pos]][i] * combinationlist[(numfruits * choicesleft) - distsum[pos]][fruits - i] / combinationlist[(numfruits * choicesleft) - distsum[pos] + distribution[pos]][fruits];
					prob += curprob * innerprob;
				}
			}
		}
		return prob;
	}
}