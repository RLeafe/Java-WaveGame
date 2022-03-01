import javafx.scene.*;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;

/**
 * Controls the players health, score & ammo, displays the Wave, FPS and Time.
 *
 * @author Richard Healey
 * @version v1.0_Alpha
 */
public class Hud
{
    protected Handler handler;
    protected Spawn spawn;
    protected Controller controller;
    protected View view;
    protected Game game;
    protected Menu menu;
    protected Death death;
    
    // Setting the Hud scene
    protected Pane pane;
    protected Canvas canvas;
    private Label FPS, TIME, SCORE, WAVE, AMMO, HEALTH;
    
    // Text to be displayed on new wave
    private Label nextWave, bossRound;
    private boolean waveEnd;
    private int wavePause, waveStart;
    
    // Switch to GodMode or not
    protected boolean GodMode = false;
    private Color healthBar;
    
    // Size of pane
    protected int width, height;
    
    // Time Management
    protected int frames, seconds, minutes;
    
    // Wave number and score
    protected static int boss, bossRoundSpawn, wave, score = 0;
    
    // Player health and ammo
    protected static double health = 100;
    protected static int ammo = 100;
    
    /**
       * Get width and height of hud from Main
    */
    protected Hud(int w, int h){
        this.width = w;
        this.height = h;
    }
    
    /**
     * Draws the inital HUD graphics on the screen
     */
    protected Pane createHud(){
        // calls the HUD and world to display in window
        pane = new Pane();
        pane.setId("Hud");
        
        canvas = new Canvas(width, height);
        pane.getChildren().add(canvas);
        
        // adding some text to the HUD
        FPS = new Label("FPS: " + frames);
        FPS.setTranslateX(50);
        FPS.setTranslateY(10);
        
        AMMO = new Label("AMMO");
        AMMO.setTranslateX(width/2 - 20);
        AMMO.setTranslateY(height/1.1);
        AMMO.setId("AMMO");
        
        HEALTH = new Label("HEALTH");
        HEALTH.setTranslateX(width/2 - 20);
        HEALTH.setTranslateY(height/1.14);
        HEALTH.setId("HEALTH");
        
        TIME = new Label("Time" + minutes + ":" + seconds);
        TIME.setTranslateX(width/2 - 20);
        TIME.setTranslateY(10);
        TIME.setId("Time");
        
        SCORE = new Label("SCORE: " + score );
        SCORE.setTranslateX(50);
        SCORE.setTranslateY(25);
        
        WAVE = new Label("WAVE: " + wave );
        WAVE.setTranslateX(50);
        WAVE.setTranslateY(40);
        
        nextWave = new Label("");
        nextWave.setTranslateX(width/ 2 - 110);
        nextWave.setTranslateY(height/ 4 - 64);
        nextWave.setId("nextWave");
        
        bossRound = new Label("");
        bossRound.setTranslateX(width/ 2 - 110);
        bossRound.setTranslateY(height/ 4 - 64);
        bossRound.setId("bossRound");
        
        // Add every label and the place to the pane
        pane.getChildren().addAll(FPS, AMMO, WAVE, nextWave, bossRound, HEALTH, TIME, SCORE);
        
        return pane;
    }
    
    // Creates and displays the HUD Pane
    /**
     * Updates (redraws) anything on the HUD.
     */
    protected void hud(){
        
        synchronized (game){
            // Draws the Game HUD to the screen
            GraphicsContext gc = canvas.getGraphicsContext2D();
            
            gc.setFill(Color.GRAY);
            gc.fillRect(width/2 - 100,height/1.14,200,16);
            if(GodMode){
                healthBar = Color.WHITE;
            }else{ healthBar = Color.RED; }
            gc.setFill(healthBar);
            gc.fillRect(width/2 - 100,height/1.14, health * 2,16);
            
            gc.setFill(Color.GRAY);
            gc.fillRect(width/2 - 50,height/1.1,100,16);
            gc.setFill(Color.YELLOW);
            gc.fillRect(width/2 - 50,height/1.1, ammo,16);
            
            if(waveEnd && wavePause == 40 && boss != 5){ nextWave.setText("WAVE" + "\n" + wave); }
            if(wavePause == 40 && boss == 5){ nextWave.setText("BOSS" + "\n" + "WAVE"); }
            
            if(wavePause <= 20){ nextWave.setText(""); }
            
            // Update the text
            FPS.setText("FPS: " + frames);
            AMMO.setText("AMMO");
            HEALTH.setText("HEALTH");
            TIME.setText("TIME" + "\n" + minutes + ":" + seconds);
            
            SCORE.setText("SCORE: " + score );
            WAVE.setText("WAVE: " + wave );
        }
    }
    
    /**
     * When called, will update the HUD.
     */
    protected void tick(){
        // Set the hud variables to the spawn variables and update them
        seconds = spawn.getSec();
        minutes = spawn.getMin();
        
        wave = spawn.getWaveNum();
        boss = spawn.getBossRound();
        bossRoundSpawn = spawn.getBossRoundSpawn();
        
        waveEnd = spawn.getWaveEnd();
        wavePause = spawn.getWavePause();
        waveStart = spawn.getWaveStart();
        
        if(ammo >= 100){ ammo = 100; }
        if(health >= 100){ health = 100; }
        else if(health <= 0 && !GodMode){
            // remove the the world and hud
            view.root.getChildren().removeAll(view.world, view.paneHud);
                
            view.paneDeath = death.createMenu();
                
            // add the menu back to the screen after removing the world and hud
            view.root.getChildren().add(view.paneDeath);
            
            // setting the state to death menu
            game.gameState = STATE.Death;
        }else if(GodMode){
            if(health <= 0){ health = 100; }
            if(ammo <= 0){ ammo = 100; }
        }
        frames = game.getFPS();
        hud();
    }
    
    protected void setWave(int wave){ this.wave = wave; }
    protected int getWave(){ return wave; }
    protected void setScore(int score){ this.score = score; }
    protected int getScore(){ return score; }
    
    protected void setSec(int t){ this.seconds = t; }
    protected int getSec(){ return seconds; }
    protected void setMin(int t){ this.minutes = t; }
    protected int getMin(){ return minutes; }
}
