package hospital;

import estructuras.*;
import java.io.*;
import java.util.function.Function;

public class cargarArchivos {

    public static <T extends PacienteInfo> 
    void cargarArbol(String nombreArchivo, Bp_Tree<T> arbol, Function<String, T> parser) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                T obj = parser.apply(linea);
                if (obj != null) {
                    arbol.insert(obj);
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error cargando archivo " + nombreArchivo + ": " + e.getMessage());
        }
    }   

    public static void cargarHash(String nombreArchivo, hash_table<String, Integer> hash) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            boolean primeraLinea = true; // Para saltar encabezado (Bug)
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) { 
                    primeraLinea = false; 
                    continue; 
                }
                String[] partes = linea.split("->");
                if (partes.length == 2) {
                    try {
                        hash.put(partes[0].trim(), Integer.parseInt(partes[1].trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Línea ignorada en hash_nombres.txt: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error cargando hash desde " + nombreArchivo + ": " + e.getMessage());
        }
    }
}
