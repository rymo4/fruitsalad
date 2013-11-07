package fruit.g4;
import java.util.*;

public class Stats {

  private ArrayList<Float> data;
    
  public Stats() {
    data = new ArrayList<Float>();
  }

  public void addData(float point) {
    data.add(point);
  }

  public float mean() {
    float sum = 0;
    for (float x : data) {
      sum += x;
    }
    return sum / data.size();
  }

  public float variance() {
    float mean = mean();
    float totalDiff = 0;
    for (float x : data) {
      totalDiff += (mean - x) * (mean - x);
    }
    return totalDiff / data.size();
  }

  public float standardDeviation() {
    return (float) Math.sqrt(variance());
  }

  public float median() {
    Collections.sort(data);
    if (data.size() % 2 == 0) {
      return (data.get(data.size() / 2 - 1) + data.get(data.size())) / 2;
    }
    return data.get(data.size() / 2);
  }
}
