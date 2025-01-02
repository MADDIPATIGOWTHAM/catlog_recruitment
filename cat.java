import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.*;

public class cat {
    private static final BigInteger MAX_256BIT = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

    public static void main(String[] args) {
        try {
            // Define the file paths for each test case
            String filePath1 = "./testcase1.json";  // Relative path
            String filePath2 = "./testcase2.json";  // Relative path


            // Call method for each test case and print results
            System.out.println("Result for Test Case 1:");
            processTestCase(filePath1);

            System.out.println("\nResult for Test Case 2:");
            processTestCase(filePath2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to process each test case
    private static void processTestCase(String filePath) {
        try {
            // Load and parse the JSON input file
            String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject input = new JSONObject(jsonString);

            // Extract n, k, and points
            JSONObject keys = input.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");

            // Collect points (x, y) for interpolation
            List<BigInteger[]> points = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                if (input.has(String.valueOf(i))) {
                    JSONObject root = input.getJSONObject(String.valueOf(i));
                    int x = i;
                    int base = Integer.parseInt(root.getString("base"));
                    String encodedValue = root.getString("value");

                    // Use BigInteger for large values
                    BigInteger y = new BigInteger(encodedValue, base);
                    points.add(new BigInteger[]{BigInteger.valueOf(x), y});
                }
            }

            // Ensure we have at least k points for interpolation
            if (points.size() < k) {
                throw new IllegalArgumentException("Insufficient points for interpolation");
            }

            // Use the first k points for Lagrange Interpolation
            BigInteger constantTerm = findConstantTerm(points.subList(0, k));

            // Output the result for the current test case
            System.out.println("Constant term (c): " + constantTerm.toString(16));  // Output in hex format

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lagrange Interpolation to find the constant term
    private static BigInteger findConstantTerm(List<BigInteger[]> points) {
        BigInteger c = BigInteger.ZERO;
        int k = points.size();
    
        // Lagrange Interpolation
        for (int i = 0; i < k; i++) {
            BigInteger[] p1 = points.get(i);
            BigInteger xi = p1[0], yi = p1[1];
            BigInteger li = BigInteger.ONE;
    
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger[] p2 = points.get(j);
                    BigInteger xj = p2[0];

                    BigInteger diff = xi.subtract(xj).mod(MAX_256BIT);
                    // Check if diff is invertible (GCD == 1)
                    if (diff.gcd(MAX_256BIT).equals(BigInteger.ONE)) {
                        li = li.multiply(diff.modInverse(MAX_256BIT)).mod(MAX_256BIT);
                    } else {
                        // Log or handle the case where inverse is not possible
                        continue;  // Skip this invalid pair
                    }
                }
            }
            c = c.add(yi.multiply(li)).mod(MAX_256BIT); // Add the contribution of this term and reduce modulo 2^256
        }
        return c;
    }
}