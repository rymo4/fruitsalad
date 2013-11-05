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
  private Stats scoreStats;

  private MLE mle;


  public void init(int nplayers, int[] pref) {
    numPlayers = nplayers;
    prefs = Vectors.castToFloatArray(pref);
    prefsInt = pref;
    platter = new float[pref.length];
    bowlsRemaining = (float)(nplayers - getIndex());
    totalNumBowls = bowlsRemaining;
    scoreStats = new Stats();
    System.out.println(getIndex());
  }

  public boolean pass(int[] bowl, int bowlId, int round, boolean canPick, boolean mustTake) {
    float[] currentBowl = Vectors.castToFloatArray(bowl);
    numFruits = Vectors.sum(currentBowl);
    if (!canPick){
      log("CANT PICK RETURNING EARLY");
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
    scoreStats.addData(score);

    // get MLE and score it
    float[] uniformBowl = new float[currentBowl.length];
    for (int i = 0 ; i < bowl.length; i++){
      uniformBowl[i] = numFruits / bowl.length;
    }
    float uniformScore = score(uniformBowl);

    log("Uniform Score: " + uniformScore);
    log("MLE Score: " + score(mle.bowl(round == 0)));
    log("Score: " + score);
    float[] mleBowl = mle.bowl(round == 0);
    float[] mlePlatter = mle.platter();
    float maxScore = maxScore(mlePlatter);
    boolean take = shouldTakeBasedOnScore(score, score(mleBowl), maxScore);
    bowlsRemaining--;
    return take;
  }

  private boolean shouldTakeBasedOnScore(float currentScore, float mle, float maxScore){
    // based on number of bowls remaining to pass you, decide if you should take
    if (currentScore < mle) return false;
    float diff = maxScore - mle;
    float percentage = 0.1f; // OPTIMIZE THIS NUMBER!!!
    float upperBound = mle + (percentage * diff);
    float currentPercent = (bowlsRemaining + 1) / (totalNumBowls + 1);
    assert currentPercent <= 1f;
    assert currentPercent >= 0f;

    float currentThresh = mle + ((upperBound - mle) * currentPercent);
    log("maxScore: " + maxScore);
    log("upperBound: " + upperBound);
    log("currentThresh: " + currentThresh);

    return currentScore >= currentThresh;
  }

  private float maxScore(float[] mlePlatter) {
    float fruitsTaken = 0;
    float maxScore = 0;
    float currentPref = prefs.length;
    while (fruitsTaken < numFruits && currentPref > 0) {
      int currentFruit = indexOf(prefs, currentPref);
      if (numFruits - fruitsTaken < mlePlatter[currentFruit]) {
        maxScore += (numFruits - fruitsTaken) * currentPref;
        fruitsTaken = numFruits;
      } else {
        maxScore += mlePlatter[currentFruit] * currentPref;
        fruitsTaken += mlePlatter[currentFruit];
      }
      currentPref--;
    }
    return maxScore;
  }

  private int indexOf(float[] a, float x) {
    for (int i = 0; i < a.length; i++) {
      if (a[i] == x) {
        return i;
      }
    }
    return -1;
  }

  private float score(float[] bowl){
    return Vectors.dot(bowl, prefs);
  }

  private Random random = new Random();

  private void log(String str){
    System.out.println("| " + str);
  }
}
