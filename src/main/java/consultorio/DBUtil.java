package consultorio;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static final Properties props = new Properties();
    static {
        try (InputStream in = DBUtil.class.getResourceAsStream("/db.properties")) {
            if (in == null) throw new RuntimeException("db.properties not found on classpath");
            props.load(in);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load db.properties: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL driver class not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, pass);
    }
}
