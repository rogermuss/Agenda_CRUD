package DB;

import repositories.IPersonaRepository;
import java.sql.*;

public class PersonaDAO extends BaseDAO<Persona> implements IPersonaRepository {

    @Override
    protected String getTableName() {
        return "Personas";
    }

    @Override
    protected String getIdColumn() {
        return "id";
    }

    @Override
    protected String getInsertColumns() {
        return "nombre";
    }

    @Override
    protected String getInsertValues() {
        return "?";
    }

    @Override
    protected String getUpdateSet() {
        return "nombre = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Persona persona) throws SQLException {
        ps.setString(1, persona.getNombre());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, Persona persona) throws SQLException {
        ps.setString(1, persona.getNombre());
        ps.setInt(2, persona.getId());
    }

    @Override
    protected Persona mapRow(ResultSet rs) throws SQLException {
        return new Persona(
                rs.getInt("id"),
                rs.getString("nombre")
        );
    }

}