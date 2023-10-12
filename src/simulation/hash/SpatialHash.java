package simulation.hash;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import simulation.Settings;
import simulation.util.Vec2;

public final class SpatialHash implements Serializable {

  private static final long serialVersionUID = 7642507362804L;

  private final ConcurrentHashMap<String, HashSet<Client>> grid;
  private long preCleanTime;

  public SpatialHash() {
    grid = new ConcurrentHashMap<>();
    preCleanTime = System.currentTimeMillis();
  }

  public void newClient(Client client) {
    getSet(client).forEach(s -> s.add(client));
  }

  public void removeClient(Client client) {
    getSet(client).forEach(s -> s.remove(client));
    cleanGrid();
  }

  public Client[] findNear(Vec2 client, short radius) {
    if (client == null) return new Client[0];

    final HashSet<Client> nearClients = new HashSet<>();
    final float cellSize = Settings.get(Settings.Constants.CELL_SIZE);
    final float hx = client.x() / cellSize;
    final float hy = client.y() / cellSize;
    final short cellRadius = (short) Math.ceil(radius / cellSize);

    for (
      int cx = (int) Math.floor(hx) - cellRadius;
      cx <= Math.ceil(hx) + cellRadius;
      cx++
    ) {
      for (
        int cy = (int) Math.floor(hy) - cellRadius;
        cy <= Math.ceil(hy) + cellRadius;
        cy++
      ) {
        grid.computeIfPresent(
          cx + "," + cy,
          (k, v) -> {
            for (Client c : v) {
              final float dx = c.getX() - client.x();
              final float dy = c.getY() - client.y();
              if (
                dx * dx + dy * dy <= radius * radius && !client.isSamePos(c)
              ) {
                nearClients.add(c);
              }
            }
            return v;
          }
        );
      }
    }

    return nearClients.toArray(new Client[0]);
  }

  public Client[] findNear(Client client, short radius) {
    return findNear(new Vec2(client.getX(), client.getY()), radius);
  }

  private String[] getHashKey(Client client) {
    final float cellSize = Settings.get(Settings.Constants.CELL_SIZE);
    final float x = client.getX() / cellSize;
    final float y = client.getY() / cellSize;
    return new String[] {
      ((short) Math.floor(x)) + "," + ((short) Math.floor(y)),
      ((short) Math.ceil(x)) + "," + ((short) Math.floor(y)),
      ((short) Math.floor(x)) + "," + ((short) Math.ceil(y)),
      ((short) Math.ceil(x)) + "," + ((short) Math.ceil(y)),
    };
  }

  private Stream<HashSet<Client>> getSet(Client client) {
    return Arrays
      .stream(getHashKey(client))
      .map(key -> grid.computeIfAbsent(key, k -> new HashSet<>()));
  }

  private void cleanGrid() {
    if (System.currentTimeMillis() - preCleanTime < 1_000) return;

    grid.forEach((k, v) -> {
      if (v.isEmpty()) grid.remove(k);
    });

    preCleanTime = System.currentTimeMillis();
  }

  @Override
  public String toString() {
    return (
      getClass().getName() +
      " [grid=" +
      grid +
      ", preCleanTime=" +
      preCleanTime +
      "]"
    );
  }
}
