package connection;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {

    public static void main(String[] args) {
        System.out.println("Testing DB connection using ConnectionFactory...");
        System.out.println("JDBC URL: " + System.getenv().getOrDefault("MYSQL_URL", "(from db.properties or default)"));
        try {

            Connection c = ConnectionFactory.getConnection();
            System.out.println("Connection successful: " + (c != null && !c.isClosed()));
            if (c != null) c.close();
        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (RuntimeException rex) {
            System.err.println("RuntimeException: " + rex.getMessage());
            rex.printStackTrace();
        }
    }
}

