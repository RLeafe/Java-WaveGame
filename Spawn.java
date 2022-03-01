        import javafx.scene.transform.Translate;
        import javafx.scene.paint.Color;
        import javafx.geometry.Point3D;
        import javafx.geometry.BoundingBox;
        import java.util.Random;
        
        /**
         * Spawn: Used to control the spawning of enemies, boss rounds, event waves.
         * Spawn is also used for the complete control of each wave and time & wave incrementing.
         * 
         * @author Richard Healey
         * @version v1.0_Alpha
         */
        public class Spawn
        {
            protected Handler handler;
            protected Hud hud;
            protected Game game;
            protected View view;
            protected Controller controller;
            protected Menu menu;
            
            // temp objects to be manipulated
            private GameObject easyEnemy, fastEnemy, slowEnemy, boss, healthDrop, ammoDrop;
            
            // randomSpawn will set different positions for the enemies to spawn at
            private int randomSpawn, prevRandomSpawn, type;
            private int[] spawnLoc = new int[3];
            
            // Random variables
            private Random rand = new Random();
            private int r = 0;
            
            // Events
            private boolean bombEvent, changeDir, switchDir;
            private int eventToken = 0, xPos = -20, zPos = -20;
            private Timer bombTimer;
            
            // Timer, Wave & Boss controls
            private double timeCounter = 0, waitTimer = 0;
            private int seconds = 0, minutes = 0;
            
            private int wavePause = 50, waveStart = 10, bossRoundSpawn = 5;
            private boolean newWave=false, waveEnd = false, bossStart = false, bossAlive = false;
            
            // WaveCounter and score counter
            protected static int waveCounter = 0, bossRound = 0;
            private int score = 0;
            
            protected Spawn(Handler handler){
                this.handler = handler;
            }
            
            /**
             * Updates Spawn when called.
             */
            protected void tick(){
                // Running through the handler and setting the private gameObjects to the actual objects
                for(int i=0; i < handler.object.size(); i++){
                    GameObject tempObj = handler.object.get(i);
                    if(handler.object.get(i).getID() == ID.EasyEnemy){ easyEnemy = handler.object.get(i); }
                    if(handler.object.get(i).getID() == ID.FastEnemy){ fastEnemy = handler.object.get(i); }
                    if(handler.object.get(i).getID() == ID.SlowEnemy){ slowEnemy = handler.object.get(i); }
                    if(handler.object.get(i).getID() == ID.SlimeBoss){ boss = handler.object.get(i); }
                    if(handler.object.get(i).getID() == ID.HealthDrop){ healthDrop = handler.object.get(i); }
                    if(handler.object.get(i).getID() == ID.AmmoDrop){ ammoDrop = handler.object.get(i); }
                }
                
                // Counting the time in milliseconds
                timeCounter++;
                
                // set the randomSpawn int
                type = rand.nextInt(3 - 0);
                randomSpawn = rand.nextInt(5 - 0);
                r = rand.nextInt(10 + 2) - 2;
                //Debug.trace("Rand: " + randomSpawn);
                
                // Check if it's time for the boss to spawn
                if(bossRound == bossRoundSpawn && !bossAlive){ bossStart = true; }
                
                // Checking to see if no enemies remain in the game
                if(!bossStart && bossRound != bossRoundSpawn){
                    if(!easyEnemy.getInGroup()
                    && !fastEnemy.getInGroup()
                    && !slowEnemy.getInGroup()){ waveEnd = true; }
                }
                
                if(newWave && !bossStart){
                    // Call the spawnRandom method to spawn the enemies and drops
                    spawnRandom();
                    spawnEnemy();
                }
                
                // Spawn health and/or ammo every 30 seconds
                if(!healthDrop.getInGroup() || !ammoDrop.getInGroup()){
                    if(seconds == 16 || seconds == 31 || seconds == 1){ spawnRandom(); spawnDrop(); }
                }
                
                // start and reset the waves
                startWave();
                resetWave();
                // Loop through the event if active
                bombDrops();
               
                // Spawning the kept throwing out an threading error
                // this is because more than 1 boss tries to spawn within 1 millisecond 
                try{
                    if(newWave && bossStart){
                        this.bossAlive = true;
                        this.bombEvent = true;
                        //Debug.trace("Spawn::bossAlive set to: " + bossAlive);
                        if(timeCounter == 1){
                            // Timer for each row of bombs to be placed (0.7 seconds)
                            handler.addTimer(new Timer(1, 7, handler));
                            handler.addObj(new SlimeBoss(ID.SlimeBoss, 6, 6, 6, Color.ORCHID, 0,-10,0, handler));
                        }
                    }
                    
                    if(bossAlive){
                        // add timer to replace bombs if boss is still alive (10 seconds)
                        if(!bombEvent){ handler.addTimer(new Timer(2, 100, handler)); }
                        if(timeCounter == 2){
                            if(!boss.getInGroup()){
                                bombEvent = false; bossAlive = false; bossRound = 0; bossStart = false; waveEnd = true;
                                xPos = -20;
                                zPos = -20;
                                
                                for(int i=0; i<handler.times.size(); i++){
                                    Timer timeTemp = handler.times.get(i);
                                    if(timeTemp.getId() == 1){ handler.removeTimer(timeTemp); }
                                }
                            }
                        }
                    }
                }catch(Exception err){
                    Debug.trace("Spawn::bossStart -" + err);
                }
                
                // Change the seconds and minutes
                if(timeCounter >= 10){
                    timeCounter = 0;
                    seconds = (seconds + 1);
                    if(seconds >= 60){
                        seconds = 0;
                        minutes = (minutes + 1);
                    }
                }
            }
            
            // Bomb Event
            /**
             * Loops to spawn the bombs and place them in the level when called. Checks if it should also loop again.
             * @param checks for bomb event before executing
             */
            protected void bombDrops(){
                // Drop bombs around the boss IF there are no bombs on the map
                for(int i=0; i<handler.times.size(); i++){
                    Timer timeTemp = handler.times.get(i);
                    if(handler.times.get(i).getId() == 1){ bombTimer = handler.times.get(i); }
                    
                    if(bombEvent && timeTemp.getId() == 1){
                        bombTimer.setLoopTimer(true);
                        //Debug.trace("bomb time-" + timeTemp.getTime());
                        if(timeTemp.getCountedDown()){
                            bombSpawn();
                            handler.addObj(new Bomb(ID.Bomb, 1, 1, 1, Color.PALETURQUOISE, spawnLoc[0],spawnLoc[1],spawnLoc[2], handler));
                            for(int gO=0; gO<handler.object.size(); gO++){
                                GameObject tempObj = handler.object.get(gO);
                                if(tempObj.getID() == ID.Bomb){ 
                                    if(tempObj.getInGroup()){ tempObj.bombEvent = bombEvent; }
                                }
                            }
                            //Debug.trace("Spawn::bombDrops -" + timeTemp.getCountedDown());
                        }
                    }
                    if(!bombEvent && timeTemp.getId() == 2){
                        //Debug.trace("bomb time-" + timeTemp.getTime());
                        if(timeTemp.getCountedDown()){
                            if(timeCounter == 1){
                                bombEvent = true;
                                changeDir = false;
                                eventToken = 0;
                                if(xPos >= 19){ switchDir = true; zPos = 20; xPos = 20; }
                                if(xPos <= -19){ switchDir = false; zPos = -20; xPos = -20; }
                            }
                        }
                    }
                }
            }
            
            /**
             * The location loop of where to spawn each bomb, can be used for different events, if they exist.
             * @return bomb positons
             */
            // Bomb spawn locations
            protected void bombSpawn(){
                // Spawn the bombs from the left side
                if(!switchDir){
                    if(!changeDir && eventToken == 0){
                        // bomb spawn from Z positon -19
                        if(zPos <= 19){
                            spawnLoc[2] = zPos;
                            zPos = zPos + 4;
                            // once the bombs reach posititon 19 
                            // the bombs spawn on the next 'column' by adding 4 to X position
                        }else if(zPos >= 19){
                            spawnLoc[0] = xPos;
                            xPos = xPos + 4;
                            eventToken = 1;
                            changeDir = true;
                        }
                    }
                    if(changeDir && eventToken == 1){
                        if(zPos >= -19){
                            spawnLoc[2] = zPos;
                            zPos = zPos - 4;
                        }else if(zPos <= 19){
                            spawnLoc[0] = xPos;
                            xPos = xPos + 4;
                            eventToken = 0;
                            changeDir = false;
                        }
                    }
                    if(xPos >= 19){ bombEvent = false; }
                }//Switch the bombs spawn location from the right to left
                else if(switchDir){
                    if(!changeDir && eventToken == 0){
                        if(zPos >= -19){
                            spawnLoc[2] = zPos;
                            zPos = zPos - 4;
                        }else if(zPos <= -19){
                            spawnLoc[0] = xPos;
                            xPos = xPos - 4;
                            eventToken = 1;
                            changeDir = true;
                        }
                    }
                    if(changeDir && eventToken == 1){
                        if(zPos <= 19){
                            spawnLoc[2] = zPos;
                            zPos = zPos + 4;
                        }else if(zPos >= -19){
                            spawnLoc[0] = xPos;
                            xPos = xPos - 4;
                            eventToken = 0;
                            changeDir = false;
                        }
                    }
                    if(xPos <= -19){ bombEvent = false; }
                }
                
                spawnLoc[0] = xPos;
                // Default Y position is always -12
                spawnLoc[1] = -12;
                
        }
        
        /**
         * spawnRandom() used 'randomSpawn' (random int, rang (5 - 0)) and chooses set positions in switch statement
         * @return new enemy & drop spawn location
         */
        // Spawn the enemies at different locations
        protected void spawnRandom(){
            spawnLoc[1] = 0;
            
            // Trying to avoid double spawning a little bit
            if(prevRandomSpawn == randomSpawn){
                randomSpawn = randomSpawn + 1;
            }
            
            switch(randomSpawn){
                case 0:
                    spawnLoc[0] = -4;  spawnLoc[2] = 4;
                    break;
                case 1:
                    spawnLoc[0] = 16; spawnLoc[2] = 0;
                    //handler.addObj(new EasyEnemy(ID.EasyEnemy, 1, 1, 1, Color.ORCHID, 16,0,0, handler));
                    break;
                case 2:
                    spawnLoc[0] = -16; spawnLoc[2] = 4;
                    //handler.addObj(new EasyEnemy(ID.EasyEnemy, 1, 1, 1, Color.ORCHID, -16,0,0, handler));
                    break;
                case 3:
                    spawnLoc[0] = 0; spawnLoc[2] = 16;
                    //handler.addObj(new EasyEnemy(ID.EasyEnemy, 1, 1, 1, Color.ORCHID, 0,0,16, handler));
                    break;
                case 4:
                    spawnLoc[0] = 0; spawnLoc[2] = -16;
                    //handler.addObj(new EasyEnemy(ID.EasyEnemy, 1, 1, 1, Color.ORCHID, 0,0,-16, handler));
                    break;
                default:
                    Debug.trace("Spawn::randomSpawner - No enemies spawned this time!");
                    break;
            }
            // Set prevRandomSpawn to the last randomSpawn
            prevRandomSpawn = randomSpawn;
        }
        
        /**
         * Spawn the different types of drops
         * @param different depending on type
         */
        protected void spawnDrop(){
            switch(type){
                case 0:
                    handler.addObj(new HealthDrop(ID.HealthDrop, 1, 1, 1, Color.LIMEGREEN, spawnLoc[0],spawnLoc[1],spawnLoc[2], handler));
                    break;
                case 1:
                    handler.addObj(new AmmoDrop(ID.AmmoDrop, 1.5, 1, 1, Color.YELLOW, spawnLoc[0],spawnLoc[1],spawnLoc[2], handler));
                    break;
                default:
                    Debug.trace("Spawn::spawnDrop - No drops spawned!");
                    break;
            }
        }
        
        // Spawn the different types of enemies to mix up the playing field
        /**
         * Spawn the different types of enemies
         * @param different enemy types depending on type
         */
        protected void spawnEnemy(){
            switch(type){
                case 0:
                    handler.addObj(new EasyEnemy(ID.EasyEnemy, 1, 1, 1, Color.ORCHID, spawnLoc[0],spawnLoc[1],spawnLoc[2], handler));
                    break;
                case 1:
                    handler.addObj(new FastEnemy(ID.FastEnemy, 1, 1, 1, Color.SEASHELL, spawnLoc[0],spawnLoc[1],spawnLoc[2], handler));
                    break;
                case 2:
                    handler.addObj(new SlowEnemy(ID.SlowEnemy, 1, 1, 1, Color.MEDIUMVIOLETRED, spawnLoc[0],spawnLoc[1],spawnLoc[2], handler));
                    break;
                default:
                    Debug.trace("Spawn::spawnEnemy - No enemies spawned!");
                    break;
            }
        }
        
        // Method to start the new wave and give a few second before more enemies spawn
        /**
         * Set the new wave to spawn the enemies and add to the boss & wave counter
         */
        protected void startWave(){
            if(waveEnd){ wavePause--;}
            if(wavePause == 49){ waveCounter++; bossRound++; }
            if(wavePause <= 0){ waveStart--; waveEnd = false; }
            if(waveStart == 10 && !waveEnd){ newWave = true; }
            if(waveStart <= 0 && newWave){ newWave = false; }
        }
        
        // resetting the counters
        /**
         * Reset the counters after looped
         * @param reset the wavePause & waveStart to be used again when needed
         * @return new times
         */
        protected void resetWave(){
            if(wavePause <= 0 && waveStart <= 0){
                wavePause = 50;
                waveStart = 30;
            }
        }
        
        protected void setScore(int score){ this.score = score; }
        protected int getScore(){ return score; }
        
        protected boolean getBombEvent(){ return bombEvent; }
        
        protected int getBossRoundSpawn(){ return bossRoundSpawn; }
        
        // Getting and setting the wave numbers
        protected void setWaveEnd(boolean we){ this.waveEnd = we; }
        protected boolean getWaveEnd(){ return waveEnd; }
        
        // Wave timers
        protected void setWavePause(int wp){ this.wavePause = wp; }
        protected int getWavePause(){ return wavePause; }
        protected void setWaveStart(int ws){ this.waveStart = ws; }
        protected int getWaveStart(){ return waveStart; }
        
        // Wave Number
        protected void setWaveNum(int wn){ this.waveCounter = wn; }
        protected int getWaveNum(){ return waveCounter; }
        
        protected int getBossRound(){ return bossRound; }
        
        // Timer counters
        protected void setSec(int t){ this.seconds = t; }
        protected int getSec(){ return seconds; }
        protected void setMin(int t){ this.minutes = t; }
        protected int getMin(){ return minutes; }
}
