import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Plush_OJ_Server_Test {
    public static StringBuffer console_output = new StringBuffer();
    private static class StreamGobbler implements Runnable {
        private final InputStreamReader inputStreamReader;
        private final Consumer<String> consumer;

        public StreamGobbler(InputStreamReader inputStreamReader, Consumer<String> consumer) {
            this.inputStreamReader = inputStreamReader;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(inputStreamReader).lines().forEach(consumer);
        }
    }

    public static int FOFE(String QN, String info, String lang) {  // Question Number, Information, Language
        Process process = null;
        int exitCode;
        final String TCP = "Server_Test/FileOnFileExecution_Framework/TempCode/" + info + "." + lang;  // Temp Code Path
        final String QDP = "Server_Test/Database/QuestionDB/TestDATA/" + QN + ".json";  // Question Database Path

        try (java.util.concurrent.ExecutorService executor = Executors.newFixedThreadPool(2)) {
            ProcessBuilder commandBuilder = new ProcessBuilder(
                    "python",
                    "Server_Test/FileOnFileExecution_Framework/FF_Python_CTR.py",
                    TCP,
                    QDP,
                    info
            );

            process = commandBuilder.start();
            boolean exited = process.waitFor(90, TimeUnit.SECONDS);  // Timeout set to 90 seconds

            StreamGobbler outputGobbler = new StreamGobbler(
                    new InputStreamReader(process.getInputStream()),
                    line -> console_output.append(line).append('\n')
            );
            executor.submit(outputGobbler);

            StreamGobbler errorGobbler = new StreamGobbler(
                    new InputStreamReader(process.getErrorStream()),
                    line -> console_output.append(line).append('\n')
            );
            executor.submit(errorGobbler);

            if (exited) {
                exitCode = process.exitValue();
                System.out.println("Return Code: " + exitCode);

                if (exitCode == 0) {
                    System.out.println("Python script executed successfully.");
                } else {
                    System.err.println("Python script execution failed with error code: " + exitCode);
                }
            } else {
                process.destroyForcibly();
                System.err.println("Unexpected System Error, Terminated...");
            }

        } catch (Exception e) {
            if (process != null) {
                process.destroyForcibly();
            }
            e.printStackTrace();
            System.err.println("Unexpected System Error: " + e.getMessage());
        } 

        // System.out.println(console_output);
        return exitCode;
    }

    public static void main(String[] args) {
        FOFE("QN001A", "QN001A-XXXXX-0000-00-00", "cpp");
        System.out.println("Output:\n" + console_output);
    }
}