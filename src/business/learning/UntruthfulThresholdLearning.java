/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package business.learning;

import business.processing.PreprocessingModule;
import java.io.File;
import java.io.IOException;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import model.data.Data;
import model.data.Dictionany;
import model.ontology.OntologyTree;

/**
 *
 * @author LongNguyen & NghiaPham
 */
public class UntruthfulThresholdLearning {
    
    public static float positivePercentage1;
    public static float negativePercentage1;
    public static float neutrelPercentage1;
    
//    public static float  threshold1 = (float) 0.046394326;
//    public static float  threshold2 = (float) 0.026144547;
//    public static float  threshold3 = (float) 0.036533102;
//    public static float  threshold4 = (float) 0.02666619;
//    public static float  threshold  = (threshold1+threshold2+threshold3+threshold4) / 4;
//    
//    public static float positivePercentage1 = (float) 0.060988717;
//    public static float positivePercentage2 = (float) 0.05028042;      
//    public static float positivePercentage3 = (float) 0.0810633;     
//    public static float positivePercentage4 = (float) 0.061114334;
//    
//    public static float negativePercentage1 = (float) 0.004552161;    
//    public static float negativePercentage2 = (float) 0.007249885; 
//    public static float negativePercentage3 = (float) 0.0018930925;  
//    public static float negativePercentage4 = (float) 0.0058979606;
//    
//    public static float sentimentThreshold = 
//            (positivePercentage1+positivePercentage2+positivePercentage3+positivePercentage4)/4;
    
    public static void main(String agrs[]) throws IOException, BiffException{
        init();
        untruthfulThreshold();
    }
    
    public static void init(){
        PreprocessingModule.initPosTagger();
        PreprocessingModule.initParser();
        OntologyTree.initTree();
        Dictionany.initUnusualPattern();
        Dictionany.initNonExtremeWord(); 
        Dictionany.initSentimentWord();
        //Dictionany.initProductName();
    }
    
    public static void untruthfulThreshold() throws IOException, BiffException{
        
       float threshold1 = 0;
       float threshold2 = 0;
        
       File file = new File("./autotest/dataset1.xls");
       Workbook wb = Workbook.getWorkbook(file);                        
       Sheet spamSheet = wb.getSheet(0); 
       Sheet nonspamSheet = wb.getSheet(1);
       
       for(int i=301; i<400; i++){    
           System.out.println(i);
           String productType      = spamSheet.getCell(1, i).getContents();
           String productName      = spamSheet.getCell(2, i).getContents();
           String reviewContent    = spamSheet.getCell(3, i).getContents();         
           
           Data.setProductType(productType);     
           Data.setProductName(productName);
           Data.setReviewContent(reviewContent);                           
           PreprocessingModule.setReviewPosTagging(Data.getReviewContent());               
           PreprocessingModule.createPosList();                
           PreprocessingModule.createEntitiesList();
           
           // count nExtremeWords
           int nExtremeWords = 0;
           for (int j = 0; j < Data.getPosList().length; j++) {
                String word = Data.getPosList()[j][0];
                String pos = Data.getPosList()[j][1];
                if (pos.equals("RB") || pos.equals("RBR") || pos.equals("RBS")) {
                    if (Dictionany.getNonExtremeWord().indexOf(word) == -1) {
                        nExtremeWords++;
                    }
                }
            }                               
            float ratio = (float)nExtremeWords / Data.getNumberOfPOS();
            threshold1 += ratio;
            sentimentDetection();
       }
       
//       for(int i=0; i<400; i++){       
//           System.out.println(i);
//           String productType      = nonspamSheet.getCell(1, i).getContents();
//           String productName      = nonspamSheet.getCell(2, i).getContents();
//           String reviewContent    = nonspamSheet.getCell(3, i).getContents();         
//           
//           Data.setProductType(productType);     
//           Data.setProductName(productName);
//           Data.setReviewContent(reviewContent);                           
//           PreprocessingModule.setReviewPosTagging(Data.getReviewContent());               
//           PreprocessingModule.createPosList();                
//           PreprocessingModule.createEntitiesList();
//           
//           // count nExtremeWords
//           int nExtremeWords = 0;
//           for (int j = 0; j < Data.getPosList().length; j++) {
//                String word = Data.getPosList()[j][0];
//                String pos = Data.getPosList()[j][1];
//                if (pos.equals("RB") || pos.equals("RBR") || pos.equals("RBS")) {
//                    if (Dictionany.getNonExtremeWord().indexOf(word) == -1) {
//                        nExtremeWords++;
//                    }
//                }
//            }                               
//            float ratio = (float)nExtremeWords / Data.getNumberOfPOS();
//            threshold2 += ratio;
//            sentimentDetection();
//       }     
            
       threshold1 = threshold1/100;
       threshold2 = threshold2/400;       
       System.out.println("threshold1 = " + threshold1);
       System.out.println("threshold2 = " + threshold2);    
       
       positivePercentage1 /= 100;
       negativePercentage1 /= 100;
       neutrelPercentage1 /= 100;
       System.out.println("positivePercentage1 = " + positivePercentage1);
       System.out.println("negativePercentage1 = " + negativePercentage1);    
       System.out.println("neutrelPercentage1 = " + neutrelPercentage1);    
    }
    
    // check opinion
    public static void sentimentDetection() {   
        int nPositive = 0;
        int nNegative = 0;
        int nNeutral  = 0;
        int nPos = model.data.Data.getNumberOfPOS();
        
        for(int i = 0; i <nPos;  i++) {
            String word = Data.getPosList()[i][0];
            String pos = Data.getPosList()[i][1];
            if(pos.equals("NN")
                    || pos.equals("JJ") || pos.equals("JJR") || pos.equals("JJS")
                    || pos.equals("RB") || pos.equals("RBR") || pos.equals("RBS") ) {
                switch(checkSentiment(word)) {
                    case 1:
                        nPositive++;
                        break;
                    case -1:
                        nNegative++;
                        break;
                    case 0:  
                        nNeutral++;
                        break;
                }
            }
        }
        int n = nPositive + nNegative + nNeutral;
        positivePercentage1 += (float)nPositive/n;
        negativePercentage1 += (float)nNegative/n;
        neutrelPercentage1 += (float)nNeutral/n;
        
    }  

    public static int checkSentiment(String word) {
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
}
