package model;

import java.util.Collections;
import java.util.List;

public class AnalysisModel
{
    public static double getMean(List<Integer> data)
    {
        return data.stream().mapToDouble(a -> a).average().orElse(0);
    }

    public static double getMedian(List<Integer> data)
    {
        Collections.sort(data);
        int n  = data.size();
        if(n % 2 != 0) return data.get(n/2);
        else return (data.get(n/2 - 1) + data.get(n/2)) / 2.0;
    }

    public static double getVariance(List<Integer> data)
    {
        double mean = getMean(data);
        return data.stream().mapToDouble(i -> (i - mean) * (i - mean)).sum() / data.size();
    }

}
