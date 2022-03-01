import javafx.scene.paint.*;
import javafx.geometry.Point3D;
import javafx.geometry.BoundingBox;
import java.util.Random;

/**
 * Exploding devices that explodes on player or bullet collision. Deals damage to everything with health and canDie = true.
 *
 * @author Richard Healey
 * @version v1.0_Alpha
 */
public class Bomb extends GameObject
{
    private Handler handler;
    private Color color;
    private double w,h,d,x,y,z;
    
    private boolean isExploding = false, hasExploded = false;
    Random rand = new Random();
    
    // Timers for the bomb
    private boolean isActive = false;
    private double timer = 2;
    
    // Used to flash bomb color when active
    private boolean flashColor;
    private double alpha = 1;
    private double flashTime = 0.8;

    protected Bomb(ID id, double w, double h, double d, Color color, double x, double y, double z, Handler handler){
        super(id, w, h, d, color, x, y, z);
        
        //Debug.trace(id + " spawned...");
        
        this.handler = handler;
        this.color = color;
        this.w = w;
        this.h = h;
        this.d = d;
        this.x = x;
        this.y = y;
        this.z = z;
        
        posX = x;
        posY = y;
        posZ = z;
        
        //Debug.trace("Pos: " + this.pos + " ID: " + getID());
    }
    
    // Information in here is processed every millisecond
    /**
     * Updates the Bomb when called through the process loop.
     */
    protected void tick(){
        posY += velY;
        
        // Updates the position of the bomb
        Point3D nextPos = new Point3D(posX,posY,posZ);
        set(nextPos);
        
        // Checking collision
        collision();
        if(timer <= 1){ explode(); }
        if(timer <= 0){ damageArea(); }
        
        // Check to see if bombEvent = true
        if(bombEvent && !isFalling){ isExploding = true; }
        
        // Checking for explosion
        exploding();
        
        // Handles explotions
        if(hasExploded){
            this.setIsColliding(true);
            reset();
        }
        //if(hasExploded == true){ setIsColliding(true); }
    }
    
    // Resetting the bomb to be used for the bomb event
    protected void reset(){
        alpha = 1;
        timer = 2;
        setPosY(this.y);
        flashTime = 0.8;
        isActive = false;
        isExploding = false;
        flashColor = false;
        hasExploded = false;
        setIsFalling(true);
    }
    
    /**
     * Handles anything colliding and updates it every tick
     */
    protected void collision(){
        for(int i =0; i < handler.object.size(); i++){
            GameObject tempObj = handler.object.get(i);
            
            // Check if colliding with floor or not
            if(tempObj.getID() == ID.Floor){
                if(getBounds().intersects(tempObj.getBounds())){
                    setIsFalling(false);
                }else{ setIsFalling(true); }
            }
            
            if(tempObj.getID() == ID.Player || tempObj.getID() == ID.Bullet){
                if(this.isColliding(tempObj)){
                    // Handles the player touch and exploding
                    isExploding = true;
                }
            }
        }
    }
    
    // Finaly explode, the particle release
    protected void explode(){
        for(int i=0; i<8; i++){
            handler.addObj(new Particles(ID.Particles, 0.5, 0.5, 0.5, Color.WHITE, posX, posY-1, posZ, 0.5, handler));
        }
        
        for(int i=0; i<handler.object.size(); i++){
            GameObject tempObj = handler.object.get(i);
            // randomise the direction of the particles
            if(tempObj.getID() == ID.Particles){
                tempObj.posX += tempObj.velX;
                tempObj.posY += tempObj.velY;
                tempObj.posZ += tempObj.velZ;
                Point3D nextPos = new Point3D(tempObj.posX,tempObj.posY,tempObj.posZ);
                tempObj.set(nextPos);
            }
        }
    }
    
    // the exploding 'animation'
    protected void exploding(){
        if(isExploding){
            timer--;
            isActive = true;
            flashColor = true;
        }
        if(isActive && timer > 0){
            // flash when the player touches the bomb
            if(flashColor == true){
                if(alpha >= flashTime){
                    alpha -= (flashTime - 0.004);
                    setMaterial(new PhongMaterial(Color.RED));
                }else{ flashColor = false; }
            }
            if(flashColor == false){
                setMaterial(new PhongMaterial(color));
                alpha = 1;
            }
        }
    }
    
    // the area of damage if the player doesnt get hit by particles
    protected void damageArea(){
        handler.addObj(new Particles(ID.Particles, 2, 2, 2, Color.GREEN, posX, posY, posZ, 0.5, handler));
        hasExploded = true;
    }
    
    protected BoundingBox getBounds(){
        return new BoundingBox(posX, posY, posZ, w,h,d);
    }
}
