package e2e;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseResetUtility {

    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");


    /**
     * Execute the SQL reset script
     */
    public static void resetDatabase() throws Exception {
        // Read SQL file from classpath
        System.out.println("Executing SQL script:\n"); // Debug output
        String sql = readSqlFile("/sql/reset_and_populate_db.sql");
        System.out.println(sql);
        // Execute SQL
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Execute the entire script
            stmt.execute(sql);
        }
    }

    /**
     * Read SQL file from resources
     */
    private static String readSqlFile(String path) throws Exception {
        try (InputStream is = DatabaseResetUtility.class.getResourceAsStream(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}

