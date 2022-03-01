import javafx.scene.paint.*;
import javafx.geometry.Point3D;
import javafx.geometry.BoundingBox;
import java.util.Random;
import java.lang.Math;

/**
 * NPE (Non Playable Entity).
 * SlimeBoss is the most common type of enemy that spawns in the game.
 * This class controls how much damage the enemy can do, what color it flashes when damage is taken and how much
 * damage it can do.
 */
public class SlimeBoss extends GameObject
{
    private Handler handler;
    private Spawn spawn;
    private GameObject player;
    
    // Used to manipulate given code.
    private Color color;
    private double w,h,d;
    private double health;
    
    // Timers for the boss
    private int timer = 30;
    
    // flashColor is used to flash enemy color when dmg is taken
    private boolean flashColor, canMove = false;
    
    // Used to display how long the Obj is in the world for
    private double alpha = 1;
    private double flashTime = 0.8;
    
    // Random vars to be used
    private Random rand = new Random();
    private int range = 800 - 500 + 500;
    
    /**
       * Create the boss with given variables from Spawn
       * @param boss initializers
    */
    protected SlimeBoss(ID id, double w, double h, double d, Color color, double x, double y, double z, Handler handler){
        super(id, w, h, d, color, x, y, z);
        
        this.handler = handler;
        this.color = color;
        this.w = w;
        this.h = h;
        this.d = d;
        
        /**
         * Health is increased every wave to make it harder for the player
         */
        this.health = rand.nextInt(range) + 100 * Hud.wave;
        
        // setting the HP & attack
        setHP(health);
        setATK(6);
        
        posX = x;
        posY = y;
        posZ = z;
        
        // Running through the array and setting the private player equal to the actual player
        for(int i=0; i < handler.object.size(); i++){
            if(handler.object.get(i).getID() == ID.Player){ player = handler.object.get(i); }
        }
        
        Debug.trace("Enemy: " + id + " HP: " + getHP());
    }
    
    // Information in here is processed every millisecond
    /**
       * Updates every (tick) loop
       * @param check collision occurance
       * @return Collisions
    */
    protected void tick(){
        posX += velX;
        posY += velY;
        posZ += velZ;
        
        // Velocity is 0 until the timer is 0, gives the chance for player to prepare
        if(timer <= 0){ canMove = true; }
        timer--;
        
        if(canMove){
            // The basic enemy will always follow the centre of the player and
            // change the direction of the enemy depending on where the player is.
            /**
             * Player following code used and modified from tutorials
             * @author RealTutsGML
             * @see https://youtu.be/JrSjwQbTldg?t=592
             */
            double diffX = posX - player.getPosX();
            double diffZ = posZ - player.getPosZ();
            double dis = (double) Math.sqrt((posX-player.getPosX()) * (posX-player.getPosX()) + (posZ-player.getPosZ())*(posZ-player.getPosZ()));
            
            // Changing the speed of the enemy
            velX = (double) ( (-0.5/dis) * diffX );
            velZ = (double) ( (-0.5/dis) * diffZ );
        }
        
        // updating the position of the enemy
        Point3D nextPos = new Point3D(posX,posY,posZ);
        set(nextPos);
        
        //Debug.trace("boss-" + this.getBounds());
        
        collision();
        
        // flash the enemy color if the enemy has taken damage.
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
        
        // Destroy the enemy if it has no health.
        if(getHP() <= 0){
            this.setIsColliding(true);
            Hud.score = Hud.score += 125 * Hud.wave;
        }
        //if(posX <= -5 || posX >= 5){ velX *= -1; };
    }
    
    // Basic collision of the boss
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
            
            /* This is the old collision
            if(tempObj.getID() == ID.Player){
                if(getBounds().intersects(tempObj.getBounds())){
                    Debug.trace("Old collision");
                    setVelX(0);
                    setVelZ(0);
                    setVelY(0);
                }
            }
            */
           
            // Collision with the bullet & bomb particles
            if(tempObj.getID() == ID.Bullet){
                if(this.isColliding(tempObj)){ hp -= tempObj.getDMG(); flashColor = true; }
            }
            // Take damage when colliding with particles
            if(tempObj.getID() == ID.Particles){ 
                if(this.isColliding(tempObj)){ hp = hp - tempObj.getATK(); flashColor = true; }
                //Debug.trace("boss hp-" + getHP());
            }
        }
    }
    
    protected BoundingBox getBounds(){
        return new BoundingBox(posX, posY-2, posZ, w, h, d);
    }
}
