package view;

import controller.MainController;
import javax.swing.*;
import java.awt.*;
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
        loadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                controller.readCSV(String.valueOf(chooser.getSelectedFile().getAbsoluteFile()));
            }
        });

        JButton sortBtn = new JButton("排序数据");
        sortBtn.addActionListener(e -> controller.sortData());

        String[] analysisOptions = {"Mean", "Median", "Variance"};
        JComboBox<String> analysisBox = new JComboBox<>(analysisOptions);
        JButton analyzeBtn = new JButton("数据分析");
        analyzeBtn.addActionListener(e -> controller.analyzeData(String.valueOf(analysisBox.getSelectedItem())));

        panel.add(loadBtn);
        panel.add(sortBtn);
        panel.add(analysisBox);
        panel.add(analyzeBtn);

        return panel;
    }

    public void updateTable(List<Integer> data)
    {
        dataPanel.updateData(data);
    }

    public void updateChart(List<Integer> data)
    {
        chartPanel.updateChart(data);
    }

}
