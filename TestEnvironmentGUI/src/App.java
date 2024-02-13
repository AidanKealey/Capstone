import javax.swing.SwingUtilities;

// TODO: make the data append to a csv file
// TODO: measure the time taken for a user to guess the target
// TODO: make it easier to run tests using various sizes of activation radius'
// TODO: make a calibration round to center the mouse

public class App {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameController();
            }
        });
    }
}
