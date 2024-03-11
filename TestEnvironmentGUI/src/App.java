import javax.swing.SwingUtilities;

// TODO: add a sleep time after a click is made so accidental double clicks are less likely
// TODO: restarting USED TO mess up serial writer, please test
// TODO: make a new app for just turning on specific coils

public class App {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameController();
            }
        });
    }
}
