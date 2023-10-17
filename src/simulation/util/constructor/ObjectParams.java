package simulation.util.constructor;

import java.awt.Color;
import simulation.body.object.*;
import simulation.util.Vec2;

public final record ObjectParams(
  Class<? extends Environment> type,
  Vec2 position,
  short width,
  short height,
  short radius,
  Color colour
) {
  private static final short ZERO = 0;

  /**
   * {@link simulation.body.object.Rectangle Rectangle} constructor
   * @param x the x position of the top left corner of the rectangle
   * @param y the y position of the top left corner of the rectangle
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public ObjectParams(float x, float y, short width, short height) {
    this(new Vec2(x, y), width, height);
  }

  /**
   * {@link simulation.body.object.Rectangle Rectangle} constructor
   * @param position the position of the top left corner of the rectangle
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public ObjectParams(Vec2 position, short width, short height) {
    this(Rectangle.class, position, width, height, ZERO, null);
  }

  /**
   * {@link simulation.body.object.Circle Circle} constructor
   * @param x the x position of the center of the circle
   * @param y the y position of the center of the circle
   * @param radius the radius of the circle
   */
  public ObjectParams(float x, float y, short radius) {
    this(new Vec2(x, y), radius);
  }

  /**
   * {@link simulation.body.object.Circle Circle} constructor
   * @param position the position of the center of the circle
   * @param radius the radius of the circle
   */
  public ObjectParams(Vec2 position, short radius) {
    this(Circle.class, position, ZERO, ZERO, radius, null);
  }

  /**
   * {@link simulation.body.object.Splat Splat} constructor
   * @param x the x position of the center of the splat
   * @param y the y position of the center of the splat
   * @param radius the radius of the splat
   * @param strength the slow effect strength of the splat
   * @param colour the colour of the splat
   */
  public ObjectParams(float x, float y, short radius, Color colour) {
    this(new Vec2(x, y), radius, colour);
  }

  /**
   * {@link simulation.body.object.Splat Splat} constructor
   * @param position the position of the center of the splat
   * @param radius the radius of the splat
   * @param colour the colour of the splat
   */
  public ObjectParams(Vec2 position, short radius, Color colour) {
    this(Splat.class, position, ZERO, ZERO, radius, colour);
  }

  public ObjectParams {
    position = new Vec2(position);

    // Account for negative width
    if (width < 0) {
      width *= -1;
      position.sub(width, 0);
    }

    // Account for negative height
    if (height < 0) {
      height *= -1;
      position.sub(0, height);
    }

    // Account for negative radius
    if (radius < 0) radius *= -1;
  }
}
