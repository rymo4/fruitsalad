package fruit.g4;

import java.util.*;

public class Player extends fruit.sim.Player
{
  private int[] prefs;
  private int[] platter;

  public void init(int nplayers, int[] pref) {
    prefs = pref;
    platter = new int[pref.length];
  }

  public boolean pass(int[] bowl, int bowlId, int round,
                      boolean canPick,
                      boolean mustTake) {
    int score = score(bowl);
    int numFruits = 0;
    for (int i = 0 ; i < bowl.length; i++){
      numFruits += bowl[i];
    }
    int[] uniformBowl = new int[bowl.length];

    for (int i = 0 ; i < bowl.length; i++){
      uniformBowl[i] = numFruits / bowl.length;
    }

    int uniformScore = score(uniformBowl);
    System.out.println("uniformScore: " + uniformScore);
    System.out.println("score: " + score);
    return uniformScore < score;
  }

  private int score(int[] bowl){
    int score = 0;
    for (int i = 0 ; i < bowl.length; i++){
      score += bowl[i] * prefs[i];
    }
    return score;
  }

  private Random random = new Random();
}
