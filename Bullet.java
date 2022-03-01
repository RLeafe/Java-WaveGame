import javafx.scene.paint.Color;
import javafx.geometry.Point3D;
import javafx.geometry.BoundingBox;
import java.util.Random;

/**
 * The bullet class is used to determin what happens when a bullet is added/removed to/from the handler,
 * how long it can stay in the game for and how fast it can travel.
 * 
 * @author Richard Healey
 * @version v1.0_Alpha
 */
public class Bullet extends GameObject
{
    private Handler handler;
    private Hud hud;
    private GameObject player;
    
    private double w,h,d;
    // Used to display how long the Obj is in the world for
    private double life = 0.5;
    
    private Random rand = new Random();
    
    protected Bullet(ID id, double w, double h, double d, Color color, double x, double y, double z, Handler handler){
        super(id, w, h, d, color, x, y, z);
        
        // Used to be manipulated in this class
        this.handler = handler;
        this.w = w;
        this.h = h;
        this.d = d;
        
        setAlpha(1);
        
        // Running through the array and setting the private player equal to the actual player
        for(int i=0; i < handler.object.size(); i++){
            if(handler.object.get(i).getID() == ID.Player){ player = handler.object.get(i); }
        }
        
        // Set the damage of each bullet
        setDMG(6);
        
        // setting the speed of the bullet
        velX = 1.25;
        velZ = 1.25;
        
        posX = x;
        posY = y;
        posZ = z;
    }
    
    // Information in here is processed every millisecond
    /**
     * Updates the bullet when called through the process loop.
     */
    protected void tick(){
        // Destroy the bullet after this loop has been completed.
        // Sets the player can shoot to false to stop invisible bullets
        if(alpha >= life){
            alpha -= (life - 0.004);
        }else{
            player.setCanShoot(false);
            this.setIsColliding(true);
        }
    }
    
    protected BoundingBox getBounds(){
        return new BoundingBox(posX, posY, posZ, w,h,d);
    }
}
