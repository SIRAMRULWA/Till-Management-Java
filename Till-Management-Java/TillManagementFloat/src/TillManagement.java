import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TillManagement {

    public static void main(String[] args) {
        try {
            processTransactions("input.txt", "output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void processTransactions(String inputFile, String outputFile) throws IOException {
        int till = 500; // Starting float
        FileWriter writer = new FileWriter(outputFile);
        writer.write("Till Start, Transaction Total, Paid, Change Total, Change Breakdown\n");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length != 2) {
                System.out.println("Invalid input format: " + line);
                continue; // Skip this line and proceed to the next one
            }
            String itemsStr = parts[0];
            String paymentStr = parts[1];

            // Calculate total cost of items
            int totalCost = calculateTotalCost(itemsStr);

            // Parse payment amounts and calculate total paid
            int totalPaid = calculateTotalPaid(paymentStr);

            // Calculate change
            int change = totalPaid - totalCost;

            // Format change breakdown
            String changeBreakdown = calculateChange(change);

            // Write transaction details to output file
            writer.write(String.format("R%d, R%d, R%d, R%d, %s\n", till, totalCost, totalPaid, change, changeBreakdown));

            // Update till amount
            till += totalCost;
        }
        reader.close();

        // Write amount left in till to output file
        writer.write(String.format("R%d", till));
        writer.close();
    }

    public static int calculateTotalCost(String itemsStr) {
        String[] items = itemsStr.split(";");
        int totalCost = 0;
        for (String item : items) {
            String[] itemParts = item.trim().split(" R");
            totalCost += Integer.parseInt(itemParts[itemParts.length - 1]);
        }
        return totalCost;
    }

    public static int calculateTotalPaid(String paymentStr) {
        String[] payments = paymentStr.split("-");
        int totalPaid = 0;
        for (String payment : payments) {
            // Extract numeric part from the payment string
            String numericPart = payment.replaceAll("[^0-9]", "");
            totalPaid += Integer.parseInt(numericPart);
        }
        return totalPaid;
    }

    public static String calculateChange(int change) {
        int[] denominations = {50, 20, 10, 5, 2, 1};
        StringBuilder changeBreakdown = new StringBuilder();
        boolean firstDenomination = true;
        for (int denom : denominations) {
            int count = change / denom;
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    if (!firstDenomination) {
                        changeBreakdown.append("-");
                    } else {
                        firstDenomination = false;
                    }
                    changeBreakdown.append("R").append(denom);
                }
                change -= count * denom;
            }
        }
        return changeBreakdown.toString();
    }
}