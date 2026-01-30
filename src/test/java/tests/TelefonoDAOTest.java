package tests;

import DB.*;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TelefonoDAOTest {
    @Test
    public void getAllByPersonaIdTest() throws SQLException {
        int personaId = 1;

        List<Telefono> lista = new TelefonoDAO().getAllByPersonaId(personaId);

        assertNotNull(lista);
        assertFalse(lista.isEmpty(), "Debe haber teléfonos para personaId=" + personaId);

        for (Telefono t : lista) {
            assertTrue(t.getId() > 0);

            assertNotNull(t.getTelefono());
            assertFalse(t.getTelefono().isBlank());

            assertTrue(t.getPersonaId() > 0);
            assertEquals(personaId, t.getPersonaId(), "El teléfono debe pertenecer a la persona solicitada");
        }
    }
    @Test
    public void insertarTest() throws SQLException {

        //Creo e inserto el objeto
        Telefono p = new Telefono(0, 1, "6861238080");

        TelefonoDAO dao = new TelefonoDAO();

        int id = dao.insert(p);

        // Verificar que el ID se genero correctamente
        assertTrue(id > 0, "El ID generado debe ser mayor que 0");

        // Revisar en la DB que se creo la referencia
        String sql = "SELECT id, personaId, telefono FROM Telefonos WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                assertTrue(rs.next(), "Debe existir el telefono insertado");

                assertEquals(id, rs.getInt("id"));
                assertEquals(1, rs.getInt("personaId"));
                assertEquals("6861238080", rs.getString("telefono"));

                assertFalse(rs.next(), "No debería haber más de un registro con ese ID");
            }
        }

        // Eliminar la prueba de la DB
        String deleteSql = "DELETE FROM Telefonos WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSql)) {

            ps.setInt(1, id);
            int filas = ps.executeUpdate();

            assertEquals(1, filas, "Se debe eliminar exactamente 1 fila");
        }
    }

    @Test
    public void actualizarTest() throws SQLException {

        TelefonoDAO dao = new TelefonoDAO();

        // Inserto un telefono que modificare despues
        Telefono t = new Telefono(0, 1, "6861238080");
        int id = dao.insert(t);
        assertTrue(id > 0);

        Telefono tMod = new Telefono(id, 1, "6869990000");
        dao.update(tMod);

        // Reviso que se haya modificado correctamente
        String sql = "SELECT personaId, telefono FROM Telefonos WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Debe existir el telefono insertado");

                assertEquals(1, rs.getInt("personaId")); // debe seguir igual
                assertEquals("6869990000", rs.getString("telefono")); // debe cambiar
            }
        }

        // eliminar la prueba de la DB
        String deleteSql = "DELETE FROM Telefonos WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Test
    public void eliminarTest() throws SQLException {
        TelefonoDAO dao = new TelefonoDAO();

        // Inserto una celular
        Telefono p = new Telefono(0, 1, "6861238080");
        int id = dao.insert(p);
        assertTrue(id > 0);

        // Revisar en la DB que se creo la referencia
        String sql = "SELECT id, personaId, telefono FROM Telefonos WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                assertTrue(rs.next(), "Debe existir un telefono con el ID insertado");

                assertEquals(id, rs.getInt("id"));
                assertEquals(1, rs.getInt("personaId"));
                assertEquals("6861238080", rs.getString("telefono"));

                assertFalse(rs.next(), "No debería haber más de un telefono con ese ID");
            }
        }

        // Eliminar
        dao.delete(id);

        // Confirmar que ya NOoo existe
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                assertFalse(rs.next(), "No debe existir un telefono con el ID insertado");
            }
        }
    }


}
