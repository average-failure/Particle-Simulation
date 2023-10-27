package simulation.body.object;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RectangularShape;
import java.util.stream.Stream;
import simulation.body.particle.Particle;
import simulation.hash.Client;
import simulation.util.Vec2;

public abstract class Environment implements Client {

  public static final Color COLOUR = Color.LIGHT_GRAY;

  protected final Vec2 position;
  protected final Vec2 center;

  /**
   * @param position the position of the object
   * @param cx the x offset from the position to the center of the object
   * @param cy the y offset from the position to the center of the object
   */
  protected Environment(Vec2 position, float cx, float cy) {
    this.position = new Vec2(position);
    center = new Vec2(position).add(cx, cy);
  }

  public abstract RectangularShape getBounds();

  public final Vec2 getCenter() {
    return center;
  }

  public abstract short getNearRadius();

  public abstract void update(Stream<Particle> nearParticles);

  public void draw(Graphics2D g) {
    g.setComposite(AlphaComposite.SrcOver);
    g.fill(getBounds());
  }
}
