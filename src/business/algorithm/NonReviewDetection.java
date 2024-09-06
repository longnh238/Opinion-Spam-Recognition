package business.algorithm;

import business.processing.Logging;
import business.processing.PreprocessingModule;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.data.Data;
import model.data.Dictionany;
import model.ontology.OntologyNode;
import model.ontology.OntologyTree;
import view.OpinionSpamView;

/**
 * This class designs the algorithm to detect a non-review
 * @author LongNguyen & NghiaPham
 */

public class NonReviewDetection {  
            
    // threshold to be a non-review
    private static float ratio; 
    private static float ratioPlus;
    
    
    // main method of non-review detection 
    public static boolean isNonReview () {
        Logging.printLog(Logging.SEPERATOR2 + "NonReview Detection: isNonReview()\n");

        // The more unsual patterns a review contains,
        // the more likely it will be a non-review
        ratioPlus = unusualPatternDetection();
        Logging.printLog("=> ratioPlus = " + ratioPlus + "\n");
         
        if(conditionOpinionWord()) {
            return true;
        }
        else if(conditionOnTopic()) {
            return true;
        }    
        // Only check sentence if user is checking a review
        // Not check sentence for auto test because parser run quite slow
        if(OpinionSpamView.testAReviewFlag) {
            if(conditionNumberOfSentence()) {
                return false;
            }
        }      
        return false;       
    }
    
    
    // detect unusual patterns that often used in non-review
    // such as: phone number, price, ads, email, random and pointless letters
    public static float unusualPatternDetection(){
        Logging.printLog("**unusualPattern:\n");        
        float plus = (float)1/Data.getNumberOfPOS();        
        ratioPlus = 0; 
        
        for(int i=0; i<Dictionany.getUnusualPattern().size(); i++){
            String unusualRex = Dictionany.getUnusualPattern().get(i);
            Pattern unusualPattern = Pattern.compile(unusualRex  , Pattern.CASE_INSENSITIVE);        
            Matcher m = unusualPattern.matcher(Data.getReviewContent());                        
             if(m.find()) {
                if(i==0) {
                     ratioPlus += (float)2*plus;
                 } 
                // double possibility for the question mark
                else {
                     ratioPlus += plus;
                 }
                Logging.printLog(unusualPattern + "\n");                
            }                
        }        
        return ratioPlus;
    }
    
    public static boolean conditionOpinionWord () {
        // count the number of opinion words
        Logging.printLog("**opinionWords: ");
        int numberOfOpinionWord = 0;
        for (int i = 0; i < Data.getPosList().length; i++) {
            String pos = Data.getPosList()[i][1];
            if (pos.equals("JJ") || pos.equals("JJR") || pos.equals("JJS")) {
                numberOfOpinionWord++;
                Logging.printLog(Data.getPosList()[i][0] + " ");
            }
        }        
        
        // calculate the ratio     
        ratio = (float)numberOfOpinionWord / Data.getNumberOfPOS() - ratioPlus;
        Logging.printLog("\nnumberOfOpinionWord= " + numberOfOpinionWord + " => ratio= " + ratio);
        if (ratio < 0.02) {
            Logging.printLog(" < 0.02 \nfew opinion words => non-review");
            return true;
        }
        return false;
    }
    
    public static boolean conditionOnTopic () {
        // count the number of entities existing in ontology
        Logging.printLog("\n**ontopicEntities: ");
        int numberOfOnTopicEntity = 0;
        for(int i = 0; i < Data.getEntitiesList().length; i++) {
            String entity = Data.getEntitiesList()[i][0];
            OntologyNode[] parentsOfAnEntity = OntologyTree.getEntityParents(entity);
            if(parentsOfAnEntity[0] != null) {
                numberOfOnTopicEntity++;    
                Logging.printLog(entity + " ");
            }
        }      
        
        // calculate the ratio
        ratio = (float)numberOfOnTopicEntity / Data.getNumberOfEntities() - ratioPlus;
        Logging.printLog("\nnumberOfOntopicEntity= " + numberOfOnTopicEntity + " => ratio= " + ratio);
        if(ratio < 0.055) {
            Logging.printLog(" < 0.055 \nfew ontopic entities => non-review");
            return true; 
        }
        return false;
    }
    
        
    public static boolean conditionNumberOfSentence () {
        PreprocessingModule.createSentencesList(Data.getReviewContent());
        
        // count number sentence of review
        Logging.printLog("\n**sentence: ");
        int numberOfSentence = 0;
        
        if(Data.getSentenceList() != null) {
            for (int i = 0; i < Data.getSentenceList().length; i++) {
                if (Data.getSentenceList()[i][1].equals("S")) {
                    numberOfSentence++;
                    Logging.printLog("\n" + Data.getSentenceList()[i][0]);
                }
            }    
        }
        
        // calculate the ratio     
        ratio = (float)numberOfSentence / Data.getNumberOfSentences();
        Logging.printLog("\nnumberOfSentence= " + numberOfSentence + " => ratio= " + ratio);
        if (ratio < 0.2) {
            Logging.printLog(" < 0.2 \nfew sentences => non-review");
            return true;
        }
        return false;
    }       
}