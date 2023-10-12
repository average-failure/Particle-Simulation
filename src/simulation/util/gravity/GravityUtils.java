package simulation.util.gravity;

import simulation.Settings;
import simulation.body.particle.Particle;
import simulation.util.Vec2;

public final class GravityUtils {

  private enum Mode {
    ATTRACT,
    REPEL,
  }

  public static void attract(Gravity g, Particle p) {
    Vec2 av = calculate(Mode.ATTRACT, g, p);

    short immortality = p.getImmortality();

    if (immortality > 0) {
      p.getVelocity().add(av.div(immortality));
    } else p.getVelocity().add(av);
  }

  public static void repel(Gravity g, Particle p) {
    Vec2 av = calculate(Mode.REPEL, g, p);

    short immortality = p.getImmortality();

    if (immortality > 0) {
      p.getVelocity().sub(av.div(immortality));
    } else p.getVelocity().sub(av);
  }

  private static Vec2 calculate(Mode mode, Gravity g1, Particle g2) {
    Vec2 dv = new Vec2(g1.getPosition()).sub(g2.getPosition());
    float dSq = dv.getLengthSq();

    float strengthMultiplier = mode == Mode.ATTRACT
      ? Settings.get(Settings.ATTRACTION_STRENGTH)
      : Settings.get(Settings.REPULSION_STRENGTH);

    float force = (float) (
      g1.getStrength() *
      100 *
      strengthMultiplier /
      (
        dSq *
        Math.sqrt(dSq + Settings.get(Settings.Constants.SOFTENING_CONSTANT))
      )
    );

    float acceleration = Math.min(
      force / g2.getMass(),
      mode == Mode.ATTRACT ? 100 : 300
    );

    return dv.mul(acceleration * Settings.get(Settings.DT));
  }

  private GravityUtils() {
    throw new IllegalStateException("Utility class");
  }
}
