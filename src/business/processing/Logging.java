package business.processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import view.OpinionSpamView;

/**
 * This class defines all methods used to log message during the runtime.
 * @author LongNguyen & NghiaPham
 */

public class Logging {    

    public static String SEPERATOR2 = "\n-----------------------------------\n";
    public static String SEPERATOR3 = "\n************************************"
                                        + "************************\n";
    public static String SEPERATOR1 = "\n\n==================================="
                                        + "==================================="
                                        + "=================================\n";
    
    public static File logFile;
    public static FileOutputStream fos;
    public static PrintWriter pw;
    
    // init a logger file and its writer
    public static void initLogger() {      
        if(OpinionSpamView.testAReviewFlag) {
            logFile = new File("./log/testAReview.log");
        }
        else if(OpinionSpamView.autoTestFlag) {
            logFile = new File("./log/autoTest.log");
        }
        
        try {
            fos = new FileOutputStream(logFile, false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Logging.class.getName()).log(Level.SEVERE, null, ex);
        }
        pw = new PrintWriter(fos);           
    }
    
    public static void stop(){
        pw.close();
    }
    
    // print the message string s to logFile
    public static void printLog(Object s)  {         
        pw.print(s);
    }       
    
    // print out posList or entityList
    public static void printList(String[][] l)  {          
        String s ="";
        for(int i=0; i<l.length; i++){
            s += (l[i][0] + "(" + l[i][1] + ") ");            
        }    
        pw.print(s);
    }       
}
