package simulation.util.gravity;

import simulation.util.Vec2;

public interface Gravity {
  public Vec2 getPosition();

  public short getStrength();

  public short getMass();

  public short getImmortality();

  public Vec2 getVelocity();

  public short getNearRadius();
}
