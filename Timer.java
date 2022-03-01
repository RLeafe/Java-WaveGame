

/**
 * Used to make multiple timers instead of re-creating them every time they are needed
 * 
 * @author Richard Healey
 * @version v1.0_Alpha
 */
public class Timer
{
    protected Handler handler;
    protected int eventToken = 0;
    private int time = 0, countDown, id, endTime = 20, wait = 50;
    private boolean isRunning = true, destroy = false, loopTimer = false, countedDown;
    
    // Used a simple int Id to access desired timer instead of enum array to save myself time for handin
    /**
     * Timer initalize information needed when new timer is created
     * @param Timer initializers
    */
    protected Timer(int id, int countDown, Handler handler){
        this.handler = handler;
        this.countDown = countDown;
        this.id = id;
    }
    
    // Information in here is processed every millisecond
    /**
     * Updates the timer tick when called through the process loop.
     * @param updates through the process loop
     */
    protected void tick(){
        if(time <= 0){ countedDown = false; }
        
        if(countDown > 0){ counter(); }
        
        // Remove from the handler if destroyed
        if(destroy){ handler.removeTimer(this); }
        
        // Remove the timer after 5 seconds on inactivity
        if(!isRunning){ wait--; }
        if(wait <= 0){ handler.removeTimer(this); }
        
        //Debug.trace("Timer::counter -" + counter);
    }
    
    // Acual counter
    /**
     * Actual timer, can be used multiple times
     * @param add to time until it reaches countDown
     * @return loopTimer if set
     * @retrun countedDown to check if timer has ended
    */
    protected void counter(){
        time++;
        //Debug.trace("Timer::countDown -" + time);
        if(time >= countDown){
            countedDown = true;
            if(loopTimer){ time = 0; }
            else{ isRunning = false; }
        }
    }
    
    protected void setLoopTimer(boolean l){ this.loopTimer = l; }
    protected void setDestroy(boolean d){ this.destroy = d; }
    protected void setCount(int c){ this.countDown = c; }
    protected void setID(int id){ this.id = id; }
    
    protected boolean getCountedDown(){ return countedDown; }
    protected boolean getLoopTimer(){ return loopTimer; }
    protected boolean getIsRunning(){ return isRunning; }
    protected int getEventToken(){ return eventToken; }
    protected int getId(){ return id; }
    protected int getCount(){ return countDown; }
    protected int getTime(){ return time; }
}
