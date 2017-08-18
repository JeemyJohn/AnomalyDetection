# AnomalyDetection

This Project aim of implements most of Anomaly Detection Algorithms in Java.
If you want to contribute source code, please write Email to jeemy145@outlook.com, or you can add my WeChat Number: **JeemyJohn**

# 1. Isolation Forest

This algorithm is realized in package iforest.<br/> 
Algorithm Home Page: http://blog.csdn.net/u013709270/article/details/73436588

Source Paper: <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://cs.nju.edu.cn/zhouzh/zhouzh.files/publication/icdm08b.pdf <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://cs.nju.edu.cn/zhouzh/zhouzh.files/publication/tkdd11.pdf

Usage step:

1. **Create an object of class IForest**

```java
      IForest iForest = new IForest();
```

2. **Get samples and train**

```java
      double[][] samples = new double[1000][2];
      
      //get samples
      ...
      
      int[] ans = iForest.train(samples, 100);
```
&nbsp;&nbsp;&nbsp;&nbsp; We have two declaration of function train, the implementation of them are same to each other.
The only difference of them is one have default parameter. As the results of function train ans,
if ans[i]==0 means it's an Anomaly(or Isolation) Point, else a Normal Point.

3. **Predict a new sample**

&nbsp;&nbsp;&nbsp;&nbsp; If a sample does not in samples, we can use function predict to judge it a Normal point or not.

```java
     double[] sample = ...
     int label = iForest.predict(sample);
     System.out.println(label);
```


----------
对机器学习，人工智能感兴趣的小伙伴可以加我微信：JeemyJohn，我拉你进我的机器学习群（群里很多高手哦！），或者扫描二维码！当然你也可以关注我的公众号，点击链接：[燕哥带你学算法公众号团队简介](http://mp.weixin.qq.com/s/ny4LvsGsVk1Em30UwZsjWw)

![这里写图片描述](http://img.blog.csdn.net/20170818214456712?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzcwOTI3MA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)



