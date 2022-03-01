import javafx.scene.*;
import javafx.scene.transform.Translate;
import javafx.scene.paint.Color;
import javafx.geometry.Point3D;
import javafx.geometry.BoundingBox;
import javafx.scene.PointLight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.Random;

/**
 * PE (Playable Entity)
 * This class is for the initialisation of the player. 
 * Which handles everything about the player, including shooting and ammo.
 * 
 * @author Richard Healey
 * @version v1.0_Alpha
 */
public class Player extends GameObject
{
    private Random r = new Random();
    private Handler handler;
    private GameObject enemy;
    private GameObject sceneObj;
    
    // Initialise the close camera
    private PerspectiveCamera camera;
    
    // Used to check which arrowkey has been pressed
    private int dirNum;
    
    // Player ammo check
    protected boolean hasAmmo;
    
    // Get the w,h,d to be manipulated
    private double w,h,d,x,y,z;
    
    /**
       * Create the player with given variables from Game
       * @param player initializers
    */
    protected Player(ID id, double w, double h, double d, Color color, double x, double y, double z, Handler handler, PerspectiveCamera camera){
        super(id, w, h, d, color, x, y, z);
        
        this.handler = handler;
        this.w = w;
        this.h = h;
        this.d = d;
        this.x = x;
        this.y = y;
        this.z = z;
        
        // Display player connecting in the console
        Debug.trace(id + " is connecting..");
        
        // Setting the camera position to be above 1 above the player model
        this.camera = camera;
        
        Debug.trace("Pos: " + this.pos + " ID: " + getID());
    }
    
    // Information in here is processed every millisecond
    /**
     * Updates the player tick when called through the process loop.
     */
    protected void tick(){
        // Updating the position to the velocity. The velocity is changed in the controller
        posX += velX;
        posY += velY;
        posZ += velZ;
        
        // Updates the position of the player
        Point3D nextPos = new Point3D(posX,posY,posZ);
        set(nextPos);
        
        //Changes the position of the camera to where the player is
        setCam(nextPos);
        
        // Check to see if the player has ammo
        if(Hud.ammo > 0){ hasAmmo = true; }
        else if(Hud.ammo <= 0){ hasAmmo = false; } 
        
        // If the player does have ammo, do this below
        if(hasAmmo && getCanShoot()){
            if(getFront()){ this.dirNum = 0; isShooting(); }
            if(getBack()){ this.dirNum = 1; isShooting(); }
            if(getLeft()){ this.dirNum = 2; isShooting(); }
            if(getRight()){ this.dirNum = 3; isShooting(); }
        }
        
        // Running through the array and setting the private player equal to the actual player
        for(int i=0; i < handler.object.size(); i++){
            if(handler.object.get(i).getID() == ID.EasyEnemy){ enemy = handler.object.get(i); }
            if(handler.object.get(i).getID() == ID.SceneObject){ sceneObj = handler.object.get(i); }
        }
        
        // Checks and updates the Collision method
        collision();
    }
    
    // Preparing to set the camera to the position of the player
    // Comment out the camera code in Game::initialiseWorld() to see what it looks like
    // it's not very good, but i tried
    /**
       * was going to be used for first person camera
    */
    private void setCam(Point3D p){
        camera.setTranslateX(p.getX());
        camera.setTranslateY(p.getY());
        camera.setTranslateZ(p.getZ());
    }
    
