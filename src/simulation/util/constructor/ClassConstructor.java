package simulation.util.constructor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import simulation.body.object.Environment;
import simulation.body.particle.Particle;

public final class ClassConstructor {

  private static final Random RANDOM = new Random();

  private static final List<Class<?>> PARTICLE_CLASSES = getClassesInPackage(
    "simulation.body.particle",
    Particle.class
  );
  private static final int NUM_PARTICLE_TYPES = PARTICLE_CLASSES.size();

  public static Particle build(
    ParticleParams params,
    Class<? extends Particle> type
  ) {
    try {
      return (Particle) (
        type != null
          ? type
          : PARTICLE_CLASSES.get(RANDOM.nextInt(NUM_PARTICLE_TYPES))
      ).getConstructor(ParticleParams.class)
        .newInstance(params);
    } catch (Exception e) {
      throw new IllegalStateException("Error creating particle");
    }
  }

  public static Environment build(ObjectParams params) {
    try {
      return params
        .type()
        .getConstructor(ObjectParams.class)
        .newInstance(params);
    } catch (Exception e) {
      throw new IllegalStateException("Error creating object");
    }
  }

  public static List<Class<?>> getClassesInPackage(
    String packageName,
    Class<?> type
  ) {
    InputStream stream = ClassLoader
      .getSystemClassLoader()
      .getResourceAsStream(packageName.replaceAll("[.]", "/"));
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    return reader
      .lines()
      .filter(l -> l.endsWith(".class"))
      .map(l -> getClass(l, packageName))
      .filter(type::isAssignableFrom)
      .collect(Collectors.toUnmodifiableList());
  }

  private static Class<?> getClass(String className, String packageName) {
    try {
      return Class.forName(
        packageName + "." + className.substring(0, className.lastIndexOf("."))
      );
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  private ClassConstructor() {}
}
