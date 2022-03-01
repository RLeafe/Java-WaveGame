import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Connecting the HUD, SubScene and get waits for input controls from the user.
 *
 * @author Richard Healey
 * @version v1.0_Alpha
 */

public class View
{   
    // Width and Height of the window
    protected int width, height;
    
    /**
       * Calling and connecting the other classes if/when needed
    */
    // Calling the other classes in case used
    protected Handler handler;
    protected Spawn spawn;
    protected Controller controller;
    protected Game game;
    protected Hud hud;
    protected Menu menu;
    protected Death death;
    
    // setting the scene(s)
    protected Stage window;
    protected Scene scene;
    protected Pane pane;
    protected Pane paneHud;
    protected Pane paneDeath;
    protected SubScene world;
    protected Group root = new Group();
    
    protected boolean hasSwitched = false;
    
    protected View(int w, int h){
        Debug.trace("View: <constructor>");
        this.width = w;
        this.height = h;
    }
    
    /**
       * Create the root scene and apply the menu pane
    */
    protected void start(Stage window)
    {   
        if(game.gameState == STATE.Menu){
            pane = menu.createMenu();
            // attaches the menu to the scene
            root.getChildren().add(pane);
        }
        
        // Used to style anything in the engine
        root.getStylesheets().add("mainStyling.css");
        
        // Creates the inital scene and attaches all of the content to it
        scene = new Scene(root, width, height, true);
        
        // Gets the controls and waits for inputs
        scene.setOnKeyPressed(p -> { controller.getPressed(p); });
        scene.setOnKeyReleased(r -> { controller.getReleased(r); });
        
        // Stop the window from being resized
        window.setResizable(false);
        window.getIcons().add(new Image("./images/icon.png"));
        window.setTitle("WaveGame");
        window.isFocused();
        window.setScene(scene);
        window.show();
    }
}
