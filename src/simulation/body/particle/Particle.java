package simulation.body.particle;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.stream.Stream;
import simulation.Settings;
import simulation.Settings.Constants;
import simulation.hash.Client;
import simulation.util.MathUtils;
import simulation.util.Vec2;
import simulation.util.constructor.ParticleParams;

public class Particle implements Client {

  private static final long serialVersionUID = 93427523424L;

  protected final Vec2 position;
  protected final Vec2 velocity;
  protected final short radius;
  protected final short mass;
  private short immortality;
  private byte intangibility = 0;
  private float lifespan;
  protected Color colour;
  private final float initialLife;
  private float lifeDrain = 1;
  private boolean grabbed = false;

  protected final Ellipse2D.Float bounds;

  /**
   * @return the bounds
   */
  public Ellipse2D.Float getBounds() {
    return bounds;
  }

  public Particle(ParticleParams p) {
    position = new Vec2(p.position());
    velocity = new Vec2(p.velocity());

    immortality = p.immortality();
    initialLife = p.initialLife();
    lifespan = p.initialLife();

    mass = p.mass();
    radius =
      (short) MathUtils.clamp(
        p.mass() / Settings.get(Constants.MASS_RADIUS_RATIO),
        Settings.get(Constants.MIN_RADIUS),
        Settings.get(Constants.MAX_RADIUS)
      );

    bounds = new Ellipse2D.Float(position.x(), position.y(), radius, radius);
  }

  /**
   * @return the immortality
   */
  public short getImmortality() {
    return immortality;
  }

  public boolean collisionEnabled() {
    return intangibility <= 0;
  }

  /**
   * @return the radius
   */
  public short getRadius() {
    return radius;
  }

  public boolean isDead() {
    return lifespan <= initialLife / 2;
  }

  /**
   * @return the mass
   */
  public short getMass() {
    return mass;
  }

  public void detectCollision(Particle other) {
    if (
      !collisionEnabled() || !other.collisionEnabled() || other.grabbed
    ) return;

    final Vec2 dv = new Vec2(position).sub(other.getPosition());

    if (dv.getLength() > radius + other.getRadius()) return;

    final Vec2 rv = new Vec2(other.getVelocity()).sub(velocity);

    final float speed = rv.dot(dv.normalise()) * Settings.get(Settings.COR);

    if (speed <= 0) return;

    if (!grabbed) {
      final float impulse = (2 * speed) / (mass + other.mass);

      velocity.add(new Vec2(dv).mul(other.mass * impulse));
      other.velocity.sub(dv.mul(mass * impulse));
    } else other.getVelocity().sub(dv.mul(speed * 2));

    final float drain = speed / 500;

    if (!grabbed) lifeDrain += drain;
    other.lifeDrain += drain;
  }

  public void update(
    short width,
    short height,
    Stream<Particle> nearParticles
  ) {
    updateCalculations(width, height, nearParticles);
    updatePosition();
  }

  /**
   * @return the position
   */
  public Vec2 getPosition() {
    return position;
  }

  /**
   * @return the velocity
   */
  public Vec2 getVelocity() {
    return velocity;
  }

  /**
   * @return the colour
   */
  public Color getColour() {
    return colour;
  }

  /**
   * @return the lifespan
   */
  public float getLifespan() {
    return lifespan;
  }

  @Override
  public float getX() {
    return position.x();
  }

  @Override
  public float getY() {
    return position.y();
  }

  public short getNearRadius() {
    return 0;
  }

  public void grab() {
    grabbed = true;
    velocity.set(0, 0);
  }

  public void release() {
    grabbed = false;
  }

  private void checkBoundaries(short width, short height) {
    float diff;

    final float x = position.x();
    final float y = position.y();

    final float vx = Math.abs(velocity.x());
    final float vy = Math.abs(velocity.y());

    final float cor = Settings.get(Settings.COR);

    if ((diff = x - radius) < 0) {
      position.setX(radius);
      velocity.setX(vx * cor + diff);
      lifeDrain += 0.012 + (vx / 10000);
    } else if ((diff = x + radius) > width) {
      position.setX((float) width - radius);
      velocity.setX(-vx * cor - (width - diff));
      lifeDrain += 0.012 + (vx / 10000);
    }

    if ((diff = y - radius) < 0) {
      position.setY(radius);
      velocity.setY(vy * cor + diff);
      lifeDrain += 0.012 + (vy / 10000);
    } else if ((diff = y + radius) > height) {
      position.setY((float) height - radius);
      velocity.setY(-vy * cor - (height - diff));
      lifeDrain += 0.012 + (vy / 10000);
    }
  }

  private void updatePosition() {
    position.add(new Vec2(velocity).mul(Settings.get(Settings.DT)));
    bounds.setFrame(
      position.x() - radius,
      position.y() - radius,
      radius * 2f,
      radius * 2f
    );
  }

  private void updateVelocity() {
    final float dt = Settings.get(Settings.DT);
    // Gravity
    velocity.add(0, Settings.get(Settings.GRAVITY) * dt);

    // Air resistance
    velocity.sub(
      new Vec2(velocity)
        .square()
        .mul(
          (float) Math.PI *
          ((radius * radius) / 50_000f) *
          Settings.get(Constants.AIR_CONSTANT) *
          Settings.get(Settings.AIR_DENSITY) *
          dt
        )
    );

    updateColour();
  }

  private void updateColour() {
    if (getClass() != Particle.class) return;
    colour =
      Color.getHSBColor(
        Math.min(330f / 360, velocity.getLength() / 800),
        0.9f,
        0.75f
      );
  }

  private void updateStats() {
    final float time = Settings.get(Settings.DT) * 100;
    if (immortality > 0) immortality -= time;
    if (intangibility > 0) intangibility -= time;

    final float dt = Settings.get(Settings.DT);
    if (lifespan > 0 && !grabbed) {
      float drain = 0.1f;

      if (lifespan > 50_000) {
        drain = 0.3f;
      } else if (lifespan > initialLife * 0.75) drain = 0.2f;

      lifespan -= Math.pow(lifespan, drain) * lifeDrain * dt;
    }
    if (lifeDrain > 1) lifeDrain -= 0.1 * dt; else lifeDrain = 1;
  }

  /**
   * @param width The width of the simulation
   * @param height The height of the simulation
   * @param nearParticles This stream will be used in overriding classes
   */
  protected void updateCalculations(
    short width,
    short height,
    Stream<Particle> nearParticles
  ) {
    updateCalculations(width, height);
  }

  private void updateCalculations(short width, short height) {
    checkBoundaries(width, height);
    updateVelocity();
    updateStats();
  }
}
