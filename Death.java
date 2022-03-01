
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.nio.file.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

import java.util.Arrays;
import java.util.Collections;

/**
 * Death menu when the player dies, displays their score, wave number and displays a quit button.
 *
 * @author Richard Healey
 * @version v1.0_Alpha
 */
public class Death
{
    protected Menu menu;
    protected Handler handler;
    protected Spawn spawn;
    protected Controller controller;
    protected View view;
    protected Game game;
    protected Hud hud;
    
    // Size of pane
    protected int width, height;
    
    // setting the variables for the random quote
    private Random rand = new Random();
    private int range = 4 - 0, quote;
    private String menuQuotes, switchState;
    
    // Setting the Death scene and
    // variables components for user interface
    protected Label title, highScore, waveScore;
    protected Pane pane;
    
    // to display the top 3 highscores
    private String[] deathScores = new String[2];
    private String[] topScore = new String[3];
    private Label scoreNum, waveNum;

    /**
     * Simple button main menu to start or quit the game.
     */
    
    protected Death(int w, int h){
        this.width = w;
        this.height = h;
    }
    
    /**
     * Draws the inital MENU graphics on the screen
     */
    protected Pane createMenu(){
        // layout objects
        pane = new Pane();
        pane.setId("Death");
        
        deathScores[0] = String.valueOf(hud.getScore());
        deathScores[1] = String.valueOf(hud.getWave());
        
        // getting the scores to be displayed on the menu
        scores();
        
        // title displayed at the top of the menu
        title = new Label();
        title.setText("GAMEOVER");
        title.setId("deathTitle");
        title.setTranslateX(70);
        title.setTranslateY(115);
        
        // yes button
        Button quit = new Button( "QUIT" );
        quit.setTranslateX(width/2 - 115);
        quit.setTranslateY(380);
        
        // highscore and the information below like on menu
        highScore = new Label();
        highScore.setText("SCORE");
        highScore.setId("highScore");
        highScore.setTranslateX(width/2 - 35);
        highScore.setTranslateY(480);
        
        scoreNum = new Label();
        scoreNum.setText(deathScores[0]);
        scoreNum.setId("score_1");
        scoreNum.setTranslateX(width/2 - 20);
        scoreNum.setTranslateY(520);
        
        waveScore = new Label();
        waveScore.setText("WAVE");
        waveScore.setId("waveScore");
        waveScore.setTranslateX(width/2 - 32);
        waveScore.setTranslateY(580);
        
        waveNum = new Label();
        waveNum.setText(deathScores[1]);
        waveNum.setId("score_3");
        waveNum.setTranslateX(width/2 - 5);
        waveNum.setTranslateY(620);
        
        // terminate the application when quit
        quit.setOnAction(actionEvent -> Platform.exit());
        
        // adding all of the labels to the pane
        pane.getChildren().addAll(title, quit, highScore, scoreNum, waveScore, waveNum);
        
        return pane;
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
        
        if(hud.getScore() > scores[0]){
            scores[2] = scores[1];
            scores[1] = scores[0];
            scores[0] = hud.getScore();
        }else if(hud.getScore() > scores[1]){
            scores[2] = scores[1];
            scores[1] = hud.getScore();
        }else if(hud.getScore() > scores[2]){
            scores[2] = hud.getScore();
        }
        
        // Convert Integer to String
        for (int x = 0; x < scores.length; x++) {
            topScore[x] = String.valueOf(scores[x]);
        }
        
        try {
          FileWriter myWriter = new FileWriter("./textFiles/highScores.txt");
          for(int i=0; i<topScore.length; i++){
            myWriter.write(topScore[i] + "\n");
          }
          myWriter.close();
        } catch (IOException err) {
          err.printStackTrace();
        }
        
        // List the top 3 scores highest first in console
        for(String str: topScore){
            Debug.trace("top scores " + str);
        }
    }
}
