package simulation.body.object;

import java.awt.Color;
import java.awt.Graphics;
import java.util.stream.Stream;
import simulation.Settings;
import simulation.body.particle.Particle;
import simulation.util.Vec2;
import simulation.util.constructor.ObjectParams;

public class Circle extends Solid {

  protected final short radius;

  public Circle(ObjectParams params) {
    super(params.position());
    radius = params.radius();
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
  public Vec2 getCenter() {
    return position;
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

    final Vec2 dv = new Vec2(position).sub(p.getPosition());

    if (dv.getLength() > radius + p.getRadius()) return;

    final float speed =
      p.getVelocity().dot(dv.normalise()) * Settings.get(Settings.COR);

    if (speed <= 0) return;

    p.getVelocity().sub(dv.mul(speed * 2));
  }
}
