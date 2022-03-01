import java.util.LinkedList;
import javafx.scene.*;

/**
 * Handler is used to update and process any and EVERY GameObject & Timer passed through.
 * 
 * @author Richard Healey and reference
 * @see https://youtu.be/0T1U0kbu1Sk
 * @version v1.0_Alpha
 */
public class Handler
{
    // List every instance of GameObject within the game
    LinkedList<GameObject> object = new LinkedList<GameObject>();
    // Used to list every timer made 
    LinkedList<Timer> times = new LinkedList<Timer>();
    
    // Check if visible
    private boolean isVisible;
    
    // Continuously checks for any objects that may have been updatated
    /**
     * Tick is used to manipulate everything passed through it. This is used in every single class connected
     * to the Game::handler with a tick() method.
     */
    protected void tick(){
        for(int i=0; i < object.size(); i++){
            GameObject tempObj = object.get(i);
            tempObj.tick();
        }
        // Used to create multiple different timers
        for(int t=0; t < times.size(); t++){
            Timer timeObj = times.get(t);
            timeObj.tick();
        }
    }
    
    // Add or Remove every GameObject into/from a group to be shown in the 3D scene
    /**
     * Will update with the Game::tick() and add or remove a GameObject
     */
    protected void updateGroup(Group g){
        for(int i=0; i < object.size(); i++){
            GameObject tempObj = object.get(i);
            
            // Checker to stop identical duplication
            if(!tempObj.getInGroup() && isVisible){
                g.getChildren().add(tempObj);
                tempObj.setInGroup(true);
            }
            
            if(tempObj.getIsColliding() == true){
                g.getChildren().remove(tempObj);
                removeObj(tempObj);
                tempObj.setInGroup(false);
                //Debug.trace("is colliding: " + tempObj.getIsColliding());
            }
        }
    }
    
    // Add a timer to the times list
    protected void addTimer(Timer time){ this.times.add(time); }
    // Remove a timer from the times list
    protected void removeTimer(Timer time){ this.times.remove(time); }
    
    // Add a game object to the objects list
    protected void addObj(GameObject obj){ this.object.add(obj); this.isVisible = true; }
    
    // Remove a game object from the objects list
    protected void removeObj(GameObject obj){ this.object.remove(obj); this.isVisible = false; }
    
}
