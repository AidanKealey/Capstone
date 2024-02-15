import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GameController implements ActionListener {

    public static Dimension WINDOW_SIZE;

    private boolean onStartScreen;

    private JFrame frame;
    private JPanel startPanel;
    private GamePanel gamePanel;
    private JButton startButton;
    private JTextField nameTextField;

    // --------------------------------- //
    // --- constructor and listeners --- //
    // --------------------------------- //
    public GameController() {
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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

        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                restartPrompt();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Press to Start")) {
            String name = this.nameTextField.getText().toLowerCase();
            if (name.isBlank() || name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a name to proceed");
            } else {
                this.onStartScreen = false;
                this.gamePanel = new GamePanel(this.frame.getY(), this.frame.getInsets().top, name);
                this.frame.getContentPane().remove(startPanel);
                this.frame.add(this.gamePanel);
                this.frame.pack();
                this.frame.revalidate();
                this.frame.repaint();
            }
        }
    }

    // ----------------------------------- //
    // --- screen navigation and setup --- //
    // ----------------------------------- //
    private void initStartPanel() {
        this.onStartScreen = true;
        
        this.startPanel = new JPanel();
        this.startPanel.setPreferredSize(WINDOW_SIZE);
        this.startPanel.setBackground(Color.LIGHT_GRAY);
        this.startPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 400, 125));
        
        JLabel titleLabel = new JLabel("Haptic Feedback Test Environment");
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 50));

        this.startButton = new JButton("Press to Start");
        this.startButton.setPreferredSize(new Dimension(600, 200));
        this.startButton.setFont(new Font("Calibri", Font.PLAIN, 30));

        JPanel nameFieldPanel = new JPanel();
        JLabel nameFieldLabel = new JLabel("Name: ");
        nameFieldLabel.setFont(new Font("Lato", Font.PLAIN, 25));
        this.nameTextField = new JTextField(24);
        this.nameTextField.setPreferredSize(new Dimension(325, 50));
        this.nameTextField.setFont(new Font("Lato", Font.PLAIN, 20));
        nameFieldPanel.setBackground(Color.LIGHT_GRAY);
        nameFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        nameFieldPanel.setPreferredSize(new Dimension(800, 80));
        nameFieldPanel.add(nameFieldLabel);
        nameFieldPanel.add(this.nameTextField);

        this.startPanel.add(titleLabel);
        this.startPanel.add(nameFieldPanel);
        this.startPanel.add(startButton);

        this.startButton.addActionListener(this);
        this.nameTextField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startButton.doClick();
                }
            }
        });
    }

    private void restartPrompt() {
        if (onStartScreen) {
            this.frame.dispose();
            System.exit(0);

        } else {
            int restartDialogButton = JOptionPane.showConfirmDialog(null, "Do you wish to restart?");
            
            // restarting the app
            if (restartDialogButton == JOptionPane.YES_OPTION) {
                this.frame.getContentPane().remove(gamePanel);
                initStartPanel();
                this.frame.add(startPanel);
                this.frame.pack();
                this.frame.revalidate();
                this.frame.repaint();
    
            // closing the app
            } else if (restartDialogButton == JOptionPane.NO_OPTION) {
                this.frame.dispose();
                System.exit(0);
            }
        }
    } 

    private void initCalibrationScreen() {

    }

}
