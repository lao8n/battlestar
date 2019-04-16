package swarm_wars_library.swarm_algorithms;

import swarm_wars_library.swarm_algorithms.SwarmAlgorithm;
import swarm_wars_library.engine.Transform;
import swarm_wars_library.engine.RigidBody;
import swarm_wars_library.engine.Vector2D;
import swarm_wars_library.comms.CommsGlobal;

/**
 * BoidsFlock Class is an implementation of the previous SwarmLogic
 * but making use of the SwarmRule and SwarmAlgorithm abstract classes to make
 * the coder slightly cleaner. It has typical boids flock behaviour, but
 * also partially follows the Mothership.
 * <p>
 * The class contains references to GlobalComms and properties of the 
 * entity calling it including id, Transform and RigidBody.
 * It implements abstract methods applySwarmLogic() and seekMotherShip() and 
 * also includes helper functions to help with this implementation. 
 * applySwarmLogic(), in particular, contains the private class SeparateRule, 
 * AlignRule and CoheseRule() which implement the abstract class SwarmRule 
 * which is how the swarm behaviour is calculated. 
 * <p>
 * Issues
 * 1. Ideally you'd like, via say the FSM or user input, to directly tweak the
 *    mult() hyperparameters (separate_v2d.mult() and seek_mothership_v2d), but
 *    it is unclear to me how to do this and make it generic, where you have 
 *    n rules each with n potential hyperparameters. Currently the 
 *    hyperparameters are hard-coded in but this probably needs to be changed.
 * 2. Iterating over each rule, despite being what the Nature of Code guy does,
 *    might be really inefficient. I don't understand what is fast/slow in Java.
 */
public class BoidsFlock extends SwarmAlgorithm {
  private CommsGlobal comms;
  private int id;
  public RigidBody rb;
  public Transform transform;
  private SeparateRule separate_rule;
  private AlignRule align_rule;
  private CoheseRule cohese_rule;

  public BoidsFlock(CommsGlobal comms, int id, Transform transform, RigidBody rb){
    super(transform);
    this.comms = comms;
    this.id = id;
    this.rb = rb;
    this.transform = transform;
    this.separate_rule = new SeparateRule(this.comms, this.id, 
      this.rb, this.transform);
    this.align_rule = new AlignRule(this.comms, this.id, this.rb, 
      this.transform);
    this.cohese_rule = new CoheseRule(this.comms, this.id, this.rb,
      this.transform);
  }

  @Override
  public void applySwarmAlgorithm(){

    Vector2D separate_v2d = this.separate_rule.iterateOverSwarm(20);
    Vector2D align_v2d = this.align_rule.iterateOverSwarm(40);
    Vector2D cohese_v2d = this.cohese_rule.iterateOverSwarm(40);
    Vector2D random_v2d = new Vector2D(Math.random() - 0.5, Math.random() - 0.5);
    Vector2D seek_mothership_v2d = seekMotherShip(this.comms.get("PLAYER")
                                                            .getPacket(0)
                                                            .getLocation());

    separate_v2d.mult(0.05);
    align_v2d.mult(0.004);
    cohese_v2d.mult(0.003);
    random_v2d.mult(0.001);
    seek_mothership_v2d.mult(0.1);  

    this.avoidEdge(0.2);
    this.rb.applyForce(separate_v2d);
    this.rb.applyForce(align_v2d);
    this.rb.applyForce(cohese_v2d);
    this.rb.applyForce(random_v2d);
    this.rb.applyForce(seek_mothership_v2d);
    this.transform.setHeading(this.rb.getVelocity().heading());
    this.rb.update(this.transform.getPosition(), this.transform.getHeading());
  }
  
  @Override
  public Vector2D seekMotherShip(Vector2D mothership_location) {
    Vector2D target = Vector2D.sub(mothership_location, transform.getPosition());
    target.normalise();
    target.mult(rb.getMaxSpeed());
    target.limit(rb.getMaxForce());
    return target;
  }

  private class SeparateRule extends SwarmRule {  
    public SeparateRule(CommsGlobal rule_comms, int rule_id, 
      RigidBody rule_rb, Transform rule_transform){
      super(rule_comms, rule_id, rule_rb, rule_transform);
    }

    @Override
    public void swarmRule(){
      Vector2D diff = Vector2D.sub(this.rule_transform.getPosition(), 
                                        this.rule_otherBot.getLocation());
      diff.normalise();
      diff.div(this.rule_dist);
      this.rule_v2d.add(diff);
      this.rule_neighbourCount++;
    }

    @Override
    public void neighbourCountRule(){
      this.rule_v2d.div(this.rule_neighbourCount);
      this.rule_v2d.normalise();
      this.rule_v2d.mult(rb.getMaxSpeed());
    }
  }

  private class AlignRule extends SwarmRule {  
    public AlignRule(CommsGlobal rule_comms, int rule_id, 
      RigidBody rule_rb, Transform rule_transform){
      super(rule_comms, rule_id, rule_rb, rule_transform);
    }

    @Override
    public void swarmRule(){
      this.rule_v2d.add(this.rule_otherBot.getVelocity());
      this.rule_neighbourCount++;
    }

    @Override
    public void neighbourCountRule(){
      this.rule_v2d.div(this.rule_neighbourCount);
      this.rule_v2d.normalise();
      this.rule_v2d.mult(rb.getMaxSpeed());
      this.rule_v2d.sub(rb.getVelocity());
      this.rule_v2d.limit(rb.getMaxForce());
    }
  }

  private class CoheseRule extends SwarmRule {  
    public CoheseRule(CommsGlobal rule_comms, int rule_id, 
      RigidBody rule_rb, Transform rule_transform){
      super(rule_comms, rule_id, rule_rb, rule_transform);
    }

    @Override
    public void swarmRule(){
      this.rule_v2d.add(this.rule_otherBot.getLocation());
      this.rule_neighbourCount++;
    }

    @Override
    public void neighbourCountRule(){
      this.rule_v2d.div(this.rule_neighbourCount);
      this.rule_v2d.sub(this.rule_transform.getPosition());
      this.rule_v2d.normalise();
      this.rule_v2d.mult(rb.getMaxSpeed());
      this.rule_v2d.sub(rb.getVelocity());
      this.rule_v2d.limit(rb.getMaxForce());
    }
  }
}