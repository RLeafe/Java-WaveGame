import java.awt.event.KeyAdapter;
import javafx.scene.input.KeyEvent;
import javafx.geometry.Point3D;

/**
 * Controller is used to control the player movement and shooting. It also handles the 'gravity' for every GameObject.
 * 
 * @author Richard Healey
 * @version v1.0_Alpha
 */

public class Controller
{
    protected Handler handler;
    protected View view;
    protected Game game;
    protected Hud hud;
    protected Spawn spawn;
    protected Menu menu;
    protected Death death;
    
    // Used to remove complete stop when multiple keys are pressed
    protected boolean[] keyPressed = new boolean[4];
    private double acel = 0.5, dcel = 0.25, maxSpeed = 0.5, fallSpeed = 1.5;
    
    public Controller(Handler handler){
        this.handler = handler;
        Debug.trace("Controller: <constructor>");
        
        keyPressed[0] = false;
        keyPressed[1] = false;
        keyPressed[2] = false;
        keyPressed[3] = false;
    }
    
    // Key Pressed
    public void getPressed(KeyEvent event){
        // print a debugging message to show a key has been pressed
        //Debug.trace("Control_Pressed: " + event.getCode() );
        
        for(int i=0; i < handler.object.size(); i++){
            GameObject tempObj = handler.object.get(i);
            
            if(tempObj.getID() == ID.Player){
                switch (event.getCode()){
                    // Player movement
                    case W: keyPressed[0]=true; break;
                    case A: keyPressed[1]=true; break;
                    case S: keyPressed[2]=true; break;
                    case D: keyPressed[3]=true; break;
                    
                    // GodMode (cannot die).
                    case G: hud.GodMode = true; break;
                    
                    // NormalMode (player can die again).
                    case N: hud.GodMode = false; break;
                    
                    // Player shooting
                    case UP: tempObj.setFront(true); break;
                    case DOWN: tempObj.setBack(true); break;
                    case LEFT: tempObj.setLeft(true); break;
                    case RIGHT: tempObj.setRight(true); break;
                }
            }
        }
    };
    
    // Key Released
    public void getReleased(KeyEvent event){
        // print a debugging message to show a key has been pressed
        //Debug.trace("Control_Released: " + event.getCode() );
        
        for(int i=0; i < handler.object.size(); i++){
            GameObject tempObj = handler.object.get(i);
            
            if(tempObj.getID() == ID.Player){
                switch (event.getCode()){
                    // stop player movement
                    case W: keyPressed[0]=false; break;
                    case A: keyPressed[1]=false; break;
                    case S: keyPressed[2]=false; break;
                    case D: keyPressed[3]=false; break;
                    
                    // stop player shooting.
                    // set player can shoot to true once they release the key
                    case UP: tempObj.setFront(false); tempObj.setCanShoot(true); break;
                    case DOWN: tempObj.setBack(false); tempObj.setCanShoot(true); break;
                    case LEFT: tempObj.setLeft(false); tempObj.setCanShoot(true); break;
                    case RIGHT: tempObj.setRight(false); tempObj.setCanShoot(true); break;
                }
            }
        }
    }
    
    /**
     * Updates the controller when called.
     */
    protected void tick(){
        for(int i=0; i< handler.object.size(); i++){
            GameObject tempObj = handler.object.get(i);
            
            // Set almost every object to fall automatically
            if(tempObj.getID() != ID.LevelOne && tempObj.canDie){
                if(tempObj.getIsFalling()){
                    tempObj.velY = fallSpeed;
                }else{
                    tempObj.velY = 0;
                }
                // make anything with health take damage while falling into the 'void'
                if(tempObj.posY >= 5){ tempObj.hp = tempObj.hp - 14; }
            }
            
            // Player movement speed and velocity
            if(tempObj.getID() == ID.Player){
                // make the player take damage while falling in the 'void'
                if(tempObj.posY >= 5){ Hud.health = Hud.health - 14; }
                
                // make sure the player doesnt go too much over the maximum speed
                if(tempObj.velZ > maxSpeed){ tempObj.velZ = maxSpeed; }
                else if(tempObj.velZ < -maxSpeed){ tempObj.velZ = -maxSpeed; }
                if(tempObj.velX > maxSpeed){ tempObj.velX = maxSpeed; }
                else if(tempObj.velX < -maxSpeed){ tempObj.velX = -maxSpeed; }
            
                // forwards and backwards movements
                if(keyPressed[0]){ tempObj.velZ += acel; }
                    else if(keyPressed[2]){ tempObj.velZ -= acel; }
                    else if(!keyPressed[0] && !keyPressed[2]){
                        
                    if(tempObj.velZ > 0){ tempObj.velZ -= dcel; }
                    else if(tempObj.velZ < 0){ tempObj.velZ += dcel; }
                }
                
                // left and right movements
                if(keyPressed[1]){ tempObj.velX -= acel; }
                    else if(keyPressed[3]){ tempObj.velX += acel; }
                    else if(!keyPressed[1] && !keyPressed[3]){
                        
                    if(tempObj.velX > 0){ tempObj.velX -= dcel; }
                    else if(tempObj.velX < 0){ tempObj.velX += dcel; }
                }
            }
        }
    }
}