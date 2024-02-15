import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CalibrationPanel extends JPanel implements ActionListener{
    private final int DELAY = 25;
    private final String RESET_COILS = "00000000000000000000000";
    private final String CALIBRATE_COILS = "00000000000100000000000";

    private Timer timer;
    private ArduinoSerialWriter serialWriter;
    private boolean arduinoConnected;

    public CalibrationPanel () {
        // set up JPanel stuff
        this.setPreferredSize(GameController.windowDim);
        this.setBackground(new Color(232, 232, 232));
        this.setFocusable(true);
        this.requestFocusInWindow();

        // set up the timer for graphics
        this.timer = new Timer(DELAY, this);
        this.timer.setActionCommand("tick");
        this.timer.start(); 
        
        // set up the Arduino Serial Writer
        this.serialWriter = new ArduinoSerialWriter();
        this.serialWriter.setupSerialComm();
        this.arduinoConnected = this.serialWriter.isArduinoConnected();
    }

    public void setCursorInMiddle() {
        try {
            Robot robot = new Robot();
            int centerX = GameController.windowDim.width / 2;
            int centerY = (GameController.windowDim.height / 2) + 50;
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
            serialWriter.turnOnCoils(CALIBRATE_COILS);
        }
    }

    public void finishCalibration(){
        if (arduinoConnected) {
            serialWriter.turnOnCoils(RESET_COILS);
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
        Font f = new Font("Lato", Font.PLAIN, 30);
        drawText(g, "Place the mouse in the center of the mouse pad and CLICK", f, Color.BLACK, (int) GameController.windowDim.getWidth(), (int) GameController.windowDim.getHeight()/12);
        drawScreenCenter(g);

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

    private void drawScreenCenter(Graphics g) {
        Font centerFont = new Font("Lato", Font.PLAIN, 100);
        drawText(g, "+", centerFont, Color.BLACK, (int) GameController.windowDim.getWidth(), (int) GameController.windowDim.getHeight()/2);
    }
}
