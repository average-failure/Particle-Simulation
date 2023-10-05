package simulation.body.object;

import java.util.stream.Stream;
import simulation.body.particle.Particle;
import simulation.hash.Client;
import simulation.util.Vec2;

public abstract class Environment implements Client {

  private final Vec2 position;

  protected Environment(float x, float y) {
    this.position = new Vec2(x, y);
  }

  public abstract Client getCenter();

  /**
   * @return the x
   */
  public float getX() {
    return position.getX();
  }

  /**
   * @return the y
   */
  public float getY() {
    return position.getY();
  }

  public abstract short getNearRadius();

  public abstract void update(Stream<Particle> nearParticles);
}
