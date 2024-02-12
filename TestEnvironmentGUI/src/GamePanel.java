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
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {

    private final int DELAY = 25; // 25 ms delay between ticks
    private final int MAX_ROUNDS = 5;
    private final int ACTIVATION_RADIUS = 300;
    private final int MAGNET_RADIUS = 100;
    private final int TARGET_RADIUS = 50;
    private final int GUESS_RADIUS = 25;
    private final String RESET_COILS = "00000000000000000000000";

    private static Dimension WINDOW_SIZE;
    private static Color CUSTOM_GREEN;

    private int titleBarHeight;
    private int windowTopOffset;
    private int currentRound;
    private Timer timer;
    private Point mousePos;
    private Point targetPos;
    private Point guessPos;
    private boolean guessExists;
    private boolean roundsComplete;
    private boolean arduinoConnected;
    private ArrayList<Integer> distList;
    private ArrayList<Point> magPosList;
    private ArduinoSerialWriter serialWriter;

    // ----- constructor and init ----- //
    public GamePanel(int titleBarHeight, int windowTopOffset) {
        WINDOW_SIZE = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
        CUSTOM_GREEN = new Color(30, 201, 139);

        this.titleBarHeight = titleBarHeight;
        this.windowTopOffset = windowTopOffset;
        this.guessExists = false;
        this.roundsComplete = false;
        this.currentRound = 0;
        this.distList = new ArrayList<>();
        this.magPosList = new ArrayList<>();
        this.serialWriter = new ArduinoSerialWriter();
        this.serialWriter.setupSerialComm();
        this.arduinoConnected = this.serialWriter.isArduinoConnected();

        // initialize magnet positions on screen
        initMagPositions();

        // set initial random target
        generateNewTarget();

        // set up JPanel stuff
        this.setPreferredSize(WINDOW_SIZE);
        this.setBackground(new Color(232, 232, 232));
        this.setFocusable(true);
        this.requestFocusInWindow();

        // set up tick functionality
        this.timer = new Timer(DELAY, this);
        this.timer.setActionCommand("tick");
        this.timer.start();

        // mouse clicks paint guesses on screen
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (guessExists) {
                    // display results screen
                    if (currentRound == MAX_ROUNDS) {
                        roundsComplete = true;
                        if (arduinoConnected) {
                            serialWriter.turnOnCoils(RESET_COILS);
                            serialWriter.closeSerialComm();
                        }
                    // enter next round
                    } else if (currentRound < MAX_ROUNDS) {
                        generateNewTarget();
                        guessExists = false;
                    }

                } else { 
                    // make new guess for current round
                    guessPos = new Point(me.getX(), me.getY());
                    distList.add(calcDistance(guessPos, targetPos));
                    guessExists = true;
                    if (arduinoConnected) {
                        serialWriter.turnOnCoils(RESET_COILS);
                    }
                }
            }
        });
    }

    // ----- game loop methods ----- //
    private void generateNewTarget() {
        currentRound++;

        // randomly create new target x and y
        int targetX = ThreadLocalRandom.current().nextInt(0+(2*TARGET_RADIUS), (int)WINDOW_SIZE.getWidth()-(2*TARGET_RADIUS)+1);
        int targetY = ThreadLocalRandom.current().nextInt(0+(2*TARGET_RADIUS), (int)WINDOW_SIZE.getHeight()-(2*TARGET_RADIUS)+1);
        this.targetPos = new Point(targetX, targetY);

        // send activation string to arduino
        String bitString = genCoilSerialString();
        if (this.arduinoConnected) {
            this.serialWriter.turnOnCoils(bitString);
        }
    }

    private int calcDistance(Point a, Point b) {
        return (int) Math.hypot(a.x - b.x, a.y - b.y);
    }

    private String genCoilSerialString() {
        StringBuilder bitString = new StringBuilder(23);
        for (Point p : this.magPosList) {
            int hyp = calcDistance(p, this.targetPos);
            int bit = (hyp < ACTIVATION_RADIUS) ? 1 : 0;
            bitString.append(bit);
        }
        System.out.println("Round "+this.currentRound+": "+bitString.toString());
        return bitString.toString();
    }

    private void initMagPositions() {
        int xSpacing = (int) WINDOW_SIZE.getWidth() / 10;
        int ySpacing = (int) (WINDOW_SIZE.getHeight()-(titleBarHeight + windowTopOffset)) / 6;
        int colStart;
        int colEnd;
        for (int row=1; row<6; row++) {
            colStart = (row % 2 == 0) ? 2 : 1;
            colEnd = (row % 2 == 0) ? 9 : 10;
            for (int col=colStart; col<colEnd; col+=2) {
                // this saves the position of the top-left corner of each magnet circle
                this.magPosList.add(new Point(col*xSpacing, row*ySpacing));
            }
        }
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
        if (roundsComplete) {
            drawResultsScreen(g);
        } else {
            if (guessExists) {
                drawMagnetCircles(g);
                drawMousePosLabel(g);
                drawTargetCircle(g);
                drawGuessCircle(g);
                drawNextRoundInstructions(g);
            } else {
                drawGuessScreen(g);
            }
        }

        // this smooths out animations on some systems
        Toolkit.getDefaultToolkit().sync();
    }
   
    // --- drawing methods --- //
    private void drawText(Graphics g, String text, Font f, Color c, int width, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setColor(c);
        g2d.setFont(f);
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        
        Rectangle rect = new Rectangle(0, y, width, 50);
        int xCoord = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int yCoord = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g2d.drawString(text, xCoord, yCoord);
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
        Font f = new Font("Lato", Font.PLAIN, 25);
        drawText(g, text, f, Color.BLACK, (int) WINDOW_SIZE.getWidth()/6, 5);
    }

    private void drawGuessCircle(Graphics g) {
        if (this.guessExists) {
            g.setColor(Color.ORANGE);
            g.fillOval(guessPos.x-GUESS_RADIUS, guessPos.y-GUESS_RADIUS, 2*GUESS_RADIUS, 2*GUESS_RADIUS);
            g.setColor(Color.BLACK);
            g.drawLine(guessPos.x, guessPos.y, targetPos.x, targetPos.y);
            String text = "Distance to target: "+calcDistance(guessPos, targetPos)+" pixels";
            int yCoord = 5;
            Font f = new Font("Lato", Font.BOLD, 30);
            drawText(g, text, f, Color.BLACK, (int) WINDOW_SIZE.getWidth(), yCoord);
        }
    }

    private void drawTargetCircle(Graphics g) {
        g.setColor(CUSTOM_GREEN);
        g.fillOval(targetPos.x-TARGET_RADIUS, targetPos.y-TARGET_RADIUS, 2*TARGET_RADIUS, 2*TARGET_RADIUS);
        g.setColor(Color.RED);
        g.drawOval(targetPos.x-ACTIVATION_RADIUS, targetPos.y-ACTIVATION_RADIUS, 2*ACTIVATION_RADIUS, 2*ACTIVATION_RADIUS);
    }

    private void drawMagnetCircles(Graphics g) {
        for (Point p : this.magPosList) {
            int hyp = calcDistance(p, this.targetPos);
            if (hyp < ACTIVATION_RADIUS) {
                g.setColor(Color.blue);
                g.fillOval(p.x-5, p.y-5, 10, 10);
            } else {
                g.setColor(Color.gray);
            }
            g.drawOval(p.x-MAGNET_RADIUS, p.y-MAGNET_RADIUS, MAGNET_RADIUS*2, MAGNET_RADIUS*2);
        }
    }

    private void drawGuessScreen(Graphics g) {
        int yCoord = (int) (WINDOW_SIZE.getHeight()-(titleBarHeight + windowTopOffset)) / 4;
        Font f1 = new Font("Lato", Font.BOLD, 75);
        Font f2 = new Font("Lato", Font.BOLD, 40);
        String text1 = "Round "+this.currentRound;
        String text2 = "Click when you think you have found the target.";
        drawText(g, text1, f1, Color.BLACK, (int) WINDOW_SIZE.getWidth(), yCoord*1);
        drawText(g, text2, f2, Color.BLACK, (int) WINDOW_SIZE.getWidth(), yCoord*2);
    }

    private void drawNextRoundInstructions(Graphics g) {
        int yCoord = (int) (WINDOW_SIZE.getHeight()-(titleBarHeight + windowTopOffset)) - 40;
        Font f = new Font("Lato", Font.BOLD, 60);
        String text = "CLICK anywhere for next round...";
        drawText(g, text, f, Color.BLACK, (int) WINDOW_SIZE.getWidth(), yCoord);
    }

    private void drawResultsScreen(Graphics g) {
        int yScreen = (int) WINDOW_SIZE.getHeight()-(titleBarHeight + windowTopOffset);
        int ySpacing = (int) yScreen / 10;
        drawText(g, "Results", new Font("Lato", Font.BOLD, 60), Color.BLACK, (int) WINDOW_SIZE.getWidth(), 100);
        Font scoreFont = new Font("Lato", Font.PLAIN, 40);
        int sum = 0;
        for (int idx=0; idx<5; idx++) {
            sum += distList.get(idx);
            String text = "Round "+(idx+1)+": "+distList.get(idx)+" pixels";
            drawText(g, text, scoreFont, Color.BLACK, (int) WINDOW_SIZE.getWidth(), (idx+3)*ySpacing);
        }
        int avg = (int) sum / 5;
        Font avgFont = new Font("Lato", Font.BOLD, 50);
        drawText(g, "Average: "+avg+" pixels", avgFont, Color.BLACK, (int) WINDOW_SIZE.getWidth(), yScreen-100);
    }
    
}
