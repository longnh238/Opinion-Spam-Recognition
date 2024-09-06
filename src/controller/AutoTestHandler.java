package controller;

import business.algorithm.BrandOnlyDetection;
import business.algorithm.NonReviewDetection;
import business.algorithm.OffTopicDetection;
import business.algorithm.UntruthfulDetection;
import business.processing.Logging;
import business.processing.PreprocessingModule;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import model.data.Data;
import view.AutoTestView;
import view.OpinionSpamView;

/**
 * @author LongNguyen & NghiaPham
 */
public class AutoTestHandler {
    
    /**
     * Index of every type in dataset (begin index and end index)
     */
    static int NON_REVIEW_BEGIN_INDEX = 1;
    static int NON_REVIEW_END_INDEX = 100;
    
    static int BRAND_ONLY_BEGIN_INDEX = 101;
    static int BRAND_ONLY_END_INDEX = 200;
    
    static int OFF_TOPIC_BEGIN_INDEX = 201;
    static int OFF_TOPIC_END_INDEX = 300;
    
    static int UNTRUTHFUL_BEGIN_INDEX = 301;
    static int UNTRUTHFUL_END_INDEX = 400;
    
    static int SPAM_BEGIN_INDEX = 1;
    static int SPAM_END_INDEX = 400;
    
    static int NON_SPAM_BEGIN_INDEX = 1;
    static int NON_SPAM_END_INDEX = 400;

    /**
     * Number of every type in dataset
     */
    static int NON_REVIEW = 100;
    static int BRAND_ONLY = 100;
    static int OFF_TOPIC = 100;
    static int UNTRUTHFUL = 100;       
    static int SPAM = 400;
    static int NON_SPAM = 400;
    
    static String modeSubType;
    
    /**
     * Determine which type will be check
     * - If user choose spam, non-review, brand-only, off-topic, untruthful for checking -> testSpam = true
     * - If user choose non-spam for checking -> testNonSpam = true 
     */
    private boolean testSpam = false;
    private boolean testNonSpam = false;
    
    private int testNonReview = 0;
    private int testBrandOnly = 0;
    private int testOffTopic = 0;
    private int testUntruthful = 0;

    /**
     * Variable save string result
     * - writeResultToResultTextArea: write result to result text area in AutoTestView
     * - writeResultToFile: write result to file text
     */
    private String writeResultToResultTextArea = "";
    private String writeResultToFile = "";
    
    /**
     * count...Pass: variable count number pass test case in every type
     * ratio: pass test case / total test case
     */
    private double countSpamPass, countNonSpamPass;
    private double countNonReviewPass, countBrandOnlyPass, countOffTopicPass, countUntruthfulPass;
    private double ratio;
    
    
    private Rectangle progressRect;

    private float totalReview;
    private float currentReview;
    
    private int countTotalReview() {
        int total = 0;
        if(AutoTestView.jNonReviewCheckBox.isSelected()) {
            total += NON_REVIEW;
        }
        if(AutoTestView.jBrandOnlyCheckBox.isSelected()) {
            total += BRAND_ONLY;
        }
        if(AutoTestView.jOffTopicCheckBox.isSelected()) {
            total += OFF_TOPIC;
        }
        if(AutoTestView.jUntruthfulCheckBox.isSelected()) {
            total += UNTRUTHFUL;
        }
        if(AutoTestView.jSpamCheckBox.isSelected()) {
            total += SPAM;
        }  
        if(testNonSpam) {
            total += NON_SPAM;
        }
        return total;
    }
    
    private void updateProgressBar(int value, Rectangle progressRect) {
        // Show progress bar. Update state of progress bar immediatelly
        AutoTestView.jProgressBarAutoTest.setValue(value);
        AutoTestView.jProgressBarAutoTest.paintImmediately(progressRect);
    }
    
    /**
     * Update content to Result Text Area immediately 
     */
    private void updateTextArea(final String content) {
        AutoTestView.jResultTextArea.append(content);
        AutoTestView.jResultTextArea.update(AutoTestView.jResultTextArea.getGraphics());     
    }
    
