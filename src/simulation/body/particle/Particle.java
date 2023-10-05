package simulation.body.particle;

import java.awt.Color;
import java.util.stream.Stream;
import simulation.Settings;
import simulation.hash.Client;
import simulation.util.Collision;
import simulation.util.CollisionUtils;
import simulation.util.MathUtils;
import simulation.util.Vec2;

public class Particle implements Client {

  private static final long serialVersionUID = 93427523424L;

  private final Vec2 position;
  private final Vec2 velocity;
  private final short radius;
  private final short mass;
  private short immortality;
  private byte intangibility;
  private float lifespan;
  private Color colour;
  private float initialLife;
  private float lifeDrain = 1;

  public Particle(float x, float y) {
    this(
      new Vec2(x, y),
      new Vec2(0, 0),
      MathUtils.randRange(Settings.MAX_MASS, Settings.MIN_MASS)
    );
  }

  public Particle(Vec2 position, Vec2 velocity, short mass) {
    this(
      position,
      velocity,
      mass,
      (short) 0,
      MathUtils.randRange(10_000, 1_000)
    );
  }

  public Particle(Particle p) {
    this.position = new Vec2(p.position);
    this.velocity = new Vec2(p.velocity);

    this.immortality = p.immortality;
    this.intangibility = 0;
    this.initialLife = p.initialLife;
    this.lifespan = p.initialLife;

    this.mass = p.mass;
    this.radius =
      (short) MathUtils.clamp(
        p.radius,
        Settings.MIN_RADIUS,
        Settings.MAX_RADIUS
      );
  }

  public Particle(
    Vec2 position,
    Vec2 velocity,
    short mass,
    short immortality,
    float initialLife
  ) {
    this.position = position;
    this.velocity = velocity;

    this.immortality = immortality;
    this.intangibility = 0;
    this.initialLife = initialLife;
    this.lifespan = initialLife;

    this.mass = mass;
    this.radius =
      (short) MathUtils.clamp(
        (float) mass / Settings.MASS_RADIUS_RATIO,
        Settings.MIN_RADIUS,
        Settings.MAX_RADIUS
      );
  }

  public boolean collisionEnabled() {
    return intangibility <= 0;
  }

  public short getRadius() {
    return radius;
  }

  public boolean isDead() {
    return lifespan <= initialLife / 2;
  }

  public short getMass() {
    return mass;
  }

  public void detectCollision(Particle other) {
    if (!collisionEnabled() || !other.collisionEnabled()) return;

    Collision collision = CollisionUtils.particleCollision(this, other);

    if (!collision.isColliding()) return;

    float impulse = (2 * collision.getSpeed()) / (mass + other.mass);

    Vec2 normal = collision.getNormal();

    velocity.move(normal.multiplyScalar(other.mass * impulse));
    other.velocity.move(normal.multiplyScalar(-mass * impulse));

    float drain = collision.getSpeed() / 500;

    lifeDrain += drain;
    other.lifeDrain += drain;
  }

  /**
   * @param width The width of the simulation
   * @param height The height of the simulation
   * @param nearParticles This stream will be used in overriding classes
   */
  public void update(
    short width,
    short height,
    Stream<Particle> nearParticles
  ) {
    updateCalculations(width, height);
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
    position.move(velocity.multiplyScalar(Settings.DT));
  }

  private void updateVelocity() {
    velocity.addY(Settings.GRAVITY * Settings.DT);
    updateColour();
  }

  private void updateColour() {
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

  private void updateCalculations(short width, short height) {
    checkBoundaries(width, height);
    updateVelocity();
    updateStats();
  }

  @Override
  public String toString() {
    return (
      getClass().getName() +
      " [position=" +
      position +
      ", velocity=" +
      velocity +
      ", radius=" +
      radius +
      ", mass=" +
      mass +
      ", immortality=" +
      immortality +
      ", intangibility=" +
      intangibility +
      ", lifespan=" +
      lifespan +
      ", colour=" +
      colour +
      ", initialLife=" +
      initialLife +
      ", lifeDrain=" +
      lifeDrain +
      "]"
    );
  }
}
