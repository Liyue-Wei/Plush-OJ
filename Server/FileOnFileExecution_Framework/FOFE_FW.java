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
        }
    }
}