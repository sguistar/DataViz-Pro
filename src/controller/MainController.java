package controller;

import model.*;
import view.MainFrame;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainController
{
    private final DataModel dataModel;
    private final MainFrame mainFrame;
    private List<Integer> originalData = new ArrayList<>();
    private List<Integer> filteredData = new ArrayList<>();

    public MainController()
    {
        dataModel = new DataModel();
        mainFrame = new MainFrame(this);
    }

    /** 提供给 MainFrame 的过滤调用 */
    public void applyFilter(Integer min, Integer max, boolean removeOutliers)
    {
        List<Integer> tmp = originalData;
        if(min != null && max != null)
        {
            tmp = AnalysisModel.filterByRange(tmp, min, max);
        }
        if(removeOutliers)
        {
            tmp = AnalysisModel.removeOutliers(tmp);
        }
        filteredData = new ArrayList<>(tmp);
        refreshUI();
    }

    public void readCSV(String filePath)
    {
        try
        {
            originalData  = CsvUtils.readInts(filePath);
            filteredData  = new ArrayList<>(originalData);
            dataModel.setData(originalData);   // 同步给 DataModel
            refreshUI();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "文件读取出错: " + e.getMessage());
        }
    }

    private void refreshUI()
    {
        mainFrame.updateTable(filteredData);
        mainFrame.updateChart(filteredData);
    }

    public void sortData()
    {
        Collections.sort(filteredData);   // 排当前可见数据
        refreshUI();
    }
    public void analyzeData(String type)
    {
        List<Integer> src = filteredData;   // ← 统一入口

        switch (type)
        {
            case "平均值" -> show("平均值", AnalysisModel.getMean(src));
            case "中位数" -> show("中位数", AnalysisModel.getMedian(src));
            case "方差" -> show("方差", AnalysisModel.getVariance(src));
            case "标准差" -> show("标准差", AnalysisModel.getStdDeviation(src));
            case "数据范围" -> JOptionPane.showMessageDialog(null, "数据范围: " + AnalysisModel.getRange(src));
            case "四分位数" ->
            {
                double[] q = AnalysisModel.getQuartiles(src);
                JOptionPane.showMessageDialog(null,
                        String.format("Q1: %.2f\nMedian: %.2f\nQ3: %.2f", q[0], q[1], q[2]),
                        "四分位数", JOptionPane.INFORMATION_MESSAGE);
            }
            default -> JOptionPane.showMessageDialog(null, "未知分析类型: " + type);
        }
    }

    private void show(String title,double v)
    {
        JOptionPane.showMessageDialog(null, title + ": " + v);
    }

    public List<Integer> getFilteredData()
    {
        return filteredData;
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(MainController::new);
    }
}
