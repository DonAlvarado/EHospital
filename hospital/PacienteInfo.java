package hospital;

public interface PacienteInfo extends Comparable<PacienteInfo> {
    int getID();
    int getPrioridad();
    void setPrioridad(int prioridad);

    String getKey();
    String getFirstName();
    String getFirstApellido();
    String getFullNameKey(); 

    @Override
    int compareTo(PacienteInfo otro);

    @Override
    String toString();
}