    /**
       * Updates every (tick) loop
       * @param check collision occurance
       * @return Collisions
    */
    protected void collision(){
        //Debug.trace(id + " is colliding with floor: " + getIsFalling());
        // Old collising
        /*
        if(this.isColliding(enemy)){
            Debug.trace("collided with enemy");
            if(velX > 0){
                velX = 0;
                posX = enemy.getPosX() -1;
            }else if(velX < 0){
                velX = 0;
                posX = enemy.getPosX() + enemy.getW();
            }
                        
            if(velZ > 0){
                velZ = 0;
                posZ = enemy.getPosZ() -1;
            }else if(velZ < 0){
                velZ = 0;
                posZ = enemy.getPosZ() + enemy.getD();
            }
                        
            if(velY > 0){
                velY = 0;
                posY = enemy.getPosY() -1;
            }else if(velY < 0){
                velY = 0;
                posY = enemy.getPosY() + enemy.getH();
            }  
        }
        */
        
        for(int i =0; i < handler.object.size(); i++){
            GameObject tempObj = handler.object.get(i);
            
            // Check if colliding with floor or not
            if(tempObj.getID() == ID.Floor){
                if(this.isColliding(tempObj)){
                    setIsFalling(false);
                }else{ setIsFalling(true); }
            }
            
            if(tempObj.getID() == ID.Bomb){
                // slow player speed when colliding with bomb
                if(getBounds().intersects(tempObj.getBounds())){ velX = 0; velZ = 0; }
            }
            // Take damage when colliding with particles 
            if(tempObj.getID() == ID.Particles){ 
                if(this.isColliding(tempObj)){ Hud.health = Hud.health - tempObj.getATK(); }
            }
            
            // Normal enemies collision
            if(tempObj.getID() == ID.EasyEnemy || tempObj.getID() == ID.FastEnemy || tempObj.getID() == ID.SlowEnemy){
                if(this.isColliding(tempObj)){
                    Hud.health = Hud.health -= tempObj.getATK();
                    if(velX > 0){ // Right collision with the player
                        velX = 0;
                        tempObj.velX = 0;
                        posX = tempObj.getPosX() - w;
                    }else if(velX < 0){ // Left Collision with the player
                        velX = 0;
                        tempObj.velX = 0;
                        posX = tempObj.getPosX() + w;
                    }
                    if(velZ > 0){ // Backwards collision with the player
                        velZ = 0;
                        tempObj.velZ = 0;
                        posZ = tempObj.getPosZ() - d;
                    }else if(velZ < 0){ // Forwards collision with the player
                        velZ = 0;
                        tempObj.velZ = 0;
                        posZ = tempObj.getPosZ() + d;
                    }
                    if(tempObj.velX > 0){
                        tempObj.velX = 0;
                    }else if(tempObj.velX < 0){ tempObj.velX = 0; }
                    if(tempObj.velZ > 0){
                        tempObj.velZ = 0;
                    }else if(tempObj.velZ < 0){ tempObj.velZ = 0; }
                }
            }
            
            // SlimeBoss collision
            if(tempObj.getID() == ID.SlimeBoss){
                if(this.isColliding(tempObj)){
                    Hud.health = Hud.health -= tempObj.getATK();
                    if(velX > 0){ // Right collision with the player
                        velX = 0;
                        tempObj.velX = 0;
                        posX = tempObj.getPosX() - 3.5;
                    }else if(velX < 0){ // Left Collision with the player
                        velX = 0;
                        tempObj.velX = 0;
                        posX = tempObj.getPosX() + 3.5;
                    }
                    if(velZ > 0){ // Backwards collision with the player
                        velZ = 0;
                        tempObj.velZ = 0;
                        posZ = tempObj.getPosZ() - 3.5;
                    }else if(velZ < 0){ // Forwards collision with the player
                        velZ = 0;
                        tempObj.velZ = 0;
                        posZ = tempObj.getPosZ() + 3.5;
                    }
                    if(tempObj.velX > 0){
                        tempObj.velX = 0;
                        velX = 0;
                    }else if(tempObj.velX < 0){ tempObj.velX = 0; velX = 0;}
                    if(tempObj.velZ > 0){
                        tempObj.velZ = 0;
                        velZ = 0;
                    }else if(tempObj.velZ < 0){ tempObj.velZ = 0; velZ = 0; }
                }
            }
        }
    }
    
    // Find out which key has been pressed and fire a bullet in that direction
    /**
       * Handles bullet shooting   
       * @param reduces ammo while firing
       * @param checks for button press
       * @return fires bullet accordingly
    */
    protected void isShooting(){
        // 'spawn' a bullet when the isShooting is called
        handler.addObj(new Bullet(ID.Bullet, 0.25, 0.25, 0.25, Color.WHITE, getPosX(), getPosY(), getPosZ(), handler));
        
        for(int i=0; i<handler.object.size(); i++){
            GameObject tempObj = handler.object.get(i);
            switch(dirNum){
                // Arrow UP
                case 0:
                    if(tempObj.getID() == ID.Bullet){
                        tempObj.posZ += tempObj.velZ;
                        Point3D nextPos = new Point3D(tempObj.posX,tempObj.posY,tempObj.posZ);
                        tempObj.set(nextPos);
                        Hud.ammo -= 1;
                    }
                    break;
                // Arrow DOWN
                case 1:
                    if(tempObj.getID() == ID.Bullet){
                        tempObj.posZ -= tempObj.velZ;
                        Point3D nextPos = new Point3D(tempObj.posX,tempObj.posY,tempObj.posZ);
                        tempObj.set(nextPos);
                        Hud.ammo -= 1;
                    }
                    break;
                // Arrow LEFT
                case 2:
                    if(tempObj.getID() == ID.Bullet){
                        tempObj.posX -= tempObj.velX;
                        Point3D nextPos = new Point3D(tempObj.posX,tempObj.posY,tempObj.posZ);
                        tempObj.set(nextPos);
                        Hud.ammo -= 1;
                    }
                    break;
                // Arrow RIGHT
                case 3:
                    if(tempObj.getID() == ID.Bullet){
                        tempObj.posX += tempObj.velX;
                        Point3D nextPos = new Point3D(tempObj.posX,tempObj.posY,tempObj.posZ);
                        tempObj.set(nextPos);
                        Hud.ammo -= 1;
                    }
                    break;
                default:
                    // do nothing on default
                    Debug.trace("isShooting - DEFAULT. Nothing happened.");
                    break;
            }
        }
    }
    
    // Set position and size of the BoundingBox
    protected BoundingBox getBounds(){
        return new BoundingBox(posX, posY, posZ, w, h, d);
    }
}