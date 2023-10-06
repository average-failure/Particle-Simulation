package simulation.util.collision;

import simulation.Settings;
import simulation.body.particle.Particle;
import simulation.util.Vec2;

public final class CollisionUtils {

  public static Collision particleCollision(Particle p1, Particle p2) {
    Vec2 dv = new Vec2(p1.getPosition()).sub(p2.getPosition());

    if (
      dv.getLength() > p1.getRadius() + p2.getRadius()
    ) return new Collision();

    Vec2 nv = dv.getNormal();

    Vec2 rv = new Vec2(p2.getVelocity()).sub(p1.getVelocity());

    float speed = rv.dot(nv) * Settings.COR;

    if (speed <= 0) return new Collision();

    return new Collision(nv, speed);
  }

  private CollisionUtils() {
    throw new IllegalStateException("Utility class");
  }
}
