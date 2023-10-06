package simulation.util.constructor;

import simulation.util.Vec2;

public final record EnvironmentParams(
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
  public EnvironmentParams(float x, float y, short width, short height) {
    this(new Vec2(x, y), width, height, (short) 0);
  }

  /**
   * Circle constructor
   * @param x the x position of the center of the circle
   * @param y the y position of the center of the circle
   * @param radius the radius of the circle
   */
  public EnvironmentParams(float x, float y, short radius) {
    this(new Vec2(x, y), (short) 0, (short) 0, radius);
  }
}
