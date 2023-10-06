package simulation.util;

import java.io.Serializable;

public final class Vec2 implements Serializable {

  private static final long serialVersionUID = 532529248923658L;

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
    this.x += x;
    this.y += y;
    return this;
  }

  public Vec2 sub(float x, float y) {
    this.x -= x;
    this.y -= y;
    return this;
  }

  public Vec2 add(Vec2 v) {
    return add(v.getX(), v.getY());
  }

  public Vec2 sub(Vec2 v) {
    return sub(v.getX(), v.getY());
  }

  public Vec2 multiply(float x, float y) {
    this.x *= x;
    this.y *= y;
    return this;
  }

  public Vec2 multiply(Vec2 v) {
    return multiply(v.getX(), v.getY());
  }

  public Vec2 divide(float x, float y) {
    this.x /= x;
    this.y /= y;
    return this;
  }

  public Vec2 divide(Vec2 v) {
    return divide(v.getX(), v.getY());
  }

  public Vec2 multiplyScalar(float scalar) {
    x *= scalar;
    y *= scalar;
    return this;
  }

  public Vec2 divideScalar(float scalar) {
    x /= scalar;
    y /= scalar;
    return this;
  }

  public Vec2 addScalar(float scalar) {
    x += scalar;
    y += scalar;
    return this;
  }

  public Vec2 subScalar(float scalar) {
    x -= scalar;
    y -= scalar;
    return this;
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
    x = (float) (x * Math.cos(angle) - y * Math.sin(angle));
    y = (float) (x * Math.sin(angle) + y * Math.cos(angle));
    return this;
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

  @Override
  public String toString() {
    return getClass().getName() + " [x=" + x + ", y=" + y + "]";
  }
}
