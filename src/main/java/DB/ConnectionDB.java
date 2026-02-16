package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    //Cambie el puerto al tener distintas bases de datos para otros proyectos
    private static final String URL = "jdbc:mariadb://localhost:3307/agenda";
    private static final String USER = "usuario1";
    private static final String PASSWORD = "superpassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
