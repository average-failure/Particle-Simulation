package simulation.body.particle;

import java.awt.Color;
import java.util.stream.Stream;
import simulation.Settings;
import simulation.util.constructor.ParticleParams;
import simulation.util.gravity.Gravity;
import simulation.util.gravity.GravityUtils;

public class AttractorParticle extends Particle implements Gravity {

  private final short strength;

  public AttractorParticle(ParticleParams p) {
    super(p);
    strength = p.strength();
    colour = Color.CYAN;
  }

  @Override
  public short getStrength() {
    return strength;
  }

  @Override
  public short getNearRadius() {
    return (short) (
      Settings.get(Settings.Constants.MAX_RADIUS) + (float) strength / 5
    );
  }

  @Override
  protected void updateCalculations(
    short width,
    short height,
    Stream<Particle> nearParticles
  ) {
    super.updateCalculations(width, height, nearParticles);

    nearParticles.forEach(p -> GravityUtils.attract(this, p));
  }
}
