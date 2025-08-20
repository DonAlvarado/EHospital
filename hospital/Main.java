package hospital;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            // Crear instancia del hospital
            EHospital hospital = new EHospital();

            // Archivo de entrada
            System.out.print("Ingrese el nombre del archivo de entrada: ");
            String archivoEntrada = sc.nextLine().trim();

            // Lector de archivos
            manejoArchivosEntrada lector = new manejoArchivosEntrada();

            // Leer líneas del archivo
            List<String> lineas = lector.leerArchivo(archivoEntrada);

            // Procesar acciones del archivo
            lector.procesarArchivo(
                    lineas,
                    hospital.getArbolPacientes(),   // Bp_Tree
                    hospital.getTablaHash(),        // HashTable
                    hospital.getHeapEmergencias(),  // Heap
                    hospital.getArchivosSalida()    // Archivos salida
            );

            // Atender emergencias pendientes
            hospital.atenderEmergencias();

            // Guardar pacientes finales
            hospital.guardarPacientesFinales();

            // Guardar árbol serializado y hash
            hospital.getArchivosSalida().guardarArbolSerializado(hospital.getArbolPacientes());
            hospital.getArchivosSalida().exportarHashNombres(hospital.getTablaHash(), "hash_nombres.txt");

            System.out.println("✅ Proceso completado exitosamente.");

            // Preguntar si se desea procesar otro archivo
            System.out.print("¿Desea procesar otro archivo? (s/n): ");
            String respuesta = sc.nextLine().trim().toLowerCase();
            if (!respuesta.equals("s")) {
                continuar = false;
            }
        }

        sc.close();
        System.out.println("Programa finalizado.");
    }
}