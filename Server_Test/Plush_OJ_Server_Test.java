import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
// ...existing code...
// import org.json.JSONObject; // 需要 org.json 庫

public class Plush_OJ_Server_Test {
    // 新增一個全域單執行緒排程器
    private static final java.util.concurrent.ExecutorService judgeQueue = java.util.concurrent.Executors.newSingleThreadExecutor();
    public static StringBuffer console_output = new StringBuffer();
    private static final String DB_PATH = "Database/UserDB/userdb.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

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
        final String TCP = "FileOnFileExecution_Framework/TempCode/" + info + "." + lang;  // Temp Code Path
        final String QDP = "Database/QuestionDB/TestDATA/" + QN + ".json";  // Question Database Path

        try (java.util.concurrent.ExecutorService executor = Executors.newFixedThreadPool(2)) {
            ProcessBuilder commandBuilder = new ProcessBuilder(
                    "python",
                    "FileOnFileExecution_Framework/FF_Python_CTR.py",
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

    // public static void main(String[] args) throws Exception {
    //     String qn = "QN001A";
    //     String info = "QN001A-1-20250604-233250";
    //     String lang = "cpp";
    //     console_output.setLength(0);
    //     int rc = FOFE(qn, info, lang);
    //     System.out.println("FOFE return code: " + rc);
    //     System.out.println("Console Output:\n" + console_output);
    // }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String baseDir = "WebPages";
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticFileHandler(baseDir));
        server.createContext("/signup", new SignupHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/question", new QuestionHandler());
        server.createContext("/judge", new JudgeHandler());  
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:" + port + "/");
    }

    static class StaticFileHandler implements HttpHandler {
        private final String baseDir;
        public StaticFileHandler(String baseDir) { this.baseDir = baseDir; }
        @Override
        @SuppressWarnings("ConvertToTryWithResources")
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
            if (filename.endsWith(".png")) return "image/png";
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
            if (filename.endsWith(".txt")) return "text/plain; charset=UTF-8";
            if (filename.endsWith(".json")) return "application/json; charset=UTF-8";
            if (filename.endsWith(".md")) return "text/markdown; charset=UTF-8";
            return "application/octet-stream";
        }
    }

    static class SignupHandler implements HttpHandler {
        @Override
        @SuppressWarnings("ConvertToTryWithResources")
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // 讀取表單資料
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder buf = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    buf.append(line);
                }
                String formData = buf.toString();

