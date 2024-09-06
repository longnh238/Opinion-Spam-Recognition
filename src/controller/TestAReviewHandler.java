/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import business.algorithm.BrandOnlyDetection;
import business.algorithm.NonReviewDetection;
import business.algorithm.OffTopicDetection;
import business.algorithm.UntruthfulDetection;
import business.processing.ExecutionTime;
import business.processing.Logging;
import business.processing.PreprocessingModule;
import java.awt.Rectangle;
import model.data.Data;
import model.ontology.OntologyProductList;
import view.OpinionSpamView;
import view.TestAReviewView;

/**
 * @author LongNguyen & NghiaPham
 */
public class TestAReviewHandler {
    
    /**
     * check whether user fill out 2 text field or not
     * - If 2 field was filled: checkEmptyField = 0
     * - If one of them is missing: checkEmptyField = 1
     */
    private int checkEmptyField = 0;
    
    private void updateProgressBar(int value, Rectangle progressRect) {
        // Show progress bar. Update state of progress bar immediatelly
        TestAReviewView.jProgressBarTestAReview.setValue(value);
        TestAReviewView.jProgressBarTestAReview.paintImmediately(progressRect);
    }
    
    private void updateDetailProgressBar(int startValue, int endValue, Rectangle progressRect) {
        // Show progress bar. Update state of progress bar immediatelly
        for(int i = startValue; i < endValue; i++) {
            TestAReviewView.jProgressBarTestAReview.setValue(i);
            TestAReviewView.jProgressBarTestAReview.paintImmediately(progressRect);
        }
    }
    
    public void productTypeComboBoxHandler () {
        TestAReviewView.jProductNameComboBox.removeAllItems();
        switch (TestAReviewView.jProductTypeComboBox.getSelectedItem().toString()) {
            case "Mobile Phone":
                for(int i = 0; i < OntologyProductList.getPhoneProductList().size(); i++) {
                    TestAReviewView.jProductNameComboBox.addItem(OntologyProductList.getPhoneProductList().get(i));
                }
                break;
            case "Laptop & Tablet":
                for(int i = 0; i < OntologyProductList.getLaptopProductList().size(); i++) {
                    TestAReviewView.jProductNameComboBox.addItem(OntologyProductList.getLaptopProductList().get(i));
                }
                break;
            case "Camera":  
                for(int i = 0; i < OntologyProductList.getCameraProductList().size(); i++) {
                    TestAReviewView.jProductNameComboBox.addItem(OntologyProductList.getCameraProductList().get(i));
                }
                break;
            case "Hotel":
                for(int i = 0; i < OntologyProductList.getHotelProductList().size(); i++) {
                    TestAReviewView.jProductNameComboBox.addItem(OntologyProductList.getHotelProductList().get(i));
                }
                break;
        } 
    }
    