    /**
     * @param sheet: file dataset. sheet 0: 4 type spam review; sheet 1: non-spam review
     * @param writeResultToFile: write result to file text
     */
    private void test(Sheet sheet, int begin, int end, String writeResultToFile, PrintWriter pw) {
        for(int i = begin; i <= end; i++){
            
            // Process progress bar
            currentReview += 1;
            updateProgressBar((int)((currentReview/totalReview)*100), progressRect);
            
            Logging.printLog(Logging.SEPERATOR1 + "TESTCASE #" + i);
            
            /**
             * Get information from file dataset (file excel)
             * cell(0): review ID number
             * cell(1): type of product being reviewed
             * cell(2): name of product being reviewed
             * cell(3): content of review
             * cell(4): expected type of review
            */
            String num              = sheet.getCell(0, i).getContents();
            String productType     = sheet.getCell(1, i).getContents();
            String productName      = sheet.getCell(2, i).getContents();
            String reviewContent    = sheet.getCell(3, i).getContents();
            String expectedResult   = sheet.getCell(4, i).getContents();
            
            /**
             * Write to file text and Result Text Area
             */
            writeResultToFile = num + "\t" + productType + "\t" + expectedResult;          
            writeResultToResultTextArea = num + "\t           " + productType + "\t\t" + expectedResult + "\t\t";

            /**
             * Preprocessing
             * - setProductType(): get product type of review and assign to productType variable
             * - setProductName(): get product name of review and assign to productType variable
             * - setReviewContent(): get content of review and assign to reviewContent variable
             * - setReviewPosTagging(): use Stanford PosTagger for content of review
             * - createPosList(): split tagged string and save in an array list
             * - createEntitiesList(): determine entity and create Entity list
            */
            Data.setProductType(productType);     
            Data.setProductName(productName);
            Data.setReviewContent(reviewContent);                
            PreprocessingModule.setReviewPosTagging(Data.getReviewContent());               
            PreprocessingModule.createPosList();                
            PreprocessingModule.createEntitiesList();

            /**
             * Log some important things here
             */
            Logging.printLog(Logging.SEPERATOR2 + "PRODUCT TYPE: " + productType + "\n");
            Logging.printLog("PRODUCT NAME: " + productName + "\n");
            Logging.printLog("REVIEW:\n" + reviewContent);
            Logging.printLog(Logging.SEPERATOR2 + "POS LIST contains " + Data.getNumberOfPOS() + " words:\n");   
            Logging.printList(Data.getPosList());
            Logging.printLog(Logging.SEPERATOR2 + "ENTITY LIST contains " + Data.getNumberOfEntities() + " entities:\n");
            Logging.printList(Data.getEntitiesList());

            /**
             * Begin Testing
             */
            String actualResult = "";            
            boolean isBrandOnly, isOffTopic;
            
            /**
             * Determine actual result of a review. 
             * - 5 primitive type: non-review, brand-only, off-topic, untruthful, non-spam
             * - some test cases are both brand-only and off-topic: both
             */
            if(AutoTestView.jNonReviewCheckBox.isSelected()) {
                if(NonReviewDetection.isNonReview()) {
                    writeResultToFile += "\t true \t - \t - \t -";
                    actualResult = "non-review";                       
                }
                else {
                    actualResult = "non-spam";
                }
            }
            else if(AutoTestView.jBrandOnlyCheckBox.isSelected()) {
                if(BrandOnlyDetection.isBrandOnly()) {
                    writeResultToFile += "\t false \t false \t true \t -";
                    actualResult = "brand-only";    
                }
                else {
                    actualResult = "non-spam";
                }
            }
            else if(AutoTestView.jOffTopicCheckBox.isSelected()) {
                if (OffTopicDetection.isOffTopic()) {
                    writeResultToFile += "\t false \t true \t - \t -";
                    actualResult = "off-topic";    
                }
                else {
                    actualResult = "non-spam";
                }
            }
            else if(AutoTestView.jUntruthfulCheckBox.isSelected()) {
                if(UntruthfulDetection.isUntruthful()) {
                        writeResultToFile += "\t false \t false \t false \t true";
                        actualResult = "untruthful";    
                }
                else {
                    actualResult = "non-spam";
                }
            }
            else if(AutoTestView.jSpamCheckBox.isSelected()
                    || AutoTestView.jNonSpamCheckBox.isSelected())
            {
                if(NonReviewDetection.isNonReview()) {
                    writeResultToFile += "\t true \t - \t - \t -";
                    actualResult = "non-review";                       
                }                
                else {
                    isBrandOnly=BrandOnlyDetection.isBrandOnly();
                    isOffTopic=OffTopicDetection.isOffTopic();
                    if(isBrandOnly && isOffTopic){
                        writeResultToFile += "\t false \t true \t true \t -";
                        actualResult = "both";
                    }   
                    else if(isBrandOnly){
                        writeResultToFile += "\t false \t false \t true \t -";
                        actualResult = "brand-only";    
                    }
                    else if (isOffTopic){
                        writeResultToFile += "\t false \t true \t - \t -";
                        actualResult = "off-topic";    
                    }          
                    else if(UntruthfulDetection.isUntruthful()){
                        writeResultToFile += "\t false \t false \t false \t true";
                        actualResult = "untruthful";    
                    }
                    else {
                        writeResultToFile += "\t false \t false \t false \t false";
                        actualResult = "non-spam";    
                    }
                }
            }
            
            /**
             * Compare expected result with actual result
             * - If two result is the same: increase count...Pass variable by 1 depend on which expected type is testing
             */          
            if(actualResult.equals(expectedResult)) {
                writeResultToFile += "\t" + actualResult + "\t passed";
                switch (expectedResult){
                    case "non-review":   
                        countNonReviewPass++; 
                        countSpamPass++; 
                        break;
                    case "brand-only":   
                        countBrandOnlyPass++; 
                        countSpamPass++; 
                        break;
                    case "off-topic":    
                        countOffTopicPass++; 
                        countSpamPass++; 
                        break;
                    case "untruthful":   
                        countUntruthfulPass++; 
                        countSpamPass++; 
                        break;
                    case "non-spam":     
                        countNonSpamPass++; 
                        break;
                }
                writeResultToResultTextArea += actualResult + "\t\tpass\n";
                updateTextArea(writeResultToResultTextArea);
            }
            
            /**
             * Conflict between brand-only and off-topic
             * - if expected result is brand-only: brand-only test case pass
             * - if expected result is off-topic: off-topic test case pass
             */
            else if(actualResult.equals("both")){   
                writeResultToFile += "\t" + actualResult + "\t passed";
                switch (expectedResult){                        
                    case "brand-only":   
                        countBrandOnlyPass++;                     
                        countSpamPass++;
                        break;
                    case "off-topic":    
                        countOffTopicPass++; 
                        countSpamPass++;
                        break; 
                }
                writeResultToResultTextArea += actualResult + "\t\tpass\n";
                updateTextArea(writeResultToResultTextArea);
            }
            
            /**
             * If two result is different -> fail
             */
            else {
                writeResultToFile += "\t" + actualResult + "\t fail";          
                writeResultToResultTextArea += actualResult + "\t\tfail\n";
                updateTextArea(writeResultToResultTextArea);
            }
            pw.println(writeResultToFile);
        }
        Logging.stop(); // Close print writer for writing to file text
        OpinionSpamView.autoTestFlag = false;
    } 
    
