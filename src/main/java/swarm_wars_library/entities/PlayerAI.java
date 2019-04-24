package swarm_wars_library.entities;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

import swarm_wars_library.comms.CommsGlobal;
import swarm_wars_library.engine.AI;
import swarm_wars_library.engine.AIMovement;
import swarm_wars_library.engine.Health;
import swarm_wars_library.engine.Shooter;
import swarm_wars_library.entities.ENTITY;
import swarm_wars_library.entities.STATE;
import swarm_wars_library.map.Map;
import swarm_wars_library.physics.Vector2D;

public class PlayerAI extends AbstractEntity implements IHealth, IAIMovement,
  IAIShooter, IScore{

  private Health health;
  private int score;
  private AI ai;
  private AIMovement aiMovement;
  private Shooter shooter;
  private int shootInterval = 0;

  //=========================================================================//
  // Constructor                                                             //
  //=========================================================================//
  public PlayerAI(PApplet sketch, ENTITY tag){
    super(tag, Map.getInstance().getPlayerScale());
    this.ai = new AI();
    this.aiMovement = new AIMovement(this.tag);
    this.setLocation(Map.getInstance().getPlayerStartingLocation(this.tag));
    this.health = new Health(this.tag);
    this.score = 0;
    this.shooter = new Shooter(this.tag, 10);
    this.updateCommsPacket();
    this.sendCommsPacket();  
  }

  //=========================================================================//
  // Update method                                                           //
  //=========================================================================//
  @Override
  public void update(){
    if(this.isState(STATE.ALIVE)){
      this.updateAIMovement();
      this.updateHealth();
      this.updateShooter();
      this.updateScore();
      this.updateState();
    }
    // Comms & explode last
    this.updateCommsPacket();
    this.sendCommsPacket();
    this.updateExplode2Dead();
  }

  //=========================================================================//
  // AI methods                                                              //
  //=========================================================================//
  @Override
  public void updateAI(){
    this.ai.update(this.getAITarget(), this.getAILocation());
  }

  @Override
  public Vector2D getAILocation(){
    return this.getLocation();
  }

  @Override
  public List<Vector2D> getAITarget(){
    // TODO make it look for closer of Player1 or Player2
    List<Vector2D> list = new ArrayList<Vector2D>();
    list.add(CommsGlobal.get("PLAYER1").getPacket(0).getLocation());
    return list;
  }

  public double getAIHeading(){
    return this.ai.getHeading();
  }

  //=========================================================================//
  // AI Movement methods                                                     //
  //=========================================================================//
  @Override
  public void updateAIMovement(){
    this.aiMovement.update();
    this.setLocation(this.getAIMovementLocation());
  }

  @Override
  public Vector2D getAIMovementLocation(){
    return this.aiMovement.getLocation();
  }

  //=========================================================================//
  // AI-Shooter methods                                                      //
  //=========================================================================//
  @Override
  public boolean isAIShoot(){
    if(this.ai.getInRange() && this.shootInterval()){
      return true;
    }
    return false;
  }

  @Override
  public boolean shootInterval(){
    this.shootInterval++;
    if(this.shootInterval % 2 == 0){
      this.shootInterval = 0;
      return true;
    }
    return false;
  }

  //=========================================================================//
  // Comms method                                                            //
  //=========================================================================//
  @Override
  public void updateCommsPacket(){
    this.commsPacket.setHealth(this.getHealth());
    this.commsPacket.setLocation(this.getLocation());
    this.commsPacket.setScore(this.getScore());
    this.commsPacket.setState(this.getState());
    this.commsPacket.setVelocity(this.getVelocity());
  }

  //=========================================================================//
  // Collision method                                                        //
  //=========================================================================//
  @Override
  public void collidedWith(ENTITY tag){
    this.takeDamage(5);
  }

  //=========================================================================//
  // Health methods                                                          //
  //=========================================================================//
  @Override
  public void updateHealth(){
    this.health.update();
  }

  @Override
  public int getHealth(){
    return this.health.getCurrentHealth();
  }

  @Override
  public void updateState(){
    if(health.isDead()){
      this.state = STATE.EXPLODE;
    }
  }

  @Override
  public void takeDamage(int damage){
    this.health.takeDamage(damage);
  }

  //=========================================================================//
  // Score methods                                                           //
  //=========================================================================//
  @Override
  public void updateScore(){
    this.setScore(CommsGlobal.get(this.tag.toString())
                             .getPacket(0)
                             .getScore());
  }

  @Override
  public int getScore(){
    return this.score;
  }

  @Override
  public void setScore(int score){
    this.score = score;
  }

  //=========================================================================//
  // Shooter methods                                                         //
  //=========================================================================//
  @Override
  public void updateShooter(){
    this.shooter.update();
    this.shoot();
  }

  @Override
  public void shoot(){
    if(isAIShoot()){
      this.shooter.shoot(this.getLocation(), this.getHeading());
    }
  }

  @Override
  public ArrayList<Bullet> getBullets(){
    return this.shooter.getMagazine();
  }
}
