package repositories;

import java.sql.SQLException;
import java.util.List;

// Interface genérica que todos los repositorios implementarán
public interface IRepository<T> {
    List<T> getAll() throws SQLException;
    int insert(T entity) throws SQLException;
    void update(T entity) throws SQLException;
    void delete(int id) throws SQLException;
}