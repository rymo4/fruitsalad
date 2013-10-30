package fruit.g4;

import java.util.*;

public class Player extends fruit.sim.Player
{
  private float[] prefs;
  private int[] prefsInt;
  private float[] platter;
  private float numFruits = 0;
  private float bowlsRemaining;
  private float totalNumBowls;
  private int numPlayers;

  private MLE mle;


  public void init(int nplayers, int[] pref) {
    numPlayers = nplayers;
    prefs = Vectors.castToFloatArray(pref);
    prefsInt = pref;
    platter = new float[pref.length];
    bowlsRemaining = (float)(nplayers - getIndex());
    totalNumBowls = bowlsRemaining;
  }

  public boolean pass(int[] bowl, int bowlId, int round, boolean canPick, boolean mustTake) {
    float[] currentBowl = Vectors.castToFloatArray(bowl);
    numFruits = Vectors.sum(currentBowl);
    if (!canPick){
      return false;
    }

    log("|||||||||||||||||||||||||||||||||||||||||");
    log("Number of bowls that will pass: " + totalNumBowls);
    log("Number of bowls remaining: " + bowlsRemaining);

    // Initialize the histogram now that we know how many fruit come in a bowl
    if (mle == null){
      mle = new MLE((int) numFruits, numPlayers);
    }
    mle.addObservation(currentBowl);

    // calculate score for the bowl the we get
    float score = score(currentBowl);

    // get MLE and score it
    float[] uniformBowl = new float[currentBowl.length];
    for (int i = 0 ; i < bowl.length; i++){
      uniformBowl[i] = numFruits / bowl.length;
    }
    float uniformScore = score(uniformBowl);

    log("Uniform Score: " + uniformScore);
    log("MLE Score: " + score(mle.bowl(round == 0)));
    log("Score: " + score);
    bowlsRemaining--;
    return shouldTakeBasedOnScore(score, score(mle.bowl(round == 0)));
  }

  private boolean shouldTakeBasedOnScore(float currentScore, float mle){
    // based on number of bowls remaining to pass you, decide if you should take
    if (currentScore < mle) return false;

    float diff = maxScore() - mle;

    float threshold = (0.3f * diff * (numPlayers / 9.0f * (totalNumBowls - 1) / bowlsRemaining)) + mle;
    log("Threshold: " + threshold);
    log("|||||||||||||||||||||||||||||||||||||||||");
    return currentScore > threshold;
  }

  private float maxScore(){
    int numBowlsSeen = (int) totalNumBowls - (int) bowlsRemaining;
    int cutoff = 3;

    if (numBowlsSeen > cutoff) {
      float score = 0;
      int pref = 12;
      int mostCanSee = -1;
      while (mostCanSee < numFruits) {
        int maxPrefFruit = Vectors.indexOf(prefsInt, pref);
        int mostOFThisFruit = mle.mostCanSee(maxPrefFruit);
        score += mostOFThisFruit * pref;
        mostCanSee += mostOFThisFruit;
        pref--;
      }
      return score;
    }
    return numFruits * 12;
  }

  private float score(float[] bowl){
    return Vectors.dot(bowl, prefs);
  }

  private Random random = new Random();

  private void log(String str){
    System.out.println("| " + str);
  }
}
