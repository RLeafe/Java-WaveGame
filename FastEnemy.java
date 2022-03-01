import javafx.scene.paint.*;
import javafx.geometry.Point3D;
import javafx.geometry.BoundingBox;
import java.util.Random;
import java.lang.Math;

/**
 * NPE (Non Playable Entity).
 * FastEnemy is the most common type of enemy that spawns in the game.
 * This class controls how much damage the enemy can do, what color it flashes when damage is taken and how much
 * damage it can do.
 */
public class FastEnemy extends GameObject
{
    private Handler handler;
    private GameObject player;
    
    // Used to manipulate given code.
    private Color color;
    private double w,h,d;
    private double health;
    /**
     * Stops the attack from going over 12 for the fast enemy
     */
    private double defaultATK = 12;
    private double speed = 0.85;
    
    // Used to flash enemy color when dmg is taken
    private boolean flashColor;
    
    // Used to display how long the Obj is in the world for
    private double alpha = 1;
    private double flashTime = 0.8;
    
    Random rand = new Random();
    private int range = 25 - 10 + 1;
    
    /**
       * Create the enemy with given variables from Game and Spawn
       * @param enemy initializers
    */
    protected FastEnemy(ID id, double w, double h, double d, Color color, double x, double y, double z, Handler handler){
        super(id, w, h, d, color, x, y, z);
        
        this.handler = handler;
        this.color = color;
        this.w = w;
        this.h = h;
        this.d = d;
        
        this.health = rand.nextInt(range) + 2;
        
        // setting the HP & attack
        setHP(health);
        
        // Running through the array and setting the private player equal to the actual player
        for(int i=0; i < handler.object.size(); i++){
            if(handler.object.get(i).getID() == ID.Player){ player = handler.object.get(i); }
        }
        
        posX = x;
        posY = y;
        posZ = z;
        
        Debug.trace("Enemy: " + id + " HP: " + getHP());
    }
    
    // Information in here is processed every millisecond
    /**
     * Updates the enemy tick when called through the process loop.
     */
    protected void tick(){
        posX += velX;
        posY += velY;
        posZ += velZ;
        
        // Setting and checking the attack
        atk = 2 * Spawn.waveCounter;
        /**
         * Attack is increased every wave to make it harder for the player
         */
        if(atk >= defaultATK){
            setATK(defaultATK);
        }else if(atk <= 0){
            setATK(4);
        }
        //Debug.trace("EE::atk-" + getATK());
        
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
        velX = (double) ( (-speed/dis) * diffX );
        velZ = (double) ( (-speed/dis) * diffZ );
        
        // updating the position of the enemy
        Point3D nextPos = new Point3D(posX,posY,posZ);
        set(nextPos);
        //Debug.trace("Pos: " + nextPos);
        //Debug.trace("B-box: " + getBounds());
        
        // Checks and updates the Collision method
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
            Hud.score = Hud.score += 50;
        }
        
        // move the enemy side to side
        //if(posX <= -5 || posX >= 5){ velX *= -1; };
    }
    
    // Basic collision of the enemy
    /**
       * Updates every (tick) loop
       * @param check collision occurance
       * @return Collisions
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
            
            // Checks collision with other enemies
            if(tempObj.getID() == ID.EasyEnemy || tempObj.getID() == ID.SlowEnemy){
                if(this.isColliding(tempObj)){
                    if(velX > 0){ // Right collision
                        velX = 0;
                        tempObj.velX = 0;
                        posX = tempObj.getPosX() - 2;
                    }else if(velX < 0){ // Left Collision
                        velX = 0;
                        tempObj.velX = 0;
                        posX = tempObj.getPosX() + 2;
                    }
                    if(velZ > 0){ // Backwards collision
                        velZ = 0;
                        tempObj.velZ = 0;
                        posZ = tempObj.getPosZ() - 2;
                    }else if(velZ < 0){ // Forwards collision
                        velZ = 0;
                        tempObj.velZ = 0;
                        posZ = tempObj.getPosZ() + 2;
                    }
                }
            }
            
            // Collision with the bullet & bomb particles
            if(tempObj.getID() == ID.Bullet){
                if(this.isColliding(tempObj)){ hp -= tempObj.getDMG(); flashColor = true; }
            }
            // Take damage when colliding with particles
            if(tempObj.getID() == ID.Particles){ 
                if(this.isColliding(tempObj)){ flashColor = true; hp = hp - getATK() * 5; }
            }
        }
    }
    
    // Set position and size of the BoundingBox
    protected BoundingBox getBounds(){
        return new BoundingBox(posX, posY, posZ, w, h, d);
    }
}
