package repositories;

public interface IValidator<T> {
    boolean isValid(T entity);
    String getErrorMessage();
}