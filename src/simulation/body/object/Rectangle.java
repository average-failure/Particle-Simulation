package simulation.body.object;

import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.stream.Stream;
import simulation.Settings;
import simulation.body.particle.Particle;
import simulation.util.constructor.ObjectParams;

public class Rectangle extends Environment implements Solid {

  protected final short width;
  protected final short height;

  protected final Rectangle2D.Float bounds;

  public Rectangle(ObjectParams params) {
    super(params.position(), params.width() / 2f, params.height() / 2f);
    width = params.width();
    height = params.height();

    bounds = new Rectangle2D.Float(position.x(), position.y(), width, height);
  }

  @Override
  public short getNearRadius() {
    return (short) Math.ceil(
      Math.sqrt((width * width / 4f) + (height * height / 4f)) +
      Settings.get(Settings.Constants.MAX_RADIUS)
    );
  }

  @Override
  public void update(Stream<Particle> nearParticles) {
    nearParticles.forEach(this::detectCollision);
  }

  @Override
  public float getX() {
    return center.x();
  }

  @Override
  public float getY() {
    return center.y();
  }

  @Override
  public void detectCollision(Particle p) {
    final float cor = Settings.get(Settings.COR);

    final float dx = p.getX() - getX();
    final float dy = p.getY() - getY();

    final float w = p.getRadius() + width / 2f;
    final float h = p.getRadius() + height / 2f;

    final float cw = w * dy;
    final float ch = h * dx;

    if (Math.abs(dx) <= w && Math.abs(dy) <= h) {
      if (cw > ch) {
        if (cw > -ch) {
          // Bottom
          p.getPosition().setY(p.getRadius() + position.y() + height);
          p.getVelocity().setY(Math.abs(p.getVelocity().y()) * cor);
        } else {
          // Left
          p.getPosition().setX(position.x() - p.getRadius());
          p.getVelocity().setX(-Math.abs(p.getVelocity().y()) * cor);
        }
      } else {
        if (cw > -ch) {
          // Right
          p.getPosition().setX(p.getRadius() + position.x() + width);
          p.getVelocity().setX(Math.abs(p.getVelocity().y()) * cor);
        } else {
          // Top
          p.getPosition().setY(position.y() - p.getRadius());
          p.getVelocity().setY(-Math.abs(p.getVelocity().y()) * cor);
        }
      }
    }
  }

  @Override
  public RectangularShape getBounds() {
    return bounds;
  }
}
