package simulation.body.particle;

import java.awt.Color;
import java.util.Random;
import simulation.util.constructor.ParticleParams;

public class SplatParticle extends Particle {

  private static final Random RANDOM = new Random();

  public SplatParticle(ParticleParams p) {
    super(p);
    colour =
      new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());
  }

  @Override
  public boolean isDead() {
    return lifespan <= initialLife * 0.75;
  }

  @Override
  public boolean detectCollision(Particle other) {
    if (!super.detectCollision(other)) return false;
    return isTooFast();
  }

  public boolean updateSplat(short width, short height) {
    if (checkBoundaries(width, height) && isTooFast()) return true;
    updateVelocity();
    updateStats();
    updatePosition();
    return false;
  }

  private boolean isTooFast() {
    return velocity.getLengthSq() > 100_000;
  }
}
