package swarm_wars_library.comms;

import swarm_wars_library.physics.Vector2D;
import swarm_wars_library.entities.STATE;

public class CommsPacket {
  private Vector2D location;
  private STATE state;
  private Vector2D velocity;
  private int id;
  private int health;
  private int score;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setLocation(Vector2D location) {
    this.location = location;
  }

  public Vector2D getLocation() {
    return this.location;
  }

  public Vector2D getVelocity(){
    return this.velocity;
  }

  public void setVelocity(Vector2D velocity){
    this.velocity = velocity;
  }

  public int getHealth(){
    return this.health;
  }
  public void setHealth(int health){
    this.health = health;
  }

  public int getScore(){
    return this.score;
  }

  public void setScore(int score){
    this.score = score;
  }

  public void addScore(int score){
    this.score += score;
  }

  public STATE getState(){
    return this.state;
  }

  public void setState(STATE state){
    this.state = state;
  }
}