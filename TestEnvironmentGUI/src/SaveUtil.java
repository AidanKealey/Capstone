import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVWriter;

public class SaveUtil {
    
    public final static boolean SAVE_ENABLED = true;
    private final static String CSV_PATH = "./userdata.csv";

    public static void saveToCsv(String username, ArrayList<Integer> scores, ArrayList<Double> times) {
        System.out.println("Saving score data for user: \""+username+"\"...");

        // convert scores into string array
        int csvColumns = scores.size() + times.size() + 1;
        String[] data = new String[csvColumns];
        data[0] = username;
        for (int i=1; i<csvColumns; i++) {
            data[i] = (i < 6) ? scores.get(i-1).toString() : times.get(i-6).toString();
        }

        // write data to csv file
        File file = new File(CSV_PATH);
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file, true));
            writer.writeNext(data);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
