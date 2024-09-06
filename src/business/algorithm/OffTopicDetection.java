package business.algorithm;

import business.processing.Logging;
import model.data.Data;
import model.ontology.OntologyNode;
import model.ontology.OntologyTree;
import view.OpinionSpamView;

/**
 * This class designs the algorithm to detect an off-topic review
 * @author LongNguyen & NghiaPham
 */

public class OffTopicDetection {   
    public static int nOnTopicEntities = 0;
    
    
    // main method of off-topic detection 
    public static boolean isOffTopic(){
        if(isOffType()) {
            return true;
        }
        if(OpinionSpamView.autoTestFlag) {
            return isOffBrand();
        }
        if(OpinionSpamView.testAReviewFlag) {
            return x_isOffBrand();
        }
        
        return false;
    }
    
    
    // An off-topic review often focuses on a product that has a different type 
    // from the reviewed product. This method is designed to detect such kind of reviews.
    public static boolean isOffType(){
        Logging.printLog(Logging.SEPERATOR2 + "OffTopicDetection: isOffType()\n");
        
        // number of entities existing in ontology sub-branch of specified topic
        // topic here means the title of the review
        nOnTopicEntities = 0; 
        int nEntities = Data.getNumberOfEntities();
        
        // count the number of onTopicEntities
        Logging.printLog("**ontopicEntities: ");
        for(int j=0; j<nEntities; j++){            
            String entity = Data.getEntitiesList()[j][0];            
            OntologyNode[] parents = OntologyTree.getEntityParents(entity); 
            
            for(int i=0; i<parents.length; i++){
                while(parents[i] != null){
                    if(Data.getProductType().equalsIgnoreCase(parents[i].getName())){
                        nOnTopicEntities++; 
                        Logging.printLog(entity + " ");
                        break;                 
                    }
                    parents[i] = parents[i].getParent();    
                }
            }            
        }
        
        // calculate the ratio
        float ratio = (float)nOnTopicEntities/nEntities;
        Logging.printLog("\nnumberOfOntopicEntity= " + nOnTopicEntities + " => ratio= " + ratio);
        if(ratio < 0.1) {
            Logging.printLog(" < 0.1 \nfew ontopic entities => off-topic review");
            return true; 
        }
        return false;    
    }
    
    
    // A review can point to the same type of product, or same type of title
    // but different product brands. It can be consider as an off-topic review. 
    // This method is designed to detect such kind of reviews.
    public static boolean isOffBrand(){ 
        Logging.printLog("\n----->isOffBrand()\n **OnOffBrandWords:\n");
        String productName = Data.getProductName();
        int nBrandWords = 0;
        int nOffBrandWords = 0;
        for(int i=0; i<Data.getNumberOfEntities(); i++){
            String entity = Data.getEntitiesList()[i][0];
            OntologyNode[] parents = OntologyTree.getEntityParents(entity);
            if(parents[0] != null){
                while(parents[0] != null){
                    if( //parents[0].getName().endsWith("Origin")
                        parents[0].getName().endsWith("PopularName") 
                            ){
                        nBrandWords++;                        
                        Logging.printLog(entity);
                        String s1 = productName.toLowerCase();
                        String s2 = entity.toLowerCase();
                        if(s1.contains(s2)){                            
                            Logging.printLog("(matched)\n");                            
                        }   
                        else{
                            Logging.printLog("(not matched)\n");
                            nOffBrandWords++;  
                        }
                        break;
                    }                      
                    parents[0] = parents[0].getParent();
                }
            }            
        }
        
        float ratio = (float)nOffBrandWords/nOnTopicEntities;
        Logging.printLog("nOffBrandWords= " + nOffBrandWords + " => ratio= " + ratio);
        if(ratio > 0.13) {
            Logging.printLog(" > 0.2\ntalk too much about another brand => off-topic review");
            return true;
        }
        return false;
    }  
    
    
    // extras method for checking brandname
    public static boolean x_isOffBrand(){ 
        Logging.printLog("\n----->isOffBrand()\n **OnOffBrandWords:\n");
        String productName = Data.getProductName();
        int nBrandWords = 0;
        int nOnBrandWords = 0;
        int nOffBrandWords = 0;
        for(int i=0; i<Data.getNumberOfPOS(); i++){
            String entity = Data.getPosList()[i][0];
            OntologyNode[] parents = OntologyTree.getEntityParents(entity);            
            
            for(int j=0; j<parents.length; j++){
                if(parents[j] != null){
                    if(parents[j].getName().equalsIgnoreCase(productName)){
                        nOnBrandWords++;
                        Logging.printLog(entity + "(matched)\n");
                    }
                }
                while(parents[j] != null){                    
                    if( parents[j].getName().endsWith("Name") ){
                        Logging.printLog(entity + "\n");
                        nBrandWords++;
                        break;
                    }   
                    parents[j] = parents[j].getParent();
                }                
            }
                        
        }
        
        nOffBrandWords = nBrandWords - nOnBrandWords;
        Logging.printLog("nOffBrandWords= " + nOffBrandWords + "\nnOnBrandWords = " + nOnBrandWords + "\n");
        if(nOffBrandWords > nOnBrandWords){
            Logging.printLog("talk too much about another brand => off-topic review");
            return true;
        }
        return false;
    }
}
