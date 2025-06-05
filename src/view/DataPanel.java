//unused temporarily
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DataPanel extends JPanel
{
    private final DefaultTableModel tableModel;

    public DataPanel()
    {
        setPreferredSize(new Dimension(200, 600));
        tableModel = new DefaultTableModel(new Object[]{"数据"}, 0);
        JTable table = new JTable(tableModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void updateData(List<Integer> data)
    {
        tableModel.setRowCount(0);
        data.forEach(num -> tableModel.addRow(new Object[]{num}));
    }
}
