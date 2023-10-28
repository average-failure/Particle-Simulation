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

  public ChargedParticle(ParticleParams p) {
    super(p);
    strength = p.strength();
    charge = p.charge();
    colour = charge.colour();
  }

  /**
   * @return the charge
   */
  public Charge getCharge() {
    return charge;
  }

  @Override
  public short getStrength() {
    return strength;
  }

  @Override
  public short getNearRadius() {
    return (short) (Settings.MAX_RADIUS + strength / 3f);
  }

  @Override
  protected void affectNear(Particle p, Stream<Particle> nearParticles) {
    if (!(p instanceof Gravity)) return;
    nearParticles.forEach(p1 -> {
      if (
        (
          p1 instanceof ChargedParticle &&
          charge == ((ChargedParticle) p1).charge
        ) ||
        (p1 instanceof AttractorParticle && charge == Charge.POSITIVE) ||
        (p1 instanceof RepulserParticle && charge == Charge.NEGATIVE)
      ) {
        GravityUtils.repel((Gravity) p, p1);
      } else if (
        p1 instanceof ChargedParticle ||
        p1 instanceof AttractorParticle ||
        p1 instanceof RepulserParticle
      ) {
        GravityUtils.attract((Gravity) p, p1);
      }
    });
  }
}
