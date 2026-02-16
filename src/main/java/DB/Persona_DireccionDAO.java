package DB;

import repositories.IRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Persona_DireccionDAO implements IRepository<Persona_Direccion> {
    public List<Persona_Direccion> getAll() throws SQLException {
        List<Persona_Direccion> lista = new ArrayList<>();

        String sql = "SELECT id_persona, id_direccion FROM Persona_Direccion";

        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Persona_Direccion p = new Persona_Direccion(
                        rs.getInt("id_persona"),
                        rs.getInt("id_direccion")
                );
                lista.add(p);
            }
        }

        return lista;
    }

    public boolean existe(int id_persona, int id_direccion) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Persona_Direccion WHERE id_persona = ? AND id_direccion = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id_persona);
            ps.setInt(2, id_direccion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int insert(Persona_Direccion pd) throws SQLException {
        if (existe(pd.getId_persona(), pd.getId_direccion())) {
            return 0;
        }

        String sql = "INSERT INTO Persona_Direccion(id_persona, id_direccion) VALUES (?, ?)";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pd.getId_persona());
            ps.setInt(2, pd.getId_direccion());

            return ps.executeUpdate();
        }
    }

    @Override
    public void update(Persona_Direccion entity) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }


    public void delete(int id_persona, int id_direccion) throws SQLException {
        String sql = "DELETE FROM Persona_Direccion WHERE id_persona = ? and id_direccion = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id_persona);
            ps.setInt(2, id_direccion);
            ps.executeUpdate();
        }
    }
}
