package iforest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/6/20.
 */
public class IForest {

    // center0代表异常类中心,center1代表正常类中心
    private Double center0;
    private Double center1;

    // 样本集子采样的数目
    private int subSampleSize;

    // IForest中包含的ITree链表
    private List<ITree> iTreeList;

    /**
     * 无参构造函数，contamination设置为默认值0.1
     */
    public IForest() {
        this.center0 = null;
        this.center1 = null;
        this.subSampleSize = 256;
        this.iTreeList = new ArrayList<>();
    }

    /**
     * 训练模型，并返回所有样本的异常情况，正常为1，异常为-1
     */
    public int[] train(double[][] samples, int t) throws Exception {
        return train(samples, t, 256,100);
    }

    public int[] train(double[][] samples, int t, int subSampleSize, int iters) throws Exception {

        this.subSampleSize = subSampleSize;
        if (this.subSampleSize > samples.length) {
            this.subSampleSize = samples.length;
        }

        // 第一步：创建Isolation Forest
        createIForest(samples, t);

        // 第二步：计算所有样本的异常指数
        double[] scores = computeAnomalyIndex(samples);

        // 第三步：获取类标，并设置聚类中心
        int[] labels = classifyByCluster(scores, iters);
        return labels;
    }

    /**
     * 预测样本 sample 是否为异常值，正常返回1，异常返回-1
     */
    public int predict(double[] sample) throws Exception {
        double score = computeAnomalyIndex(sample);
        double dis0 = Math.abs(score - center0);
        double dis1 = Math.abs(score - center1);

        // 与哪个中心近说明改点被判断为哪一类
        if (dis0 > dis1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 通过使用聚类的思想，根据anomalyIndex进行分类获取类标
     */
    private int[] classifyByCluster(double[] scores, int iters) {

        // 两个聚类中心
        center0 = scores[0]; // 异常类的聚类中心
        center1 = scores[0]; // 正常类的聚类中心

        // 根据原论文，异常指数接近1说明是异常点，接近0为正常点。所以，将center0、center1分别初始化为
        // scores中的最大值和最小值。这样就相当于KMeans聚类的初始点的选择，解决了KMeans聚类的不稳定性。
        for (int i = 1; i < scores.length; i++) {
            if (scores[i] > center0) {
                center0 = scores[i];
            }

            if (scores[i] < center1) {
                center1 = scores[i];
            }
        }

        int cnt0, cnt1;
        double diff0, diff1;
        int[] labels = new int[scores.length];

        // 迭代聚类(迭代iters次)
        for (int n = 0; n < iters; n++) {
            // 判断每个样本的类别
            cnt0 = 0;
            cnt1 = 0;

            for (int i = 0; i < scores.length; i++) {
                // 计算当前点与两个聚类中心的距离
                diff0 = Math.abs(scores[i] - center0);
                diff1 = Math.abs(scores[i] - center1);

                // 根据与聚类中心的距离，判断类标
                if (diff0 < diff1) {
                    labels[i] = 0;
                    cnt0++;
                } else {
                    labels[i] = 1;
                    cnt1++;
                }
            }

            // 保存旧的聚类中心
            diff0 = center0;
            diff1 = center1;

            // 重新计算聚类中心
            center0 = 0.0;
            center1 = 0.0;
            for (int i = 0; i < scores.length; i++) {
                if (labels[i] == 0) {
                    center0 += scores[i];
                } else {
                    center1 += scores[i];
                }
            }

            center0 /= cnt0;
            center1 /= cnt1;

            // 提前迭代终止条件
            if (center0 - diff0 <= 1e-6 && center1 - diff1 <= 1e-6) {
                break;
            }
        }
        return labels;
    }

    /**
     * 创建IForest
     */
    private void createIForest(double[][] samples, int t) throws Exception {

        // 方法参数合法性检验
        if (samples == null || samples.length == 0) {
            throw new Exception("Samples is null or empty, please check...");
        } else if (t <= 0) {
            throw new Exception("Number of subtree t must be a positive...");
        } else if (subSampleSize <= 0) {
            throw new Exception("subSampleSize must be a positive...");
        }

        int limitHeight = (int) Math.ceil(Math.log(subSampleSize) / Math.log(2));

        ITree iTree;
        double[][] subSample;

        for (int i = 0; i < t; i++) {
            subSample = this.getSubSamples(samples, subSampleSize);
            iTree = ITree.createITree(subSample, 0, limitHeight);
            this.iTreeList.add(iTree);
        }
    }

    /**
     * 计算某样本集合的异常指数
     */
    private double[] computeAnomalyIndex(double[][] samples) throws Exception {

        // 参数合法性检查
        if (samples == null || samples.length == 0) {
            throw new Exception("Samples is null or empty, please check...");
        }

        double[] scores = new double[samples.length];
        for (int i = 0; i < samples.length; i++) {
            scores[i] = computeAnomalyIndex(samples[i]);
        }

        return scores;
    }


    /**
     * 计算某一个样本的异常指数
     */
    private double computeAnomalyIndex(double[] sample) throws Exception {

        if (iTreeList == null || iTreeList.size() == 0) {
            throw new Exception("iTreeList is empty，please create IForest...");
        } else if (sample == null || sample.length == 0) {
            throw new Exception("Sample is null or empty, please check...");
        }

        // 样本在所有iTree上的平均高度（改进后的）
        double ehx = 0;
        double pathLength = 0;
        for (ITree iTree : iTreeList) {
            pathLength = computePathLength(sample, iTree);
            ehx += pathLength;
        }
        ehx /= iTreeList.size();

        double cn = computeCn(subSampleSize);
        double index = ehx / cn;

        double anomalyIndex = Math.pow(2, -index);
        return anomalyIndex;
    }

    /**
     * 计算样本sample在ITree上的PathLength
     */
    private double computePathLength(double[] sample, final ITree iTree) throws Exception {

        // 参数合法性检查
        if (sample == null || sample.length == 0) {
            throw new Exception("Sample is null or empty, please check...");
        } else if (iTree == null || iTree.leafNodes == 0) {
            throw new Exception("iTree is null or empty, please check...");
        }

        double pathLength = -1;
        double attrValue;
        ITree tmpITree = iTree;

        while (tmpITree != null) {
            pathLength += 1;
            attrValue = sample[tmpITree.attrIndex];

            if (tmpITree.lTree == null || tmpITree.rTree == null || attrValue == tmpITree.attrValue) {
                break;
            } else if (attrValue < tmpITree.attrValue) {
                tmpITree = tmpITree.lTree;
            } else {
                tmpITree = tmpITree.rTree;
            }
        }

        return pathLength + computeCn(tmpITree.leafNodes);
    }


    /**
     * 随机选取子样本集合
     */
    private double[][] getSubSamples(double[][] samples, int sampleNum) throws Exception {

        // 判断参数是否合理
        if (samples == null || samples.length == 0) {
            throw new Exception("Samples is null or empty, please check...");
        } else if (sampleNum <= 0) {
            throw new Exception("Number of sampleNum must be a positive...");
        }

        if (samples.length < sampleNum) {
            sampleNum = samples.length;
        }
        int cols = samples[0].length;
        double[][] subSamples = new double[sampleNum][cols];

        int randomIndex;
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < sampleNum; i++) {
            randomIndex = random.nextInt(samples.length);
            subSamples[i] = samples[randomIndex];
        }

        return subSamples;
    }

    // 论文中的 C(n) 的计算方法
    private double computeCn(double n) {
        if (n <= 1) {
            return 0;
        }
        return 2 * (Math.log(n - 1) + 0.5772156649) - 2 * ((n - 1) / n);
    }
}
