package fruit.g4;

public class Vectors {
  public static float dot(float[] a, float[] b){
    float val = 0;
    for (int i = 0 ; i < a.length; i++){
      val += a[i] * b[i];
    }
    return val;
  }
  public static float[] castToFloatArray(int[] intArr){
    float[] tmp = new float[intArr.length];
    for (int i = 0 ; i < intArr.length; i++) tmp[i] = (float) intArr[i];
    return tmp;
  }
  public static float sum(float[] arr){
    float sum = 0;
    for (int i = 0 ; i < arr.length; i++){
      sum += arr[i];
    }
    return sum;
  }
}
