import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
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

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String baseDir = "WebPages";
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticFileHandler(baseDir));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:" + port + "/");
    }

    static class StaticFileHandler implements HttpHandler {
        private final String baseDir;
        public StaticFileHandler(String baseDir) { this.baseDir = baseDir; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.equalsIgnoreCase("/home.html")) {
                path = "/Home.html";
            }
            File file = new File(baseDir + path);
            if (file.exists() && file.isFile()) {
                String contentType = guessContentType(file.getName());
                byte[] bytes = Files.readAllBytes(file.toPath());
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            } else {
                System.out.println("找不到檔案：" + file.getAbsolutePath());
                String notFound = "404 Not Found";
                exchange.sendResponseHeaders(404, notFound.length());
                exchange.getResponseBody().write(notFound.getBytes());
            }
            exchange.close();
        }
        private String guessContentType(String filename) {
            if (filename.endsWith(".html")) return "text/html; charset=UTF-8";
            if (filename.endsWith(".css")) return "text/css; charset=UTF-8";
            if (filename.endsWith(".js")) return "application/javascript";
            return "application/octet-stream";
        }
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

javac -cp ".;sqlite-jdbc-3.49.1.0.jar" Plush_OJ_Server_Test.java
java -cp ".;sqlite-jdbc-3.49.1.0.jar" Plush_OJ_Server_Test
*/

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