# AnomalyDetection

This Project aim of realize most of Anomaly Detection Algorithms in Java

# 1. Isolation Forest Algorithm in package iforest

Algorithm Home Page: http://blog.csdn.net/u013709270/article/details/73749310

Usage step:

1. Create an object of class IForest

```java
      IForest iForest = new IForest();
```

2. Get samples and train

```java
      double[][] samples = new double[1000][2];
      
      //get samples
      ...
      
      int[] ans = iForest.train(samples, 100);
```
We have two declaration of function train, the implementation of them are same to each other.
The only difference of them is one have default parameter. As the results of function train ans,
if ans[i]==0 means it's an Anomaly(or Isolation) Point, else a Normal Point.

3. Predict a new sample

If a sample does not in samples, we can use function predict to judge it a Normal point or not.

```java
     double[] sample = ...
     int label = iForest.predict(sample);
     System.out.println(label);
```






