package model.ontology;

import java.util.ArrayList;

/**
 * This class defines an ontology node object.
 * @author LongNguyen & NghiaPham
 */

public class OntologyProductList {    
        
    private static ArrayList<String> phoneProductList = new ArrayList();
    private static ArrayList<String> laptopProductList = new ArrayList();
    private static ArrayList<String> cameraProductList = new ArrayList();
    private static ArrayList<String> hotelProductList = new ArrayList();
   
    public static ArrayList<String> getPhoneProductList(){
        return phoneProductList;
    }
    
    public static ArrayList<String> getLaptopProductList(){
        return laptopProductList;
    }
   
    public static ArrayList<String> getCameraProductList(){
        return cameraProductList;
    }
    
    public static ArrayList<String> getHotelProductList(){
        return hotelProductList;
    }
    
    public static void initProductNameFromOntology(){
        for(int i=0; i< OntologyTree.getNumberOfClass(); i++){            
            OntologyNode currentClass = OntologyTree.getClassList()[i];
            if( currentClass.getName().startsWith("#l_") ){         
                for(int j=0; j<currentClass.getNumberOfChildren(); j++){
                    if(currentClass.getChildren()[j].getNumberOfChildren() != 0){
                        String productName = currentClass.getChildren()[j].getName();
                        laptopProductList.add(productName);
                    }
                }
            }
            
            if( currentClass.getName().startsWith("#p_") ){         
                for(int j=0; j<currentClass.getNumberOfChildren(); j++){
                    if(currentClass.getChildren()[j].getNumberOfChildren() != 0){
                        String productName = currentClass.getChildren()[j].getName();
                        phoneProductList.add(productName);
                    }
                }
            }
            
            if( currentClass.getName().startsWith("#c_") ){         
                for(int j=0; j<currentClass.getNumberOfChildren(); j++){
                    if(currentClass.getChildren()[j].getNumberOfChildren() != 0){
                        String productName = currentClass.getChildren()[j].getName();
                        cameraProductList.add(productName);
                    }
                }
            }
        }
        
        hotelProductList.add("#None");
    }
}

