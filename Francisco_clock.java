package csdgraphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.*;
import javax.swing.Timer;

public class Francisco_clock extends JFrame {
    Clock clockFace;

    public static void main(String[] args) {
        JFrame window = new Francisco_clock();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    public Francisco_clock() {
        Container content = this.getContentPane();
        content.setLayout(new BorderLayout());
        clockFace = new Clock();
        content.add(clockFace, BorderLayout.CENTER);
        this.setTitle("Francisco Clock MP");
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        clockFace.start();
    }
}

class Clock extends JPanel {
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private int millis = 0;
    private static final int spacing = 20;
    private static final float twoPi = (float) (2.0 * Math.PI);
    private static final float threePi = (float) (3.0 * Math.PI);
    private static final float radPerSecMin = (float) (Math.PI / 30.0);
    private int size;
    private int centerX;
    private int centerY;
    private BufferedImage clockImage;
    private Timer t;

    public Clock() {
        this.setPreferredSize(new Dimension(600, 600));
        this.setBackground(Color.BLACK);
        t = new Timer(1000, e -> update());
    }

    public void update() {
        this.repaint();
    }

    public void start() {
        t.start();
    }

    public void stop() {
        t.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        int w = getWidth();
        int h = getHeight();
        size = ((w < h) ? w : h) - 2 * spacing;
        centerX = size / 2 + spacing;
        centerY = size / 2 + spacing;

        if (clockImage == null || clockImage.getWidth() != w || clockImage.getHeight() != h) {
            clockImage = (BufferedImage) (this.createImage(w, h));
            Graphics2D gc = clockImage.createGraphics();
            gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawClockFace(gc);
        }

        Calendar now = Calendar.getInstance();
        hours = now.get(Calendar.HOUR);
        minutes = now.get(Calendar.MINUTE);
        seconds = now.get(Calendar.SECOND);
        millis = now.get(Calendar.MILLISECOND);

        g2.drawImage(clockImage, 0, 0, this);
        drawClockHands(g2);
        drawDigitalClock(g2);
    }

    private void drawClockFace(Graphics2D g) {
        GradientPaint gradient = new GradientPaint(0, 0, new Color(150, 70, 150), 0, size, new Color(80, 20, 80));
        g.setPaint(gradient);
        g.fillOval(spacing, spacing, size, size);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(16));
        g.drawOval(spacing, spacing, size, size);

        String[] romanNumerals = {"XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI"};
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        for (int i = 0; i < 12; i++) {
            double angle = Math.PI / 6 * (i - 3);
            int x = centerX + (int) (size / 2.4 * Math.cos(angle));
            int y = centerY + (int) (size / 2.4 * Math.sin(angle));
            if (i == 6) {
                y += 10;
            }
            if (i == 0 || i == 3 || i == 6 || i == 9) {
                g.setFont(new Font("Arial", Font.BOLD, 60));
                g.setColor(Color.WHITE);
            } else {
                g.setFont(new Font("Arial", Font.BOLD, 50));
                g.setColor(Color.WHITE);
            }
            g.drawString(romanNumerals[i], x - 25, y + 15);
        }

        for (int sec = 0; sec < 60; sec++) {
            int ticStart = (sec % 5 == 0) ? size / 2 - 20 : size / 2 - 12;
            g.setStroke(new BasicStroke(sec % 5 == 0 ? 3 : 1));
            g.setColor(sec % 5 == 0 ? Color.WHITE : new Color(230, 220, 255));
            drawRadius(g, centerX, centerY, radPerSecMin * sec, ticStart, size / 2);
        }

        g.setColor(Color.WHITE);
        g.fillOval(centerX - 30, centerY - 30, 60, 60);
    }

    private void drawClockHands(Graphics2D g) {
        int secondRadius = size / 2 - 40;
        int minuteRadius = size * 3 / 8;
        int hourRadius = size / 4;

        float fseconds = seconds + (float) millis / 1000;
        float secondAngle = threePi - (radPerSecMin * fseconds);
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawRadius(g, centerX, centerY, secondAngle, -15, secondRadius);

        float fminutes = (float) (minutes + fseconds / 60.0);
        float minuteAngle = threePi - (radPerSecMin * fminutes);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawRadius(g, centerX, centerY, minuteAngle, -12, minuteRadius);

        float fhours = (float) (hours + fminutes / 60.0);
        float hourAngle = threePi - (5 * radPerSecMin * fhours);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawRadius(g, centerX, centerY, hourAngle, -10, hourRadius);
    }

    private void drawRadius(Graphics g, int x, int y, double angle, int minRadius, int maxRadius) {
        float sine = (float) Math.sin(angle);
        float cosine = (float) Math.cos(angle);
        int dxmin = (int) (minRadius * sine);
        int dymin = (int) (minRadius * cosine);
        int dxmax = (int) (maxRadius * sine);
        int dymax = (int) (maxRadius * cosine);
        g.drawLine(x + dxmin, y + dymin, x + dxmax, y + dymax);
    }

    private void drawDigitalClock(Graphics2D g) {
        String timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        int textWidth = g.getFontMetrics().stringWidth(timeText);
        g.drawString(timeText, centerX - textWidth / 2, centerY - size / 2 + 160);
    }
}
