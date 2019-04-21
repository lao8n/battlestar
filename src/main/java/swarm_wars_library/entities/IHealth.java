package swarm_wars_library.entities;

public interface IHealth{
  
  //=========================================================================//
  // Health methods                                                          //
  //=========================================================================//
  public void updateHealth();
  public int getHealth();
  public void updateState();
  public void takeDamage(int damage);
}