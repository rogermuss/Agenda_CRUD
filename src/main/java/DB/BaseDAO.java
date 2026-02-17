package DB;

import repositories.IRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO<T> implements IRepository<T> {

    // Métodos abstractos que cada DAO específico debe implementar
    protected abstract String getTableName();
    protected abstract String getIdColumn();
    protected abstract String getInsertColumns();
    protected abstract String getInsertValues();
    protected abstract void setInsertParameters(PreparedStatement ps, T entity) throws SQLException;
    protected abstract void setUpdateParameters(PreparedStatement ps, T entity) throws SQLException;
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    @Override
    public List<T> getAll() throws SQLException {
        List<T> lista = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();

        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public int insert(T entity) throws SQLException {
        String sql = "INSERT INTO " + getTableName() +
                "(" + getInsertColumns() + ") VALUES (" + getInsertValues() + ")";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setInsertParameters(ps, entity);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        }
        return -1;
    }

    @Override
    public void update(T entity) throws SQLException {
        String sql = "UPDATE " + getTableName() + " SET " +
                getUpdateSet() + " WHERE " + getIdColumn() + " = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setUpdateParameters(ps, entity);
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    protected abstract String getUpdateSet();
}