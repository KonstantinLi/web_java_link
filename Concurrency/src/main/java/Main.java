import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String RESOURCE_FOLDER = "src/main/resources/data";
    private static final String RESULT = "src/main/resources/result.txt";

    public static void main(String[] args) {
        RecursiveNumberSum mainTask = new RecursiveNumberSum(new File(RESOURCE_FOLDER));
        System.out.println("Recursive program is running...\n");

        int sum = new ForkJoinPool().invoke(mainTask);
        writeNumberToFile(RESULT, sum);

        try {
            int numberInFile = Integer.parseInt(Files.readString(Paths.get(RESULT)));
            System.out.println("\nTotal sum = " + numberInFile);
        } catch (IOException | RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    public static void writeNumberToFile(String path, int number) {
        try (PrintWriter writer = new PrintWriter(RESULT)) {
            writer.write(String.valueOf(number));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
