package DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    public List<Persona> getAll() throws SQLException {
        List<Persona> lista = new ArrayList<>();

        String sql = "SELECT id, nombre FROM Personas";

        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Persona p = new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
                lista.add(p);
            }
        }

        return lista;
    }

    public int insert(Persona persona) throws SQLException {
        String sql = "INSERT INTO Personas(nombre) VALUES (?)";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, persona.getNombre());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int idGenerado = keys.getInt(1);

                // 🔥 ESTA LINEA ES LA IMPORTANTE
                persona.setId(idGenerado);

                return idGenerado;
            }
        }
        return -1;
    }

    public void update(Persona persona) throws SQLException {
        String sql = "UPDATE Personas SET nombre = ? WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, persona.getNombre());
            ps.setInt(2, persona.getId());

            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Personas WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
