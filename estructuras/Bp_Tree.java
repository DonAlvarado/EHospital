package estructuras;

import hospital.*;
import java.util.*;

//Constructor del Arbol B
public class Bp_Tree<T extends PacienteInfo>{
    private Bp_Node<T> root;
    private int d;

    public Bp_Tree(int d) {
        this.root = new Bp_Node<>(true);
        this.d = d;
    }

    // Insertar
    public void insert(T key) {
        SplitResult<T> splitResult = rInsert(root, key);
        if (splitResult != null) {
            Bp_Node<T> newRoot = new Bp_Node<>(false);
            newRoot.patientID.add(splitResult.mKey);
            newRoot.hijos.add(splitResult.lNode);
            newRoot.hijos.add(splitResult.rNode);
            root = newRoot;
        }
    }
    
    //El Insertar Recursivo
    private SplitResult<T> rInsert(Bp_Node<T> current, T key) {
        int i = current.patientID.size() - 1;

        if (current.leaf) {
            while (i >= 0 && key.compareTo(current.patientID.get(i)) < 0) {
                i--;
            }
            current.patientID.add(i + 1, key);

            if (current.patientID.size() > 2 * d) {
                return splitNode(current);
            }
            return null;
        } else {
            while (i >= 0 && key.compareTo(current.patientID.get(i)) < 0) {
                i--;
            }
            i++;
            SplitResult<T> splitChild = rInsert(current.hijos.get(i), key);

            if (splitChild != null) {
                current.patientID.add(i, splitChild.mKey);
                current.hijos.set(i, splitChild.lNode);
                current.hijos.add(i + 1, splitChild.rNode);

                if (current.patientID.size() > 2 * d) {
                    return splitNode(current);
                }
            }
            return null;
        }
    }
    
    //Dividimos el Nodo cuando se encuentre lleno
    private SplitResult<T> splitNode(Bp_Node<T> fullNode) {
        T mKey;
        Bp_Node<T> lNode = new Bp_Node<>(fullNode.leaf);
        Bp_Node<T> rNode = new Bp_Node<>(fullNode.leaf);

        if (fullNode.leaf) {
            for (int i = 0; i < d; i++) {
                lNode.patientID.add(fullNode.patientID.get(i));
            }
            for (int i = d; i < fullNode.patientID.size(); i++) {
                rNode.patientID.add(fullNode.patientID.get(i));
            }

            mKey = rNode.patientID.get(0);

            rNode.nextLeaf = fullNode.nextLeaf;
            if (fullNode.nextLeaf != null) {
                fullNode.nextLeaf.prevLeaf = rNode;
            }
            lNode.nextLeaf = rNode;
            rNode.prevLeaf = lNode;
        } else {
            mKey = fullNode.patientID.get(d);

            for (int i = 0; i < d; i++) {
                lNode.patientID.add(fullNode.patientID.get(i));
            }
            for (int i = d + 1; i < fullNode.patientID.size(); i++) {
                rNode.patientID.add(fullNode.patientID.get(i));
            }
            for (int i = 0; i <= d; i++) {
                lNode.hijos.add(fullNode.hijos.get(i));
            }
            for (int i = d + 1; i < fullNode.hijos.size(); i++) {
                rNode.hijos.add(fullNode.hijos.get(i));
            }
        }
        return new SplitResult<>(mKey, lNode, rNode);
    }
    
    //Resolver el Balance
    private void resolveBalance(Bp_Node<T> parent, int iChild) {
        Bp_Node<T> child = parent.hijos.get(iChild);
        Bp_Node<T> left  = (iChild > 0) ? parent.hijos.get(iChild - 1) : null;
        Bp_Node<T> right = (iChild < parent.hijos.size() - 1) ? parent.hijos.get(iChild + 1) : null;
    
        // 1) Intentar pedir prestado al hermano izquierdo (left)
        if (left != null && left.patientID.size() > d) {
            if (child.leaf) {
                // mover última clave de left a inicio de child
                child.patientID.add(0, left.patientID.remove(left.patientID.size() - 1));
                // actualizar separador en el padre
                parent.patientID.set(iChild - 1, child.patientID.get(0));
            } else {
                // internos: rotar por el separador
                child.hijos.add(0, left.hijos.remove(left.hijos.size() - 1));
                child.patientID.add(0, parent.patientID.get(iChild - 1));
                parent.patientID.set(iChild - 1, left.patientID.remove(left.patientID.size() - 1));
            }
            return;
        }
    
        // 2) Intentar pedir prestado al hermano derecho (right)
        if (right != null && right.patientID.size() > d) {
            if (child.leaf) {
                // mover primera clave de right al final de child
                child.patientID.add(right.patientID.remove(0));
                // actualizar separador en el padre
                parent.patientID.set(iChild, right.patientID.get(0));
            } else {
                child.hijos.add(right.hijos.remove(0));
                child.patientID.add(parent.patientID.get(iChild));
                parent.patientID.set(iChild, right.patientID.remove(0));
            }
            return;
        }
    
        // 3) Si no se puede redistribuir, MERGE
        if (left != null) {
            mergeNodes(parent, iChild - 1); // fusiona left + child
        } else if (right != null) {
            mergeNodes(parent, iChild);     // fusiona child + right
        }
    }
    
