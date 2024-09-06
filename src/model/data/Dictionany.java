/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author LongNguyen & NghiaPham
 */
public class Dictionany {
    
    // dictionary of words used for algorithm
    private static ArrayList<String> nonExtremeWord = new ArrayList(); 
    private static ArrayList<String> unusualPattern = new ArrayList();    
    
    // dictionary of opinion words used for algorithm
    private static ArrayList<String> positiveWord = new ArrayList(); 
    private static ArrayList<String> negativeWord = new ArrayList();
    
    // return the dictionary of NonExtremeWord
    public static  ArrayList<String> getNonExtremeWord() {
        return nonExtremeWord;
    }
    
    // return the dictionary of UnusualPattern
    public static  ArrayList<String> getUnusualPattern() {
        return unusualPattern;
    } 
    
    // return the dictionary of PositiveWord
    public static  ArrayList<String> getPositiveWord() {
        return positiveWord;
    }
    
    // return the dictionary of NegativeWord
    public static  ArrayList<String> getNegativeWord() {
        return negativeWord;
    }

    // parse dictionary files and init nonextreme words
    public static void initNonExtremeWord () { 
        nonExtremeWord.clear();
        try { // đọc file nonExtremeWord.txt
            FileInputStream fstream = new FileInputStream("dictionary/nonExtremeWord.txt");
            try (DataInputStream in = new DataInputStream(fstream)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if(!strLine.equals("")) {
                        nonExtremeWord.add(strLine);
                    }
                }
            }
        } catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }       
    }
    
    // parse dictionary files and init unusual patterns
    public static void initUnusualPattern(){
        unusualPattern.clear();
        try { // đọc file unusualPattern.txt
            FileInputStream fstream = new FileInputStream("dictionary/unusualPattern.txt");
            try (DataInputStream in = new DataInputStream(fstream)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if(!strLine.equals("")) {
                        unusualPattern.add(strLine);
                    }
                }
            }            
        } catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }       
    }
    
    // parse dictionary files and init nonextreme words
    public static void initPositiveWord () { 
        positiveWord.clear();
        try { // đọc file positiveWord.txt
            FileInputStream fstream = new FileInputStream("dictionary/opinionWord/positiveWord.txt");
            try (DataInputStream in = new DataInputStream(fstream)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if(!strLine.equals("")) {
                        positiveWord.add(strLine);
                    }
                }
            }
        } catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }       
    }
    
    // parse dictionary files and init unusual patterns
    public static void initNegativeWord(){
        negativeWord.clear();
        try { // đọc file negativeWord.txt
            FileInputStream fstream = new FileInputStream("dictionary/opinionWord/negativeWord.txt");
            try (DataInputStream in = new DataInputStream(fstream)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if(!strLine.equals("")) {
                        negativeWord.add(strLine);
                    }
                }
            }            
        } catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }       
    }
    
    // init all sentiment word
    public static void initSentimentWord() {
        initPositiveWord();
        initNegativeWord();
    }
}
