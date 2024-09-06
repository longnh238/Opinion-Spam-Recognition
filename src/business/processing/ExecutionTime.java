package business.processing;

/**
 * This class defines ExecutionTime object that used to measure the time consuming of the application
 * @author LongNguyen & NghiaPham
 */

public class ExecutionTime {  
    
    private long timeStart = 0;
    private long timeStop = 0;
    private long timeExecute = 0;
    
    public void start(){      
        timeStart = System.currentTimeMillis();
    }
    
    public void stop(){
        timeStop = System.currentTimeMillis();  
    }
    
    public long timeExcecute() {
        timeExecute = timeStop - timeStart;
        return timeExecute;
    }
}
