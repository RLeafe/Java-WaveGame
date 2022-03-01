import javafx.scene.paint.Color;
import javafx.geometry.Point3D;
import javafx.geometry.BoundingBox;
import java.util.Random;

/**
 * The particles class must be used to determin what happens when a particle is added/removed to/from the handler
 * and how long it can stay in the game for.
 * 
 * Particles main use is to deploy after Bomb::explode() is called.
 * 
 * @author Richard Healey
 * @version v1.0_Alpha
 */
public class Particles extends GameObject
{
    private Handler handler;
    private double w,h,d,x,y,z;
    private Color color;
    private GameObject player, enemy;
    
    // Used to display how long the Obj is in the world for
    private double life;
    
    // setting rand to new random object
    Random rand = new Random();
    
    protected Particles(ID id, double w, double h, double d, Color color, double x, double y, double z, double life, Handler handler){
        super(id, w, h, d, color, x, y, z);
        
        this.handler = handler;
        this.color = color;
        this.life = life;
        this.w = w;
        this.h = h;
        this.d = d;
        this.x = x;
        this.y = y;
        this.z = z;
        
        // Setting the damage they can do
        setATK(1.5);
        
        posX = x;
        posY = y;
        posZ = z;
    }
    
    /**
     * Updates Particle when called.
     */
    protected void tick(){
        // spread them around in a random position when created
        velX = rand.nextInt(4 + 4) - 4;
        velY = rand.nextInt(4 + 4) - 4;
        velZ = rand.nextInt(4 + 4) - 4;
        
        // Only let the particles live for however long 'life' is set to.
        // destroy them once their life has ended
        if(alpha >= life){
            alpha -= (life - 0.004);
        }else{ this.setIsColliding(true); }
    }
    
    protected BoundingBox getBounds(){
        return new BoundingBox(posX, posY, posZ, w,h,d);
    }
}
