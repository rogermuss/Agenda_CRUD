package repositories;

import DB.Persona;

// Extiende la interface genérica
public interface IPersonaRepository extends IRepository<Persona> {
    // Aquí puedes agregar métodos específicos de Persona si los necesitas
    // Por ahora, solo usa los métodos heredados de IRepository
}