import javafx.scene.*;
import javafx.scene.transform.Translate;
import javafx.scene.paint.*;
import javafx.scene.image.Image;
import javafx.geometry.Point3D;
import javafx.geometry.BoundingBox;

/**
 * LevelOne uses SceneObject to create the level, this is so multiple levels can be made.
 *
 * @author Richard Healey
 * @version v1.0_Alpha
 */
public class LevelOne extends GameObject
{
    private Handler handler;
    private double w,h,d,x,y,z;
    private Color color;
    
    public LevelOne(ID id, double w, double h, double d, Color color, double x, double y, double z, Handler handler){
        super(id, w, h, d, color, x, y, z);
        
        this.w = w;
        this.h = h;
        this.d = d;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.handler = handler;
        
        posX = x;
        posY = y;
        posZ = z;
        
        // Checks if this GameObject can die || affected by gravity
        canDie = false;
        
        PhongMaterial floorTexture = new PhongMaterial();
        
        // set the floorTexture to the .png of the floor i made
        floorTexture.setDiffuseMap(new Image(getClass().getResourceAsStream("./images/floor.png")));
        
        // Floor
        handler.addObj(new SceneObject(ID.Floor, 42, 0.1, 42, Color.GRAY, 0, 2, 0, handler));
        
        
        for(int i=0; i<handler.object.size(); i++){
            GameObject tempObj = handler.object.get(i);
            
            if(tempObj.getID() == ID.Floor){
                tempObj.setMaterial(floorTexture);
            }
        }
        
        // Bottom wall
        handler.addObj(new SceneObject(ID.SceneObject, 42, 20, 0.1, Color.GRAY, 0, 12, -21, handler));
    }
    
    // Information in here is processed every millisecond
    protected void tick(){  }
    
    protected BoundingBox getBounds(){
        return new BoundingBox(x, y, z, w,h,d);
    }
    
}
