package business.algorithm;

import business.processing.Logging;
import model.data.Data;
import model.ontology.OntologyNode;
import model.ontology.OntologyTree;

/**
 * This class designs the algorithm to detect a brand-only review
 * @author LongNguyen & NghiaPham
 */

public class BrandOnlyDetection {
    
    // main method of brand-only detection 
    public static boolean isBrandOnly(){   
        Logging.printLog(Logging.SEPERATOR2 + "BrandOnlyDetection: isBrandOnly()\n");
        
        //count the number of words that point to a brand, seller, producer or a company
        Logging.printLog("**brandWords: ");
        int NumberOfBrandWord = 0;
        for(int i=0; i<Data.getEntitiesList().length; i++){
            String entity = Data.getEntitiesList()[i][0];
            OntologyNode[] parents = OntologyTree.getEntityParents(entity);
            for(int j=0; j<parents.length; j++){
                if(parents[j] != null){
                    while(parents[j] != null){
                        if(  parents[j].getName().endsWith("Origin") ){
                            NumberOfBrandWord++;
                            Logging.printLog(entity + " ");
                            j = parents.length;
                            break;
                        }
                        parents[j] = parents[j].getParent();
                    }
                }
            }
        }
        
        // calculate the ratio
        float ratio = (float)NumberOfBrandWord/Data.getNumberOfEntities(); 
        Logging.printLog("\nnumberOfBrandWord= " + NumberOfBrandWord + " => ratio= " + ratio);
        if(ratio > 0.1) {
            Logging.printLog(" > 0.1 \ntoo many brand words => brand-only review");
            return true; 
        }
        return false;        
    }
     
}
