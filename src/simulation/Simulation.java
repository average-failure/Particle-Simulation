package simulation;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;
import simulation.body.object.Environment;
import simulation.body.particle.Particle;
import simulation.hash.Client;
import simulation.hash.SpatialHash;
import simulation.util.ClassConstructor;
import simulation.util.MathUtils;
import simulation.util.Vec2;

public class Simulation implements Serializable {

  private static final long serialVersionUID = 148627507412075L;

  private final SpatialHash hash = new SpatialHash();

  private final HashSet<Particle> particles = new HashSet<>();
  private final HashSet<Environment> objects = new HashSet<>();

  private short width;
  private short height;

  public void start() {
    for (short i = 0; i < Settings.INITIAL_PARTICLES; i++) {
      particles.add(
        new Particle(
          MathUtils.randRange(width, 0),
          MathUtils.randRange(height, 0)
        )
      );
    }
  }

  public void resize(short width, short height) {
    this.width = width;
    this.height = height;
  }

  private void envCalculations(Environment o) {
    o.update(findNearParticles(o.getCenter(), o.getNearRadius()));
  }

  private void calculations(Particle p) {
    hash.removeClient(p);

    p.update(width, height, findNearParticles(p, p.getNearRadius()));

    if (p.collisionEnabled()) {
      findNearParticles(p, (short) (p.getRadius() + Settings.MAX_RADIUS))
        .filter(Particle::collisionEnabled)
        .forEach(p::detectCollision);
    }

    hash.newClient(p);
  }

  private void splitParticle(Particle p) {
    splitParticle(p, p.getClass());
  }

  private void splitParticle(Particle p, Class<? extends Particle> type) {
    final float LOSS = 0.95f;
    final float THRESHOLD = (float) p.getRadius() / 2;

    final ArrayList<Short> masses = new ArrayList<>();

    float r = p.getMass() * LOSS;

    while (r > 0) {
      if (r < Settings.MIN_MASS) break;

      short s = (short) Math.floor(MathUtils.randRange(r, Settings.MIN_MASS));
      masses.add(MathUtils.randInt(masses.size()), s);
      r -= s;
    }

    int parts = masses.size();
    float initialLife = (p.getLifespan() * LOSS) / parts;

    float x = p.getPosition().getX();
    float y = p.getPosition().getY();

    Vec2 velocity = p.getVelocity().multiplyScalar(LOSS);

    masses.forEach(mass -> {
      float angle = 0;

      if (parts > 1) {
        if (parts < 5) {
          angle = MathUtils.randFloat((float) Math.PI * 2);
        } else angle =
          MathUtils.randFloat(
            (float) Math.min((float) parts / 20, Math.PI / 2)
          );
      }

      Particle newP = new Particle(
        new Vec2(
          x + MathUtils.randRange(parts, -parts),
          y + MathUtils.randRange(parts, -parts)
        ),
        velocity.rotate(angle),
        mass,
        (short) MathUtils.randRange(150, 30),
        initialLife
      );

      if (newP.getRadius() > THRESHOLD) {
        splitParticle(newP, type);
      } else newParticle(newP, type);
    });

    deleteParticle(p);
  }

  private void deleteParticle(Particle p) {
    hash.removeClient(p);
    particles.remove(p);
  }

  public void newParticle(Particle p, Class<? extends Particle> type) {
    Particle newP = p;
    if (p.getClass() != type) {
      newP = ClassConstructor.buildParticle(p, type);
    }
    hash.newClient(newP);
    particles.add(newP);
  }

  public void newParticle(float x, float y) {
    newParticle(new Particle(x, y), Particle.class);
  }

  private Stream<Particle> findNearParticles(Client p, short radius) {
    return Arrays
      .stream(hash.findNear(p, radius))
      .filter(Particle.class::isInstance)
      .map(c -> (Particle) c);
  }

  public void update() {
    ArrayList<Particle> split = new ArrayList<>();
    ArrayList<Particle> delete = new ArrayList<>();
    particles.forEach(p -> {
      calculations(p);
      if (p.isDead()) {
        if (p.getMass() > Settings.MIN_MASS * 2) {
          split.add(p);
        } else delete.add(p);
      }
    });
    split.forEach(this::splitParticle);
    delete.forEach(this::deleteParticle);

    objects.forEach(this::envCalculations);
  }

  public void draw(Graphics g) {
    g.drawRect(0, 0, 100, 100);
    g.drawRect(width - 100, height - 100, 100, 100);
    particles.forEach(p -> {
      g.setColor(p.getColour());
      short radius = p.getRadius();
      g.fillOval(
        Math.round(p.getX()) - radius,
        Math.round(p.getY()) - radius,
        radius * 2,
        radius * 2
      );
    });
  }
}
