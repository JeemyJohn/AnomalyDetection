import iforest.IForest;

import java.util.Random;

/**
 * Created by Administrator on 2017/6/20.
 */
public class Test {

    public static void main(String[] args) {

        double[][] samples = new double[1000][2];
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < 1000; i++) {
            if (i < 990) {
                samples[i][0] = random.nextDouble() * 10;
                samples[i][1] = random.nextDouble() * 10;
            } else {
                samples[i][0] = random.nextDouble() + 100;
                samples[i][1] = random.nextDouble() + 100;
            }
        }

        IForest iForest = new IForest();
        try {
            int[] labels = iForest.train(samples, 100);
            int n = 0;
            for (int label : labels) {
                System.out.print(label + " ");
                if (n % 90 == 89)
                    System.out.println();
                n++;
            }

            System.out.println();
            double[] sample = {50, 50};
            int label = iForest.predict(sample);
            System.out.println(label);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}