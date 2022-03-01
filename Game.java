import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;
import javafx.scene.transform.Rotate;
import javafx.geometry.Point3D;
import javafx.animation.AnimationTimer;

import java.util.Random;

/**
 * Game controls every entity and most importantly, the animation timer.
 *
 * @author Richard Healey
 * @version v1.0_Alpha
 */

public class Game
{   
    protected boolean isRunning = false;
    
    // Calling these classes to be used
    protected Handler handler;
    protected View view;
    protected Hud hud;
    protected Controller controller;
    protected Spawn spawn;
    protected Menu menu;
    protected Death death;
    
    // Place holder for W & H
    private int width, height;
    
    // Creating the main group to add any and all objects to be displayed
    private Group root = new Group();
    
    // Variables for the AnimationTimer
    private double t = 0;
    private long time = System.currentTimeMillis();
    protected int frames;
    protected int fps;
    protected AnimationTimer timer;
    
    // Creating the inital camera
    protected PerspectiveCamera camera = new PerspectiveCamera(true);
    
    // default gameState is the menu
    protected STATE gameState = STATE.Menu;
    
    // Initiate every game object
    /**
       * Width and Height from Main
       * @param create AnimationTimer
    */
    protected Game(int w, int h){
        Debug.trace("GAME: <constructor>");
        width = w;
        height = h;
        
        // Instantiating the handler and controller
        handler = new Handler();
        controller = new Controller(handler);
        
        // Handle and process the loop of the game using AnimationTimer
        /**
         * AnimationTimer method modified but provided by Almas' snake tutorial
         * @see https://www.youtube.com/watch?v=mjfgGJHAuvI
         */
        timer = new AnimationTimer(){
            @Override
            public void handle(long now){
                t += 0.016;
                frames++;
                       
                if(System.currentTimeMillis() - time > 1000){
                    time += 1000;
                    setFPS(frames);
                    Debug.trace("FPS: " + frames);
                    frames = 0;
                }
                       
                if (t > 0.1){
                    // Actually updates and processes the game
                    tick();
                    t = 0;
                }
            }
        };
    }
    
    // When called, will add every GameObject stored in Handler to the initial start up
    /**
       * Create the first level, eneimes, camera and player
       * @param first level, enemies, camera and player
    */
    private Group initialiseWorld(){
        getLevel();
        
        if(gameState == STATE.Game){
            // Setting the camera to show the whole scene including the level for development
            camera.getTransforms().addAll(new Translate(0, -25, -25), new Rotate(-45, Rotate.X_AXIS));
                
            // Setting the player and first enemies/drops in the game
            handler.addObj(new Player(ID.Player, 1, 1, 1, Color.GREEN, 0, -2, 0, handler, camera));
            handler.addObj(new HealthDrop(ID.HealthDrop, 1, 1, 1, Color.LIMEGREEN, -16,-6,-16, handler));
            handler.addObj(new AmmoDrop(ID.AmmoDrop, 1.5, 1, 1, Color.YELLOW, 16,-6,16, handler));
            handler.addObj(new EasyEnemy(ID.EasyEnemy, 1, 1, 1, Color.ORCHID, 0, 0, -10, handler));
            handler.addObj(new FastEnemy(ID.FastEnemy, 1, 1, 1, Color.SEASHELL, 0, 0, 16, handler));
            handler.addObj(new SlowEnemy(ID.SlowEnemy, 1, 1, 1, Color.MEDIUMVIOLETRED, 0, 0, -16, handler));
        }
        
        return new Group();
    }
    
    // Start the Run method
    /**
       * Start the process loop (tick method)
       * @param tick process loop
    */
    protected synchronized void startGame(){
        try{
            initialiseWorld();
            isRunning = true;
            Debug.trace("Building world...");
        }catch(Exception err){
            err.printStackTrace();
        }
    }
    
    // Pause the Run method
    // was going to be used for a little in game store and settings page for in game
    /**
       * Used for an ingame pause Menu
       * @param pause tick methods
    */
    protected synchronized void pauseGame(){
        try{
            timer.stop();
            isRunning = false;
            Debug.trace("Game::pauseGame: paused");
        }catch(Exception err){
            err.printStackTrace();
        }
    }
    
    // Process any information passed through tick
    /**
       * Process any information passed through
       * @param checks gameState to process tick
       * @return updated GameObject / Menu
    */
    private void tick(){
        handler.tick();
        if(gameState == STATE.Game){
            hud.tick();
            controller.tick();
            spawn.tick();
            handler.updateGroup(root);
        }else if(gameState == STATE.Menu){
            menu.tick();
        }
    }
    
    /**
     * Creates the main 3D environment in the game
     */
    // Create the initial 3D SubScene
    protected SubScene create3DScene(){
        Debug.trace("Building 3D Scene...");
        
        // creating the 3D scene
        SubScene world = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);
        
        // displaying the camera
        world.setCamera(camera);
        
        return world;
    }
    
    /**
     * Used to get set levels to be generated in the SubScene
     */
    protected void getLevel(){
        handler.addObj(new LevelOne(ID.LevelOne, 1, 1, 1, Color.RED, 0, 5, 0, handler));
    }
    
    // Used to display the FPS on the screen
    protected void setFPS(int fps){ this.fps = fps; }
    protected int getFPS(){ return(fps); }
}
