package simulation.util.constructor;

import simulation.util.Vec2;

public final record ObjectParams(
  Vec2 position,
  short width,
  short height,
  short radius
) {
  /**
   * Rectangle constructor
   * @param x the x position of the top left corner of the rectangle
   * @param y the y position of the top left corner of the rectangle
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public ObjectParams(float x, float y, short width, short height) {
    this(new Vec2(x, y), width, height, (short) 0);
  }

  /**
   * Circle constructor
   * @param x the x position of the center of the circle
   * @param y the y position of the center of the circle
   * @param radius the radius of the circle
   */
  public ObjectParams(float x, float y, short radius) {
    this(new Vec2(x, y), (short) 0, (short) 0, radius);
  }

  /**
   * Rectangle constructor
   * @param position the position of the top left corner of the rectangle
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public ObjectParams(Vec2 position, short width, short height) {
    this(position, width, height, (short) 0);
  }

  /**
   * Circle constructor
   * @param position the position of the center of the circle
   * @param radius the radius of the circle
   */
  public ObjectParams(Vec2 position, short radius) {
    this(position, (short) 0, (short) 0, radius);
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
