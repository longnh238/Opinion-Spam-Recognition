package business.algorithm;

import business.processing.Logging;
import business.processing.PreprocessingModule;
import model.data.Data;
import model.data.Dictionany;
import model.ontology.OntologyNode;
import model.ontology.OntologyTree;
import view.OpinionSpamView;

/**
 * This class designs the algorithm to detect an untruthful review
 * @author LongNguyen & NghiaPham
 */

public class UntruthfulDetection {
    
    // threshold to be a utruthful
    private static float ratio;   
    private static float ratioPlus;
    
    public static int countPositive;
    public static int countNegative;
    
    public static int nOnTopicEntities;
    public static int nUppercase;      
    
    // check whether a word is positive or negative or neutral
    // if positive return 1; if negative return -1; if neutral return 0
    private static int checkTypeSentiment(String word) {
        for(int i = 0; i < Dictionany.getPositiveWord().size(); i++) {
            String positiveWord = Dictionany.getPositiveWord().get(i);
            if(word.equalsIgnoreCase(positiveWord)) {
                return 1;
            }
        }
        
        for(int i = 0; i < Dictionany.getNegativeWord().size(); i++) {
            String negativeWord = Dictionany.getNegativeWord().get(i);
            if(word.equalsIgnoreCase(negativeWord)) {
                return -1;
            }
        }
        return 0;
    }
        
    // check opinion of reviewer
    // return result use for ratio plus
    public static float sentimentDetection() {
        Logging.printLog("**sentimentDetection:\n");
        countPositive = countNegative = 0;     
        float deviation = 0;
        int nPos = model.data.Data.getNumberOfPOS();
        for(int i = 0; i < nPos;  i++) {
            String word = Data.getPosList()[i][0];
            String pos = Data.getPosList()[i][1];
            if(pos.equals("NN")
                    || pos.equals("JJ") || pos.equals("JJR") || pos.equals("JJS")
                    || pos.equals("RB") || pos.equals("RBR") || pos.equals("RBS") ) {
                switch(checkTypeSentiment(word)) {
                    case 1:
                        Logging.printLog(word + " 1 ");                
                        countPositive++;
                        break;
                    case -1:
                        Logging.printLog(word + " -1 ");                
                        countNegative++;
                        break;
                }
            }
        }
        
        Logging.printLog("\n");
        Logging.printLog("positive: " + countPositive + " & negative: " + countNegative);
        
        deviation = Math.abs(countPositive - countNegative);
        
        ratioPlus = (float) deviation / (countPositive + countNegative) / 10000;
        Logging.printLog("\nratioPlus = " + ratioPlus);  
        
        return ratioPlus;
    }
    
    // count the number of extreme words in the review content.
    // extreme words here mean words that show an exaggration, something 
    // better, worse than it really is
    public static boolean extremeWordCondition(){
        Logging.printLog("\n**extremeWord: ");
        int nExtremeWords = 0;
        for (int i = 0; i < Data.getPosList().length; i++) {
            String word = Data.getPosList()[i][0];
            String pos = Data.getPosList()[i][1];
            if (pos.equals("RB") || pos.equals("RBR") || pos.equals("RBS")) {
                if (Dictionany.getNonExtremeWord().indexOf(word) == -1) {
                    Logging.printLog(word + " ");
                    nExtremeWords++;
                }
            }
        }                   
        // calculate the ratio
        ratio = (float)nExtremeWords / Data.getNumberOfPOS() + ratioPlus;
        
        Logging.printLog("\nnumberOfExtremeWord= " + nExtremeWords + " => ratio= " + ratio);
        if (ratio > 0.034) {             
            Logging.printLog(" > 0.034 \ntoo many extreme words => untruthful review\n");
            return true;
        }        
        return false;
    }
      
    // fake review often repeat the product name many many times in order to
    // attract the reader to target product
    public static boolean repeatProductNameCondition(){   
        Logging.printLog("\n**repeatProductName: ");
        int nRepeatedProductName = 0; 
        int nPos = Data.getNumberOfPOS();   
        int nSentence = Data.getNumberOfSentences();
        
        if(OpinionSpamView.autoTestFlag) {
            PreprocessingModule.createSentencesList(Data.getReviewContent());
        }

        for(int j=0; j<nPos; j++){                        
            String word = Data.getPosList()[j][0];            
            OntologyNode[] parents = OntologyTree.getEntityParents(word); 
            
            for(int i=0; i<parents.length; i++){
                while(parents[i] != null){
                    if(parents[i].getName().endsWith("Name")){
                        nRepeatedProductName++; 
                        Logging.printLog(word + " ");
                        break;                 
                    }
                    parents[i] = parents[i].getParent();    
                }
            }            
        }
        
        // calculate the ratio
        ratio = (float)nRepeatedProductName/nSentence + ratioPlus;
        
        Logging.printLog("\nnumberOfRepeatedName= " + nRepeatedProductName + " => ratio= " + ratio);
        if(ratio > 0.75) {
            Logging.printLog(" > 0.75 \ntoo many repeat product name => untruthful review\n");
            return true; 
        }
        return false;    
    }
    
    // spam reviewer often use UPPER CASE to write Product Name or Product Brand
    // instead of lower case
    public static boolean makeAttentionByUppercaseCondition() {
        Logging.printLog("\n**makeAttentionByUppercase: ");
        int nUpperCaseProductName = 0; 
        int nName = 1;
        int nPos = Data.getNumberOfPOS();        

        for(int j=0; j<nPos; j++){                        
            String word = Data.getPosList()[j][0];            
            OntologyNode[] parents = OntologyTree.getEntityParents(word); 
            
            for(int i=0; i<parents.length; i++){
                while(parents[i] != null){
                    if(parents[i].getName().endsWith("Name")){
                        nName++; 
                        String up = word.toUpperCase();
                        if(word.equals(up)) {
                            nUpperCaseProductName++;
                            Logging.printLog(word + " ");
                        }
                        break;                 
                    }
                    parents[i] = parents[i].getParent();    
                }
            }            
        }
        
        // calculate the ratio
        ratio = (float)nUpperCaseProductName / nName + ratioPlus;
        
        Logging.printLog("\nnumberOfOntopicEntity= " + nOnTopicEntities + " => ratio= " + ratio);
        if(ratio > 0.75) {
            Logging.printLog(" > 0.75 \ntoo many uppercase product name => untruthful review\n");
            return true; 
        }
        return false; 
    }

    // main method of untruthful detection 
    public static boolean isUntruthful() { 
        Logging.printLog(Logging.SEPERATOR2 + "UntruthfulDetection: isUntruthful()\n");           
        
        ratioPlus = sentimentDetection();
        if(extremeWordCondition()) {
            return true;
        }
        else if(repeatProductNameCondition()) {
            return true;
        }
        else if(makeAttentionByUppercaseCondition()) {
            return true;
        }
        else {
            return false;
        }
    }
}