import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameController implements ActionListener {

    public static Dimension WINDOW_SIZE;

    private Boolean enableHaptic;

    private JFrame frame;
    private JPanel startPanel;
    private GamePanel gamePanel;
    private JButton startButton;

    public GameController() {
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setName("Haptic Feedback Test Environment");
        this.frame.setBackground(Color.DARK_GRAY);
        WINDOW_SIZE = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
        this.frame.setPreferredSize(WINDOW_SIZE);
        initStartPanel();
        this.frame.add(startPanel);
        this.frame.setResizable(false);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Press to Start")) {
            this.gamePanel = new GamePanel(this.frame.getY(), this.frame.getInsets().top);
            this.frame.getContentPane().remove(startPanel);
            this.frame.add(this.gamePanel);
            this.frame.pack();
            this.frame.revalidate();
            this.frame.repaint();
        }
    }

    private void initStartPanel() {
        this.startPanel = new JPanel();
        this.startPanel.setPreferredSize(WINDOW_SIZE);
        this.startPanel.setBackground(Color.LIGHT_GRAY);
        this.startPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 400, 200));

        this.startButton = new JButton("Press to Start");
        this.startButton.setPreferredSize(new Dimension(600, 200));
        this.startButton.setFont(new Font("Calibri", Font.PLAIN, 30));

        JLabel titleLabel = new JLabel("Haptic Feedback Test Environment");
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 50));

        this.startPanel.add(titleLabel);
        this.startPanel.add(startButton);

        this.startButton.addActionListener(this);
    }

}
