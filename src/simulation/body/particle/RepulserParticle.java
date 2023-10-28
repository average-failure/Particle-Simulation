package simulation.body.particle;

import java.awt.Color;
import java.util.stream.Stream;
import simulation.Settings;
import simulation.util.constructor.ParticleParams;
import simulation.util.gravity.Gravity;
import simulation.util.gravity.GravityUtils;

public class RepulserParticle extends Particle implements Gravity {

  private final short strength;

  public RepulserParticle(ParticleParams p) {
    super(p);
    strength = p.strength();
    colour = Color.GREEN;
  }

  @Override
  public short getStrength() {
    return strength;
  }

  @Override
  public short getNearRadius() {
    return (short) (Settings.MAX_RADIUS + strength / 2f);
  }

  @Override
  protected void affectNear(Particle p, Stream<Particle> nearParticles) {
    if (!(p instanceof Gravity)) return;
    nearParticles.forEach(p1 -> GravityUtils.repel((Gravity) p, p1));
  }
}
