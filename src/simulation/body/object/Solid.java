package simulation.body.object;

import simulation.body.particle.Particle;
import simulation.util.Vec2;

public abstract class Solid extends Environment {

  protected Solid(Vec2 position) {
    super(position);
  }

  protected abstract void detectCollision(Particle p);
}
