package services;

import DB.Persona;
import repositories.IPersonaRepository;
import repositories.IValidator;
import java.sql.SQLException;
import java.util.List;

public class PersonaService {

    private final IPersonaRepository repository;
    private final IValidator<Persona> validator;

    // Constructor con inyección de dependencias (DIP)
    public PersonaService(IPersonaRepository repository, IValidator<Persona> validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<Persona> getAllPersonas() throws SQLException {
        return repository.getAll();
    }

    public boolean createPersona(String nombre) throws SQLException {
        Persona persona = new Persona(0, nombre);

        if (!validator.isValid(persona)) {
            System.err.println("Error: " + validator.getErrorMessage());
            return false;
        }

        int id = repository.insert(persona);
        persona.setId(id);
        return true;
    }

    public boolean updatePersona(int id, String nombre) throws SQLException {
        Persona persona = new Persona(id, nombre);

        if (!validator.isValid(persona)) {
            System.err.println("Error: " + validator.getErrorMessage());
            return false;
        }

        repository.update(persona);
        return true;
    }

    public void deletePersona(int id) throws SQLException {
        repository.delete(id);
    }
}