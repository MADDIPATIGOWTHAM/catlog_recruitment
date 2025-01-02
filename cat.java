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
            
            String filePath1 = "./testcase1.json";  
            String filePath2 = "./testcase2.json";  


            
            System.out.println("Result for Test Case 1:");
            processTestCase(filePath1);

            System.out.println("\nResult for Test Case 2:");
            processTestCase(filePath2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private static void processTestCase(String filePath) {
        try {
            
            String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject input = new JSONObject(jsonString);

            
            JSONObject keys = input.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");

            
            List<BigInteger[]> points = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                if (input.has(String.valueOf(i))) {
                    JSONObject root = input.getJSONObject(String.valueOf(i));
                    int x = i;
                    int base = Integer.parseInt(root.getString("base"));
                    String encodedValue = root.getString("value");

                    
                    BigInteger y = new BigInteger(encodedValue, base);
                    points.add(new BigInteger[]{BigInteger.valueOf(x), y});
                }
            }

            
            if (points.size() < k) {
                throw new IllegalArgumentException("Insufficient points for interpolation");
            }

            
            BigInteger constantTerm = findConstantTerm(points.subList(0, k));

            
            System.out.println("Constant term (c): " + constantTerm.toString(16));  // Output in hex format

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private static BigInteger findConstantTerm(List<BigInteger[]> points) {
        BigInteger c = BigInteger.ZERO;
        int k = points.size();
    
        
        for (int i = 0; i < k; i++) {
            BigInteger[] p1 = points.get(i);
            BigInteger xi = p1[0], yi = p1[1];
            BigInteger li = BigInteger.ONE;
    
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger[] p2 = points.get(j);
                    BigInteger xj = p2[0];

                    BigInteger diff = xi.subtract(xj).mod(MAX_256BIT);
                    
                    if (diff.gcd(MAX_256BIT).equals(BigInteger.ONE)) {
                        li = li.multiply(diff.modInverse(MAX_256BIT)).mod(MAX_256BIT);
                    } else {
                        
                        continue;  
                    }
                }
            }
            c = c.add(yi.multiply(li)).mod(MAX_256BIT); 
        }
        return c;
    }
}