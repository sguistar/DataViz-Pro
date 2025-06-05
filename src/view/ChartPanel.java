package view;

import javax.swing.*;
import javax.swing.ToolTipManager;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartPanel extends JPanel
{

    /* ---------- 图表类型 ---------- */
    public enum ChartType
    {BAR, LINE, PIE}

    /* ---------- 数据 & 状态 ---------- */
    private List<Integer> data;
    private ChartType chartType = ChartType.BAR;

    /* ---------- 动画 ---------- */
    private double progress = 1.0;                       // 0→1
    private final Timer animTimer = new Timer(16, e -> tick());
    private long animStart;

    /* ---------- 交互 ---------- */
    private int hoverIndex = -1;
    private int mouseX, mouseY;

    public ChartPanel()
    {
        setPreferredSize(new Dimension(600, 600));

        /* 鼠标移动：更新 hoverIndex & 坐标 */
        addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                mouseX = e.getX();
                mouseY = e.getY();
                updateHover(mouseX, mouseY);
            }
        });
        /* 鼠标离开：清空 hoverIndex */
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseExited(MouseEvent e)
            {
                hoverIndex = -1;
                repaint();
            }
        });

        /* 同时保留系统 Tooltip，取消注册 ↓ */
        ToolTipManager.sharedInstance().unregisterComponent(this);
    }

    /* ------------- 外部 API ------------- */
    public void updateChart(List<Integer> data)
    {
        this.data = new ArrayList<>(data);      // 保证是可变数组
        startAnim();
    }

    public void setChartType(ChartType t)
    {
        if (t != chartType)
        {
            chartType = t;
            startAnim();
        }
    }

    /* ------------- 动画逐帧 ------------- */
    private void startAnim()
    {
        progress = 0;
        animStart = System.currentTimeMillis();
        animTimer.start();
    }

    private void tick()
    {
        progress = Math.min(1.0, (System.currentTimeMillis() - animStart) / 600.0);
        repaint();
        if (progress >= 1.0) animTimer.stop();
    }

    /* ------------- 绘制入口 ------------- */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) return;

        switch (chartType)
        {
            case BAR -> paintBar(g);
            case LINE -> paintLine(g);
            case PIE -> paintPie(g);
        }

        /* 最后绘制自定义 Tooltip（最上层） */
        if (hoverIndex >= 0 && hoverIndex < data.size()) drawTooltip(g);
    }


    /*---------各图表绘制方法---------*/

    /* ---- 1. 柱状图 ---- */
    private void paintBar(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        int barW = Math.max(5, w / data.size());
        int max = Collections.max(data);

        for (int i = 0; i < data.size(); i++)
        {
            double pct = data.get(i) / (double) max;
            int full = (int) (pct * (h - 40));
            int barH = (int) (full * progress);
            int x = i * barW;
            int y = h - barH - 20;

            g2.setColor(i == hoverIndex ? Color.BLUE : Color.BLACK);
            g2.fillRect(x, y, barW - 4, barH);
        }
        g2.dispose();
    }

    /* ---- 2. 折线图 ---- */
    private void paintLine(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        int max = Collections.max(data);
        int stepX = w / (data.size() - 1);

        /* 线条 */
        for (int i = 0; i < data.size() - 1; i++)
        {
            int x1 = i * stepX;
            int y1 = h - (int) (data.get(i) / (double) max * (h - 40));
            int x2 = (i + 1) * stepX;
            int y2 = h - (int) (data.get(i + 1) / (double) max * (h - 40));

            /* 进度裁剪 */
            double drawn = progress * (data.size() - 1);
            if (i + 1 > drawn)
            {
                double frac = drawn - i;
                x2 = (int) (x1 + frac * stepX);
                y2 = (int) (y1 + frac * (y2 - y1));
            }
            g2.drawLine(x1, y1, x2, y2);
            if (i + 1 > drawn) break;
        }

        /* 高亮顶点 */
        if (hoverIndex >= 0)
        {
            int cx = hoverIndex * stepX;
            int cy = h - (int) (data.get(hoverIndex) / (double) max * (h - 40));
            g2.setColor(Color.RED);
            g2.fillOval(cx - 6, cy - 6, 12, 12);
        }
        g2.dispose();
    }

    /* ---- 3. 饼图 ---- */
    private void paintPie(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        int size = Math.min(getWidth(), getHeight()) - 40;
        int cx = getWidth() / 2, cy = getHeight() / 2;
        int r = size / 2;

        double sum = data.stream().mapToDouble(i -> i).sum();
        double cur = 0;
        for (int i = 0; i < data.size(); i++)
        {
            double ang = 360.0 * data.get(i) / sum;

            /* 计算爆炸位移 */
            int dx = 0, dy = 0;
            if (i == hoverIndex)
            {
                double mid = Math.toRadians(cur + ang / 2);
                dx = (int) (Math.cos(mid) * 15);
                dy = (int) (-Math.sin(mid) * 15);
            }

            /* 动画裁剪 */
            double drawAng = Math.min(ang, Math.max(0, progress * 360 - cur));

            /* 填充 */
            g2.setColor(i == hoverIndex ? Color.YELLOW : Color.CYAN);
            g2.fillArc(cx - r + dx, cy - r + dy, size, size, (int) cur, (int) drawAng);

            /* 高亮描边 */
            if (i == hoverIndex && drawAng > 0)
            {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawArc(cx - r + dx, cy - r + dy, size, size, (int) cur, (int) drawAng);
                g2.setStroke(new BasicStroke(1f));
            }

            cur += ang;
            if (cur >= progress * 360) break;
        }
        g2.dispose();
    }

    /*        Hover 检测 & Tooltip绘制       */
    private void updateHover(int mx, int my)
    {
        if (data == null || data.isEmpty())
        {
            hoverIndex = -1;
            return;
        }

        int idx = switch (chartType)
        {
            case BAR, LINE -> hitBar(mx);     // x 轴分段足够
            case PIE -> hitPie(mx, my);
        };
        if (idx != hoverIndex)
        {
            hoverIndex = idx;
            repaint();                       // 让高亮以及Tooltip刷新
        }
    }

    private int hitBar(int mx)
    {
        int barW = Math.max(5, getWidth() / data.size());
        int idx = mx / barW;
        return (idx >= 0 && idx < data.size()) ? idx : -1;
    }

    private int hitPie(int mx, int my)
    {
        int size = Math.min(getWidth(), getHeight()) - 40;
        int cx = getWidth() / 2, cy = getHeight() / 2, r = size / 2;

        int dx = mx - cx, dy = my - cy;
        if (dx * dx + dy * dy > r * r) return -1;

        double ang = Math.toDegrees(Math.atan2(-dy, dx));
        if (ang < 0) ang += 360;

        double sum = data.stream().mapToDouble(i -> i).sum();
        double cur = 0;
        for (int i = 0; i < data.size(); i++)
        {
            double slice = 360.0 * data.get(i) / sum;
            if (ang >= cur && ang < cur + slice) return i;
            cur += slice;
        }
        return -1;
    }

    private void drawTooltip(Graphics g)
    {
        String txt = String.valueOf(data.get(hoverIndex));
        Graphics2D g2 = (Graphics2D) g.create();
        FontMetrics fm = g2.getFontMetrics();
        int pad = 4;
        int txtW = fm.stringWidth(txt);
        int txtH = fm.getHeight();

        int boxX = mouseX + 12;
        int boxY = mouseY - txtH - 8;
        g2.setColor(new Color(255, 255, 225, 230));
        g2.fillRoundRect(boxX, boxY, txtW + pad * 2, txtH + pad * 2, 8, 8);
        g2.setColor(Color.BLACK);
        g2.drawString(txt, boxX + pad, boxY + txtH);
        g2.dispose();
    }
}
