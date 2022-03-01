import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.geometry.Point3D;
import javafx.geometry.BoundingBox;

/**
 * SceneObject is used to make the floors, walls, pillars, etc... for different levels.
 * SceneObject was created to be used in multiple different levels.
 * 
 * @author Richard Healey
 * @version v1.0_Alpha
 */
public class SceneObject extends GameObject
{
    private Handler handler;
    private GameObject player;
    private double w,h,d,x,y,z;
    
    // Used to get the centre of the sceneObject
    private double centX, centY, centZ;
    
    public SceneObject(ID id, double w, double h, double d, Color color, double x, double y, double z, Handler handler){
        super(id, w, h, d, color, x, y, z);
        
        this.w = w;
        this.h = h;
        this.d = d;
        this.x = x;
        this.y = y;
        this.z = z;
        this.handler = handler;
        
        canDie = false;
        
        posX = x;
        posY = y;
        posZ = z;
        
        centX =  posX - w / 2;
        centY =  posY - h  / 2;
        centZ =  posZ - d  / 2;
        
    }
    
    // Information in here is processed every millisecond
    protected void tick(){}
    
    protected BoundingBox getBounds(){
        return new BoundingBox(centX, centY, centZ, w,h,d);
    } 
    
}

