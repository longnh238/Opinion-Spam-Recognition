package model.ontology;

/**
 * This class defines an ontology node object.
 * @author LongNguyen & NghiaPham
 */

public class OntologyNode {
    
    private OntologyNode parent = null;                        // parent node
    private OntologyNode[] children = new OntologyNode[1000];  // children nodes
    private String name;                                       // node name 
    private int nChildren = 0;                                 // number of children 
    
    // return the parent of this node
    public OntologyNode getParent(){    
        return this.parent;
    } 
    
    // return the list of all children of this node
    public OntologyNode[] getChildren(){
        return this.children;
    } 
    
    // return the name of this node
    public String getName(){
        return this.name;
    }
    
    // return the number of children of this node
    public int getNumberOfChildren(){
        return this.nChildren;       
    }
    
    // assign the parent to this node
    public void setParent(OntologyNode parent){
        this.parent = parent;
    }
    
    // insert a child into children list
    public void appendChild(OntologyNode newchild){
        children[nChildren++] = newchild;
        newchild.setParent(this);
    }
    
    // assign the name to this node
    public void setName(String name){
        this.name = name;
    }
}

