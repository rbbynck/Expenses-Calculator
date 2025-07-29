import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/*
This class is for the breakdown of what I've been spending each month
What this app should do
 - 1. Get the textfile and turn it into an object
 - 2. Trim the txt file (Remove the comma, and whitespaces)
 - Before
    JANUARY
    1
    Food - 2,000 500
 - After
    JANUARY
    1
    Food - 2000 500
 - 3. Map the Item and Amount to a HashMap (Food - 500) Food - Item(String) | 500 - Amount(Double)
 - 4. Add the amount if there's a space in the line (Food - 350 100 50) (Food - 500)
 - 5. Map it for every month, HashMap<Month, HashMap<Item, Amount>>
 - 6. Display:
        Month
            - Breakdowns
        = Total

        Year
            - Breakdown (For the whole year)
        = Total
 */

public class ExpensesBreakdown {

    public static void main(String[] args) {
        String fileLocation = "src/expenses.txt"; // Txt file
        String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

        displayTotal(months, mappingItemAndAmount(fileLocation, months));
    }

    public static HashMap<Integer, HashMap<String, Double>> mappingItemAndAmount(String fileLocation, String[] months) {
        List<String> words; // Container for the txt
        String line; // Container for the current line from words
        HashMap<Integer, HashMap<String, Double>> monthBreakdown = new HashMap<>(); // Container for the breakdown per month
        int monthCounter = 0;
        HashMap<String, Double> breakdown = new HashMap<>();
        String[] container; //An array container for split line (Grocery - 5040) [Grocery, , , 5040]
        double[] totalPerMonth = new double[12];

        try {
            Path path = Paths.get(fileLocation);

            // Removes the comma from the every line (ex. Comics - 15,000)
            words = commaRemoval((ArrayList<String>) Files.readAllLines(path));

            //Transfer expenses per day into a double data
            for (String word : words) {
                line = word.trim();

                //Check if the month changes(I'M HIM)
                for (int j = 1; j < 12; j++) {
                    if (line.equals(months[j])) {
                        monthBreakdown.put(monthCounter, new HashMap<>(breakdown));
                        breakdown.clear();
                        monthCounter++;
                    }
                }

                // Check if line contains "-", for the expenses.txt the example is "Food - 50"
                if (line.contains(" - ")) {
                    container = line.split("[-\\s]"); // Split the line and get the integer

                    for (int j = 2; j < container.length; j++) {
                        if (!("".equals(container[j]))) {
                            if (!breakdown.containsKey(container[0])) {
                                breakdown.put(container[0], (Double.parseDouble(container[j])));
                            } else {
                                double updatedNumber = breakdown.get(container[0]) + (Double.parseDouble(container[j]));
                                breakdown.put(container[0], updatedNumber);
                            }
                            totalPerMonth[monthCounter] += (Double.parseDouble(container[j]));
                        }
                    }

                }
            }
            // Add breakdown for December
            for (int i = 0; i < months.length; i++) {
                if (totalPerMonth[i] > 0) {
                    if (!monthBreakdown.containsKey(i)) {
                        monthBreakdown.put(i, new HashMap<>(breakdown));
                        breakdown.clear();
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return monthBreakdown;
    }

    /*
    Returns the updated ArrayList words (expenses.txt)
    What it does:
        - Remove comma from numbers (Food - 1,500) becomes (Food - 1500)
        so it can be read as double or int
     */
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

    public static int getMaxLength(HashMap<Integer, HashMap<String, Double>> monthBreakdown) {
        // Find the longest item name for alignment for the whole year
        int maxLength = 0;
        for (HashMap<String, Double> month : monthBreakdown.values()) {
            for (String key : month.keySet()) {
                if (key.length() > maxLength) {
                    maxLength = key.length();
                }
            }
        }

        return maxLength;
    }

    /*
    Display the Total Breakdown for the Year
    What it does:
        - Get the HashMap<Item, Amount> from monthBreakdown.
        - It summarize all the Item and its amount for the year
        - Example: January can have (Food, Grocery, Shipping, Books) and February can have (Food, Grocery, Books, Shoes)
        - The total breakdown should be (Food, Grocery, Books, Shoes, Shipping)
        - It joins all the things I've spent on and creates a sum of it
     */
    public static void yearTotalBreakdown(HashMap<Integer, HashMap<String, Double>> monthBreakdown) {
        // Get the total expenses for the whole year
        double totalForTheYear = monthBreakdown.values().stream().mapToDouble(entry -> entry.values().stream().mapToDouble(Double::doubleValue).sum()).sum();

        HashMap<String, Double> totalBreakdown = new HashMap<>();

        for (int i = 0; i < monthBreakdown.size(); i++) {
            monthBreakdown.get(i).entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        if (totalBreakdown.containsKey(entry.getKey())) {
                            double updatedNumber = totalBreakdown.get(entry.getKey()) + entry.getValue();
                            totalBreakdown.put(entry.getKey(), updatedNumber);
                        } else {
                            totalBreakdown.put(entry.getKey(), entry.getValue());
                        }
                    });
        }

        // Print the year breakdown
        totalBreakdown.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> System.out.printf("\t%-" + (getMaxLength(monthBreakdown) + 1) + "s: ₱%,.2f%n", entry.getKey(), entry.getValue()));

        // Print the sum of all the breakdown
        System.out.println("_______________________________________");
        System.out.printf("\t%-" + (getMaxLength(monthBreakdown) + 1) + "s: ₱%,.2f", "TOTAL", totalForTheYear);
    }

    /*
    Display the whole calculations including the yearTotalBreakdown
     */
    public static void displayTotal(String[] months, HashMap<Integer, HashMap<String, Double>> monthBreakdown) {
        // Print the expenses
        System.out.println("=======================================");
        System.out.println("\t\tEXPENSES 2025");
        System.out.println("=======================================");


        // Print breakdown and total per month
        for (int i = 0; i < months.length; i++) {
            // Breakdown per month
            if (monthBreakdown.get(i) != null) {
                System.out.println(months[i] + "\n_______________________________________");
                monthBreakdown.get(i).entrySet().stream().sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> System.out.printf("\t%-" + (getMaxLength(monthBreakdown) + 1) + "s: ₱%,.2f%n", entry.getKey(), entry.getValue()));
                System.out.println("_______________________________________");
                System.out.printf("\t%-" + (getMaxLength(monthBreakdown) + 1) + "s: ₱%,.2f%n%n",
                        "TOTAL", monthBreakdown.get(i).values().stream().mapToDouble(Double::doubleValue).sum());

                System.out.println("---------------------------------------");
                System.out.println("---------------------------------------\n");
            }
        }

        System.out.println("\n\n_____________________________________");
        System.out.println("\t\t2025 TOTAL Breakdown");
        System.out.println("_____________________________________");
        yearTotalBreakdown(monthBreakdown);
    }
}