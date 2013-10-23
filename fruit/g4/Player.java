package fruit.g4;

import java.util.*;

public class Player extends fruit.sim.Player
{
  private float[] prefs;
  private float[] platter;

  public void init(int nplayers, int[] pref) {
    prefs = Vectors.castToFloatArray(pref);
    platter = new float[pref.length];
  }

  public boolean pass(int[] bowl, int bowlId, int round,
                      boolean canPick,
                      boolean mustTake) {
    float[] currentBowl = Vectors.castToFloatArray(bowl);
    float score = score(currentBowl);

    float numFruits = Vectors.sum(currentBowl);

    float[] uniformBowl = new float[currentBowl.length];
    for (int i = 0 ; i < bowl.length; i++){
      uniformBowl[i] = numFruits / bowl.length;
    }
    float uniformScore = score(uniformBowl);

    System.out.println("Uniform Score: " + uniformScore);
    System.out.println("Score: " + score);
    return uniformScore < score;
  }

  private float score(float[] bowl){
    return Vectors.dot(bowl, prefs);
  }

  private Random random = new Random();
}