    /**
     * Handler for button Browse Input: show open dialog and user choose file dataset for testing
     */
    public void browseInputButtonHandler () {
        /**
         * Process choosing file dataset
         */
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.showOpenDialog(null);
        
        /**
         * Show directory of chosen file to text field
         */
        AutoTestView.jInputTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        AutoTestView.jOutputTextField.setText(fileChooser.getSelectedFile().getParent() + "\\result.txt");
    }
    
    /**
     * Handler for button Browse Output: show open dialog and user choose directory for file result
     */
    public void browseOutputButtonHandler () {
        /**
         * Process choosing directory for file result
         */
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(null);
        
        /**
         * Show chosen directory of file result to text field
         */
        AutoTestView.jOutputTextField.setText(fileChooser.getSelectedFile().getAbsolutePath() + "\\result.txt");
    }
    
    /**
     * Handler for test Button. Begin auto test
     */
    public void testButtonHandler () {
        try {
            /**
             * Determine which type of review user chose
             * - If user choose spam, non-review, brand-only, off-topic, untruthful for checking -> testSpam = true
             * - If user choose non-spam for checking -> testNonSpam = true 
             */   
            AutoTestView.jResultTextArea.removeAll();
            if((AutoTestView.jSpamCheckBox.isSelected()
                    || AutoTestView.jNonSpamCheckBox.isSelected()) 
                    && (AutoTestView.jNonReviewCheckBox.isSelected()
                    || AutoTestView.jBrandOnlyCheckBox.isSelected()
                    || AutoTestView.jOffTopicCheckBox.isSelected()
                    || AutoTestView.jUntruthfulCheckBox.isSelected())) {
                AutoTestView.jResultTextArea.setText("Please do not select both main type and sub type for checking at same time");
                return;
            }
            
            if(AutoTestView.jNonReviewCheckBox.isSelected()) {
                testNonReview = 1;
            }
            if(AutoTestView.jBrandOnlyCheckBox.isSelected()) {
                testBrandOnly = 1;
            }
            if(AutoTestView.jOffTopicCheckBox.isSelected()) {
                testOffTopic = 1;
            }
            if(AutoTestView.jUntruthfulCheckBox.isSelected()) {
                testUntruthful = 1;
            }
            
            if((testNonReview
                    + testBrandOnly
                    + testOffTopic
                    + testUntruthful) > 1){
                AutoTestView.jResultTextArea.setText("Please select only one type of sub type");
                return;
            }
            
            ////////////////////////////////////////////////////////////////////
            
            if(AutoTestView.jSpamCheckBox.isSelected()) {
                testSpam = true;
            }
            if(AutoTestView.jNonSpamCheckBox.isSelected()) {
                testNonSpam = true;
            }
            
            ////////////////////////////////////////////////////////////////////
            
            modeSubType = "";
            
            if(AutoTestView.jNonReviewCheckBox.isSelected()) {
                modeSubType = "non-review";
            }
            else if(AutoTestView.jBrandOnlyCheckBox.isSelected()) {
                modeSubType = "brand-only";
            }
            else if(AutoTestView.jOffTopicCheckBox.isSelected()) {    
                modeSubType = "off-topic";
            }
            else if(AutoTestView.jUntruthfulCheckBox.isSelected()) {
                modeSubType = "untruthful";
            }
            
            if(!modeSubType.equals("")) {
                testSpam = true;
                testNonSpam = true;
                
                SPAM = 100;
                NON_SPAM = 100;
                
                switch (modeSubType) {             
                    case "non-review":
                        NON_SPAM_BEGIN_INDEX = 1;
                        NON_SPAM_END_INDEX = 100;
                        break;
                    case "brand-only":
                        NON_SPAM_BEGIN_INDEX = 101;
                        NON_SPAM_END_INDEX = 200;                        
                        break;
                    case "off-topic":
                        NON_SPAM_BEGIN_INDEX = 201;
                        NON_SPAM_END_INDEX = 300;                        
                        break;
                    case "untruthful":
                        NON_SPAM_BEGIN_INDEX = 301;
                        NON_SPAM_END_INDEX = 400;                        
                        break;
                }
            }
            
            /**
             * Process file result: open file, create print writer in order to write result to file text
             */
            Logging.initLogger(); 
            File result = new File(AutoTestView.jOutputTextField.getText());
            FileOutputStream fos = new FileOutputStream(result, false);
            PrintWriter pw = new PrintWriter(fos);
            
            
             /**
             * Write to file text and Result Text Area
             */        
            writeResultToFile = "****************************************************************************************** \n";
            writeResultToFile += " OPINION SPAM DETECTING SYSTEM \n TESTING RESULT \n DESIGNED BY LONGNH & NGHIAPHT \n UNIVERSITY OF TECHNOLOGY \n";
            writeResultToFile += "******************************************************************************************\n\n";
            pw.println(writeResultToFile);
                   
            AutoTestView.jResultTextArea.setText(""); // Reset Text Area before writing          
            writeResultToResultTextArea = writeResultToFile;
            updateTextArea(writeResultToResultTextArea);

            /**
             * Read file dataset
             */
            File file = new File(AutoTestView.jInputTextField.getText());
            
            progressRect = AutoTestView.jProgressBarAutoTest.getBounds();
            progressRect.x = 0;
            progressRect.y = 0;
        
            // Show progress bar
            AutoTestView.jProgressBarAutoTest.setStringPainted(true); // Set value for progrss bar update value immediately
            AutoTestView.jProgressBarAutoTest.setValue(0); // Set value for progress bar
            
            // Init for progress bar
            currentReview = 0;
            totalReview = countTotalReview();
            
            /**
             * - If user choose test spam or test non spam or both -> begin auto test
             * - If user don't choose any type of review -> program will not process anymore
             */
            if(testSpam || testNonSpam) {         
                /**
                 * Link to excel file (file dataset)
                 */
                Workbook wb = Workbook.getWorkbook(file);

                if(testSpam) {                    
                    Sheet sheetSpam = wb.getSheet(0); // Spam review is at sheet 0
                    
                    /**
                     * init count...Pass variable for calculate system performance
                     */   
                    countSpamPass = countNonSpamPass = 0;
                    countNonReviewPass = countBrandOnlyPass = countOffTopicPass = countUntruthfulPass = 0;
                    
                    /**
                    * Write to file text and Result Text Area
                    */
                    writeResultToFile = "*******************************\n";
                    writeResultToFile += "TEST SPAM \n";
                    writeResultToFile += "*******************************\n";
                    writeResultToFile += "NUM \t PRODUCT TYPE \t EXPECTED TYPE \t CONDITION1 \t CONDITION2 \t CONDITION3 \t CONDITION4 \t ACTUAL TYPE \t RESULT";
                    pw.println(writeResultToFile);
                    
                    writeResultToResultTextArea = "TEST SPAM\n";
                    writeResultToResultTextArea += "Num \t           Product Type \tExpect Result \t\tActual Result \t\tResult\n";
                    writeResultToResultTextArea += "----------------------------------------------------------------------------------------------------------------------------------------------------------------------\n";
                    updateTextArea(writeResultToResultTextArea);
                    
                    /**
                     * Run auto test for spam types that user chose
                     */
                    if(AutoTestView.jSpamCheckBox.isSelected()) {
                        test(sheetSpam, SPAM_BEGIN_INDEX, SPAM_END_INDEX, writeResultToFile, pw);
                    }
                    else {
                        if(AutoTestView.jNonReviewCheckBox.isSelected()) {
                            test(sheetSpam, NON_REVIEW_BEGIN_INDEX, NON_REVIEW_END_INDEX, writeResultToFile, pw);
                        }
                        if(AutoTestView.jBrandOnlyCheckBox.isSelected()) {
                            test(sheetSpam, BRAND_ONLY_BEGIN_INDEX, BRAND_ONLY_END_INDEX, writeResultToFile, pw);
                        }
                        if(AutoTestView.jOffTopicCheckBox.isSelected()) {
                            test(sheetSpam, OFF_TOPIC_BEGIN_INDEX, OFF_TOPIC_END_INDEX, writeResultToFile, pw);
                        }
                        if(AutoTestView.jUntruthfulCheckBox.isSelected()) {
                            test(sheetSpam, UNTRUTHFUL_BEGIN_INDEX, UNTRUTHFUL_END_INDEX, writeResultToFile, pw);
                        }
                    }
                }
                /**
                 * Run auto test for non spam if user chose
                 */
                if(testNonSpam) {
                    Sheet sheetNonSpam = wb.getSheet(1);
                    
                    writeResultToFile = "\n\n*******************************\n";
                    writeResultToFile += "TEST NON SPAM \n";
                    writeResultToFile += "*******************************\n";
                    writeResultToFile += "NUM \t PRODUCT TYPE \t EXPECTED TYPE \t CONDITION1 \t CONDITION2 \t CONDITION3 \t CONDITION4 \t ACTUAL TYPE \t RESULT";
                    pw.println(writeResultToFile);
                    
                    writeResultToResultTextArea = "\nTEST NON SPAM\n";
                    writeResultToResultTextArea += "Num \t           Product Type \tExpect Result \t\tActual Result \t\tResult\n";
                    writeResultToResultTextArea += "----------------------------------------------------------------------------------------------------------------------------------------------------------------------\n";
                    updateTextArea(writeResultToResultTextArea);
                    
                    test(sheetNonSpam, NON_SPAM_BEGIN_INDEX, NON_SPAM_END_INDEX, writeResultToFile, pw);
                }
                                               
                DecimalFormat df = new DecimalFormat("#.###"); // Show 3 number after .
                
                 /**
                 * Finish testing, print out result
                 */
                if(AutoTestView.jSpamCheckBox.isSelected()
                    || AutoTestView.jNonReviewCheckBox.isSelected()
                    || AutoTestView.jBrandOnlyCheckBox.isSelected()
                    || AutoTestView.jOffTopicCheckBox.isSelected()
                    || AutoTestView.jUntruthfulCheckBox.isSelected()) {
                    writeResultToFile = "\n\n*******************************\n";
                    writeResultToFile += "Statistic Spam:\n";
                    writeResultToFile += "*******************************\n";
                    if(AutoTestView.jNonReviewCheckBox.isSelected() || AutoTestView.jSpamCheckBox.isSelected()) {
                        ratio = (countNonReviewPass/NON_REVIEW);
                        writeResultToFile += "Type1 (non-review): \t" + NON_REVIEW + " testcases\t              " + countNonReviewPass + " passed\t"
                            + (NON_REVIEW-countNonReviewPass) + " failed\t          P = " + df.format(ratio) + "\n";
                    }
                    if(AutoTestView.jBrandOnlyCheckBox.isSelected() || AutoTestView.jSpamCheckBox.isSelected()) {
                        ratio = (countBrandOnlyPass/BRAND_ONLY);
                        writeResultToFile += "Type2 (brand-only): \t" +  BRAND_ONLY + " testcases\t              " + countBrandOnlyPass + " passed\t"
                            + (BRAND_ONLY-countBrandOnlyPass) + " failed\t          P = " + df.format(ratio) + "\n";
                    }
                    if(AutoTestView.jOffTopicCheckBox.isSelected() || AutoTestView.jSpamCheckBox.isSelected()) {
                        ratio = (countOffTopicPass/OFF_TOPIC);
                        writeResultToFile += "Type3 (off-topic): \t" + OFF_TOPIC + " testcases\t              " + countOffTopicPass + " passed\t"
                            + (OFF_TOPIC-countOffTopicPass) + " failed\t          P = " + df.format(ratio) + "\n";
                    }
                    if(AutoTestView.jUntruthfulCheckBox.isSelected() || AutoTestView.jSpamCheckBox.isSelected()) {
                        ratio = (countUntruthfulPass/UNTRUTHFUL);
                        writeResultToFile += "Type4 (untruthful): \t" + UNTRUTHFUL + " testcases\t              " + countUntruthfulPass + " passed\t"
                            + (UNTRUTHFUL-countUntruthfulPass) + " failed\t          P = " + df.format(ratio) + "\n";
                    }
                    if(AutoTestView.jSpamCheckBox.isSelected()) {
                        ratio = (countSpamPass/SPAM);
                        writeResultToFile += "All Type: \t\t" + SPAM + " testcases\t              " + countSpamPass + " passed\t" 
                                + (SPAM-countSpamPass) + " failed\t          P = " + df.format(ratio) + "\n";
                    }
                    pw.println(writeResultToFile);  
                    
                    writeResultToResultTextArea = writeResultToFile;
                    updateTextArea(writeResultToResultTextArea);
                }
                if (testNonSpam) {
                    writeResultToFile = "\n\n*******************************\n";
                    writeResultToFile += "Statistic Non Spam:\n";
                    writeResultToFile += "*******************************\n";
                    ratio = (countNonSpamPass/NON_SPAM);
                    writeResultToFile += "All Type: \t\t" + NON_SPAM + " testcases\t              " + countNonSpamPass + " passed\t" 
                            + (NON_SPAM-countNonSpamPass) + " failed\t          P = " + df.format(ratio) + "\n";             
                    pw.println(writeResultToFile);
                    
                    writeResultToResultTextArea = writeResultToFile;
                    updateTextArea(writeResultToResultTextArea);
                }    
                testSpam = false; // Reset variable for next run
                testNonSpam = false;
                pw.close();
            }  
        }             
        catch (IOException | BiffException | IndexOutOfBoundsException ex) {
            Logger.getLogger(AutoTestHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
