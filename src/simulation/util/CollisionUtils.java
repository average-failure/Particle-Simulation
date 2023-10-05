package simulation.util;

import simulation.Settings;
import simulation.body.particle.Particle;

public class CollisionUtils {

  private CollisionUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static Collision particleCollision(Particle p1, Particle p2) {
    Vec2 dv = p1.getPosition().sub(p2.getPosition());

    if (
      dv.getLength() > p1.getRadius() + p2.getRadius()
    ) return new Collision();

    Vec2 nv = dv.getNormal();

    Vec2 rv = p2.getVelocity().sub(p1.getVelocity());

    float speed = rv.dot(nv) * Settings.COR;

    if (speed <= 0) return new Collision();

    return new Collision(nv, speed);
  }
}
