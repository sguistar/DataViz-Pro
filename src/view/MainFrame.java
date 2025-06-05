package view;

import controller.MainController;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class MainFrame extends JFrame
{
    private final MainController controller;
    private final DataPanel dataPanel;
    private final ChartPanel chartPanel;

    public MainFrame(MainController controller)
    {
        this.controller = controller;
        setTitle("DataViz Pro");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        dataPanel = new DataPanel();
        chartPanel = new ChartPanel();

        add(dataPanel, BorderLayout.WEST);
        add(chartPanel, BorderLayout.CENTER);
        add(controlPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    public JPanel controlPanel()
    {
        JPanel panel = new JPanel();
        JButton loadBtn = new JButton("导入数据");
        loadBtn.addActionListener(e ->
        {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                controller.readCSV(String.valueOf(chooser.getSelectedFile().getAbsoluteFile()));
            }
        });

        JButton sortBtn = new JButton("排序数据");
        sortBtn.addActionListener(e -> controller.sortData());
        String[] analysisOptions = {"平均值", "中位数", "方差", "标准差", "数据范围", "四分位数"};
        JComboBox<String> analysisBox = new JComboBox<>(analysisOptions);
        String[] chartTypes = {"柱状图", "折线图", "饼图"};
        JComboBox<String> chartBox = new JComboBox<>(chartTypes);
        chartBox.addActionListener(e ->
        {
            switch (chartBox.getSelectedIndex())
            {
                case 0 -> chartPanel.setChartType(ChartPanel.ChartType.BAR);
                case 1 -> chartPanel.setChartType(ChartPanel.ChartType.LINE);
                case 2 -> chartPanel.setChartType(ChartPanel.ChartType.PIE);
            }
        });
        panel.add(chartBox);
        JButton analyzeBtn = new JButton("数据分析");
        analyzeBtn.addActionListener(e -> controller.analyzeData(String.valueOf(analysisBox.getSelectedItem())));

        JButton calcBtn = new JButton("计算器");
        calcBtn.addActionListener(e -> SwingUtilities.invokeLater(SimpleCalculator::new));

        JButton filterBtn = new JButton("筛选");
        filterBtn.addActionListener(e -> openFilterDialog());

        panel.add(filterBtn);
        panel.add(loadBtn);
        panel.add(sortBtn);
        panel.add(analysisBox);
        panel.add(analyzeBtn);
        panel.add(calcBtn);

        return panel;
    }

    private void openFilterDialog()
    {
        JTextField minField = new JTextField(5);
        JTextField maxField = new JTextField(5);
        JCheckBox outlierBox = new JCheckBox("自动剔除离群值(IQR)");

        JPanel form = new JPanel(new GridLayout(0, 1));
        form.add(new JLabel("最小值:"));
        form.add(minField);
        form.add(new JLabel("最大值:"));
        form.add(maxField);
        form.add(outlierBox);

        int ok = JOptionPane.showConfirmDialog(
                this, form, "设置筛选条件",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (ok == JOptionPane.OK_OPTION)
        {
            Integer min = minField.getText().isBlank() ? null : Integer.parseInt(minField.getText());
            Integer max = maxField.getText().isBlank() ? null : Integer.parseInt(maxField.getText());
            controller.applyFilter(min, max, outlierBox.isSelected());
        }
    }


    public void updateTable(List<Integer> data)
    {
        dataPanel.updateData(controller.getFilteredData());
    }

    public void updateChart(List<Integer> data)
    {
        chartPanel.updateChart(controller.getFilteredData());
    }

}
