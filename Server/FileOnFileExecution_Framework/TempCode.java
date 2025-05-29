package FileOnFileExecution_Framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TempCode {

    // 輔助類別，用於非阻塞地讀取串流
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
        Process process = null; // 將 process 宣告在 try 區塊外部，以便 finally 中可以存取
        // 使用 ExecutorService 來管理讀取輸出和錯誤流的執行緒
        // 建議使用 try-with-resources 來確保 ExecutorService 被正確關閉
        try (java.util.concurrent.ExecutorService executor = Executors.newFixedThreadPool(2)) {
            ProcessBuilder commandBuilder = new ProcessBuilder(
                    "python",
                    "Server/FileOnFileExecution_Framework/FF_Container/FF_Python_CTR.py",
                    "Test/Temp_Code/QN001A-XXXXX-0000-00-00.cpp",
                    "Test/Temp_JSON/QN001A.json",
                    "QN001A-XXXXX-0000-00-00"
            );
            // 可以選擇性地設定工作目錄
            // commandBuilder.directory(new File("C:/path/to/your/working/directory"));

            // 可以選擇性地合併標準錯誤流到標準輸出流
            // commandBuilder.redirectErrorStream(true); // 如果這樣做，就不需要單獨讀取 errorStream

            System.out.println("Starting Python script...");
            process = commandBuilder.start();

            // 非阻塞地讀取標準輸出
            StreamGobbler outputGobbler = new StreamGobbler(
                    new InputStreamReader(process.getInputStream()),
                    System.out::println // 將每一行輸出到控制台
            );
            executor.submit(outputGobbler);

            // 非阻塞地讀取標準錯誤
            StreamGobbler errorGobbler = new StreamGobbler(
                    new InputStreamReader(process.getErrorStream()),
                    System.err::println // 將每一行錯誤輸出到控制台
            );
            executor.submit(errorGobbler);

            // 等待外部程序執行完成，並獲取 return code
            // 可以設定一個超時時間，避免無限期等待
            boolean exited = process.waitFor(60, TimeUnit.SECONDS); // 例如，等待最多 60 秒

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
            // 確保 ExecutorService 被關閉
            // 如果使用 try-with-resources，這一步驟會自動處理
            // if (executor != null && !executor.isShutdown()) {
            //     executor.shutdown();
            //     try {
            //         // 等待一段時間讓執行緒完成任務
            //         if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            //             executor.shutdownNow();
            //         }
            //     } catch (InterruptedException e) {
            //         executor.shutdownNow();
            //         Thread.currentThread().interrupt();
            //     }
            // }

            // 確保程序被銷毀（如果它仍在運行且發生錯誤）
            // 這部分邏輯可以根據你的需求調整
            if (process != null && process.isAlive()) {
                System.err.println("Process is still alive, attempting to destroy it.");
                process.destroyForcibly(); // 強制終止
            }
        }
    }
}

// python Server\FileOnFileExecution_Framework\FF_Container\FF_Python_CTR.py Test\Temp_Code\QN001A-XXXXX-0000-00-00.cpp Test\Temp_JSON\QN001A.json QN001A-XXXXX-0000-00-00