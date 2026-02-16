package DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DireccionDAO {
    public List<Direccion> getAll() throws SQLException {
        List<Direccion> lista = new ArrayList<>();

        String sql = "SELECT id_direccion, calle FROM Direccion";

        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Direccion p = new Direccion(
                        rs.getInt("id_direccion"),
                        rs.getString("calle")
                );
                lista.add(p);
            }
        }

        return lista;
    }

    public int insert(Direccion direccion) throws SQLException {
        String sql = "INSERT INTO Direccion(calle) VALUES (?)";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, direccion.getCalle());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int idGenerado = keys.getInt(1);

                // 🔥 ESTA LINEA ES LA IMPORTANTE
                direccion.setId(idGenerado);

                return idGenerado;
            }
        }
        return -1;
    }

    public void update(Direccion direccion) throws SQLException {
        String sql = "UPDATE Direccion SET calle = ? WHERE id_direccion = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, direccion.getCalle());
            ps.setInt(2, direccion.getId());

            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String deleteRelation = "DELETE FROM persona_direccion WHERE id_direccion = ?";
        String deleteDireccion = "DELETE FROM Direccion WHERE id_direccion = ?";

        try (Connection conn = ConnectionDB.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psRel = conn.prepareStatement(deleteRelation)) {
                psRel.setInt(1, id);
                psRel.executeUpdate();
            }
            try (PreparedStatement psDir = conn.prepareStatement(deleteDireccion)) {
                psDir.setInt(1, id);
                psDir.executeUpdate();
            }
            conn.commit();
        }
    }
}
