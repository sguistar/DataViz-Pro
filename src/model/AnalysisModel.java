package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnalysisModel
{

    //获取平均值
    public static double getMean(List<Integer> data)
    {
        return data.stream().mapToDouble(a -> a).average().orElse(0);
    }

    //获取中位数
    public static double getMedian(List<Integer> data)
    {
        List<Integer> copy = new ArrayList<>(data);
        Collections.sort(copy);
        int n  = copy.size();
        if(n % 2 != 0) return copy.get(n/2);
        else return (copy.get(n/2 - 1) + copy.get(n/2)) / 2.0;
    }

    //获取方差
    public static double getVariance(List<Integer> data)
    {
        double mean = getMean(data);
        return data.stream().mapToDouble(i -> (i - mean) * (i - mean)).sum() / data.size();
    }

    // 标准差
    public static double getStdDeviation(List<Integer> data)
    {
        return Math.sqrt(getVariance(data));
    }

    // 数据范围
    public static String getRange(List<Integer> data)
    {
        return "[" +  Collections.min(data).toString() + " - " + Collections.max(data).toString() + "]";
    }

    // 四分位数：返回 Q1、Median、Q3
    public static double[] getQuartiles(List<Integer> data)
    {
        List<Integer> copy = new ArrayList<>(data);   // 避免破坏原顺序
        Collections.sort(copy);
        int n = copy.size();
        double q1 = copy.get(n / 4);
        double median = getMedian(copy);
        double q3 = copy.get(3 * n / 4);
        return new double[]{q1, median, q3};
    }

    // 区间过滤
    public static List<Integer> filterByRange(List<Integer> src, int min, int max)
    {
        return src.stream()
                .filter(v -> v >= min && v <= max)
                .toList();                       // Java 17+ Collectors.toList()
    }

    // IQR 离群值剔除  (1.5×IQR 规则)
    public static List<Integer> removeOutliers(List<Integer> src)
    {
        if(src.size() < 4) return new ArrayList<>(src);             // 太少就不剔了
        List<Integer> sorted = new ArrayList<>(src);
        Collections.sort(sorted);

        double q1 = sorted.get(sorted.size()/4);
        double q3 = sorted.get(3*sorted.size()/4);
        double iqr = q3 - q1;
        double low  = q1 - 1.5 * iqr;
        double high = q3 + 1.5 * iqr;

        return sorted.stream()
                .filter(v -> v >= low && v <= high)
                .toList();
    }

}
