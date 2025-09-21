package app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL  = "jdbc:sqlserver://localhost:1433;databaseName=BibliotecaDB;encrypt=false";
    private static final String USER = "sa";           // tu usuario
    private static final String PASS = "V!V!EQAq5D6G";  // tu contraseña*/
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Método de prueba
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Conexión exitosa a SQL Server");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
