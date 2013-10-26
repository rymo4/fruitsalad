package fruit.g1;

public class FruitGenerator implements fruit.sim.FruitGenerator
{
    public int[] generate(int nplayers, int bowlsize) {
        int nfruits = nplayers * bowlsize;
        
        // first randomly select one fruit to be guaranteed absent
        int r1 = (int) (Math.random() * 12);

        // fill distribution with random number from 1 to nfruits
        // disregarding the fruit we are omitting
        int[] dist = new int[12];
        int avg = 0;
        for (int i = 0; i < dist.length; i++) {
            if (i != r1) {
                dist[i] = (int) (Math.random() * nfruits);
                avg += dist[i];
            }
            else {
                dist[i] = 0;
            }
        }
        avg /= nfruits;
        
        // divide out each value by the average to "ensure" the values
        // sum to nfruits
        int sum = 0;
        for (int i = 0; i < dist.length; i++) {
            dist[i] /= avg;
            sum += dist[i];
        }
        
        // if the values do not sum to nfruits, compute the offset
        // and correct it
        int diff = sum - nfruits;
        if (diff != 0) {
            int sign = diff / Math.abs(diff);
            int numChanged = 0;
            while (numChanged < Math.abs(diff)) {
                for (int i = 0; i < dist.length; i++) {
                    if (numChanged == Math.abs(diff)) break;
                    if (dist[i] > 1) {
                        dist[i] -= sign;
                        numChanged++;
                    }
                }
            }
        }
        
        return dist;
    }
}
