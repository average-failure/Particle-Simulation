package simulation.util;

public class Collision {

  private final boolean isColliding;
  private final Vec2 normal;
  private final float speed;

  public Collision(Vec2 n, float s) {
    isColliding = true;
    normal = n;
    speed = s;
  }

  public Collision() {
    isColliding = false;
    normal = null;
    speed = 0;
  }

  /**
   * @return the collision
   */
  public boolean isColliding() {
    return isColliding;
  }

  /**
   * @return the normal
   */
  public Vec2 getNormal() {
    if (normal == null) throw new IllegalStateException("Not colliding");
    return normal;
  }

  /**
   * @return the speed
   */
  public float getSpeed() {
    if (speed == 0) throw new IllegalStateException("Not colliding");
    return speed;
  }

  @Override
  public String toString() {
    return (
      getClass().getName() +
      " [isColliding=" +
      isColliding +
      ", normal=" +
      normal +
      ", speed=" +
      speed +
      "]"
    );
  }
}