                // 解析表單資料
                String email = null;
                String account = null;
                String passwd = null;
                String[] pairs = formData.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=", 2);
                    String key = java.net.URLDecoder.decode(kv[0], "UTF-8");
                    String value = kv.length > 1 ? java.net.URLDecoder.decode(kv[1], "UTF-8") : "";
                    switch (key) {
                        case "email" -> email = value;
                        case "account" -> account = value;
                        case "password" -> passwd = value;
                    }
                }
                System.out.println("email = " + email);
                System.out.println("account = " + account);
                System.out.println("passwd = " + passwd);

                String response;
                // 資料庫操作
                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                    System.out.println("資料庫連線成功!");
                    // 1. 檢查帳號是否已存在
                    String checkSql = "SELECT COUNT(*) FROM UserInfo WHERE Account = ?";
                    try (var pstmt = conn.prepareStatement(checkSql)) {
                        pstmt.setString(1, account);
                        try (var rs = pstmt.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                // 帳號已存在
                                response = """
                                    <script>
                                        alert('註冊失敗：帳號已存在，請重新輸入!');
                                        window.location.href = '/SignUP.html';
                                    </script>
                                    """;
                                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                                exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                                exchange.getResponseBody().write(response.getBytes("UTF-8"));
                                exchange.close();
                                return;
                            }
                        }
                    }
                    // 2. 新增帳號
                    String insertSql = "INSERT INTO UserInfo (Account, PassWD, Email) VALUES (?, ?, ?)";
                    try (var pstmt = conn.prepareStatement(insertSql)) {
                        pstmt.setString(1, account);
                        pstmt.setString(2, passwd);
                        pstmt.setString(3, email);
                        pstmt.executeUpdate();
                    }
                    // 3. 註冊成功回應
                    response = String.format(
                        """
                        <script>
                            alert('註冊成功! 歡迎 %s');
                            window.location.href = '/Home.html';
                        </script>
                        """,
                        account
                    );
                } catch (Exception e) {
                    response = """
                        <script>
                            alert('註冊失敗：系統錯誤，請稍後再試!');
                            window.location.href = '/SignUP.html';
                        </script>
                        """;
                    System.err.println("資料庫操作失敗：" + e.getMessage());
                }
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                exchange.getResponseBody().write(response.getBytes("UTF-8"));
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                exchange.close();
            }
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        @SuppressWarnings("ConvertToTryWithResources")
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // 讀取表單資料
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder buf = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    buf.append(line);
                }
                String formData = buf.toString();

                // 解析表單資料
                String account = null;
                String passwd = null;
                String[] pairs = formData.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=", 2);
                    String key = java.net.URLDecoder.decode(kv[0], "UTF-8");
                    String value = kv.length > 1 ? java.net.URLDecoder.decode(kv[1], "UTF-8") : "";
                    switch (key) {
                        case "account" -> account = value;
                        case "password" -> passwd = value;
                    }
                }

                String response;
                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                    String sql = "SELECT PassWD FROM UserInfo WHERE Account = ?";
                    try (var pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, account);
                        try (var rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                String dbPasswd = rs.getString("PassWD");
                                if (dbPasswd.equals(passwd)) {
                                    response = String.format(
                                        """
                                            <script>
                                                document.cookie = 'account=%s; path=/';
                                                alert('登入成功，歡迎 %s!');
                                                window.location.href = '/Home.html';
                                            </script>
                                        """, 
                                        account, account
                                    );
                                    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                    System.out.println(account + " 登入成功 " + now);
                                } else {
                                    response = 
                                    """
                                        <script>
                                            alert('密碼錯誤，請重新輸入!');
                                            window.location.href = '/Login.html';
                                        </script>
                                    """;
                                }
                            } else {
                                response = 
                                """
                                    <script>
                                        alert('帳號不存在，請重新輸入!');
                                        window.location.href = '/Login.html';
                                    </script>
                                """;
                            }
                        }
                    }
                } catch (Exception e) {
                    response = 
                    """
                        <script>
                            alert('登入失敗：系統錯誤，請稍後再試!');
                            window.location.href = '/Login.html';
                        </script>
                    """;
                    System.err.println("登入時資料庫操作失敗：" + e.getMessage());
                }
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                exchange.getResponseBody().write(response.getBytes("UTF-8"));
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                exchange.close();
            }
        }
    }

    static class QuestionHandler implements HttpHandler {
        @Override
        @SuppressWarnings("ConvertToTryWithResources")
        public void handle(HttpExchange exchange) throws IOException {
            String qn = null;
            String query = exchange.getRequestURI().getQuery();
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] kv = param.split("=", 2);
                    if (kv.length == 2 && kv[0].equals("qn")) {
                        qn = java.net.URLDecoder.decode(kv[1], "UTF-8");
                    }
                }
            }
            String basePath = "Database/QuestionDB/Questions/";
            String filePath = basePath + qn + ".md"; 
            File file = new File(filePath);
            String response;
            if (qn == null) {
                response = "未指定題號";
                exchange.sendResponseHeaders(400, response.getBytes("UTF-8").length);
            } else if (!file.exists()) {
                response = "找不到題目：" + qn;
                exchange.sendResponseHeaders(404, response.getBytes("UTF-8").length);
            } else {
                response = new String(java.nio.file.Files.readAllBytes(file.toPath()), "UTF-8");
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
            }
            exchange.getResponseBody().write(response.getBytes("UTF-8"));
            exchange.close();
        }
    }

    static class JudgeHandler implements HttpHandler {
        @Override
        @SuppressWarnings({"ConvertToTryWithResources", "CallToPrintStackTrace", "UseSpecificCatch"})
        public void handle(HttpExchange exchange) throws IOException {
            // 先複製 exchange 內容到本地變數，避免在排隊時 HttpExchange 已經 close
            byte[] requestBody = exchange.getRequestBody().readAllBytes();
            HttpExchange exchangeCopy = exchange;
            judgeQueue.submit(() -> {
                try {
                    // 重新用 ByteArrayInputStream 包裝 requestBody
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestBody), "UTF-8"));
                    String line;
                    String account = null, qn = null, lang = null, code = null;
                    while ((line = reader.readLine()) != null) {
                        int idx = line.indexOf('=');
                        if (idx == -1) continue;
                        String key = line.substring(0, idx);
                        String value = java.net.URLDecoder.decode(line.substring(idx + 1), "UTF-8");
                        switch (key) {
                            case "account" -> account = value;
                            case "qn" -> qn = value;
                            case "lang" -> lang = value;
                            case "code" -> code = value;
                        }
                    }

                    String response;
                    if (account == null || qn == null || lang == null || code == null) {
                        response = "缺少必要參數";
                        exchangeCopy.sendResponseHeaders(400, response.getBytes("UTF-8").length);
                        exchangeCopy.getResponseBody().write(response.getBytes("UTF-8"));
                        exchangeCopy.close();
                        return;
                    }

                    // 產生唯一檔名（根據 account 查 UID，找不到就報錯）
                    String uid = null;
                    try (Connection conn = DriverManager.getConnection(DB_URL)) {
                        String sql = "SELECT UID FROM UserInfo WHERE Account = ?";
                        try (var pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, account);
                            try (var rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                    uid = rs.getString("UID");
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("查詢 UID 失敗：" + e.getMessage());
                        response = "系統錯誤：查詢 UID 失敗\n" + e.getClass().getName() + ": " + e.getMessage();
                        exchangeCopy.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                        exchangeCopy.sendResponseHeaders(500, response.getBytes("UTF-8").length);
                        exchangeCopy.getResponseBody().write(response.getBytes("UTF-8"));
                        exchangeCopy.close();
                        return;
                    }
                    if (uid == null) {
                        response = "查無此帳號對應的 UID，請重新登入或聯絡管理員";
                        exchangeCopy.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                        exchangeCopy.sendResponseHeaders(400, response.getBytes("UTF-8").length);
                        exchangeCopy.getResponseBody().write(response.getBytes("UTF-8"));
                        exchangeCopy.close();
                        return;
                    }
                    // 取得當前時間（格式：yyyyMMdd-HHmmss）
                    String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
                    String info = String.format("%s-%s-%s", qn, uid, now); // QN001A-XXXXX-20240604-153012
                    String ext = switch (lang.toUpperCase()) {
                        case "C" -> "c";
                        case "CPP" -> "cpp";
                        case "CS" -> "cs";
                        case "PY" -> "py";
                        case "JAVA" -> "java";
                        case "JS" -> "js";
                        case "RS" -> "rs";
                        case "KT" -> "kt";
                        case "R" -> "r";
                        case "GO" -> "go";
                        case "JL" -> "jl";
                        default -> "txt";
                    };
                    String tempCodePath = "FileOnFileExecution_Framework/TempCode/" + info + "." + ext;

                    // 寫入程式碼檔案
                    try {
                        try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempCodePath), StandardCharsets.UTF_8)) {
                            writer.write(code.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("無法寫入程式碼檔案：" + e.getMessage());
                        response = "系統錯誤：無法寫入程式碼檔案\n" + e.getClass().getName() + ": " + e.getMessage();
                        exchangeCopy.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                        exchangeCopy.sendResponseHeaders(500, response.getBytes("UTF-8").length);
                        exchangeCopy.getResponseBody().write(response.getBytes("UTF-8"));
                        exchangeCopy.close();
                        return;
                    }

                    // 執行評測
                    console_output.setLength(0); // 清空
                    int rc = FOFE(qn, info, ext);

                    // 組合回傳訊息
                    StringBuilder sb = new StringBuilder();
                    switch (rc) {
                        case 0 -> sb.append(console_output);
                        case 3 -> sb.append("Prohibited Header Detected\n");
                        case 4 -> sb.append("Compile Error\n");
                        case 5 -> sb.append("Time Limit Exceeded\n");
                        case 6 -> sb.append("Memory Limit Exceeded\n");
                        case 7 -> sb.append("Wrong Answer\n");
                        case 10 -> sb.append("Unsupported Language\n");
                        default -> sb.append("Unexpected System Error\n");
                    }
                    response = sb.toString();

                    exchangeCopy.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                    exchangeCopy.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                    exchangeCopy.getResponseBody().write(response.getBytes("UTF-8"));
                    exchangeCopy.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    try {
                        String response = "系統錯誤：排程處理失敗\n" + ex.getClass().getName() + ": " + ex.getMessage();
                        exchangeCopy.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                        exchangeCopy.sendResponseHeaders(500, response.getBytes("UTF-8").length);
                        exchangeCopy.getResponseBody().write(response.getBytes("UTF-8"));
                        exchangeCopy.close();
                    } catch (IOException ignored) {}
                }
            });
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

javac -cp ".;sqlite-jdbc-3.49.1.0.jar;json-20250517.jar" Plush_OJ_Server_Test.java
java -cp ".;sqlite-jdbc-3.49.1.0.jar;json-20250517.jar" Plush_OJ_Server_Test
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