    private void mergeNodes(Bp_Node<T> parent, int sepIdx) {
        Bp_Node<T> left  = parent.hijos.get(sepIdx);
        Bp_Node<T> right = parent.hijos.get(sepIdx + 1);
    
        if (left.leaf) {
            // hojas: juntar claves y arreglar enlaces
            left.patientID.addAll(right.patientID);
            left.nextLeaf = right.nextLeaf;
            if (right.nextLeaf != null) right.nextLeaf.prevLeaf = left;
        
            // en B+ no subimos clave al padre desde hojas; solo quitamos el separador
            parent.patientID.remove(sepIdx);
            parent.hijos.remove(sepIdx + 1);
        } else {
            // internos: bajar separador
            left.patientID.add(parent.patientID.remove(sepIdx));
            left.patientID.addAll(right.patientID);
            left.hijos.addAll(right.hijos);
            parent.hijos.remove(sepIdx + 1);
        }
    }
    
    public void delete(T key) {
        if (root == null) return;
        eliminarRecursivo(root, key);
    
        // Si la raíz quedó vacía y no es hoja, bajar un nivel
        if (!root.leaf && root.patientID.isEmpty()) {
            root = root.hijos.get(0);
        }
    }
    
    private boolean eliminarRecursivo(Bp_Node<T> node, T key) {
        int i = 0;
        while (i < node.patientID.size() && key.compareTo(node.patientID.get(i)) > 0) i++;
    
        if (node.leaf) {
            // remover en hoja
            if (i < node.patientID.size() && node.patientID.get(i).compareTo(key) == 0) {
                node.patientID.remove(i);
            } else {
                return false; // no estaba
            }
            // devolver underflow
            return node != root && node.patientID.size() < d;
        } else {
            // bajar al hijo adecuado
            boolean childUnderflow = eliminarRecursivo(node.hijos.get(i), key);
        
            if (childUnderflow) {
                resolveBalance(node, i);
            }
        
            // underflow de este interno (excepto raíz)
            return node != root && node.patientID.size() < d;
        }
    }

    //Buscar por ID
    public T searchByID(int ID) {
        for (T p : obtenerTodos()) { // asumir método que devuelve todos los pacientes
                if (p.getID() == ID) return p;
        }
        return null;
    }

    
    //Buscar
    public T search(T key) {
        Bp_Node<T> node = root;
        while (!node.leaf) {
            int i = 0;
            while (i < node.patientID.size() && key.compareTo(node.patientID.get(i)) >= 0) {
                i++;
            }
            node = node.hijos.get(i);
        }
        // Buscar en la hoja
        for (T k : node.patientID) {
            if (k.compareTo(key) == 0) return k;
        }
        return null; // no encontrado
    }

    
    public List<T> obtenerTodos() {
        List<T> out = new ArrayList<>();
        if (root == null) return out;
    
        // bajar hasta la hoja más a la izquierda
        Bp_Node<T> n = root;
        while (!n.leaf) {
            n = n.hijos.get(0);
        }
    
        // recorrer lista de hojas enlazadas
        while (n != null) {
            out.addAll(n.patientID);
            n = n.nextLeaf;
        }
        return out;
    }
    
        public String toJson() {
        // OPCIÓN A: DTO sin enlaces prev/next
        return new com.google.gson.GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(toDto());
    }
    
    private NodeDto<T> toDto() { // recursivo
        return toDto(this.root);
    }

    private NodeDto<T> toDto(Bp_Node<T> n) {
        NodeDto<T> dto = new NodeDto<>();
        dto.keys = new ArrayList<>(n.patientID);
        dto.leaf = n.leaf;
        if (!n.leaf) {
            dto.children = new ArrayList<>();
            for (Bp_Node<T> h : n.hijos) dto.children.add(toDto(h));
        }
        // NO copiar prevLeaf/nextLeaf
        return dto;
    }
    
    private static class NodeDto<T> {
        boolean leaf;
        List<T> keys;
        List<NodeDto<T>> children;
    }
    
    public Bp_Node<T> getRoot(){
        return root;
    }
}
