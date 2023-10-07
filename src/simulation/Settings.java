package simulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum Settings {
  TIME_FACTOR,
  DT,
  GRAVITY,
  SOFTENING_CONSTANT,
  COR,
  ATTRACTION_STRENGTH,
  REPULSION_STRENGTH;

  public enum Constants {
    MIN_RADIUS,
    MAX_RADIUS,
    CELL_SIZE,
    MASS_RADIUS_RATIO,
    MIN_MASS,
    MAX_MASS,
    INITIAL_PARTICLES,
  }

  private static final Properties props = new Properties();

  public static float get(Settings setting) {
    return Float.parseFloat(props.getProperty(setting.name()));
  }

  public static float get(Constants constant) {
    return Float.parseFloat(props.getProperty(constant.name()));
  }

  public static void put(Settings setting, float value) {
    props.setProperty(setting.name(), Float.toString(value));
  }

  private static void put(Constants setting, float value) {
    props.setProperty(setting.name(), Float.toString(value));
  }

  public static void load() {
    try (
      InputStream in = Settings.class.getResourceAsStream("config.properties")
    ) {
      if (in == null) throw new IllegalStateException(
        "Cannot find config.properties"
      );

      props.load(in);

      put(
        Constants.CELL_SIZE,
        (get(Constants.MAX_RADIUS) - get(Constants.MIN_RADIUS)) / 2
      );
      put(
        Constants.MIN_MASS,
        get(Constants.MIN_RADIUS) * get(Constants.MASS_RADIUS_RATIO)
      );
      put(
        Constants.MAX_MASS,
        get(Constants.MAX_RADIUS) * get(Constants.MASS_RADIUS_RATIO)
      );
      put(DT, get(TIME_FACTOR) / 1000);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
