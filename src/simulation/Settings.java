package simulation;

import simulation.body.particle.*;

public final class Settings {

  private Settings() {}

  // Variable settings
  private static float dt = 0.1f;
  private static float gravity = 9.8f;
  private static float airDensity = 1.225f;
  private static float cor = 0.95f;
  private static float attractionStrength = 50;
  private static float repulsionStrength = 100;

  // Constant settings
  public static final byte MIN_RADIUS = 3;
  public static final byte MAX_RADIUS = 5;
  public static final byte CELL_SIZE = (MAX_RADIUS - MIN_RADIUS) / 2;
  public static final byte MASS_RADIUS_RATIO = 10;
  public static final byte MIN_MASS = MIN_RADIUS * MASS_RADIUS_RATIO;
  public static final byte MAX_MASS = MAX_RADIUS * MASS_RADIUS_RATIO;
  public static final float SOFTENING_CONSTANT = 0.15f;
  public static final float AIR_CONSTANT = 0.08f;
  public static final short INITIAL_PARTICLES = 10_000;
  public static final Class<? extends Particle> INITIAL_PARTICLE_TYPE =
    Particle.class;

  /**
   * @return the dt
   */
  public static float getDt() {
    return dt;
  }

  /**
   * @param dt the dt to set
   */
  public static void setDt(float dt) {
    Settings.dt = dt;
  }

  /**
   * @return the gravity
   */
  public static float getGravity() {
    return gravity;
  }

  /**
   * @param gravity the gravity to set
   */
  public static void setGravity(float gravity) {
    Settings.gravity = gravity;
  }

  /**
   * @return the airDensity
   */
  public static float getAirDensity() {
    return airDensity;
  }

  /**
   * @param airDensity the airDensity to set
   */
  public static void setAirDensity(float airDensity) {
    Settings.airDensity = airDensity;
  }

  /**
   * @return the cor
   */
  public static float getCor() {
    return cor;
  }

  /**
   * @param cor the cor to set
   */
  public static void setCor(float cor) {
    Settings.cor = cor;
  }

  /**
   * @return the attractionStrength
   */
  public static float getAttractionStrength() {
    return attractionStrength;
  }

  /**
   * @param attractionStrength the attractionStrength to set
   */
  public static void setAttractionStrength(float attractionStrength) {
    Settings.attractionStrength = attractionStrength;
  }

  /**
   * @return the repulsionStrength
   */
  public static float getRepulsionStrength() {
    return repulsionStrength;
  }

  /**
   * @param repulsionStrength the repulsionStrength to set
   */
  public static void setRepulsionStrength(float repulsionStrength) {
    Settings.repulsionStrength = repulsionStrength;
  }
}
