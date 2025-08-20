package hospital;

import estructuras.*;
import java.util.*;

public class EHospital <T extends PacienteInfo> {
    
    private Bp_Tree<Pacientes> arbolPacientes;
    private hash_table<String, Integer> tablaHash;
    private Heap<PrioridadPacientes> heapEmergencias;
    private manejoArchivosSalida<Pacientes> archivos; 

    
    //Cambios
    public EHospital() {
        this.arbolPacientes = new Bp_Tree<Pacientes>(2);
        this.tablaHash = new hash_table<String, Integer>(100, 104729);
        this.heapEmergencias = new Heap<>();
        this.archivos = new manejoArchivosSalida<Pacientes>();
    }

    // ================== CREAR PACIENTE ==================
    public void crearPaciente(Pacientes p) {
        
        arbolPacientes.insert(p);

        // Insertar en Hash → clave: "NombreApellido"
        String clave = p.getFirstName() + "_" + p.getFirstApellido();
        tablaHash.put(clave, p.getID());


        archivos.registrarOperacion("Creado paciente: " + p.getID(), true);
    }

    // ================== BORRAR PACIENTE ==================

     private void borrarPaciente(String linea,
                            Bp_Tree<Pacientes> arbol,
                            hash_table<String, Integer> hash,
                            manejoArchivosSalida<Pacientes> manejoSalida) {
        try {
            int ID = Integer.parseInt(linea.trim());
            
            // Buscar el paciente real por ID
            Pacientes pacienteEncontrado = null;
            for (Pacientes p : arbol.obtenerTodos()) {
                if (p.getID() == ID) {
                    pacienteEncontrado = p;
                    break;
                }
            }

            if (pacienteEncontrado != null) {
                // Eliminar del B+Tree usando el objeto completo
                arbol.delete(pacienteEncontrado);
                
                // Eliminar del hash
                hash.remove(pacienteEncontrado.getKey());
                
                // Registrar operación exitosa
                manejoSalida.registrarOperacion("Borrado paciente: " + pacienteEncontrado.getKey(), true);
            } else {
                // Paciente no encontrado
                manejoSalida.registrarOperacion("Paciente no encontrado para borrado: " + ID, false);
            }
            
        } catch (Exception e) {
            // Error general al parsear o eliminar
            manejoSalida.registrarOperacion("Error borrado paciente: " + linea, false);
        }
    }

    // ================== ATENCION EMERGENCIA ==================
    public void registrarEmergenciaPorID(int ID, int prioridad) {
        Pacientes p = arbolPacientes.searchByID(ID);
        if (p != null) {
            heapEmergencias.insert(new PrioridadPacientes(p.getPrioridad(), prioridad));
            archivos.registrarEmergencia("Emergencia registrada por ID " + ID, true);
        } else {
            archivos.registrarEmergencia("Paciente ID " + ID + " no encontrado", false);
        }
    }

    public void registrarEmergenciaPorNombre(String nombre, String apellido, int prioridad) {
        String clave = nombre + "_" + apellido;
        Integer id = tablaHash.get(clave);

        if (id != null) {
            heapEmergencias.insert(new PrioridadPacientes(id, prioridad));
            archivos.registrarEmergencia("Emergencia registrada por NOMBRE " + nombre + " " + apellido, true);
        } else {
            archivos.registrarEmergencia("Paciente " + nombre + " " + apellido + " no encontrado", false);
        }
    }

    // ================== PROCESAR PACIENTES EN EL HEAP ==================
    public void atenderEmergencias() {
        while (!heapEmergencias.isEmpty()) {
            PrioridadPacientes p = (PrioridadPacientes) heapEmergencias.extractMax();
            System.out.println("Atendiendo paciente " + p.getID() + " con prioridad " + p.getPrioridad());
        }
    }

    // ================== GUARDAR PACIENTES FINALES ==================
    public void guardarPacientesFinales() {
        List<Pacientes> listaPacientes = arbolPacientes.obtenerTodos();
        archivos.guardarPacientesFinales(listaPacientes);
    }
    
    //Getters:
    public Bp_Tree<Pacientes> getArbolPacientes() {
        return arbolPacientes;
    }
    
    public hash_table<String, Integer> getTablaHash() {
        return tablaHash;
    }
    
    public Heap<PrioridadPacientes> getHeapEmergencias() {
        return heapEmergencias;
    }
    
    public manejoArchivosSalida<Pacientes> getArchivosSalida() {
        return archivos;
    }
    // ================== GUARDAR ARBOL SERIALIZADO ==================
    public void guardarEstado() {
        guardarPacientesFinales();
        archivos.guardarArbol(arbolPacientes); 
        archivos.guardarArbolSerializado(arbolPacientes);
    }    
}