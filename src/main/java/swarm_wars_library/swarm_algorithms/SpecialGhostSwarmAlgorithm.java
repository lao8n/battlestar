package swarm_wars_library.swarm_algorithms;

import java.util.ArrayList;

import swarm_wars_library.comms.CommsGlobal;
import swarm_wars_library.comms.CommsPacket;
import swarm_wars_library.entities.ENTITY;
import swarm_wars_library.entities.STATE;
import swarm_wars_library.entities.Tag;
import swarm_wars_library.physics.Transform;
import swarm_wars_library.physics.RigidBody;
import swarm_wars_library.physics.Vector2D;
import swarm_wars_library.swarm_algorithms.AbstractSwarmAlgorithm;

public class SpecialGhostSwarmAlgorithm extends AbstractSwarmAlgorithm {

  private int id;
  public RigidBody rb;
  public Transform transform;
  private SpecialGhostSwarmRules specialGhostSwarmRules;
  private double orbitDistance = 200;
  private double stopDistance = 20;
  private Vector2D separateV2D;
  private Vector2D seekMotherShipV2D;
  private double weightSeparate = 0.2;
  private double weightMotherShip = 1.5;
  private double weightAvoidEdge = 0.1;
  private static int count = 0;

  //=========================================================================//
  // DefendFlock Constructor                                                 //
  //=========================================================================//
  public SpecialGhostSwarmAlgorithm(ENTITY tag, int id, Transform transform,
                                   RigidBody rb){
    super(tag, transform);
    this.id = id;
    this.rb = rb;
    this.transform = transform;
    this.specialGhostSwarmRules = new SpecialGhostSwarmRules(this.id,
            this.rb,
            this.transform);
  }

  //=========================================================================//
  // Swarm Algorithm                                                         //
  //=========================================================================//
  @Override
  public void applySwarmAlgorithm(){
    // Get vectors from rules
    ArrayList<Vector2D> rulesV2D = this.specialGhostSwarmRules
            .iterateOverSwarm(this.tag);
    this.separateV2D = rulesV2D.get(0);
    this.seekMotherShipV2D = this.seekMotherShip();

    // Apply weights to vectors
    this.separateV2D.mult(this.weightSeparate);
    this.seekMotherShipV2D.mult(this.weightMotherShip);
    this.avoidEdge(this.weightAvoidEdge);

    // Apply forces
    this.rb.applyForce(this.separateV2D);
    this.rb.applyForce(this.seekMotherShipV2D);
    this.transform.setHeading(this.rb.getVelocity().heading());
    this.rb.update(this.transform.getLocation(), this.transform.getHeading());

    // Bring back some dead bots
    ArrayList<CommsPacket> allBots = CommsGlobal.get(this.tag.toString()).getPackets();
    int botID;
    for(CommsPacket i: allBots){
      if (i.getState().equals(STATE.DEAD) && count < 10){
        botID = i.getId();
        if (botID == this.id) {
          setState(STATE.ALIVE);
          System.out.println(i);
        }
        count++;
        System.out.println(i);
        }
      }

  }

  //=========================================================================//
  // Misc Swarm Rules                                                        //
  //=========================================================================//
  @Override
  public Vector2D seekMotherShip(){
    Vector2D locationMotherShip =
            CommsGlobal.get(Tag.getMotherShipTag(this.tag).toString())
                    .getPacket(0)
                    .getLocation();
    Vector2D target = Vector2D.sub(locationMotherShip,
            transform.getLocation());
    target = this.findOrbit(target);
    target = this.checkStopDistance(target);
    target.limit(rb.getMaxForce());
    return target;
  }

  private Vector2D checkStopDistance(Vector2D desired) {
    double dist = desired.mag();
    desired.normalise();
    if (dist > this.stopDistance) {
      desired.mult(rb.getMaxSpeed());
    }
    else {
      double mappedSpeed = dist * rb.getMaxSpeed() / (this.stopDistance);
      desired.mult(mappedSpeed);
    }
    return desired;
  }

  private Vector2D findOrbit(Vector2D desired) {
    Vector2D orbitTarget = new Vector2D(desired.getX(), desired.getY());
    orbitTarget.normalise();
    orbitTarget.mult(this.orbitDistance);
    desired.sub(orbitTarget);

    return desired;
  }

  //=========================================================================//
  // Getters & setters for swarm weights                                     //
  //=========================================================================//
  public double getWeightSeparate(){
    return this.weightSeparate;
  }

  public void setWeightSeparate(double weight){
    this.weightSeparate = weight;
  }
}