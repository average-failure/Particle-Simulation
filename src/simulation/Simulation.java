package simulation;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;
import simulation.Settings.Constants;
import simulation.body.object.Environment;
import simulation.body.particle.*;
import simulation.hash.Client;
import simulation.hash.SpatialHash;
import simulation.util.MathUtils;
import simulation.util.Vec2;
import simulation.util.constructor.ClassConstructor;
import simulation.util.constructor.ParticleParams;

public class Simulation implements Serializable {

  private static final long serialVersionUID = 148627507412075L;

  private final SpatialHash hash = new SpatialHash();

  private final HashSet<Particle> particles = new HashSet<>();
  private final HashSet<Environment> objects = new HashSet<>();

  private short width;
  private short height;

  public void start() {
    for (short i = 0; i < Settings.get(Constants.INITIAL_PARTICLES); i++) {
      newParticle(
        new ParticleParams(
          MathUtils.randRange(width, 0),
          MathUtils.randRange(height, 0)
        ),
        Particle.class
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
      findNearParticles(
        p,
        (short) (p.getRadius() + Settings.get(Constants.MAX_RADIUS))
      )
        .filter(Particle::collisionEnabled)
        .forEach(p::detectCollision);
    }

    hash.newClient(p);
  }

  private void splitParticle(Particle p) {
    ParticleParams params = new ParticleParams(
      p.getPosition(),
      p.getVelocity(),
      p.getMass(),
      p.getImmortality(),
      p.getLifespan()
    );
    if (p instanceof AttractorParticle) {
      params = params.withStrength(((AttractorParticle) p).getStrength());
    } else if (p instanceof RepulserParticle) {
      params = params.withStrength(((RepulserParticle) p).getStrength());
    } else if (p instanceof ChargedParticle) {
      params =
        params
          .withStrength(((ChargedParticle) p).getStrength())
          .withCharge(((ChargedParticle) p).getCharge());
    }

    splitParticle(params, p.getClass());
    deleteParticle(p);
  }

  private void splitParticle(ParticleParams p, Class<? extends Particle> type) {
    final float LOSS = 0.95f;
    final float THRESHOLD = (float) (
      (p.mass() / Settings.get(Constants.MASS_RADIUS_RATIO)) * 0.75
    );

    final ArrayList<Short> masses = new ArrayList<>();

    float r = p.mass() * LOSS;

    while (r > 0) {
      if (r < Settings.get(Constants.MIN_MASS)) break;

      short s = (short) Math.floor(
        MathUtils.randRange(r, Settings.get(Constants.MIN_MASS))
      );
      masses.add(MathUtils.randInt(masses.size()), s);
      r -= s;
    }

    final int parts = masses.size();
    final float initialLife = (p.initialLife() * LOSS) / parts;

    final float x = p.position().getX();
    final float y = p.position().getY();

    final Vec2 velocity = p.velocity().multiplyScalar(LOSS);

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

      ParticleParams newP = new ParticleParams(
        new Vec2(
          x + MathUtils.randRange(parts, -parts),
          y + MathUtils.randRange(parts, -parts)
        ),
        new Vec2(velocity).rotate(angle),
        mass,
        (short) MathUtils.randRange(150, 30),
        initialLife
      );

      if (mass / Settings.get(Constants.MASS_RADIUS_RATIO) > THRESHOLD) {
        splitParticle(newP, type);
      } else newParticle(newP, type);
    });
  }

  private void deleteParticle(Particle p) {
    hash.removeClient(p);
    particles.remove(p);
  }

  public void newParticle(ParticleParams p, Class<? extends Particle> type) {
    Particle newP = ClassConstructor.build(p, type);
    hash.newClient(newP);
    particles.add(newP);
  }

  public void newParticle(float x, float y) {
    newParticle(new ParticleParams(x, y), null);
  }

  private Stream<Particle> findNearParticles(Client c, short radius) {
    return Arrays
      .stream(hash.findNear(c, radius))
      .filter(Particle.class::isInstance)
      .map(c2 -> (Particle) c2);
  }

  public void update() {
    ArrayList<Particle> split = new ArrayList<>();
    ArrayList<Particle> delete = new ArrayList<>();
    particles.forEach(p -> {
      calculations(p);
      if (p.isDead()) {
        if (p.getMass() > Settings.get(Constants.MIN_MASS) * 2) {
          split.add(p);
        } else delete.add(p);
      }
    });
    split.forEach(this::splitParticle);
    delete.forEach(this::deleteParticle);

    objects.forEach(this::envCalculations);
  }

  public void draw(Graphics g) {
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
