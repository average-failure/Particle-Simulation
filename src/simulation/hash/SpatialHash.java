package simulation.hash;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import simulation.Settings;

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

  public Client[] findNear(Client client, short radius) {
    HashSet<Client> nearClients = new HashSet<>();
    float hx = client.getX() / Settings.CELL_SIZE;
    float hy = client.getY() / Settings.CELL_SIZE;
    short cellRadius = (short) Math.ceil((float) radius / Settings.CELL_SIZE);

    for (
      int cx = (int) Math.floor(hx) - cellRadius, mx = (int) Math.ceil(hx) +
      cellRadius;
      cx <= mx;
      cx++
    ) {
      for (
        int cy = (int) Math.floor(hy) - cellRadius, my = (int) Math.ceil(hy) +
        cellRadius;
        cy <= my;
        cy++
      ) {
        grid.computeIfPresent(
          cx + "," + cy,
          (k, v) -> {
            for (Client c : v) {
              float dx = c.getX() - client.getX();
              float dy = c.getY() - client.getY();
              if (dx * dx + dy * dy <= radius * radius && c != client) {
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

  private String[] getHashKey(Client client) {
    float x = client.getX() / Settings.CELL_SIZE;
    float y = client.getY() / Settings.CELL_SIZE;
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
