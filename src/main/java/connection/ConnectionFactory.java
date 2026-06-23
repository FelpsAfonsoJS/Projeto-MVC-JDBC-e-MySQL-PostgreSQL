package connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

	private static final String PROPS = "/db.properties";
	private static String url;
	private static String user;
	private static String password;

	static {
		try (InputStream in = ConnectionFactory.class.getResourceAsStream(PROPS)) {
			Properties p = new Properties();
			if (in != null) {
				p.load(in);
				url = p.getProperty("url");
				user = p.getProperty("user");
				password = p.getProperty("password");
			}
		} catch (IOException e) {
			// ignore, will try env vars
		}

		if (url == null) {
			url = System.getenv().getOrDefault("MYSQL_URL", "jdbc:mysql://127.0.0.1:3307/OficinaMecanica?useSSL=false&serverTimezone=UTC");
		}
		if (user == null) {
			user = System.getenv().getOrDefault("MYSQL_USER", "root");
		}
		if (password == null) {
			password = System.getenv().getOrDefault("MYSQL_PASS", "");
		}


		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("MySQL JDBC driver not found on classpath. Ensure dependency 'mysql-connector-java' is added and your IDE/Maven build refreshed.", e);
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}
}
