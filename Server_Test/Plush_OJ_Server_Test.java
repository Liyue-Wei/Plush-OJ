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
        int exitCode = -1;
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
            boolean exited = process.waitFor(90, TimeUnit.SECONDS);  

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
                return exitCode;
            } else {
                process.destroyForcibly();
                System.err.println("Unexpected System Error, Terminated...");
            }

        } catch (Exception e) {
            if (process != null) {
                process.destroyForcibly();
            }
            System.err.println("Unexpected System Error, Terminated...");
        } 
        return exitCode;
    }

    public static void main(String[] args) {
        /*
        int RC = FOFE("QN001A", "QN001A-XXXXX-0000-00-00", "cpp");  // Return Code
        switch (RC) {
            case 0:
                System.out.println("Console Output:\n" + console_output);
                break;
            case 3:
                System.out.println("Prohibited Header Detected");
                break;
            case 4:
                System.out.println("Compile Error");
                break;
            case 5:
                System.out.println("Time Limit Exceeded");
                break;
            case 6:
                System.out.println("Memory Limit Exceeded");
                break;
            case 7:
                System.out.println("Wrong Answer");
                break;
            default:
                System.out.println("Unexpected System Error");
                break;
        }
        */


    }
}

/*
Error Code 1 : Invalid args Input
Error Code 2 : Reading Temp Code
Error Code 3 : Prohibited Header Detected
Error Code 4 : Compile Error
Error Code 5 : Time Limit Exceeded
Error Code 6 : Memory Limit Exceeded
Error Code 7 : Wrong Answer
Error Code 8 :
Error Code 9 : Unexpected System Error
Error Code 10 : Unsupported Language
 */