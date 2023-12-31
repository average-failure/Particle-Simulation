package simulation;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import simulation.Settings.Constants;
import simulation.body.object.*;
import simulation.body.particle.*;
import simulation.hash.Client;
import simulation.hash.SpatialHash;
import simulation.util.MathUtils;
import simulation.util.Vec2;
import simulation.util.constructor.*;

public class Simulation implements Serializable {

  private class Flag {

    private boolean value = false;

    public boolean isTrue() {
      return value;
    }

    public void setValue(boolean value) {
      this.value = value;
    }
  }

  private final record Grab(Particle particle, float xOffset, float yOffset) {}

  private static final long serialVersionUID = 148627507412075L;

  private final SpatialHash hash = new SpatialHash();
  private final Set<Particle> particles = ConcurrentHashMap.newKeySet();
  private final Set<Environment> objects = ConcurrentHashMap.newKeySet();
  private final Set<Grab> grabbed = new HashSet<>();
  private final transient ExecutorService pool = Executors.newFixedThreadPool(
    8
  );
  private final List<Future<?>> futures = new ArrayList<>();

  private short width;
  private short height;

  private Vec2 preGrabPos;
  private Vec2 grabPos;

  public void start() {
    for (short i = 0; i < Settings.get(Constants.INITIAL_PARTICLES); i++) {
      newParticle(
        new ParticleParams(
          MathUtils.randRange(width, 0),
          MathUtils.randRange(height, 0)
        ),
        Settings.INITIAL_PARTICLE_TYPE
      );
    }
  }

  public void resize(short width, short height) {
    this.width = width;
    this.height = height;
  }

  public int getNumParticles() {
    return particles.size();
  }

  public int getNumObjects() {
    return objects.size();
  }

  public Particle newParticle(
    ParticleParams p,
    Class<? extends Particle> type
  ) {
    Particle newP = ClassConstructor.build(p, type);
    hash.newClient(newP);
    particles.add(newP);
    return newP;
  }

  public Particle newParticle(
    Vec2 position,
    Vec2 velocity,
    Class<? extends Particle> type
  ) {
    return newParticle(new ParticleParams(position, velocity), type);
  }

  public Environment newObject(ObjectParams params) {
    Environment newO = ClassConstructor.build(params);
    hash.newClient(newO);
    objects.add(newO);
    return newO;
  }

  public Environment deleteObject(float x, float y) {
    for (Environment o : objects) {
      if (o.getBounds().contains(x, y)) return deleteObject(o);
    }
    return null;
  }

  public Environment deleteObject(Vec2 position) {
    return deleteObject(position.x(), position.y());
  }

  public Environment deleteObject(Environment o) {
    hash.removeClient(o);
    objects.remove(o);
    return o;
  }

  public void update() {
    futures.clear();

    particles.forEach(p ->
      futures.add(
        pool.submit(() -> {
          if (calculations(p)) return;
          p.affectNear(findNearParticles(p, p.getNearRadius()));
          if (collisionCheck(p)) return;
        })
      )
    );
    futures.forEach(this::block);

    objects.forEach(this::envCalculations);

    updateGrab();
  }

  private void block(Future<?> f) {
    try {
      f.get();
    } catch (InterruptedException e) {
      System.err.println("InterruptedException");
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      System.err.println("ExecutionException");
      e.printStackTrace();
    }
  }

  public void draw(Graphics2D g) {
    g.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    g.setColor(Environment.COLOUR);
    objects.forEach(o -> o.draw(g));

    g.setComposite(AlphaComposite.SrcOver);
    particles.forEach(p -> {
      g.setColor(p.getColour());
      g.fill(p.getBounds());
    });
  }

  public void grab(Vec2 position) {
    preGrabPos = new Vec2(position);
    grabPos = new Vec2(position);
    particles.forEach(p -> {
      if (p.getBounds().contains(grabPos.x(), grabPos.y())) {
        grabbed.add(
          new Grab(p, grabPos.x() - p.getX(), grabPos.y() - p.getY())
        );
        p.grab();
      }
    });
  }

  public void moveGrab(Vec2 position) {
    if (grabbed.isEmpty()) return;

    preGrabPos.set(grabPos);
    grabPos.set(position).sub(preGrabPos);
    grabbed.forEach(g -> g.particle.getVelocity().add(grabPos));
    grabPos.set(position);
  }

  public void releaseGrab() {
    preGrabPos = null;
    grabPos = null;

    if (grabbed.isEmpty()) return;

    grabbed.forEach(g -> g.particle.release());
    grabbed.clear();
  }

  private void envCalculations(Environment o) {
    o.update(findNearParticles(o.getCenter(), o.getNearRadius()));

    if (o instanceof Splat && ((Splat) o).isDead()) deleteObject(o);
    // TODO: splat particles on collision with objects
  }

  /**
   * @param p the particle to do calculations for
   * @return {@code true} if the particle is deleted, {@code false} otherwise
   */
  private boolean calculations(Particle p) {
    hash.removeClient(p);

    boolean delete = false;
    boolean split = false;

    if (p.getClass() == SplatParticle.class) {
      if (((SplatParticle) p).updateSplat(width, height)) delete = true;
    } else p.update(width, height);

    if (p.isDead()) {
      if (p.getMass() > Settings.get(Constants.MIN_MASS) * 2) {
        split = true;
      } else delete = true;
    }

    if (delete) deleteParticle(p); else if (split) splitParticle(p);
    return delete || split;
  }

  /**
   * @param p the particle to check collisions for
   * @return {@code true} if the particle is deleted, {@code false} otherwise
   */
  private boolean collisionCheck(Particle p) {
    if (!p.collisionEnabled()) return false;

    final Flag delete = new Flag();

    findNearParticles(
      p,
      (short) (p.getRadius() + Settings.get(Constants.MAX_RADIUS))
    )
      .filter(Particle::collisionEnabled)
      .forEach(p2 -> {
        if (p.detectCollision(p2) && p instanceof SplatParticle) {
          delete.setValue(true);
        }
      });

    if (delete.isTrue()) deleteParticle(p); else hash.newClient(p);
    return delete.isTrue();
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

    final float x = p.position().x();
    final float y = p.position().y();

    final Vec2 velocity = p.velocity().mul(LOSS);

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

  private Particle deleteParticle(Particle p) {
    hash.removeClient(p);
    particles.remove(p);

    if (p.getClass() == SplatParticle.class) {
      newObject(
        new ObjectParams(
          p.getPosition(),
          (short) (p.getRadius() * 10),
          p.getColour()
        )
      );
    }

    return p;
  }

  private Stream<Particle> findNearParticles(Client c, short radius) {
    return findNearParticles(new Vec2(c.getX(), c.getY()), radius);
  }

  private Stream<Particle> findNearParticles(Vec2 c, short radius) {
    return hash
      .findNear(c, radius)
      .stream()
      .filter(Particle.class::isInstance)
      .map(c2 -> (Particle) c2);
  }

  private void updateGrab() {
    if (grabbed.isEmpty()) return;

    grabbed.forEach(g -> {
      g.particle
        .getPosition()
        .set(grabPos.x() - g.xOffset, grabPos.y() - g.yOffset);
      g.particle.getVelocity().pow(0.95f);
    });
  }
}
