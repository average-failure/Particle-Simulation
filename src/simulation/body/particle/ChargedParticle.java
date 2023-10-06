package simulation.body.particle;

import java.awt.Color;
import java.util.Random;
import java.util.stream.Stream;
import simulation.Settings;
import simulation.util.constructor.ParticleParams;
import simulation.util.gravity.Gravity;
import simulation.util.gravity.GravityUtils;

public class ChargedParticle extends Particle implements Gravity {

  public enum Charge {
    POSITIVE,
    NEGATIVE;

    private static final Charge[] VALUES = values();
    private static final int SIZE = VALUES.length;
    private static final Random RANDOM = new Random();

    public static Charge random() {
      return VALUES[RANDOM.nextInt(SIZE)];
    }

    public Color colour() {
      switch (this) {
        case POSITIVE:
          return Color.RED;
        case NEGATIVE:
          return Color.BLUE;
        default:
          return null;
      }
    }
  }

  private final short strength;
  private final Charge charge;

  /**
   * @return the charge
   */
  public Charge getCharge() {
    return charge;
  }

  public ChargedParticle(ParticleParams p) {
    super(p);
    strength = p.strength();
    charge = p.charge();
    colour = charge.colour();
  }

  @Override
  public short getStrength() {
    return strength;
  }

  @Override
  protected void updateCalculations(
    short width,
    short height,
    Stream<Particle> nearParticles
  ) {
    super.updateCalculations(width, height, nearParticles);

    nearParticles.forEach(p -> {
      if (
        (
          p instanceof ChargedParticle && charge == ((ChargedParticle) p).charge
        ) ||
        (p instanceof AttractorParticle && charge == Charge.POSITIVE) ||
        (p instanceof RepulserParticle && charge == Charge.NEGATIVE)
      ) {
        GravityUtils.repel(this, p);
      } else if (
        p instanceof ChargedParticle ||
        p instanceof AttractorParticle ||
        p instanceof RepulserParticle
      ) {
        GravityUtils.attract(this, p);
      }
    });
  }

  @Override
  public short getNearRadius() {
    return (short) (Settings.MAX_RADIUS + strength / 3);
  }
}
