package business.processing;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.WordLemmaTag;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.data.Data;

/**
 * This class defines static methods used for preprocessing modules. Such kind 
 * of modules create necessary info of the review (content, PosList, 
 * entityList) to process the algorithm later.  
 * @author LongNguyen & NghiaPham
 */

public class PreprocessingModule {
    // a string return by Stanford POS tagger after tagging the reviewContent
    private static String reviewPosTagging;
    // Stanford POS tagger object
    private static MaxentTagger tagger;
    // Stanford Parser object
    private static LexicalizedParser lp;
    
    // return the reviewPosTagging
    public static String getReviewPosTagging () { 
        return reviewPosTagging;
    }
    
    // assign value to reviewPosTagging
    public static void setReviewPosTagging (String reviewContent) { 
        reviewPosTagging = posTagging(reviewContent);        
    }
      
    // init a POS Tagger
    public static void initPosTagger () {        
        try {     
            // model english-bidirectional-distsim: high accuracy but highly time consuming 
            // model wsj-0-18-left3words: lower accuracy but quick performance
            tagger = new MaxentTagger("./lib/wsj-0-18-left3words.tagger");
        } 
        catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(PreprocessingModule.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    // init a POS Tagger
    public static void initParser () {        
        // Select model for parsing
        String grammar = "./lib/englishPCFG.ser.gz";
        
        // Set option for parsing
        String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };  
        
        // Create model
        lp = LexicalizedParser.loadModel(grammar, options);  
    }
    
    // tag an input string and return the tagged string that contains all 
    // sub-words following with their POSs
    public static String posTagging (String content) {         
        String tagged = tagger.tagString(content); 
        return tagged;
    }
    
    // tokenize and parse the tagged string to create Data.PosList
    public static void createPosList () { 
        int nEntities = 0; 
        String [] createPosListByBlank = reviewPosTagging.split(" "); 
        Data.setNumberOfPos(createPosListByBlank.length); 
        Data.posListConstructor(); 
        String [][] tempPosList = new String [Data.getNumberOfPOS()][2];         
        for (int i = 0; i < createPosListByBlank.length; i++) { 
            String [] createPosListBySlash = createPosListByBlank[i].split("/"); 
            for (int j = 0; j < createPosListBySlash.length; j++) {
                tempPosList[i][j] = createPosListBySlash[j];
                // count the number of entities existing in PosList
                if(j == 1) {
                    if (createPosListBySlash[j].equals("JJ") 
                    || createPosListBySlash[j].equals("JJR")
                    || createPosListBySlash[j].equals("JJS")
                    || createPosListBySlash[j].equals("NN")
                    || createPosListBySlash[j].equals("NNS")
                    || createPosListBySlash[j].equals("NNP")
                    || createPosListBySlash[j].equals("NNPS")) {
                        nEntities++;
                    }
                    // normalize plural nouns
                    if (createPosListBySlash[j].equals("NN") 
                    || createPosListBySlash[j].equals("NNS")){
                        Morphology mor = new Morphology();
                        WordTag wordTag = new WordTag(createPosListBySlash[0]);
                        WordLemmaTag wordLemmaTag = mor.lemmatize(wordTag);
                        String[] lemmaString = wordLemmaTag.toString().split("/");
                        tempPosList[i][0] = lemmaString[1];
                    }                 
                }
            }
        }        
        Data.setPosList(tempPosList); 
        Data.setNumberOfEntities(nEntities); 
    }
    
    // filter out posList to create entityList
    public static void createEntitiesList () { 
        int nEntities = 0; 
        String [][] tempEntitiesList = new String[Data.getNumberOfEntities()][2];
        Data.entitiesListConstructor();
        for (int i = 0; i < Data.getPosList().length; i++) {
            if (Data.getPosList()[i][1].equals("JJ")
            || Data.getPosList()[i][1].equals("JJR")
            || Data.getPosList()[i][1].equals("JJS")
            || Data.getPosList()[i][1].equals("NN")
            || Data.getPosList()[i][1].equals("NNS")
            || Data.getPosList()[i][1].equals("NNP")
            || Data.getPosList()[i][1].equals("NNPS")) {
                tempEntitiesList[nEntities][0] = Data.getPosList()[i][0]; 
                tempEntitiesList[nEntities][1] = Data.getPosList()[i][1];
                nEntities++;
            }
        }
        Data.setEntityList(tempEntitiesList);       
    }   
    
    // Parse sentence into token
    public static void createSentencesList (String content) {
        // count number of sentence in content
        int countSentence = 0; 
          
        // Convert string content to String reader content in order to parse every sentence
        StringReader reader = new StringReader(content);
        
        // Create temp list to save result
        List<List<? extends HasWord>> tmp = new ArrayList<List<? extends HasWord>>();
        
        // Seperate content into sentence
        DocumentPreprocessor db = new DocumentPreprocessor(reader);
        
        for(List sentence : db) {
            countSentence++;
            tmp.add(sentence);
        }
        
        // assign number of sentence value
        Data.setNumberOfSentences(countSentence);
        
        // init sentenceList
        Data.sentencesListConstructor();
        int i = 0; // show current position of sentence List
        
        Iterable<List<? extends HasWord>> sentences = tmp;
        
        // Parse every sentence in list
        for (List<? extends HasWord> sentence : sentences) {
            Tree parse = lp.apply(sentence);
            Data.getSentenceList()[i][0] = sentence.toString();
            Data.getSentenceList()[i][1] = parse.firstChild().value();
            i++;
        }
    }
}
