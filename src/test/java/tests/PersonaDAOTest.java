package tests;

import DB.ConnectionDB;
import DB.Persona;
import DB.PersonaDAO;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersonaDAOTest {

    @Test
    public void getAllTest() throws SQLException {
        List<Persona> lista = new PersonaDAO().getAll();
        if (!lista.isEmpty()) {
            for (Persona p : lista) {
                assertTrue(p.getId() > 0);

                assertNotNull(p.getNombre());
                assertFalse(p.getNombre().isBlank());

                assertNotNull(p.getDireccion());
                assertFalse(p.getDireccion().isBlank());
            }
        } else {
            System.out.println("No hay personas en la DB");
        }
    }

    @Test
    public void insertarTest() throws SQLException {

        //Creo e inserto el objeto
        Persona p = new Persona(0, "Pepe", "Calle Chino");

        PersonaDAO dao = new PersonaDAO();

        int id = dao.insert(p);

        // Verificar que el ID se genero correctamente
        assertTrue(id > 0, "El ID generado debe ser mayor que 0");

        // Revisar en la DB que se creo la referencia
        String sql = "SELECT id, nombre, direccion FROM Personas WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                assertTrue(rs.next(), "Debe existir un registro con el ID insertado");

                assertEquals(id, rs.getInt("id"));
                assertEquals("Pepe", rs.getString("nombre"));
                assertEquals("Calle Chino", rs.getString("direccion"));

                assertFalse(rs.next(), "No debería haber más de un registro con ese ID");
            }
        }

        // Eliminar la prueba de la DB
        String deleteSql = "DELETE FROM Personas WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSql)) {

            ps.setInt(1, id);
            int filas = ps.executeUpdate();

            assertEquals(1, filas, "Se debe eliminar exactamente 1 fila");
        }
    }

    @Test
    public void actualizarTest() throws SQLException {

        PersonaDAO dao = new PersonaDAO();

        // Inserto una persona que modificare despues
        Persona p = new Persona(0, "Pepe", "Calle Chino");
        int id = dao.insert(p);
        assertTrue(id > 0);

        // Modifico la persona
        Persona pMod = new Persona(id, "Pepe MOD", "Calle 999");
        dao.update(pMod);

        // Reviso que se haya modificado correctamente
        String sql = "SELECT nombre, direccion FROM Personas WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());

                assertEquals("Pepe MOD", rs.getString("nombre"));
                assertEquals("Calle 999", rs.getString("direccion"));
            }
        }

        // Elimino la prueba de la DB
        String deleteSql = "DELETE FROM Personas WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Test
    public void eliminarTest() throws SQLException {
        PersonaDAO dao = new PersonaDAO();

        // Inserto una persona
        Persona p = new Persona(0, "Pepe", "Calle Chino");
        int id = dao.insert(p);
        assertTrue(id > 0);

        // Revisar en la DB que se creo la referencia
        String sql = "SELECT id, nombre, direccion FROM Personas WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                assertTrue(rs.next(), "Debe existir un registro con el ID insertado");

                assertEquals(id, rs.getInt("id"));
                assertEquals("Pepe", rs.getString("nombre"));
                assertEquals("Calle Chino", rs.getString("direccion"));

                assertFalse(rs.next(), "No debería haber más de un registro con ese ID");
            }
        }

        // Eliminar
        dao.delete(id);

        // Confirmar que ya NOoo existe
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                assertFalse(rs.next(), "No debe existir un registro con el ID insertado");
            }
        }
    }
}
