import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.opencsv.*;

public class GameController implements ActionListener {

    private final boolean SAVE_ENABLED = true;
    private final String CSV_PATH = "./userdata.csv";

    public static Dimension WINDOW_SIZE;

    private boolean onStartScreen;
    private String userName;

    private JFrame frame;
    private JPanel startPanel;
    private GamePanel gamePanel;
    private JButton startButton;
    private JTextField nameTextField;

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
            String name = this.nameTextField.getText().toUpperCase();
            if (name.isBlank() || name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a name to proceed");
            } else {
                this.userName = name;
                this.onStartScreen = false;
                this.gamePanel = new GamePanel(this.frame.getY(), this.frame.getInsets().top);
                this.frame.getContentPane().remove(startPanel);
                this.frame.add(this.gamePanel);
                this.frame.pack();
                this.frame.revalidate();
                this.frame.repaint();
            }
        }
    }

    private void initStartPanel() {
        this.onStartScreen = true;

        this.startPanel = new JPanel();
        this.startPanel.setPreferredSize(WINDOW_SIZE);
        this.startPanel.setBackground(Color.LIGHT_GRAY);
        this.startPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 400, 125));

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

        JLabel titleLabel = new JLabel("Haptic Feedback Test Environment");
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 50));

        this.startPanel.add(titleLabel);
        this.startPanel.add(nameFieldPanel);
        this.startPanel.add(startButton);

        this.startButton.addActionListener(this);
    }

    private void restartPrompt() {
        if (onStartScreen) {
            this.frame.dispose();
            System.exit(0);

        } else {
            int restartDialogButton = JOptionPane.showConfirmDialog (null, "Do you wish to restart?","Warning", JOptionPane.YES_NO_OPTION);
            if (restartDialogButton == JOptionPane.YES_OPTION) {
                // restarting the app
                if (SAVE_ENABLED) {
                    saveScoreToCsv();
                }

                

                this.frame.getContentPane().remove(gamePanel);
                initStartPanel();
                this.frame.add(startPanel);
                this.frame.pack();
                this.frame.revalidate();
                this.frame.repaint();
    
            } else {
                // closing the app
                int exitDialogButton = JOptionPane.showConfirmDialog (null, "Do you wish to close the app?","Warning", JOptionPane.YES_NO_OPTION);
                if (exitDialogButton == JOptionPane.YES_OPTION) {
                    this.frame.dispose();
                    System.exit(0);
                }
            }
        }
    } 

    private void saveScoreToCsv() {
        // convert scores into string[]
        ArrayList<Integer> scores = this.gamePanel.getScores();
        int csvColumns = scores.size()+1;

        String[] csvLine = new String[csvColumns];
        csvLine[0] = this.userName;
        for (int i=1; i<csvColumns; i++) {
            csvLine[i] = scores.get(i-1).toString();
        }
        
        ArrayList<String[]> data = new ArrayList<>();
        data.add(csvLine);

        // write data to csv file
        File file = new File(CSV_PATH);
        try {
            FileWriter outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);
            writer.writeAll(data);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
