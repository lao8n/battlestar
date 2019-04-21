package swarm_wars_library.entities;

import swarm_wars_library.engine.SwarmLogic;
import swarm_wars_library.map.Map;
import swarm_wars_library.physics.Vector2D;

public class Bot extends AbstractEntity implements ISwarm{

  private int id;
  private SwarmLogic swarmLogic;
  
  //=========================================================================//
  // Constructor                                                             //
  //=========================================================================//
  public Bot(ENTITY tag, String startingSwarmAlgorithm, int id){
    super(tag, Map.getInstance().getBotScale());
    Vector2D motherShipLocation = 
      Map.getInstance()
         .getPlayerStartingLocation(Tag.getMothershipTag(tag));
    this.setLocation(
      new Vector2D(motherShipLocation.getX() - 100 + 200 * Math.random(),
                   motherShipLocation.getY() - 100 + 200 * Math.random()));
    this.swarmLogic = 
      new SwarmLogic(this.tag, this.transform, this.rigidbody, id);
    this.swarmLogic.selectSwarmAlgorithm(startingSwarmAlgorithm);
    this.id = id;
    this.updateCommsPacket();
    this.sendCommsPacket();
  }

  //=========================================================================//
  // Update method                                                           //
  //=========================================================================//
  @Override
  public void update(){
    if(this.isState(STATE.ALIVE)){
      this.updateBot();
    }
    // Comms & explode last
    this.updateCommsPacket();
    this.sendCommsPacket();
    this.updateExplode2Dead();
  }

  //=========================================================================//
  // Bot method                                                              //
  //=========================================================================//
  public void updateBot(){
    if(this.isState(STATE.ALIVE)){
      this.updateSwarm();
    }
  }

  //=========================================================================//
  // Comms method                                                            //
  //=========================================================================//
  @Override
  public void updateCommsPacket(){
    this.commsPacket.setId(this.id);
    this.commsPacket.setState(this.getState());
    this.commsPacket.setLocation(this.getLocation());
    this.commsPacket.setVelocity(this.getVelocity());
  }

  //=========================================================================//
  // Collision method                                                        //
  //=========================================================================//
  @Override
  public void collidedWith(ENTITY tag){
    this.setState(STATE.EXPLODE);
  }

  //=========================================================================//
  // Swarm methods                                                           //
  //=========================================================================//
  @Override
  public void updateSwarm(){
    this.swarmLogic.setTransform(this.transform);
    this.swarmLogic.update();
    this.transform = this.swarmLogic.getTransform();
    // this.setLocation(this.getSwarmLocation());
    // this.setHeading(this.getSwarmHeading());
  }

  @Override
  public Vector2D getSwarmLocation(){
    return this.swarmLogic.getTransform().getLocation();
  }

  @Override
  public double getSwarmHeading(){
    return this.swarmLogic.getTransform().getHeading();
  }

}