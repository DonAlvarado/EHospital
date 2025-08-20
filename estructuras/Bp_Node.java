package estructuras;

import java.util.*;
import hospital.*;

/*
    Se crean los nodos de forma generica para almacenar cualquier tipo de dato
*/
public class Bp_Node<T extends PacienteInfo> {
    public List<T> patientID = new ArrayList<>();
    public List <Bp_Node<T>> hijos = new ArrayList<>();
    public boolean leaf;
    
    transient Bp_Node<T> nextLeaf;
    transient Bp_Node<T> prevLeaf;
    
    public Bp_Node(boolean leaf){
        this.leaf = leaf;
    }
}
