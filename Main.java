import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main is used to connect every class together and initialise whole application.
 *
 * @author Richard Healey
 * @version v1.0_Alpha
 */
public class Main extends Application
{
    // The Games main start method
    public static void main(String[] args) throws Exception{
        launch(args);
    }
    
    // Connects every main class to synchronise the tick and handler.
    public synchronized void start(final Stage primaryStage) throws Exception
    {
        int height = 720, width = 1280;
        // set up debugging and print initial debugging message
        Debug.set(true);    // change this to 'false' to stop engine printing messages         
        Debug.trace("Main_run: Engine Running..."); 
        
        
        // Create the View and Controller objs.
        View view = new View(width, height);
        Game game = new Game(width, height);
        Hud hud = new Hud(width, height);
        Menu menu = new Menu(width, height);
        Death death = new Death(width, height);
        Spawn spawn = new Spawn(game.handler);
        
        // Link them together so they can talk to each other
        game.view = view;
        game.controller = game.controller;
        game.hud = hud;
        game.handler = game.handler;
        game.spawn = spawn;
        game.menu = menu;
        game.death = death;
        
        game.controller.view = view;
        game.controller.game = game;
        game.controller.hud = hud;
        game.controller.handler = game.handler;
        game.controller.spawn = spawn;
        game.controller.menu = menu;
        game.controller.death = death;
        
        view.controller = game.controller;
        view.game = game;
        view.hud = hud;
        view.handler = game.handler;
        view.spawn = spawn;
        view.menu = menu;
        view.death = death;
        
        hud.game = game;
        hud.view = view;
        hud.controller = game.controller;
        hud.handler = game.handler;
        hud.spawn = spawn;
        hud.menu = menu;
        hud.death = death;
        
        menu.game = game;
        menu.view = view;
        menu.controller = game.controller;
        menu.handler = game.handler;
        menu.spawn = spawn;
        menu.hud = hud;
        menu.death = death;
        
        death.game = game;
        death.view = view;
        death.controller = game.controller;
        death.handler = game.handler;
        death.spawn = spawn;
        death.hud = hud;
        death.menu = menu;
        
        // Start the window ( the menu when the application has been launched )
        view.start(primaryStage);
        // start the animation timer
        game.timer.start();
    }
}
