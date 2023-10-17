package simulation.body.object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import scalr.Scalr;
import simulation.Settings;
import simulation.body.particle.Particle;
import simulation.util.MathUtils;
import simulation.util.constructor.ObjectParams;

public class Splat extends Environment {

  protected float radius;
  protected final float drainRate;

  protected final Ellipse2D.Float bounds;
  protected transient BufferedImage image;

  public Splat(ObjectParams params) {
    super(params.position(), 0, 0);
    radius = params.radius();
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

  public boolean updateSplat(Stream<Particle> nearParticles) {
    nearParticles.forEach(p ->
      p
        .getVelocity()
        .pow(1 - radius / (Settings.get(Settings.Constants.MAX_RADIUS) * 50))
    );

    if (radius > 0) {
      radius -= drainRate;
      resizeImage(image);
    }

    return radius <= 0;
  }

  /**
   * Replaced by {@link simulation.body.object.Splat#updateSplat updateSplat} method
   */
  @Override
  public void update(Stream<Particle> nearParticles) {
    // Look at javadoc
  }

  public boolean isDead() {
    return radius <= 0;
  }

  @Override
  public void draw(Graphics2D g) {
    g.drawImage(
      image,
      Math.round(getX() - radius),
      Math.round(getY() - radius),
      null
    );
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
