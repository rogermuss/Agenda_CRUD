package DB;

public class Telefono {
    private int id;
    private int personaId;
    private String telefono;

    public Telefono(int id, int personaId, String telefono) {
        this.id = id;
        this.personaId = personaId;
        this.telefono = telefono;
    }



    public int getId() { return id; }
    public int getPersonaId() { return personaId; }
    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) { this.telefono = telefono; }
}
