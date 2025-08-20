package hospital;

public class Pacientes implements PacienteInfo {
    private String pNombre, sNombre, pApellido, sApellido, fNacimiento, cElectronico;
    private int ID, prioridad;

    public Pacientes(int ID, int prioridad, String pNombre, String sNombre,
                     String pApellido, String sApellido, String fNacimiento, String cElectronico) {
        this.ID = ID;
        this.prioridad = prioridad;
        this.pNombre = pNombre;
        this.sNombre = sNombre;
        this.pApellido = pApellido;
        this.sApellido = sApellido;
        this.fNacimiento = fNacimiento;
        this.cElectronico = cElectronico;
    }

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public int getPrioridad() {
        return this.prioridad;
    }

    @Override
    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    @Override
    public String getKey() {
        return pNombre + pApellido + ID;
    }

    @Override
    public String getFirstName() {
        return pNombre;
    }

    @Override
    public String getFirstApellido() {
        return pApellido;
    }

    @Override
    public String getFullNameKey() {
        return pNombre + sNombre + pApellido + sApellido;
    }

    @Override
    public int compareTo(PacienteInfo otro) {
        // Primero comparar por prioridad (mayor prioridad = más urgente)
        if (this.getPrioridad() != otro.getPrioridad()) {
            return Integer.compare(otro.getPrioridad(), this.getPrioridad());
        }
        // Si la prioridad es igual, ordenar por ID
        return Integer.compare(this.ID, otro.getID());
    }

    @Override
    public String toString() {
        return ID + ", " + pNombre + " " + sNombre + " " +
               pApellido + " " + sApellido + ", " +
               fNacimiento + ", " + cElectronico;
    }
    
    public static String generarClave(String pNombre, String pApellido){
        return pNombre + " " + pApellido;
    }
}
