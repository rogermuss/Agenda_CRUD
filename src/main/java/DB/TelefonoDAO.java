package DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class TelefonoDAO {
    public List<Telefono> getAllByPersonaId(int personaId) throws SQLException {
        List<Telefono> telefonos = new ArrayList<>();
        String sql = "SELECT id, personaId, telefono FROM Telefonos WHERE personaId = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, personaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int personaIdDB = rs.getInt("personaId");
                    String telefono = rs.getString("telefono");

                    telefonos.add(new Telefono(id, personaIdDB, telefono));
                }
            }
        }

        return telefonos;
    }

    public int insert(Telefono telefono) throws SQLException {
        String sql = "INSERT INTO Telefonos(personaId, telefono) VALUES (?, ?)";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, telefono.getPersonaId());
            ps.setString(2, telefono.getTelefono());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        }

        return -1;
    }

    //Actualizar la informacion modificable
    public void update(Telefono telefono) throws SQLException {
        String sql = "UPDATE Telefonos SET telefono = ? WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, telefono.getTelefono());
            ps.setInt(2, telefono.getId());

            ps.executeUpdate();
        }
    }

    //Metodo para eliminar un telefono de la DB a partir del ID
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Telefonos WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
