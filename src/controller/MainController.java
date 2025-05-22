package controller;

import model.*;
import view.MainFrame;
import javax.swing.*;
import java.io.IOException;

public class MainController
{
    private final DataModel dataModel;
    private final MainFrame mainFrame;

    public MainController()
    {
        dataModel = new DataModel();
        mainFrame = new MainFrame(this);
    }

    public void readCSV(String filePath)
    {
        try
        {
            dataModel.readCSV(filePath);
            mainFrame.updateTable(dataModel.getData());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "文件读取出错:" + e.getMessage());
        }
    }

    public void sortData()
    {
        dataModel.quickSort();
        mainFrame.updateTable(dataModel.getData());
        mainFrame.updateChart(dataModel.getData());
    }

    public void analyzeData(String type)
    {
        double result = switch (type)
        {
            case "Mean" -> AnalysisModel.getMean(dataModel.getData());
            case "Median" -> AnalysisModel.getMedian(dataModel.getData());
            case "Variance" -> AnalysisModel.getVariance(dataModel.getData());
            default -> 0;
        };
        JOptionPane.showMessageDialog(null, type + ": " + result);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(MainController::new);
    }
}
