package simulation.util.collision;

import simulation.util.Vec2;

public final record Collision(Vec2 normal, float speed, boolean colliding) {
  public Collision(Vec2 normal, float speed) {
    this(normal, speed, true);
  }

  public Collision() {
    this(null, 0, false);
  }
}
