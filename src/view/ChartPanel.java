package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChartPanel extends JPanel
{
    private List<Integer> data;

    public ChartPanel()
    {
        setPreferredSize(new Dimension(600, 600));
    }

    public void updateChart(List<Integer> data)
    {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        int barWidth = Math.max(5, width / data.size());

        int max = data.stream().max(Integer::compare).get();
        for (int i = 0; i < data.size(); i++) {
            int barHeight = (int) ((double)data.get(i) / max * (height - 20));
            g2d.fillRect(i * barWidth, height - barHeight - 10, barWidth - 2, barHeight);
        }
    }
}
