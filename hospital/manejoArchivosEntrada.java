package hospital;

import estructuras.*;
import java.io.*;
import java.text.Normalizer;
import java.util.*;

public class manejoArchivosEntrada<T extends PacienteInfo> {

    // ================== LEER ARCHIVO ==================
    public List<String> leerArchivo(String filename) {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty())
                    lineas.add(linea.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineas;
    }

    // ================== PROCESAR ARCHIVO ==================
    public void procesarArchivo(List<String> lineas, 
                                Bp_Tree<Pacientes> arbolPacientes,
                                hash_table<String, Integer> hashPacientes,
                                Heap<PrioridadPacientes> heapEmergencias,
                                manejoArchivosSalida<Pacientes> manejoSalida) {

        String seccionActual = "";

        for (String linea : lineas) {
            switch (linea.toUpperCase()) {
                case "CREAR PACIENTES":
                case "BORRAR PACIENTES":
                case "ATENCION EMERGENCIA":
                    seccionActual = linea.toUpperCase();
                    break;
                default:
                    switch (seccionActual) {
                        case "CREAR PACIENTES":
                            crearPaciente(linea, arbolPacientes, hashPacientes, manejoSalida);
                            break;
                        case "BORRAR PACIENTES":
                            borrarPaciente(linea, arbolPacientes, hashPacientes, manejoSalida);
                            break;
                        case "ATENCION EMERGENCIA":
                            registrarEmergencia(linea, arbolPacientes, hashPacientes, heapEmergencias, manejoSalida);
                            break;
                    }
            }
        }
    }

    // ================== CREAR PACIENTE ==================
    private void crearPaciente(String linea, Bp_Tree<Pacientes> arbol,
                               hash_table<String, Integer> hash,
                               manejoArchivosSalida<Pacientes> manejoSalida) {
        try {
            String[] partes = linea.split(",");
            if (partes.length < 7) return;

            int ID = Integer.parseInt(partes[0].trim());
            String pNombre = partes[1].trim();
            String sNombre = partes[2].trim();
            String pApellido = partes[3].trim();
            String sApellido = partes[4].trim();
            String fNacimiento = partes[5].trim();
            String cElectronico = partes[6].trim();

            Pacientes paciente = new Pacientes(ID, 0, pNombre, sNombre, pApellido, sApellido, fNacimiento, cElectronico);

            // Insertar en árbol
            arbol.insert(paciente);

            String clave = normalizarClave(pNombre + "_" + pApellido);
            hash.put(clave, ID);

            manejoSalida.registrarOperacion("Creación paciente: " + paciente.getKey(), true);

        } catch (Exception e) {
            manejoSalida.registrarOperacion("Error creación paciente: " + linea, false);
        }
    }

    // ================== BORRAR PACIENTE ==================
    private void borrarPaciente(String linea, Bp_Tree<Pacientes> arbol, 
                                hash_table<String, Integer> hash, 
                                manejoArchivosSalida<Pacientes> manejoSalida) {
        try {
            int ID = Integer.parseInt(linea.trim());
            Pacientes paciente = null;
            for (Pacientes p : arbol.obtenerTodos()) {
                if (p.getID() == ID) {
                    paciente = p;
                    break;
                }
            }
            if (paciente != null) {
                arbol.delete(paciente);
                String clave = normalizarClave(paciente.getFirstName() + "_" + paciente.getFirstApellido());
                hash.remove(clave);
                manejoSalida.registrarOperacion("Borrado paciente: " + paciente.getKey(), true);
            } else {
                manejoSalida.registrarOperacion("Paciente no encontrado para borrar: " + ID, false);
            }
        } catch (Exception e) {
            manejoSalida.registrarOperacion("Error al borrar paciente: " + linea, false);
        }
    }

    // ================== REGISTRAR EMERGENCIA ==================
    private void registrarEmergencia(String linea,
                                     Bp_Tree<Pacientes> arbol,
                                     hash_table<String, Integer> hash,
                                     Heap<PrioridadPacientes> heap,
                                     manejoArchivosSalida<Pacientes> manejoSalida) {
        try {
            if (linea.startsWith("ID:")) {
                String[] partes = linea.split(",");
                int ID = Integer.parseInt(partes[0].split(":")[1].trim());
                int prioridad = Integer.parseInt(partes[1].split(":")[1].trim());

                heap.insert(new PrioridadPacientes(ID, prioridad));
                manejoSalida.registrarEmergencia("ID: " + ID, true);

            } else if (linea.startsWith("NOMBRES:")) {
                String[] partes = linea.split(",");
                String pNombre = partes[0].split(":")[1].trim();
                String pApellido = partes[1].trim();
                int prioridad = Integer.parseInt(partes[2].split(":")[1].trim());

                // Buscar paciente usando clave consistente
                String clave = normalizarClave(pNombre + "_" + pApellido);
                Integer ID = hash.get(clave);

                if (ID != null) {
                    heap.insert(new PrioridadPacientes(ID, prioridad));
                    manejoSalida.registrarEmergencia("NOMBRES: " + pNombre + " " + pApellido, true);
                } else {
                    manejoSalida.registrarEmergencia("Paciente no encontrado: " + pNombre + " " + pApellido, false);
                }
            }
        } catch (Exception e) {
            manejoSalida.registrarEmergencia("Error emergencia: " + linea, false);
        }
    }

    // ================== NORMALIZAR CLAVE ==================
    private String normalizarClave(String clave) {
        return Normalizer.normalize(clave, Normalizer.Form.NFC).replaceAll("\\s+", "");
    }
}
