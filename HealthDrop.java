    import javafx.scene.paint.*;
    import javafx.scene.transform.*;
    import javafx.geometry.Point3D;
    import javafx.scene.image.Image;
    import javafx.geometry.BoundingBox;
    import java.util.Random;
    
    /**
     * healthDrop gives the player health when player collides with it.
     *
     * @author Richard Healey
     * @version v1.0_Alpha
     */
    public class HealthDrop extends GameObject
    {
        private Handler handler;
        private double w,h,d,x,y,z;
        
        // colors and timers
        private PhongMaterial healthTexture;
        private Color color;
        private Timer flashHealth;
        
        Random rand = new Random();
        
        // check if the item has been picked up
        private boolean isCollected = false;
        
        // Used to flash color when active
        private boolean flashColor = true;
        private double alpha = 1;
        private double flashTime = 0.4;
        
        // random health range to give the player
        private int range = 25 - 12 + 1;
        
        // Rotate speed
        private double rotateSpeed = 2;
        
        /**
           * Create the drop with given variables from Spawn and Game
           * @param drop initializers
        */
        protected HealthDrop(ID id, double w, double h, double d, Color color, double x, double y, double z, Handler handler){
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
            
            healthTexture = new PhongMaterial();
        
            // set the healthTexture to the .png of the floor i made
            healthTexture.setDiffuseMap(new Image(getClass().getResourceAsStream("./images/health.png")));
            
            this.setMaterial(healthTexture);
            
            //Debug.trace("Pos: " + this.pos + " ID: " + getID());
        }
        
        // Information in here is processed every millisecond
        /**
         * Updates the Drop when called through the process loop.
         */
    protected void tick(){
        posY += velY;
        
        if(getIsFalling()){
            // flashHealth is the countdown to when the health flashes
            handler.addTimer(new Timer(7, 10, handler));
            for(int i=0; i<handler.times.size(); i++){
                if(handler.times.get(i).getId() == 7){ flashHealth = handler.times.get(i); }
            }
            
            flashHealth.setLoopTimer(true);
        }
        
        // Rotate the drop every tick
        this.getTransforms().add(new Rotate(rotateSpeed, Rotate.Y_AXIS));
        
        // Updates the position of the player
        Point3D nextPos = new Point3D(posX,posY,posZ);
        set(nextPos);
        
        // Checking collision
        collision();
        
        if(!isCollected){
            for(int i=0; i<handler.times.size(); i++){
                Timer tempTime = handler.times.get(i);
                
                if(tempTime.getId() == 7){
                    if(tempTime.getCountedDown()){
                        setMaterial(new PhongMaterial(Color.GREEN));
                    }else{
                        this.setMaterial(healthTexture);
                    }
                }
            }
            //Debug.trace("flashHealth " + flashHealth.getTime());
        }else{
            // Destroy the Drop and timer when the player collides with it
            this.setIsColliding(true);
            flashHealth.setLoopTimer(false);
        }
    }
    
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
            
            if(tempObj.getID() == ID.Player){
                if(this.isColliding(tempObj)){
                    Hud.health = Hud.health + rand.nextInt(range) + 13;
                    isCollected = true;
                }
            }
        }
    }
    
    protected BoundingBox getBounds(){
        return new BoundingBox(posX, posY, posZ, w,h,d);
    }
}
