package DB;

public class Persona_Direccion {
    int id_persona;
    int id_direccion;

    public Persona_Direccion(int id_persona, int id_direccion) {
        this.id_persona = id_persona;
        this.id_direccion = id_direccion;
    }
    public int getId_persona() { return id_persona; }
    public int getId_direccion() { return id_direccion; }
    public void setId_persona(int id_persona) { this.id_persona = id_persona; }
    public void setId_direccion(int id_direccion) { this.id_direccion = id_direccion; }
    @Override
    public String toString() { return id_persona + " - " + id_direccion; }

}
