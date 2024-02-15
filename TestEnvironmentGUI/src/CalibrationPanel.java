import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CalibrationPanel extends JPanel implements ActionListener{

    private Timer timer;
    private ArduinoSerialWriter serialWriter;
    private boolean arduinoConnected;

    private Font font;
    private String title;

    public CalibrationPanel () {
        // set up JPanel stuff
        this.setPreferredSize(GameController.windowDim);
        this.setBackground(new Color(232, 232, 232));
        this.setFocusable(true);
        this.requestFocusInWindow();

        // set up the timer for graphics
        this.timer = new Timer(Consts.DELAY, this);
        this.timer.setActionCommand("tick");
        this.timer.start(); 
        
        // set up the Arduino Serial Writer
        this.serialWriter = new ArduinoSerialWriter();
        this.serialWriter.setupSerialComm();
        this.arduinoConnected = this.serialWriter.isArduinoConnected();

        // set up other variables
        this.font = new Font("Lato", Font.PLAIN, 30);
        this.title = "Place the mouse in the center of the mouse pad and CLICK";
    }

    private void setCursorInMiddle() {
        try {
            Robot robot = new Robot();
            // int centerX = GameController.windowDim.width / 2;
            // int centerY = (GameController.windowDim.height / 2) + 50;
            int centerX = GameController.magPosList.get(11).x;
            int centerY = GameController.magPosList.get(11).y + 65;
            robot.mouseMove(centerX, centerY);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }

    public boolean getArduinoConnected(){
        return this.arduinoConnected;
    }

    public void calibrateCursor() {
        if (arduinoConnected) {
            serialWriter.turnOnCoils(Consts.CALIBRATE_COILS);
        }
    }

    public void finishCalibration(){
        if (arduinoConnected) {
            serialWriter.turnOnCoils(Consts.RESET_COILS);
        }
        setCursorInMiddle();
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
        drawMagnetCircles(g);
        drawText(g, title, font, Color.BLACK, (int) GameController.windowDim.getWidth(), 15);

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

    private void drawMagnetCircles(Graphics g) {
        for (int i=0; i<GameController.magPosList.size(); i++) {
            Point p = GameController.magPosList.get(i);
            if (i==11) {
                g.setColor(Color.blue);
                g.fillOval(p.x-5, p.y-5, 10, 10);
            } else {
                g.setColor(Color.gray);
            }
            g.drawOval(p.x-Consts.MAGNET_RADIUS, p.y-Consts.MAGNET_RADIUS, Consts.MAGNET_RADIUS*2, Consts.MAGNET_RADIUS*2);
        }
    }
}
