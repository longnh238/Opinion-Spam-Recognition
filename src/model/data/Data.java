package model.data;

/**
 * This class defines all static data used for the application
 * @author LongNguyen & NghiaPham
 */


public class Data {
    private static String productType;          // The type of the product being reviewed
    private static String productName;          // The name of the product being reviewed
    private static String reviewContent;        // The content of the review
    private static String [][] posList;         // List of all POSs tagged from the review
    private static String [][] entityList;      // List of all entities of the review
    private static String [][] sentenceList;    // List of all sentences of the review
    private static int nPOS;                    // Number of POSs in review
    private static int nEntities;               // Number of entities in review
    private static int nSentences;              // Number of sentences in review
    
    // return the product type
    public static String getProductType () { 
        return productType;
    }
    
    // return the product name
    public static String getProductName () { 
        return productName;
    }
    
    // return the content of the review
    public static String getReviewContent () { 
        return reviewContent;
    }
    
    // return the list of all POSs tagged from the review
    public static String [][] getPosList() { 
        return posList;
    }
   
    // return the ist of all entities of the review
    public static String [][] getEntitiesList() { 
        return entityList;        
    }
    
    // return the ist of all sentence of the review
    public static String [][] getSentenceList() { 
        return sentenceList;        
    }
    
    // return the number of POSs
    public static int getNumberOfPOS() { 
        return nPOS;
    }   
    
    // return the number of entities
    public static int getNumberOfEntities() { 
        return nEntities;        
    }    
    
    // return the number of sentences
    public static int getNumberOfSentences() { 
        return nSentences;        
    }     
    
    // assign string value to productType, # used to match the OWL format
    public static void setProductType (String _productType) { 
        productType = "#" + _productType;
    }
    
    // assign string value to productName
    public static void setProductName (String _productName) { 
        productName = _productName;
    }
    
    // assign string value to review content
    public static void setReviewContent (String _reviewContent) { 
        reviewContent = _reviewContent;
    }
    
    // assign value to posList
    public static void setPosList (String [][] _posList) { 
        posList = _posList;
    }

    // assign value to entityList
    public static void setEntityList (String [][] _entityList) { 
        entityList = _entityList;
    }
    
    // assign value to sentenceList
    public static void setSentenceList (String [][] _sentenceList) { 
        sentenceList = _sentenceList;
    }
    
    // assign value to nPOS
    public static void setNumberOfPos (int _nPOS) { 
        nPOS = _nPOS;
    }    
    
    // assign value to nEntities
    public static void setNumberOfEntities (int _nEntities) { 
        nEntities = _nEntities;
    }
    
    // assign value to nSentences
    public static void setNumberOfSentences (int _nSentences) { 
        nSentences = _nSentences;
    }
    
    // init posList
    public static void posListConstructor () { 
        posList = new String [nPOS][2];
    }
    
    // init entityList
    public static void entitiesListConstructor () { 
        entityList = new String [nEntities][2];
    }
    
    // init sentenceList
    public static void sentencesListConstructor () { 
        sentenceList = new String [nSentences][2];
    }
}