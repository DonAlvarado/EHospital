package hospital;

public class PrioridadPacientes implements PacienteInfo {
    private int ID;
    private int prioridad;

    public PrioridadPacientes(int ID, int prioridad) {
        this.ID = ID;
        this.prioridad = prioridad;
    }

    @Override
    public int getID() { return ID; }

    @Override
    public int getPrioridad() { return prioridad; }

    @Override
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    @Override
    public String getKey() { return String.valueOf(ID); }

    @Override
    public String getFirstName() { return "N/A"; }

    @Override
    public String getFirstApellido() { return "N/A"; }

    @Override
    public String getFullNameKey() { return String.valueOf(ID); }

    @Override
    public int compareTo(PacienteInfo otro) {
        return Integer.compare(this.prioridad, otro.getPrioridad());
    }

    @Override
    public String toString() {
        return "ID: " + ID + ", Prioridad: " + prioridad;
    }
}
