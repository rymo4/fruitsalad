package fruit.g4;
import java.util.*;

class MLE {
  private int[][] occuranceHist;
  private int numFruitsPerBowl;
  private int NUM_FRUIT_TYPES = 12;
  private int numPlayers;

  public MLE(int numFruitsPer, int nplayers){
    numPlayers = nplayers;
    numFruitsPerBowl = numFruitsPer;
    occuranceHist = new int[NUM_FRUIT_TYPES][numFruitsPerBowl];
  }

  public void addObservation(float[] bowl){
    for (int i = 0; i < NUM_FRUIT_TYPES; i++){
      occuranceHist[i][(int) bowl[i]] += 1;
    }
  }

  // Discrete gaussian roughly sigma = 1
  private float gaussian(int sample, int mean){
    int diff = Math.abs(mean - sample);

    // .46 + 2(.22) + 2(.04) + 2(0.01) = 1.0
    if (diff == 0) return 0.46f;
    else if (diff == 1) return 0.22f;
    else if (diff == 2) return 0.04f;
    else if (diff == 3) return 0.01f;
    return 0f;
  }

  public float[] distribution(){
    float[] bowl = new float[NUM_FRUIT_TYPES];
    for (int i = 0; i < NUM_FRUIT_TYPES; i++){ // each fruit
      bowl[i] = fruitOccuranceMLE(i);
    }
    return Vectors.normalize(bowl);
  }

  public float fruitOccuranceMLE(int fruit) {
    float[] gaussianArr = new float[numFruitsPerBowl];
    for (int i = 0; i < numFruitsPerBowl; i++){
      // num times seen * gaussian probability
      int numTimesSeenIFruitsInBowl = occuranceHist[fruit][i];
      float gaussianProbSum = 0f;

      for (int j = 0; j < numFruitsPerBowl; j++){ //TODO: just loop across 3 on each side
        gaussianProbSum += gaussian(fruit, j);
      }

      gaussianArr[i] = gaussianProbSum * numTimesSeenIFruitsInBowl;
    }
    return Vectors.maxIndex(gaussianArr);
  }

  // Get MLE for a platter by inferring from bowls that youve seen
  private float[] platter(){
    return Vectors.scale(
      distribution(),
      (float) (numPlayers * numFruitsPerBowl)
    );
  }

  private float numOccurances(int fruit){
    float num = 0f;
    for (int i = 0; i < occuranceHist[fruit].length; i++){
      num += occuranceHist[fruit][i] * i;
    }
    System.out.println("Num: " + num);
    return num;
  }

  //TODO: Sample many bowls in same way that sim does, return the average
  public float[] bowl(boolean firstRound){
    // Count
    float[] plat = platter();
    for (int i = 0; i < plat.length; i++){
      if (firstRound)
        plat[i] -= numOccurances(i);
      else
        // If second round, we have occurances from first, so dont subtract them
        plat[i] -= numOccurances(i) / 2;
    }
    return Vectors.scale(
      Vectors.normalize(plat),
      (float) numFruitsPerBowl
    );
  }

}
