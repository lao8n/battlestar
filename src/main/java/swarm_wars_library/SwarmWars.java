package swarm_wars_library;

import processing.core.PApplet;

import swarm_wars_library.engine.*;
import java.util.Random;
import java.util.*;

/*control which screen is active by setting/updating gameScreen var
0: initial screen
1: game screen - game object
2: game-over screen
*/

public class SwarmWars extends PApplet {

  // Player must be here so that event listeners can access it
  Entity player;

  // Entity list that has all our game things.
  ArrayList < Entity > entityList = new ArrayList < Entity > ();
  // Entity builder class
  EntityBuilder eb = new EntityBuilder(this);

  // To render UI / Game screens
  Render render = new Render(this, width);

  int MAXSCREENS = 3;
  int gameScreen = 0;
  int initScreenTimer = 120;
  int numBots = 100;
  int numTurrets = 5;

  int pointsToAdd = 0;

  // global comms channel any entity that has comms should set comms to this
  CommsGlobal comms = new CommsGlobal();

  public void setup() {
    frameRate(60); // We will need to test how frameRate affects our network - slower FR = less messages per second

    /* GUIDE TO ADDING NEW THINGS
      Use the EntityBuilder, for example: player = eb.newPlayer()
      this creates new entity - and automatically sets alls it's components
      optional - if has comms. add a space for it in a CommsChannel and set it's comms to the global comms
      add the entity to the entityList
    */

    // set up comms before entities
    comms.add("PLAYER", new CommsChannel(numBots + 1));
    comms.add("ENEMY", new CommsChannel(numTurrets)); // we will add 1 turret therefore we have 1 item in enemy comms channel

    // add a player
    player = eb.newPlayer();
    player.setComms(comms);
    entityList.add(player);
    //add player bullets
    entityList.addAll(player.getMagazine());

    //add player bots
    for (int i = 0; i < numBots; i++) {
      Entity bot = eb.newBot();
      bot.setSwarmLogic();
      bot.setComms(comms);
      entityList.add(bot);
      // Note: if bots later get shooters: need to add magazines here
    }

    // add an Enemy Turrets
    for (int i = 0; i < numTurrets; i++){
      Entity turret = eb.newTurret();
      turret.setPosition(Math.random() * width +1, Math.random() * height + 1);
      turret.setComms(comms);
      entityList.add(turret);
      // Add enemy shooter magazines (bullets)
      entityList.addAll(turret.getMagazine());
    }

    // IMPORTANT to do at end of setup - sets all initial packets to current
    comms.update();
  }

  public void settings() {
    size(900, 700, "processing.awt.PGraphicsJava2D");
  }

  public void draw() {
    //display contents of the current screen
    if (gameScreen == 0) {
      initScreen();
    } else if (gameScreen == 1) {
      gameScreen();
    } else {
      gameOverScreen();
    }
  }

  /*--------GAME SCREENS ----*/

  public void initScreen() {
    render.drawInitScreen((float) width, (float) height);

    // After timer, switch to game
    if (initScreenTimer-- < 0) {
      gameScreen = 1;
    }
  }

  // >>>>>> MAIN GAME LOOP <<<<<<<<<<
  public void gameScreen() {
    background(22, 0, 8);

    // Points player earns in a loop
    pointsToAdd = 0;

    // Update all game things
    for (int i = entityList.size()-1; i >= 0; i--) {
      entityList.get(i).update();

      // Collision detection - avoids double checking
      for(int j = entityList.size()-1; j > i; j--){

        // Stop checking i if entity dies
        if (entityList.get(i).isDead()){j = i;}

        // All responses to collisions handled in BoxCollider
        BoxCollider.boundingCheck(entityList.get(i), entityList.get(j));
      }

      // Remove if entity dead
      if (entityList.get(i).isDead()){
        // Respawn if turret
        if (entityList.get(i).getTag().equals(Tag.ENEMY)){
          // Give player points for kill
          pointsToAdd += 10;

          entityList.get(i).setPosition(Math.random() * width +1, Math.random() * height + 1);
          entityList.get(i).setAlive();
          entityList.get(i).setAlive(true);
        // If player, move to game over
        } else if (entityList.get(i).getTag().equals(Tag.PLAYER)){
          gameScreen = 3;
        }else {
          entityList.remove(i);
        }
      }      
    }

    // Add points player earned for enemies killed this loop
    player.addPoints(pointsToAdd);

    // Sets future comms to current for next loop
    comms.update();

  }

  public void gameOverScreen() {
    render.drawGameOverScreen(width, height);
  }

  public void changeScreen(int k) {
    //TODO add more checks here, only change screens in certain cases
    if (k == 'n' || k == 'N') {
      gameScreen++;
      if (gameScreen > MAXSCREENS) {
        gameScreen = 0;
      }
    }
    //add pause screen on 'p'
  }

  public static void main(String[] passedArgs) {
    String[] appletArgs = new String[] {
      "swarm_wars_library.SwarmWars"
    };
    PApplet.main(appletArgs);
  }

  /* ------ EVENT LISTENERS ------ */
  public void keyPressed() {
    //changeScreen(keyCode);
    player.input.setMove(keyCode, 1);
  }

  public void keyReleased() {
    player.input.setMove(keyCode, 0);
  }

  // public void mousePressed(MouseEvent e) {
  //   player.input.setMouse(1);
    
  // }
  // public void mouseReleased(MouseEvent e) {
  //   player.input.setMouse(0);
    
  // }
}