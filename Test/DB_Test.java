import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_Test {

    // 資料庫檔案路徑。建議使用相對路徑，讓程式碼更具可攜性。
    // 這會在您的專案根目錄下建立或連接到 userInfo.db 檔案。
    private static final String DB_FILE_PATH = "C:\\Users\\eric2\\Desktop\\Plush-OJ\\Test\\userdb.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE_PATH;

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        Connection conn = null; // 資料庫連接物件

        try {
            // 1. 建立資料庫連接
            conn = DriverManager.getConnection(URL);
            System.out.println("成功連接到 SQLite 資料庫: " + DB_FILE_PATH);

            // 2. 建立 UserInfo 資料表 (如果不存在)
            createUserInfoTable(conn);

            // 3. 測試資料插入
            System.out.println("\n--- 測試資料插入 ---");
            insertUser(conn, "user001", "pass123", "user001@example.com");
            insertUser(conn, "test_account", "test_pass", "test@domain.com");
            insertUser(conn, "admin", "admin_pass", "admin@company.com");

            // 4. 測試資料查詢
            System.out.println("\n--- 測試資料查詢 (所有用戶) ---");
            queryAllUsers(conn);

            System.out.println("\n--- 測試資料查詢 (特定 UID) ---");
            queryUserByUID(conn, 2); // 查詢 UID 為 2 的用戶

            // 5. 測試資料更新
            System.out.println("\n--- 測試資料更新 ---");
            updateUserEmail(conn, "test_account", "new.test@domain.com");
            queryAllUsers(conn); // 再次查詢確認更新

            // 6. 測試資料刪除
            System.out.println("\n--- 測試資料刪除 ---");
            deleteUser(conn, "user001");
            queryAllUsers(conn); // 再次查詢確認刪除

        } catch (SQLException e) {
            System.err.println("資料庫操作發生錯誤: " + e.getMessage());
            e.printStackTrace(); // 打印完整的堆疊追蹤，便於偵錯
        } finally {
            // 7. 關閉資料庫連接
            try {
                if (conn != null) {
                    conn.close();
                    System.out.println("\n資料庫連接已關閉。");
                }
            } catch (SQLException ex) {
                System.err.println("關閉連接時發生錯誤: " + ex.getMessage());
            }
        }
    }

    /**
     * 建立 UserInfo 資料表 (如果不存在)。
     * @param conn 資料庫連接物件
     * @throws SQLException 如果 SQL 執行失敗
     */
    private static void createUserInfoTable(Connection conn) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS UserInfo (" +
                                "UID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "Account TEXT NOT NULL," +
                                "PassWD TEXT NOT NULL," +
                                "Email TEXT NOT NULL" +
                                ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("資料表 'UserInfo' 已建立或已存在。");
        }
    }

    /**
     * 插入新用戶資料。
     * @param conn 資料庫連接物件
     * @param account 用戶帳號
     * @param passWD 用戶密碼
     * @param email 用戶電子郵件
     * @throws SQLException 如果 SQL 執行失敗
     */
    private static void insertUser(Connection conn, String account, String passWD, String email) throws SQLException {
        String insertSQL = "INSERT INTO UserInfo (Account, PassWD, Email) VALUES (?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, account);
            pstmt.setString(2, passWD);
            pstmt.setString(3, email);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("成功插入用戶: " + account + ", 影響行數: " + rowsAffected);
        }
    }

    /**
     * 查詢並顯示所有用戶資料。
     * @param conn 資料庫連接物件
     * @throws SQLException 如果 SQL 執行失敗
     */
    private static void queryAllUsers(Connection conn) throws SQLException {
        String selectSQL = "SELECT UID, Account, PassWD, Email FROM UserInfo;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            System.out.println("------------------------------------------");
            System.out.printf("%-5s %-15s %-15s %s\n", "UID", "Account", "PassWD", "Email");
            System.out.println("------------------------------------------");

            while (rs.next()) {
                // 從 ResultSet 中取出資料
                int uid = rs.getInt("UID");
                String account = rs.getString("Account");
                String passWD = rs.getString("PassWD");
                String email = rs.getString("Email");
                System.out.printf("%-5d %-15s %-15s %s\n", uid, account, passWD, email);
            }
            System.out.println("------------------------------------------");
        }
    }

    /**
     * 根據 UID 查詢特定用戶資料。
     * @param conn 資料庫連接物件
     * @param uid 要查詢的 UID
     * @throws SQLException 如果 SQL 執行失敗
     */
    private static void queryUserByUID(Connection conn, int uid) throws SQLException {
        String selectSQL = "SELECT UID, Account, PassWD, Email FROM UserInfo WHERE UID = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setInt(1, uid); // 設定查詢參數

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("查詢到 UID " + uid + " 的用戶:");
                    System.out.printf("%-5s %-15s %-15s %s\n", "UID", "Account", "PassWD", "Email");
                    System.out.println("------------------------------------------");
                    System.out.printf("%-5d %-15s %-15s %s\n",
                                      rs.getInt("UID"),
                                      rs.getString("Account"),
                                      rs.getString("PassWD"),
                                      rs.getString("Email"));
                    System.out.println("------------------------------------------");
                } else {
                    System.out.println("未找到 UID 為 " + uid + " 的用戶。");
                }
            }
        }
    }

    /**
     * 更新指定帳號的用戶電子郵件。
     * @param conn 資料庫連接物件
     * @param account 要更新的用戶帳號
     * @param newEmail 新的電子郵件地址
     * @throws SQLException 如果 SQL 執行失敗
     */
    private static void updateUserEmail(Connection conn, String account, String newEmail) throws SQLException {
        String updateSQL = "UPDATE UserInfo SET Email = ? WHERE Account = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, account);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("成功更新用戶 " + account + " 的 Email 為 " + newEmail + ", 影響行數: " + rowsAffected);
        }
    }

    /**
     * 刪除指定帳號的用戶。
     * @param conn 資料庫連接物件
     * @param account 要刪除的用戶帳號
     * @throws SQLException 如果 SQL 執行失敗
     */
    private static void deleteUser(Connection conn, String account) throws SQLException {
        String deleteSQL = "DELETE FROM UserInfo WHERE Account = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setString(1, account);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("成功刪除用戶: " + account + ", 影響行數: " + rowsAffected);
        }
    }
}

/*
javac -cp ".;sqlite-jdbc-3.49.1.0.jar" DB_Test.java
java -cp ".;sqlite-jdbc-3.49.1.0.jar" DB_Test
*/