package iforest;

import java.util.Random;

/**
 * Created by Administrator on 2017/6/20.
 */
public class ITree {

    // 被选中的属性索引
    public int attrIndex;

    // 被选中的属性的一个具体的值
    public double attrValue;

    // 树的总叶子节点数
    public int leafNodes;

    // 该节点在树种的高度
    public int curHeight;

    // 左右孩子书
    public ITree lTree, rTree;

    // 构造函数，初始化ITree中的值
    public ITree(int attrIndex, double attrValue) {
        // 默认高度，树的高度从0开始计算
        this.curHeight = 0;

        this.lTree = null;
        this.rTree = null;
        this.leafNodes = 1;
        this.attrIndex = attrIndex;
        this.attrValue = attrValue;
    }

    /**
     * 根据samples样本数据递归的创建 ITree 树
     */
    public static ITree createITree(double[][] samples, int curHeight, int limitHeight) {

        ITree iTree = null;

        /*************** 第一步：判断递归是否满足结束条件 **************/
        if (samples.length == 0) {
            return iTree;
        } else if (curHeight >= limitHeight || samples.length == 1) {
            iTree = new ITree(0, samples[0][0]);
            iTree.leafNodes = 1;
            iTree.curHeight = curHeight;
            return iTree;
        }

        int rows = samples.length;
        int cols = samples[0].length;

        // 判断是否所有样本都一样，如果都一样构建也终止
        boolean isAllSame = true;
        break_label:
        for (int i = 0; i < rows - 1; i++) {
            for (int j = 0; j < cols; j++) {
                if (samples[i][j] != samples[i + 1][j]) {
                    isAllSame = false;
                    break break_label;
                }
            }
        }

        // 所有的样本都一样，构建终止，返回的是叶节点
        if (isAllSame == true) {
            iTree = new ITree(0, samples[0][0]);
            iTree.leafNodes = samples.length;
            iTree.curHeight = curHeight;
            return iTree;
        }


        /***************** 第二步：不满足递归结束条件，继续递归产生子树 ***************/
        Random random = new Random(System.currentTimeMillis());
        int attrIndex = random.nextInt(cols);

        // 找这个被选维度的最大值和最小值
        double min, max;
        min = samples[0][attrIndex];
        max = min;
        for (int i = 1; i < rows; i++) {
            if (samples[i][attrIndex] < min) {
                min = samples[i][attrIndex];
            }
            if (samples[i][attrIndex] > max) {
                max = samples[i][attrIndex];
            }
        }

        // 计算划分属性值
        double attrValue = random.nextDouble() * (max - min) + min;

        // 将所有的样本的attrIndex对应的属性与
        // attrValue 进行比较以选出左右子树对应的样本
        int lnodes = 0, rnodes = 0;
        double curValue;
        for (int i = 0; i < rows; i++) {
            curValue = samples[i][attrIndex];
            if (curValue < attrValue) {
                lnodes++;
            } else {
                rnodes++;
            }
        }

        double[][] lSamples = new double[lnodes][cols];
        double[][] rSamples = new double[rnodes][cols];

        lnodes = 0;
        rnodes = 0;
        for (int i = 0; i < rows; i++) {
            curValue = samples[i][attrIndex];
            if (curValue < attrValue) {
                lSamples[lnodes++] = samples[i];
            } else {
                rSamples[rnodes++] = samples[i];
            }
        }

        // 创建父节点
        ITree parent = new ITree(attrIndex, attrValue);
        parent.leafNodes = rows;
        parent.curHeight = curHeight;
        parent.lTree = createITree(lSamples, curHeight + 1, limitHeight);
        parent.rTree = createITree(rSamples, curHeight + 1, limitHeight);

        return parent;
    }
}