    public void checkButtonHandler () {
        Logging.initLogger(); 

        /**
         * Check product name text field and review content text field
         * If one of them is missing -> program will not run
         */
        
        if(TestAReviewView.jProductTypeComboBox.getSelectedIndex() == -1
                || TestAReviewView.jProductTypeComboBox.getSelectedIndex() == 0
                || TestAReviewView.jProductNameComboBox.getSelectedIndex() == -1) {
            checkEmptyField = 1;
        }
        if(TestAReviewView.jReviewTextArea.getText().equals("") 
                || TestAReviewView.jReviewTextArea.getText().equals("Please insert review")) {
            TestAReviewView.jReviewTextArea.setText("Please insert review");
            checkEmptyField = 1;
        }
        if(checkEmptyField == 1) {
            return;
        }
        
        /**
         * Get product type
         */
        String productType = "";
        switch (TestAReviewView.jProductTypeComboBox.getSelectedItem().toString()) {
            case "Mobile Phone":
                productType = "phone";
                break;
            case "Laptop & Tablet":
                productType = "laptop";
                break;
            case "Hotel":
                productType = "hotel";
                break;
            case "Camera":         
                productType = "camera";
                break;
        }
        
        /**
         * Create time variable and start timing
         */
        ExecutionTime time = new ExecutionTime();
        time.start();
        
        // Show progress bar
        Rectangle progressRect = TestAReviewView.jProgressBarTestAReview.getBounds();
        progressRect.x = 0;
        progressRect.y = 0;
        
        TestAReviewView.jProgressBarTestAReview.setStringPainted(true); // Set value for progrss bar update value immediately
        TestAReviewView.jProgressBarTestAReview.setValue(0); // Set value for progress bar
        updateDetailProgressBar(1, 9, progressRect);
        
        /** 
         * Preprocessing steps
             * - setProductType(): get product type of review and assign to productType variable
             * - setProductName(): get product name of review and assign to productType variable
             * - setReviewContent(): get content of review and assign to reviewContent variable
             * - setReviewPosTagging(): use Stanford PosTagger for content of review
             * - createPosList(): split tagged string and save in an array list
             * - createEntitiesList(): determine entity and create Entity list
         */
        Data.setProductType(productType); 
        updateProgressBar(10, progressRect); // update progerss bar
        updateDetailProgressBar(11, 19, progressRect);

        Data.setProductName(TestAReviewView.jProductNameComboBox.getSelectedItem().toString());
        updateProgressBar(20, progressRect); // update progerss bar
        updateDetailProgressBar(21, 29, progressRect);
        
        Data.setReviewContent(TestAReviewView.jReviewTextArea.getText()); 
        updateProgressBar(30, progressRect); // update progerss bar
        updateDetailProgressBar(31, 44, progressRect);
        
        PreprocessingModule.setReviewPosTagging(Data.getReviewContent()); 
        updateProgressBar(45, progressRect); // update progerss bar
        updateDetailProgressBar(46, 59, progressRect);
        
        PreprocessingModule.createPosList();
        updateProgressBar(60, progressRect); // update progerss bar
        updateDetailProgressBar(61, 79, progressRect);
        
        PreprocessingModule.createEntitiesList();
        updateProgressBar(80, progressRect); // update progerss bar
        updateDetailProgressBar(81, 99, progressRect);
        
        /**
         * Log some important things here
         */
        Logging.printLog(Logging.SEPERATOR2 + "PRODUCT TYPE: " + productType + "\n");
        Logging.printLog("PRODUCT NAME: " + Data.getProductName() + "\n");
        Logging.printLog("REVIEW:\n" + Data.getReviewContent());
        Logging.printLog(Logging.SEPERATOR2 + "POS LIST contains " + Data.getNumberOfPOS() + " words:\n");   
        Logging.printList(Data.getPosList());
        Logging.printLog(Logging.SEPERATOR2 + "ENTITY LIST contains " + Data.getNumberOfEntities() + " entities:\n");
        Logging.printList(Data.getEntitiesList());
              
        /**
         * Begin test. Check a review whether it is a spam or not. If a review is spam, show type spam
         */
        if(NonReviewDetection.isNonReview()) {
            TestAReviewView.jResultTextField.setText("This is an opinion spam. Type: Non Review");
        }
        else if(BrandOnlyDetection.isBrandOnly()){
            TestAReviewView.jResultTextField.setText("This is an opinion spam. Type: Brand Only Review");
        }
        else if(OffTopicDetection.isOffTopic()){
            TestAReviewView.jResultTextField.setText("This is an opinion spam. Type: Off Topic Review");
        }        
        else if(UntruthfulDetection.isUntruthful()){
            TestAReviewView.jResultTextField.setText("This is an opinion spam. Type: Untruthful Review");
        }
        else {
            TestAReviewView.jResultTextField.setText("This is a truthful review");
        }
        updateProgressBar(100, progressRect); // update progerss bar
        
        /**
         * Stop timing and show time to view
         */
        time.stop();
        TestAReviewView.jTime.setText("Total time: " + time.timeExcecute() + " ms");
        Logging.stop(); // Close print writer for writing to file text
        OpinionSpamView.testAReviewFlag = false;
    } 
    
}
