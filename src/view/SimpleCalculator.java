package view;

import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class SimpleCalculator extends JFrame
{
    JTextField input;
    JButton[] button;
    JButton plus, reset, minus, multiply, divide, equal;
    private char op;
    private final List<BigDecimal> addends;

    public SimpleCalculator()
    {
        super("Simple Calculator");
        addends = new ArrayList<>();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout());
        input = new JTextField("0",30);
        add(input);
        button = new JButton[10];
        for (int i = 0; i < button.length; i++)
        {
            button[i] = new JButton(String.valueOf(i));
            add(button[i]);
            final int digit = i;
            button[i].addActionListener(e ->
            {
                String cur = input.getText();
                if (cur.equals("0") || cur.isBlank())
                {
                    input.setText(String.valueOf(digit));
                }
                else
                {
                    input.setText(cur + digit);
                }
            });
        }
        add(plus = new JButton("+"));
        add(minus = new JButton("-"));
        add(multiply = new JButton("*"));
        add(divide = new JButton("÷"));
        add(equal = new JButton("="));
        add(reset = new JButton("C"));
        plus.addActionListener(l -> {op = '+'; setSymbol();});
        minus.addActionListener(l -> {op = '-'; setSymbol();});
        multiply.addActionListener(l -> {op = '*'; setSymbol();});
        divide.addActionListener(l -> {op = '÷'; setSymbol();});
        equal.addActionListener(l -> getResult());
        reset.addActionListener(l -> Clear());
        setSize(400, 350);
        setVisible(true);
    }

    public void setSymbol()
    {
        String symbol = input.getText();
        if(!symbol.isBlank())
        {
            addends.add(new BigDecimal(symbol));
            input.setText("");
        }
    }

    public void getResult()
    {
        String cur = input.getText().strip();
        if (!cur.isEmpty())
        {
            addends.add(new BigDecimal(cur));
        }
        BigDecimal result;
        switch (op)
        {
            case '+': result = addends.stream().reduce(BigDecimal.ZERO, BigDecimal::add); break;
            case '-': result = addends.get(0); for (int i = 1; i < addends.size(); i++) result = result.subtract(addends.get(i)); break;
            //case '-' :result = addends.stream().skip(0).reduce(BigDecimal.ZERO, BigDecimal::subtract); break;
            case '*': result = addends.stream().reduce(BigDecimal.ONE, BigDecimal::multiply); break;
            case '÷':
                result = addends.get(0);
                for (int i = 1; i < addends.size(); i++)
                {
                    BigDecimal d = addends.get(i);
                    if (d.compareTo(BigDecimal.ZERO)==0)
                    {
                        input.setText("除数不能为0");
                        return;
                    }
                    result = result.divide(d, 10, RoundingMode.HALF_UP);
                }
                break;
            default: result = BigDecimal.ZERO;
        }
        input.setText(result.stripTrailingZeros().toPlainString());
        addends.clear();
    }


    public void Clear()
    {
        input.setText("");
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(SimpleCalculator::new);
    }
}
