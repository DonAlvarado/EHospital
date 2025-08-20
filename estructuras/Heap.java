package estructuras;

import java.util.*;
import hospital.*;

public class Heap<T extends PacienteInfo> {
    private PriorityQueue<PacienteInfo> cola;

    public Heap() {
        // Max-heap usando reverseOrder()
        this.cola = new PriorityQueue<>(Collections.reverseOrder());
    }

    // Inserta un elemento en el heap
    public void insert(PacienteInfo priority) {
        cola.offer(priority);
    }

    // Extrae el elemento con mayor prioridad
    public PacienteInfo extractMax() {
        return cola.poll();
    }

    // Consulta el elemento con mayor prioridad sin extraer
    public PacienteInfo peek() {
        return cola.peek();
    }

    // Retorna true si el heap está vacío
    public boolean isEmpty() {
        return cola.isEmpty();
    }

    // Tamaño del heap
    public int size() {
        return cola.size();
    }

    // Muestra todos los elementos del heap
    public void mostrarCola() {
        for (PacienteInfo p : cola) {
            System.out.println(p);
        }
    }
}
