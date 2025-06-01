import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer; // 新增

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
        String baseDir = "Web_DEMO";
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticFileHandler(baseDir));
        server.createContext("/signup", new SignUpHandler()); // 新增註冊處理器
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:" + port + "/");
    }

    static class StaticFileHandler implements HttpHandler {
        private final String baseDir;
        public StaticFileHandler(String baseDir) { this.baseDir = baseDir; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (exchange) {
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
            }
        }
        private String guessContentType(String filename) {
            if (filename.endsWith(".html")) return "text/html; charset=UTF-8";
            if (filename.endsWith(".css")) return "text/css; charset=UTF-8";
            if (filename.endsWith(".js")) return "application/javascript";
            return "application/octet-stream";
        }
    }

    static class SignUpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (exchange) {
                if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                    return;
                }
                // 解析表單資料
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder buf = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) buf.append(line);
                String formData = buf.toString();
                // 解析欄位
                String[] pairs = formData.split("&");
                String email = "", account = "", password = "", confirm = "";
                for (String pair : pairs) {
                    String[] kv = pair.split("=", 2);
                    String key = java.net.URLDecoder.decode(kv[0], "UTF-8");
                    String value = kv.length > 1 ? java.net.URLDecoder.decode(kv[1], "UTF-8") : "";
                    switch (key) {
                        case "email" -> email = value;
                        case "account" -> account = value;
                        case "password" -> password = value;
                        case "confirm-password" -> confirm = value;
                    }
                }
                String response;
                if (!password.equals(confirm)) {
                    try (exchange) {
                        response = "<script>alert('兩次密碼不一致');window.location='/SignUP.html';</script>";
                        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        exchange.getResponseBody().write(response.getBytes());
                    }
                    return;
                }
                // 寫入 SQLite
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Server_Test/Database/UserDB/userdb.db")) {
                    String sql = "INSERT INTO UserInfo (Account, PassWD, Email) VALUES (?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, account);
                        pstmt.setString(2, password); // 實際應加密
                        pstmt.setString(3, email);
                        pstmt.executeUpdate();
                    }
                    response = "<script>alert('註冊成功，請登入');window.location='/Login.html';</script>";
                } catch (SQLException e) {
                    response = "<script>alert('註冊失敗，帳號或信箱可能已存在');window.location='/SignUP.html';</script>";
                }
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
            }        }
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

javac -cp ".;sqlite-jdbc-3.49.0.1.jar" Plush_OJ_Server_Test.java
java -cp ".;sqlite-jdbc-3.36.0.3.jar" Plush_OJ_Server_Test
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