import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {

    private final int DELAY = 25; // 25 ms delay between ticks
    private final int TARGET_RADIUS = 50;
    private final int GUESS_RADIUS = 25;
    private static Dimension WINDOW_SIZE;
    private static Color CUSTOM_GREEN;

    private int titleBarHeight;
    private int windowTopOffset;
    private Timer timer;
    private Point mousePos;
    private Point targetPos;
    private Point guessPos;
    private boolean guessExists;

    // ----- constructor and init ----- //
    public GamePanel(int titleBarHeight, int windowTopOffset) {
        WINDOW_SIZE = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
        CUSTOM_GREEN = new Color(30, 201, 139);
        this.titleBarHeight = titleBarHeight;
        this.windowTopOffset = windowTopOffset;
        this.guessExists = false;

        // set random target
        int targetX = ThreadLocalRandom.current().nextInt(0+(2*TARGET_RADIUS), (int)WINDOW_SIZE.getWidth()-(2*TARGET_RADIUS)+1);
        int targetY = ThreadLocalRandom.current().nextInt(0+(2*TARGET_RADIUS), (int)WINDOW_SIZE.getHeight()-(2*TARGET_RADIUS)+1);
        this.targetPos = new Point(targetX, targetY);

        // set up JPanel stuff
        this.setPreferredSize(WINDOW_SIZE);
        this.setBackground(new Color(232, 232, 232));

        // set up tick functionality
        this.timer = new Timer(DELAY, this);
        this.timer.setActionCommand("tick");
        this.timer.start();

        // mouse clicks paint guesses on screen
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                guessExists = true;
                guessPos = new Point(e.getX(), e.getY());
            }
        });
    }
   
    // ----- overriden methods ----- //
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("tick")) {
            revalidate();
            repaint(); // will call paintComponent()
        }
    }
   
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw graphics.
        drawMousePosLabel(g);
        drawTargetCircle(g);
        drawGuessCircle(g);

        // this smooths out animations on some systems
        Toolkit.getDefaultToolkit().sync();
    }
   
    // --- drawing methods --- //
    private void drawText(Graphics g, String text, Color c, int yCoordinate) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setColor(c);
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        
        Rectangle rect = new Rectangle(0, yCoordinate, (int) WINDOW_SIZE.getWidth(), 50);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g2d.drawString(text, x, y);
    }
   
    private void drawMousePosLabel(Graphics g) {
        this.mousePos = MouseInfo.getPointerInfo().getLocation();
        int yPos = this.mousePos.y - (titleBarHeight + windowTopOffset);
        if (this.mousePos.y < (titleBarHeight + windowTopOffset)) {
            yPos = 0;
        } else if (this.mousePos.y > WINDOW_SIZE.getHeight() + windowTopOffset) {
            yPos = (int) WINDOW_SIZE.getHeight();
        }
        String text = "x: "+this.mousePos.x+"   "+"y: "+yPos;
        drawText(g, text, Color.BLACK, 25);
    }

    private void drawGuessCircle(Graphics g) {
        if (this.guessExists) {
            g.setColor(Color.ORANGE);
            g.fillOval(guessPos.x-GUESS_RADIUS, guessPos.y-GUESS_RADIUS, 2*GUESS_RADIUS, 2*GUESS_RADIUS);
            g.setColor(Color.BLACK);
            g.drawLine(guessPos.x, guessPos.y, targetPos.x, targetPos.y);
            String text = "Distance to target: "+(int)Math.hypot(guessPos.x - targetPos.x, guessPos.y - targetPos.y)+" pixels";
            drawText(g, text, Color.BLACK, (int)WINDOW_SIZE.getHeight() - (titleBarHeight + windowTopOffset) - 25);
        }
    }

    private void drawTargetCircle(Graphics g) {
        g.setColor(CUSTOM_GREEN);
        g.fillOval(targetPos.x-TARGET_RADIUS, targetPos.y-TARGET_RADIUS, 2*TARGET_RADIUS, 2*TARGET_RADIUS);
    }
    
}
