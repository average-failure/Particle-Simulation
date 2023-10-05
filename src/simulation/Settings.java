package simulation;

public class Settings {

  private Settings() {
    throw new IllegalStateException("Utility class");
  }

  public static final byte MIN_RADIUS = 3;
  public static final byte MAX_RADIUS = 30;
  public static final byte CELL_SIZE = (MAX_RADIUS - MIN_RADIUS) / 2;
  public static final byte MASS_RADIUS_RATIO = 10;
  public static final short MIN_MASS = MIN_RADIUS * MASS_RADIUS_RATIO;
  public static final short MAX_MASS = MAX_RADIUS * MASS_RADIUS_RATIO;

  public static final short TIME_FACTOR = 100;
  public static final float DT = (float) TIME_FACTOR / 1000;
  public static final float GRAVITY = 9.8f;
  public static final float SOFTENING_CONSTANT = 0.15f;
  public static final float COR = 0.95f;

  public static final short INITIAL_PARTICLES = 100;
}
