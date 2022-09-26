import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class RecursiveNumberSum extends RecursiveTask<Integer> {
    private final File file;

    public RecursiveNumberSum(File file) {
        this.file = file;
    }

    @Override
    protected Integer compute() {
        String path = file.getPath();

        if (file.exists()) {
            int sum = 0;
            if (file.isDirectory()) {
                List<RecursiveNumberSum> taskList = new ArrayList<>();
                for (File child : file.listFiles()) {
                    RecursiveNumberSum task = new RecursiveNumberSum(child);
                    task.fork();
                    taskList.add(task);
                }

                for (RecursiveNumberSum task : taskList) {
                    sum += task.join();
                }

            } else if (path.endsWith(".txt")) {
                try {
                    sum += sumOfNumbersInFile(path);
                    System.out.println("Sum in file " + path + " is " + sum);
                } catch (IOException | RuntimeException e) {
                    e.printStackTrace();
                }
            }

            return sum;
        }

        System.err.print("File " + file.getPath() + " isn't found!");
        return null;
    }

    private int sumOfNumbersInFile(String path) throws IOException, RuntimeException {
        int sum = 0;
        List<String> strings = Files.readAllLines(Paths.get(path));
        for (String string : strings) {
            int number = Integer.parseInt(string);
            sum += number;
        }

        return sum;
    }
}
