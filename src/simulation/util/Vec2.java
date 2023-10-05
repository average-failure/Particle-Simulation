package simulation.util;

import java.io.Serializable;

public class Vec2 implements Serializable {

  private static final long serialVersionUID = 532529248923657L;

  private float x;
  private float y;

  public Vec2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Vec2(Vec2 v) {
    this(v.x, v.y);
  }

  public Vec2 add(float x, float y) {
    return new Vec2(this.x + x, this.y + y);
  }

  public Vec2 sub(float x, float y) {
    return new Vec2(this.x - x, this.y - y);
  }

  public Vec2 add(Vec2 v) {
    return add(v.getX(), v.getY());
  }

  public Vec2 sub(Vec2 v) {
    return sub(v.getX(), v.getY());
  }

  public Vec2 multiply(float x, float y) {
    return new Vec2(this.x * x, this.y * y);
  }

  public Vec2 multiply(Vec2 v) {
    return multiply(v.getX(), v.getY());
  }

  public Vec2 divide(float x, float y) {
    return new Vec2(this.x / x, this.y / y);
  }

  public Vec2 divide(Vec2 v) {
    return divide(v.getX(), v.getY());
  }

  public Vec2 multiplyScalar(float scalar) {
    return new Vec2(x * scalar, y * scalar);
  }

  public Vec2 divideScalar(float scalar) {
    return new Vec2(x / scalar, y / scalar);
  }

  public Vec2 addScalar(float scalar) {
    return new Vec2(x + scalar, y + scalar);
  }

  public Vec2 subScalar(float scalar) {
    return new Vec2(x - scalar, y - scalar);
  }

  public float getLength() {
    return (float) Math.sqrt(getLengthSq());
  }

  public float getLengthSq() {
    return x * x + y * y;
  }

  public Vec2 getNormal() {
    return divideScalar(getLength());
  }

  public float dot(Vec2 v) {
    return x * v.getX() + y * v.getY();
  }

  public Vec2 rotate(float angle) {
    return new Vec2(
      (float) (x * Math.cos(angle) - y * Math.sin(angle)),
      (float) (x * Math.sin(angle) + y * Math.cos(angle))
    );
  }

  /**
   * @return the x
   */
  public float getX() {
    return x;
  }

  /**
   * @return the y
   */
  public float getY() {
    return y;
  }

  /**
   * @param x the x to set
   */
  public void setX(float x) {
    this.x = x;
  }

  /**
   * @param y the y to set
   */
  public void setY(float y) {
    this.y = y;
  }

  public void addX(float x) {
    this.x += x;
  }

  public void addY(float y) {
    this.y += y;
  }

  @Override
  public String toString() {
    return getClass().getName() + " [x=" + x + ", y=" + y + "]";
  }

  public void move(Vec2 v) {
    this.x += v.getX();
    this.y += v.getY();
  }
}
