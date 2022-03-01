
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.nio.file.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

import java.util.Arrays;
import java.util.Collections;

/**
 * Main menu of the game. Some code provided by CI401 - atm project but heavily modified 
 *
 * @author Richard Healey and Roger Evans
 * @version v1.0_Alpha
 */
public class Menu
{
    protected Handler handler;
    protected Spawn spawn;
    protected Controller controller;
    protected View view;
    protected Game game;
    protected Hud hud;
    protected Death death;
    
    // Size of pane
    protected int width, height;
    
    // setting the variables for the random quote
    private Random rand = new Random();
    private int range = 4 - 0, quote;
    private String menuQuotes, switchState;
    
    // Setting the Menu scene and
    // variables components for user interface
    protected Label title, menuQ, highScore;
    protected Pane pane;
    
    // to display the top 3 highscores
    private String[] topScore = new String[3];
    private Label[] scoreNum = new Label[3];

    /**
     * Simple button main menu to start or quit the game.
     */
    
    protected Menu(int w, int h){
        this.width = w;
        this.height = h;
    }
    
    /**
     * Draws the inital MENU graphics on the screen
     */
    protected Pane createMenu(){
        // layout objects
        pane = new Pane();
        pane.setId("Menu");
        
        // getting the random quote to be displayed on the menu
        menuQuotes();
        
        // getting the scores to be displayed on the menu
        scores();
        
        // title displayed at the top of the menu
        title = new Label();
        title.setText("MENU");
        title.setId("menuTitle");
        title.setTranslateX(width/2.8);
        title.setTranslateY(5);
        
        // title displayed at the top of the menu
        menuQ = new Label();
        menuQ.setText(menuQuotes);
        menuQ.setId("menuQuote");
        menuQ.setTranslateX(width/2.8);
        menuQ.setTranslateY(150);
        
        // Start button
        Button start = new Button( "START" );
        start.setTranslateX(width/2.5);
        start.setTranslateY(280);
        
        // Quit button
        Button quit = new Button( "QUIT" );
        quit.setTranslateX(width/2 - 115);
        quit.setTranslateY(380);
        
        // highscore and information below like on menu
        highScore = new Label();
        highScore.setText("HIGHSCORE");
        highScore.setId("highScore");
        highScore.setTranslateX(width/2 - 65);
        highScore.setTranslateY(480);
        
        scoreNum[0] = new Label();
        scoreNum[0].setText(topScore[0]);
        scoreNum[0].setId("score_1");
        scoreNum[0].setTranslateX(width/2 - 22);
        scoreNum[0].setTranslateY(520);
        
        scoreNum[1] = new Label();
        scoreNum[1].setText(topScore[1]);
        scoreNum[1].setId("score_2");
        scoreNum[1].setTranslateX(width/2 - 22);
        scoreNum[1].setTranslateY(545);
        
        scoreNum[2] = new Label();
        scoreNum[2].setText(topScore[2]);
        scoreNum[2].setId("score_3");
        scoreNum[2].setTranslateX(width/2 - 22);
        scoreNum[2].setTranslateY(570);
        
        // what to do when the buttons are pressed
        start.setOnAction( this::buttonClicked );
        // terminate the application when quit
        quit.setOnAction(actionEvent -> Platform.exit());
        
        // adding all of the labels to the pane
        pane.getChildren().addAll(title, start, quit, highScore, scoreNum[0], scoreNum[1], scoreNum[2], menuQ);
        
        // make a timer to automatically switch the quotes every 4 seconds
        handler.addTimer(new Timer(21, 50, handler));
        
        return pane;
    }
    
    /**
     * Read one of four quotes from a txt document to display on the menu
     */
    protected void menuQuotes(){
        this.quote = rand.nextInt(range) + 1;
        try{
            // get the quote from a specific line number 'quote'
            menuQuotes = Files.readAllLines(Paths.get("./textFiles/menuQuotes.txt")).get(quote);
            Debug.trace("Menu::menuQuotes: " + menuQuotes);
        }catch(IOException err){
            err.printStackTrace();
        }
    }
    
    /**
     * Read top 3 highscores from a txt document to display on the menu and order the numbers by highest first
     */
    protected void scores(){
        try{
            // get the scores from the specific line numbers
            topScore[0] = Files.readAllLines(Paths.get("./textFiles/highScores.txt")).get(0);
            topScore[1] = Files.readAllLines(Paths.get("./textFiles/highScores.txt")).get(1);
            topScore[2] = Files.readAllLines(Paths.get("./textFiles/highScores.txt")).get(2);
        }catch(IOException err){
            err.printStackTrace();
        }
        
        // String to Integer conversion
        Integer[] scores = new Integer[topScore.length];
        for (int i = 0; i < topScore.length; i++) {
            scores[i] = Integer.valueOf(topScore[i]);
        }
        
        // Sort the Integer array largest number first
        Arrays.sort(scores, Collections.reverseOrder());
        
        // Convert Integer to String
        for (int x = 0; x < scores.length; x++) {
            topScore[x] = String.valueOf(scores[x]);
        }
        
        // List the top 3 scores highest first in console
        for(String str: topScore){
            Debug.trace("top scores " + str);
        }
    }
    
    // used to change any graphics on the menu or quotes
    /**
       * Check gameState, update menu quotes or destroy Timer used
       * @return updated menu
    */
    protected void menu(){
        synchronized (game){
            if(game.gameState == STATE.Menu){
                for(int i=0; i<handler.times.size(); i++){
                    Timer tempTimer = handler.times.get(i);
                    if(tempTimer.getId() == 21){
                        tempTimer.setLoopTimer(true);
                        //Debug.trace("Menu::menuTimer: " + tempTimer.getTime());
                        if(tempTimer.getCountedDown()){
                            menuQuotes();
                            menuQ.setText(menuQuotes);
                        }
                    }
                }
            }
            // Forcefully destroy the timer once the gameState switches
            if(game.gameState != STATE.Menu){
                for(int i=0; i<handler.times.size(); i++){
                    Timer tempTimer = handler.times.get(i);
                    if(tempTimer.getId() == 21){ tempTimer.setDestroy(true); }
                }
            }
        }
    }
    
    // This is how the View talks to the Controller
    // This method is called when a button is pressed
    // It fetches the label on the button and passes it to the controller's process method
    /**
     * Modified buttonClicked code from CI401 atm project
     * @see CI401 - ATM project
     */
    protected void buttonClicked(ActionEvent event) {
        // provides button object
        Button butt = ((Button) event.getSource());
        if(game != null){
            switchState = butt.getText();   // get the button label
            Debug.trace( "Menu::buttonClicked: "+ switchState );
            
            if(switchState.equals("START")){
                // remove the menu from the view
                view.root.getChildren().remove(view.pane);
                
                view.paneHud = hud.createHud();
                // create the 3D scene
                view.world = game.create3DScene();
                
                // add the HUD and world to the view once the MENU has been removed
                view.root.getChildren().addAll(view.world, view.paneHud);
                
                // setting the state to game
                game.gameState = STATE.Game;
                // starting the game ( generating the level, pov camera, etc.. )
                game.startGame();
            }
        }
    }
    
    /**
       * Update menu every loop (tick)
    */
    protected void tick(){
        menu();
    }
}
