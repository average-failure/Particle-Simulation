package simulation.body.object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.util.stream.Stream;
import simulation.Settings;
import simulation.body.particle.Particle;
import simulation.util.Vec2;
import simulation.util.constructor.ObjectParams;

public class Circle extends Solid {

  protected final short radius;

  protected final Ellipse2D.Float bounds;

  public Circle(ObjectParams params) {
    super(params.position(), 0, 0);
    radius = params.radius();
    bounds =
      new Ellipse2D.Float(
        position.x() - radius,
        position.y() - radius,
        radius * 2f,
        radius * 2f
      );
  }

  @Override
  public float getX() {
    return position.x();
  }

  @Override
  public float getY() {
    return position.y();
  }

  @Override
  public short getNearRadius() {
    return (short) (radius + Settings.get(Settings.Constants.MAX_RADIUS));
  }

  @Override
  public void update(Stream<Particle> nearParticles) {
    nearParticles.forEach(this::detectCollision);
  }

  @Override
  public void draw(Graphics g) {
    g.setColor(Color.LIGHT_GRAY);
    g.fillOval(
      Math.round(position.x() - radius),
      Math.round(position.y() - radius),
      radius * 2,
      radius * 2
    );
  }

  @Override
  protected void detectCollision(Particle p) {
    if (!p.collisionEnabled()) return;

    final Vec2 pPos = p.getPosition();
    final Vec2 dv = new Vec2(position).sub(pPos);
    final int r = radius + p.getRadius();

    if (dv.getLength() > r) return;

    final double angle = Math.atan2(-dv.y(), -dv.x());
    final Vec2 distance = new Vec2(
      (float) Math.cos(angle),
      (float) Math.sin(angle)
    )
      .mul(r);
    pPos.set(position.x() + distance.x(), position.y() + distance.y());

    final Vec2 pVelocity = p.getVelocity();
    pVelocity.add(distance.add(dv));

    final float speed =
      pVelocity.dot(dv.normalise()) * Settings.get(Settings.COR);

    if (speed <= 0) return;

    pVelocity.sub(dv.mul(speed * 2));
  }

  @Override
  public RectangularShape getBounds() {
    return bounds;
  }
}
