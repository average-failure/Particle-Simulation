package simulation.body.particle;

import java.awt.Color;
import java.util.stream.Stream;
import simulation.Settings;
import simulation.hash.Client;
import simulation.util.MathUtils;
import simulation.util.Vec2;
import simulation.util.collision.Collision;
import simulation.util.collision.CollisionUtils;
import simulation.util.constructor.ParticleParams;

public class Particle implements Client {

  private static final long serialVersionUID = 93427523424L;

  protected final Vec2 position;
  protected final Vec2 velocity;
  protected final short radius;
  protected final short mass;
  private short immortality;
  private byte intangibility;
  private float lifespan;
  protected Color colour;
  private final float initialLife;
  private float lifeDrain = 1;

  public Particle(ParticleParams p) {
    position = new Vec2(p.position());
    velocity = new Vec2(p.velocity());

    immortality = p.immortality();
    intangibility = 0;
    initialLife = p.initialLife();
    lifespan = p.initialLife();

    mass = p.mass();
    radius =
      (short) MathUtils.clamp(
        (float) p.mass() / Settings.MASS_RADIUS_RATIO,
        Settings.MIN_RADIUS,
        Settings.MAX_RADIUS
      );
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
    if (!collisionEnabled() || !other.collisionEnabled()) return;

    Collision collision = CollisionUtils.particleCollision(this, other);

    if (!collision.colliding()) return;

    float impulse = (2 * collision.speed()) / (mass + other.mass);

    Vec2 normal = collision.normal();

    velocity.add(new Vec2(normal).multiplyScalar(other.mass * impulse));
    other.velocity.sub(normal.multiplyScalar(mass * impulse));

    float drain = collision.speed() / 500;

    lifeDrain += drain;
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
    return position.getX();
  }

  @Override
  public float getY() {
    return position.getY();
  }

  public short getNearRadius() {
    return 0;
  }

  private void checkBoundaries(short width, short height) {
    float diff;

    final float x = position.getX();
    final float y = position.getY();

    final float vx = Math.abs(velocity.getX());
    final float vy = Math.abs(velocity.getY());

    if ((diff = x - radius) < 0) {
      position.setX(radius);
      velocity.setX(vx * Settings.COR + diff);
      lifeDrain += 0.012 + (vx / 10000);
    } else if ((diff = x + radius) > width) {
      position.setX((float) width - radius);
      velocity.setX(-vx * Settings.COR - (width - diff));
      lifeDrain += 0.012 + (vx / 10000);
    }

    if ((diff = y - radius) < 0) {
      position.setY(radius);
      velocity.setY(vy * Settings.COR + diff);
      lifeDrain += 0.012 + (vy / 10000);
    } else if ((diff = y + radius) > height) {
      position.setY((float) height - radius);
      velocity.setY(-vy * Settings.COR - (height - diff));
      lifeDrain += 0.012 + (vy / 10000);
    }
  }

  private void updatePosition() {
    position.add(new Vec2(velocity).multiplyScalar(Settings.DT));
  }

  private void updateVelocity() {
    velocity.add(0, Settings.GRAVITY * Settings.DT);
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
    if (immortality > 0) immortality -= Settings.TIME_FACTOR;
    if (!collisionEnabled()) intangibility -= Settings.TIME_FACTOR;
    if (lifespan > 0) {
      float drain = 0.1f;

      if (lifespan > 50_000) {
        drain = 0.3f;
      } else if (lifespan > initialLife * 0.75) drain = 0.2f;

      lifespan -= Math.pow(lifespan, drain) * lifeDrain * Settings.DT;
    }
    if (lifeDrain > 1) lifeDrain -= 0.1 * Settings.DT; else lifeDrain = 1;
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
