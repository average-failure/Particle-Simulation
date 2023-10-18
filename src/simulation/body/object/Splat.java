package simulation.body.object;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import scalr.Scalr;
import simulation.Settings;
import simulation.body.particle.Particle;
import simulation.util.MathUtils;
import simulation.util.constructor.ObjectParams;

public class Splat extends Environment {

  protected final float initRadius;
  protected float radius;
  protected final float drainRate;
  protected final HashSet<Affected> affected = new HashSet<>();
  private final HashSet<Affected> remove = new HashSet<>();

  protected final Ellipse2D.Float bounds;
  protected transient BufferedImage image;

  public Splat(ObjectParams params) {
    super(params.position(), 0, 0);
    initRadius = params.radius();
    radius = initRadius;
    drainRate = (float) (Settings.get(Settings.DT) * (Math.random() + 0.5));

    bounds =
      new Ellipse2D.Float(
        position.x() - radius,
        position.y() - radius,
        radius * 2f,
        radius * 2f
      );

    resizeImage(getImage());
    colourImage(params.colour());
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
  public RectangularShape getBounds() {
    return bounds;
  }

  @Override
  public short getNearRadius() {
    return (short) Math.round(radius);
  }

  @Override
  public void update(Stream<Particle> nearParticles) {
    final float pow =
      1 - radius / (Settings.get(Settings.Constants.MAX_RADIUS) * 500);
    nearParticles.forEach(p ->
      affected.add(
        new Affected(
          Scalr.resize(image, Math.max(0, Math.round(radius))),
          p,
          pow
        )
      )
    );

    affected.forEach(a -> {
      if (a.tick()) remove.add(a);
    });
    remove.forEach(affected::remove);
    remove.clear();

    if (radius > 0) {
      radius -= drainRate;
      resizeImage(image);
    }
  }

  public boolean isDead() {
    return radius <= 0;
  }

  @Override
  public void draw(Graphics2D g) {
    g.setComposite(AlphaComposite.SrcOver.derive(radius / initRadius));
    g.drawImage(
      image,
      Math.round(getX() - image.getWidth() / 2f),
      Math.round(getY() - image.getHeight() / 2f),
      null
    );
    affected.forEach(a -> a.draw(g));
  }

  private void resizeImage(BufferedImage image) {
    this.image = Scalr.resize(image, Math.max(0, Math.round(radius * 2)));
  }

  private void colourImage(Color colour) {
    WritableRaster raster = image.getRaster();
    int[] pixel = new int[] {
      colour.getRed(),
      colour.getGreen(),
      colour.getBlue(),
    };
    for (int x = 0; x < raster.getWidth(); x++) {
      for (int y = 0; y < raster.getHeight(); y++) {
        for (byte b = 0; b < pixel.length; b++) {
          raster.setSample(x, y, b, pixel[b]);
        }
      }
    }
  }

  private BufferedImage getImage() {
    try {
      return ImageIO.read(
        getClass()
          .getResourceAsStream(
            "/assets/splatter" + MathUtils.randRange(6, 1) + ".png"
          )
      );
    } catch (IOException e) {
      e.printStackTrace();
      return getImage();
    }
  }
}

final class Affected {

  private final BufferedImage image;
  private final float slowEffect;
  private final Particle particle;
  private float time;

  private class Position {

    private final int x;
    private final int y;
    private final float initTime;

    public Position(int x, int y, float initTime) {
      this.x = x;
      this.y = y;
      this.initTime = initTime;
    }

    public float alpha() {
      final float alpha = 1 - (initTime - time) / initTime;
      if (alpha > 0) return alpha;
      trail.remove(this);
      return 0;
    }

    public int x() {
      return x;
    }

    public int y() {
      return y;
    }
  }

  private final Set<Position> trail = ConcurrentHashMap.newKeySet();

  public Affected(BufferedImage image, Particle particle, float slowEffect) {
    this.image = image;
    this.particle = particle;
    this.slowEffect = slowEffect;
    time = slowEffect * 100;
  }

  public void draw(Graphics2D g) {
    trail.forEach(t -> {
      g.setComposite(AlphaComposite.SrcOver.derive(t.alpha()));
      g.drawImage(image, t.x(), t.y(), null);
    });
  }

  public boolean tick() {
    particle.getVelocity().pow(slowEffect);
    trail.add(new Position(getX(), getY(), time));
    time -= Settings.get(Settings.DT) * 10;
    return time <= 0;
  }

  private int getX() {
    return Math.round(particle.getX() - image.getWidth() / 2f);
  }

  private int getY() {
    return Math.round(particle.getY() - image.getHeight() / 2f);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((particle == null) ? 0 : particle.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Affected other = (Affected) obj;
    if (particle == null) {
      if (other.particle != null) return false;
    } else if (!particle.equals(other.particle)) return false;
    return true;
  }
}
