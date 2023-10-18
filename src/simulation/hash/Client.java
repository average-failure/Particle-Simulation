package simulation.hash;

import java.io.Serializable;

public interface Client extends Serializable {
  /**
   * @return the center x of the client
   */
  public float getX();

  /**
   * @return the center y of the client
   */
  public float getY();
}
