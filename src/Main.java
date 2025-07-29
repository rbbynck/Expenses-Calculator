import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String fileLocation = "src/expenses.txt"; // Txt file
        String line;
        List<String> words;
        double[] totalPerMonth = new double[12];
        String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        String[] container;
        int monthCounter = 0;

        try {
            Path path = Paths.get(fileLocation);

            words = commaRemoval((ArrayList<String>) Files.readAllLines(path));

            //Transfer expenses per day into a double data
            for (String word : words) {
                line = word.trim();

                //Check if the month changes(I'M HIM)
                for (int j = 1; j < months.length; j++) {
                    if (line.equals(months[j])) {
                        monthCounter++;
                    }
                }

                if (line.contains(" - ")) {
                    String regex = "[-\\s]";
                    container = line.split(regex);
                    for (int j = 2; j < container.length; j++) {
                        if (!("".equals(container[j]))) {
                            totalPerMonth[monthCounter] += (Double.parseDouble(container[j]));
                        }
                    }
                }
            }

            // Display the totals per month and for whole year
            displayTotal(months, totalPerMonth);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<String> commaRemoval(ArrayList<String> words) {
        String line;
        for (int i = 0; i < words.size(); i++) {
            line = words.get(i);

            if (line.equals("")) {
                words.remove(i);
            } else if (line.contains(",")) {
                line = line.replace(",", "");
                words.set(i, line);
            }
        }

        return words;
    }

    public static void displayTotal(String[] months, double[] totalPerMonth) {
        System.out.println("EXPENSES THIS YEAR: ");
        for (int i = 0; i < months.length; i++) {
            System.out.printf("%s : ₱%,.2f\n", months[i], totalPerMonth[i]);
        }
        System.out.printf("\n\nTOTAL : ₱%,.2f", Arrays.stream(totalPerMonth).sum());
    }
}