package simulation.body.object;

import java.awt.Graphics;
import java.awt.geom.RectangularShape;
import java.util.stream.Stream;
import simulation.body.particle.Particle;
import simulation.hash.Client;
import simulation.util.Vec2;

public abstract class Environment implements Client {

  protected final Vec2 position;

  protected Environment(Vec2 position) {
    this.position = new Vec2(position);
  }

  public abstract RectangularShape getBounds();

  public abstract Vec2 getCenter();

  public abstract short getNearRadius();

  public abstract void update(Stream<Particle> nearParticles);

  public abstract void draw(Graphics g);
}
