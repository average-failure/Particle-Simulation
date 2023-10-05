package simulation.util;

import simulation.body.object.Environment;
import simulation.body.particle.Particle;

public class ClassConstructor {

  private ClassConstructor() {
    throw new IllegalStateException("Utility class");
  }

  public static Particle buildParticle(
    Particle params,
    Class<? extends Particle> type
  ) {
    try {
      return type.getConstructor(Particle.class).newInstance(params);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Environment buildEnvironment(
    Environment params,
    Class<? extends Environment> type
  ) {
    try {
      return type.getConstructor(Environment.class).newInstance(params);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
