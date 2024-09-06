package model.ontology;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class defines an ontology tree object
 * @author LongNguyen & NghiaPham
 */

public class OntologyTree {
    
    private static OntologyNode[] onto_entityList;                          // List of all entities in ontology
    private static OntologyNode[] onto_classList = new OntologyNode[1000];  // List of all classes in ontology
    private static int nEntities = 0;                                       // Current number of entities
    private static int nClasses = 0;                                        // Current number of classes
    
    // return list of all entites that the current ontology contains
    public static OntologyNode[] getEntityList(){ 
        return onto_entityList;
    }
    
    // return list of all classes that the current ontology contains
    public static OntologyNode[] getClassList(){ 
        return onto_classList;
    }
    
    // return the current number of entities
    public static int getNumberOfEntity(){ 
        return nEntities;
    }    
    
    // return the current number of classes
    public static int getNumberOfClass(){ 
        return nClasses;
    }            
     
    // parse the entityList and return the list of all parent nodes
    // if the input entity doesnot exist in the ontology, the return parents list will be NULL
    public static OntologyNode[] getEntityParents(String entityName){ 
        int nParents=0;
        OntologyNode[] parents = new OntologyNode[10]; 
        String s = "#" + entityName; // insert # before entityName to match the format of OWL
        for(int i=0; i<nEntities; i++){
            if( s.equalsIgnoreCase(onto_entityList[i].getName()) ){ 
                parents[nParents++] = onto_entityList[i].getParent();
            }
        }        
        return parents; 
    }     
     
    // insert a newNode into ontology. newNode can be class or entity.
    public static OntologyNode newNode(String name){
        // if the node already exist in the ontology, return the existing node
        for(int i=0; i<nClasses; i++){ 
            if(onto_classList[i].getName().equalsIgnoreCase(name))
                return onto_classList[i];
        }
        //othersiwe, create a new node and insert into ontology
        OntologyNode newnode = new OntologyNode();
        newnode.setName(name);
        onto_classList[nClasses++] = newnode;
        return newnode;
    }
     
    // init EntityList before create ontology tree
    public static void initEntityList(int NumberOfNode){ 
        onto_entityList = new OntologyNode[NumberOfNode];
        for(int i=0; i<NumberOfNode; i++){
            onto_entityList[i] = new OntologyNode();
        }
        nEntities = NumberOfNode;
    }    
    
    // parse the ONTOLOGY.OWL and create ontology tree
    public static void initTree() {         

        try {                        
            File fXmlFile = new File("./ontology/Ontology.owl");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            //-------------------------------------------------------------
            
            NodeList entityList = doc.getElementsByTagName("ClassAssertion"); 
            
            OntologyTree.initEntityList(entityList.getLength());

            for (int temp = 0; temp < entityList.getLength(); temp++) {
                Node nNode = entityList.item(temp);                

                Element eElement = (Element) nNode;
                Element entity = (Element)eElement.getElementsByTagName("NamedIndividual").item(0);
                Element classentity = (Element)eElement.getElementsByTagName("Class").item(0);

                OntologyNode[] ontoList = OntologyTree.getEntityList();
                ontoList[temp].setName(entity.getAttribute("IRI"));

                OntologyNode newClassNode = OntologyTree.newNode(classentity.getAttribute("IRI"));
                newClassNode.appendChild(ontoList[temp]);                    
            }
            //-------------------------------------------------------------            
            NodeList classList = doc.getElementsByTagName("SubClassOf");             

            for (int i = 0; i < classList.getLength(); i++) { 
                Node eeNode = classList.item(i);

                Element eeElement = (Element)eeNode;
                NodeList classes = eeElement.getElementsByTagName("Class");
                Node childNode = classes.item(0);                   
                Node parentNode = classes.item(1);                                           
                Element child = (Element)childNode;
                Element parent = (Element)parentNode;

                OntologyNode newChild = OntologyTree.newNode(child.getAttribute("IRI"));
                OntologyNode newParent = OntologyTree.newNode(parent.getAttribute("IRI"));
                newParent.appendChild(newChild);                
            }                         
        } 
        catch (ParserConfigurationException | SAXException | IOException e) {
	}        
    }
}
