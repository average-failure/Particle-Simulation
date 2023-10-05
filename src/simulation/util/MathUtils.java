package simulation.util;

import java.util.Random;

public class MathUtils {

  private static final Random RANDOM = new Random();

  private MathUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static double clamp(double value, double min, double max) {
    return Math.max(Math.min(value, max), min);
  }

  public static float randRange(float max, float min) {
    return RANDOM.nextFloat(max - min + Float.MIN_VALUE) + min;
  }

  public static int randRange(int max, int min) {
    return RANDOM.nextInt(max - min + 1) + min;
  }

  public static short randRange(short max, short min) {
    return (short) (RANDOM.nextInt(max - min + 1) + min);
  }

  public static int randInt(int bound) {
    return RANDOM.nextInt(bound + 1);
  }

  public static float randFloat(float bound) {
    return RANDOM.nextFloat(bound + Float.MIN_VALUE);
  }
}
