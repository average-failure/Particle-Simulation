package simulation.body.particle;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;
import simulation.util.constructor.ParticleParams;
import simulation.util.gravity.Gravity;

public class CopyParticle extends Particle implements Gravity {

  protected static final float COPY_CHANCE = 0.2f;
  protected static final Color BASE_COLOUR = Color.WHITE;
  protected static final float COLOUR_MIX = 0.6f;
  private static final short COPY_DURATION = 1440;

  protected Particle copied;
  protected short copyDuration;

  public CopyParticle(ParticleParams p) {
    super(p);
    colour = BASE_COLOUR;
  }

  @Override
  public boolean detectCollision(Particle other) {
    if (!super.detectCollision(other)) return false;

    if (
      Math.random() <
      COPY_CHANCE *
      ((COPY_DURATION - copyDuration) / (float) COPY_DURATION)
    ) {
      if (!(other instanceof CopyParticle)) {
        copied = other;
        colour = mixColour(BASE_COLOUR, other.getColour(), COLOUR_MIX);
      } else if (((CopyParticle) other).copied != null) {
        copied = ((CopyParticle) other).copied;
        colour =
          mixColour(
            BASE_COLOUR,
            ((CopyParticle) other).copied.getColour(),
            COLOUR_MIX
          );
      } else return true;
      copyDuration = COPY_DURATION;
    }

    return true;
  }

  @Override
  public short getStrength() {
    try {
      return (short) copied.getClass().getMethod("getStrength").invoke(copied);
    } catch (
      IllegalAccessException
      | InvocationTargetException
      | NoSuchMethodException
      | SecurityException e
    ) {
      return 0;
    }
  }

  @Override
  public short getNearRadius() {
    if (copied == null) return 0;
    return copied.getNearRadius();
  }

  @Override
  public void affectNear(Stream<Particle> nearParticles) {
    if (copied == null) return;

    if (copied.getClass() == Particle.class) {
      colour = mixColour(BASE_COLOUR, getColour(velocity), COLOUR_MIX);
    } else copied.affectNear(this, nearParticles);

    if (copyDuration > 0) copyDuration -= 1; else {
      copied = null;
      colour = BASE_COLOUR;
    }
  }

  private Color mixColour(Color a, Color b, float percent) {
    if (a == null && b == null) {
      return BASE_COLOUR;
    } else if (a == null) return b; else if (b == null) return a;

    return new Color(
      (int) (a.getRed() * percent + b.getRed() * (1.0 - percent)),
      (int) (a.getGreen() * percent + b.getGreen() * (1.0 - percent)),
      (int) (a.getBlue() * percent + b.getBlue() * (1.0 - percent))
    );
  }
}
