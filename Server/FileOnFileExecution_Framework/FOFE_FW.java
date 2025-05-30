package FileOnFileExecution_Framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FOFE_FW {
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

    public static void main(String[] args) {
        Process process = null;
        try (java.util.concurrent.ExecutorService executor = Executors.newFixedThreadPool(2)) {
            ProcessBuilder commandBuilder = new ProcessBuilder(
                    "python",
                    "Server/FileOnFileExecution_Framework/FF_Container/FF_Python_CTR.py",
                    "Test/Temp_Code/QN001A-XXXXX-0000-00-00.cpp",
                    "Test/Temp_JSON/QN001A.json",
                    "QN001A-XXXXX-0000-00-00"
            );

            System.out.println("Starting Python script...");
            process = commandBuilder.start();

            StreamGobbler outputGobbler = new StreamGobbler(
                    new InputStreamReader(process.getInputStream()),
                    System.out::println // 將每一行輸出到控制台
            );
            executor.submit(outputGobbler);

            StreamGobbler errorGobbler = new StreamGobbler(
                    new InputStreamReader(process.getErrorStream()),
                    System.err::println // 將每一行錯誤輸出到控制台
            );
            executor.submit(errorGobbler);

            boolean exited = process.waitFor(60, TimeUnit.SECONDS);

            if (exited) {
                int exitCode = process.exitValue(); // 或者直接使用 waitFor() 的回傳值 (如果沒有超時)
                // int exitCode = process.waitFor(); // 如果不設定超時
                System.out.println("\nPython script finished.");
                System.out.println("Return Code: " + exitCode);

                if (exitCode == 0) {
                    System.out.println("Python script executed successfully.");
                } else {
                    System.err.println("Python script execution failed with error code: " + exitCode);
                }
            } else {
                System.err.println("Python script timed out.");
                // 如果超時，你可能需要強制終止程序
                process.destroyForcibly();
                System.err.println("Process forcibly terminated due to timeout.");
            }

        } catch (IOException e) {
            System.err.println("Error starting or communicating with the process: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Process waiting was interrupted: " + e.getMessage());
            // 當一個執行緒在等待、睡眠或以其他方式被佔用，並且該執行緒被另一個執行緒中斷時，會拋出此異常。
            // 恢復中斷狀態是一個好習慣。
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
            if (process != null && process.isAlive()) {
                System.err.println("Process is still alive, attempting to destroy it.");
                process.destroyForcibly(); // 強制終止
            }
        }
    }
}