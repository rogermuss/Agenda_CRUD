package validators;

import DB.Persona;
import repositories.IValidator;

public class PersonaValidator implements IValidator<Persona> {

    private String errorMessage;

    @Override
    public boolean isValid(Persona persona) {
        if (persona == null) {
            errorMessage = "La persona no puede ser null";
            return false;
        }

        String nombre = persona.getNombre();

        if (nombre == null || nombre.trim().isEmpty()) {
            errorMessage = "El nombre no puede estar vacío";
            return false;
        }

        if (nombre.length() < 3) {
            errorMessage = "El nombre debe tener al menos 3 caracteres";
            return false;
        }

        if (nombre.length() > 100) {
            errorMessage = "El nombre no puede exceder 100 caracteres";
            return false;
        }

        return true;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}