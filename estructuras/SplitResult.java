package estructuras;

import hospital.*;


//Dividir
public class SplitResult<T extends PacienteInfo> {
    public T mKey;
    public Bp_Node<T> lNode;
    public Bp_Node<T> rNode;
    
    public SplitResult(T mKey, Bp_Node<T> lNode, Bp_Node<T> rNode){
        this.mKey = mKey;
        this.lNode = lNode;
        this.rNode = rNode;
    }
}
