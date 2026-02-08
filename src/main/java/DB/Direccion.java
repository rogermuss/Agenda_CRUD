package DB;

public class Direccion {
    private int id;
    private String calle;

    public Direccion(int id, String calle) {
        this.id = id;
        this.calle = calle;
    }
    public int getId() { return id; }
    public String getCalle() { return calle; }
    public void setCalle(String direccion) { this.calle = direccion; }
    @Override
    public String toString() { return id + " - " + calle; }
    public void setId(int id) { this.id = id; }
}
