package hospital;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import estructuras.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class manejoArchivosSalida<T extends PacienteInfo> {
    private static final String TREE_FILE = "bp_tree.json";
    private static final String TREE_SERIALIZADO = "arbol_serializado.json";
    private static final String LOG_OPERACIONES = "registro_operaciones.txt";
    private static final String LOG_EMERGENCIAS = "registro_emergencias.txt";
    private static final String PACIENTES_FILE = "pacientes_final.json";

    private final Gson gson;

    public manejoArchivosSalida() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    // Guardar árbol en JSON (estructura completa -> puede fallar si hay ciclos)
    public void guardarArbol(Bp_Tree<T> arbol) {
        try (FileWriter writer = new FileWriter(TREE_FILE)) {
            gson.toJson(arbol, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Guardar árbol serializado usando el DTO de Bp_Tree
    public void guardarArbolSerializado(Bp_Tree<T> arbol) {
        try (FileWriter writer = new FileWriter(TREE_SERIALIZADO)) {
            String json = arbol.toJson();   // usa el método ya definido en Bp_Tree
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Cargar árbol desde JSON (estructura completa)
    public Bp_Tree<T> cargarArbol(Class<T> clazz) {
        try (FileReader reader = new FileReader(TREE_FILE)) {
            Type tipoArbol = TypeToken.getParameterized(Bp_Tree.class, clazz).getType();
            return gson.fromJson(reader, tipoArbol);
        } catch (IOException e) {
            return null;
        }
    }

    // Registrar operación
    public void registrarOperacion(String descripcion, boolean exito) {
        escribirLog(LOG_OPERACIONES, "[OPERACION][" + (exito ? "OK" : "FALLO") + "] " + descripcion);
    }

    // Registrar emergencia
    public void registrarEmergencia(String descripcion, boolean exito) {
        escribirLog(LOG_EMERGENCIAS, "[EMERGENCIA][" + (exito ? "ATENDIDO" : "RECHAZADO") + "] " + descripcion);
    }

    private void escribirLog(String archivo, String mensaje) {
        try (FileWriter fw = new FileWriter(archivo, true)) {
            fw.write(new Date() + " - " + mensaje + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Guardar pacientes finales en JSON
    public void guardarPacientesFinales(List<T> pacientes) {
        try (FileWriter fw = new FileWriter(PACIENTES_FILE)) {
            gson.toJson(pacientes, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<T> cargarPacientesFinales(Class<T> clazz) {
        try (FileReader reader = new FileReader(PACIENTES_FILE)) {
            Type listType = TypeToken.getParameterized(List.class, clazz).getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void exportarHashNombres(hash_table<String, Integer> hash, String nombreArchivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo, false))) {
            bw.write("Clave (Nombre+Apellido) -> ID");
            bw.newLine();
            for (String key : hash.keys()) {
                bw.write(key + " -> " + hash.get(key));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al exportar tabla hash: " + e.getMessage());
        }
    }
}
