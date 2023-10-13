package simulation.util;

import java.io.Serializable;
import simulation.hash.Client;

public final class Vec2 implements Serializable {

  private static final long serialVersionUID = 532529248923659L;

  private float x;
  private float y;

  public Vec2() {
    this(0, 0);
  }

  public Vec2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Vec2(Vec2 v) {
    this(v.x, v.y);
  }

  public Vec2 set(float x, float y) {
    this.x = x;
    this.y = y;
    return this;
  }

  public Vec2 set(Vec2 v) {
    return set(v.x, v.y);
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
    return add(v.x, v.y);
  }

  public Vec2 sub(Vec2 v) {
    return sub(v.x, v.y);
  }

  public Vec2 mul(float x, float y) {
    this.x *= x;
    this.y *= y;
    return this;
  }

  public Vec2 mul(Vec2 v) {
    return mul(v.x, v.y);
  }

  public Vec2 div(float x, float y) {
    this.x /= x;
    this.y /= y;
    return this;
  }

  public Vec2 div(Vec2 v) {
    return div(v.x, v.y);
  }

  public Vec2 pow(float x, float y) {
    this.x = (float) Math.pow(this.x, x);
    this.y = (float) Math.pow(this.y, y);
    return this;
  }

  public Vec2 pow(Vec2 v) {
    return pow(v.x, v.y);
  }

  public Vec2 mul(float scalar) {
    x *= scalar;
    y *= scalar;
    return this;
  }

  public Vec2 div(float scalar) {
    x /= scalar;
    y /= scalar;
    return this;
  }

  public Vec2 add(float scalar) {
    x += scalar;
    y += scalar;
    return this;
  }

  public Vec2 sub(float scalar) {
    x -= scalar;
    y -= scalar;
    return this;
  }

  public Vec2 pow(float scalar) {
    x = (float) Math.pow(Math.abs(x), scalar) * Math.signum(x);
    y = (float) Math.pow(Math.abs(y), scalar) * Math.signum(y);
    return this;
  }

  /**
   * Squares the vector while keeping the direction
   * @return the vector
   */
  public Vec2 square() {
    x *= x * Math.signum(x);
    y *= y * Math.signum(y);
    return this;
  }

  public float getLength() {
    return (float) Math.sqrt(getLengthSq());
  }

  public float getLengthSq() {
    return x * x + y * y;
  }

  public Vec2 getNormal() {
    final float length = getLength();
    if (length <= 0) return new Vec2(0, 0);
    return new Vec2(this).div(length);
  }

  public Vec2 normalise() {
    final float length = getLength();
    if (length <= 0) return set(0, 0);
    return div(length);
  }

  public float dot(Vec2 v) {
    return x * v.x + y * v.y;
  }

  public Vec2 rotate(float angle) {
    x = (float) (x * Math.cos(angle) - y * Math.sin(angle));
    y = (float) (x * Math.sin(angle) + y * Math.cos(angle));
    return this;
  }

  public Vec2 subScalarIgnoreSign(float scalar) {
    x -= scalar * Math.signum(x);
    y -= scalar * Math.signum(y);
    return this;
  }

  public static Vec2 setAdd(Vec2 set, Vec2... add) {
    final Vec2 newVec = new Vec2(add[0]);
    for (int i = 1; i < add.length; i++) newVec.add(add[i]);
    return set.set(newVec);
  }

  /**
   * @return the x
   */
  public float x() {
    return x;
  }

  /**
   * @return the y
   */
  public float y() {
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

  public boolean isSamePos(Client c) {
    if (c == null) return false;
    return x == c.getX() && y == c.getY();
  }
}
