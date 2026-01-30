package tests;

import DB.ConnectionDB;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionTest {
    @Test
    void testConexionBD() {
        try (Connection conn = ConnectionDB.getConnection()) {
            assertNotNull(conn, "La conexión salio null");
            assertTrue(conn.isValid(2), "La conexión no es válida");
            assertFalse(conn.isClosed(), "La conexión está cerrada");
        } catch (Exception e) {
            fail("No se pudo conectar a la BD: " + e.getMessage());
        }
    }
}
