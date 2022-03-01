import javafx.geometry.Point3D;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.scene.transform.Rotate;
import javafx.scene.shape.Box;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.geometry.BoundingBox;
import javafx.scene.*;

/**
 * GameObject - Every main variable that any GameObject needs, also includes an abstract tick method.
 *
 * @author Richard Healey
 * @version v1.0_Alpha
 */
public abstract class GameObject extends Box
{
    // Default position of the game object
    protected double posX=0, posY=0, posZ=0;
    protected Point3D pos = new Point3D(posX,posY,posZ);
    
    // Placeholders for the GameObject's size
    private double width,height,depth, x,y,z;
    
    // Checks if the GameObject has already been added to the handler, default is false
    protected boolean inGroup = false;
    // Checks if the GameObject is colliding, default is false
    protected boolean isColliding = false;
    
    // for player shooting and entity gravity
    protected boolean isFalling = true, canShoot = true, front, back, left, right;
    
    // Boss event / events checker
    protected boolean bombEvent = false, isEvent = true;
    
    // Used for velocity values to be passed through
    protected double velX, velY, velZ;
    
    // Used for any NPE
    protected double alpha = 1, dmg, atk, hp;
    protected boolean canDie = true;
    
    // ID ENUM of the GameObject
    protected ID id;
    
    // GameObject constructor - to make new GameObjects
    public GameObject(ID id, double w, double h, double d, Color color, double x, double y, double z){
        super(w, h, d);
        
        // Set the position of the object to what is entered when a new GameObject is created, else it will be the default
        this.pos = new Point3D(x,y,z);
        
        // Immediately set the position of the object when given or set to default
        set(pos);
        
        this.width = w;
        this.height = h;
        this.depth = d;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        setMaterial(new PhongMaterial(color));
    }
    
    // Position setter - for translating the object within the 3D space
    public void set(Point3D p){
        setTranslateX(p.getX());
        setTranslateY(p.getY());
        setTranslateZ(p.getZ());
    }
    
    // Abstract to make all GameObjects use the tick method
    // Information in here is processed every millisecond
    /**
     * Anything given to the tick method will be updated using the Game::processLoop()
     */
    protected abstract void tick();
    
    // New collision
    protected BBox bbox(){
        return new BBox(posX-width/2, posX+width/2, posY-height, posY+height, posZ-depth/2, posZ+depth/2);
    }
    // Collision check
    protected boolean isColliding(GameObject other){
        return bbox().isColliding(other.bbox());
    }
    
    // Abstract to make all GameObject just a BoundingBox
    protected abstract BoundingBox getBounds();
    
    // Postion setters
    protected void setPos(Point3D p){ this.pos = p; }
    protected void setPosX(double x){ this.posX = x; }
    protected void setPosY(double y){ this.posY = y; }
    protected void setPosZ(double z){ this.posZ = z; }
    // velocity
    protected void setVelX(double x){ this.velX = x; }
    protected void setVelY(double y){ this.velY = y; }
    protected void setVelZ(double z){ this.velZ = z; }
    
    protected void setW(double w){ this.width = w; }
    protected void setH(double h){ this.height = h; }
    protected void setD(double d){ this.depth = d; }
    
    // setting checking if a direction has been pressed to shoot the bullets
    protected void setFront(boolean f){ this.front = f; }
    protected void setBack(boolean b){ this.back = b; }
    protected void setLeft(boolean l){ this.left = l; }
    protected void setRight(boolean r){ this.right = r; }
    
    protected void setIsFalling(boolean fall){ this.isFalling = fall; }
    protected void setInGroup(boolean t){ this.inGroup = t; }
    protected void setIsColliding(boolean t){ this.isColliding = t; }
    
    protected void setCanShoot(boolean cS){ this.canShoot = cS; }
    
    // used for NPEs
    protected void setAlpha(double a){ this.alpha = a; }
    protected void setDMG(double dmg){ this.dmg = dmg; }
    protected void setATK(double atk){ this.atk = atk; }
    protected void setHP(double hp){ this.hp = hp; }
    
    // GameObject ID Enum
    protected void setID(ID id){ this.id = id; }
    
    // Postion getters
    protected Point3D getPos(){ return pos; }
    protected double getPosX(){ return posX; }
    protected double getPosY(){ return posY; }
    protected double getPosZ(){ return posZ; }
    // velocity
    protected double getVelX(){ return velX; }
    protected double getVelY(){ return velY; }
    protected double getVelZ(){ return velZ; }
    
    protected double getW(){ return width; }
    protected double getH(){ return height; }
    protected double getD(){ return depth; }
    
    // getting whether the shooting directions are true or false
    protected boolean getFront(){ return front; }
    protected boolean getBack(){ return back; }
    protected boolean getLeft(){ return left; }
    protected boolean getRight(){ return right; }
    
    protected boolean getIsFalling(){ return isFalling; }
    protected boolean getInGroup(){ return inGroup; }
    protected boolean getIsColliding(){ return isColliding; }
    
    protected boolean getCanShoot(){ return canShoot; }
    
    // used for NPEs
    protected double getAlpha(){ return alpha; }
    protected double getDMG(){ return dmg; }
    protected double getATK(){ return atk; }
    protected double getHP(){ return hp; }
    
    // GameObject ID Enum
    protected ID getID(){ return id; }
}
