package simulation.hash;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import simulation.Settings;
import simulation.util.Vec2;

public final class SpatialHash implements Serializable {

  private static final long serialVersionUID = 7642507362804L;

  private final transient ExecutorService pool = Executors.newFixedThreadPool(
    16
  );

  private final ConcurrentMap<String, Set<Client>> grid;
  private long preCleanTime;

  public SpatialHash() {
    grid = new ConcurrentHashMap<>();
    preCleanTime = System.currentTimeMillis();
  }

  public synchronized void newClient(Client client) {
    getSet(client).forEach(s -> s.add(client));
  }

  public void removeClient(Client client) {
    getSet(client).forEach(s -> s.remove(client));
    cleanGrid();
  }

  public Set<Client> findNearParallel(Vec2 client, short radius) {
    if (client == null || radius == 0) return new HashSet<>(0);

    final Set<Future<Set<Client>>> futures = new HashSet<>(0);

    final float cellSize = Settings.CELL_SIZE;
    final float cellX = client.x() / cellSize;
    final float cellY = client.y() / cellSize;
    final short cellRadius = (short) Math.ceil(radius / cellSize);

    final int startX = (int) Math.floor(cellX) - cellRadius;
    final int startY = (int) Math.floor(cellY) - cellRadius;
    final int endX = (int) Math.ceil(cellX) + cellRadius;
    final int endY = (int) Math.ceil(cellY) + cellRadius;

    final Set<Client> defaultReturn = new HashSet<>(0);

    for (int cx = startX; cx <= endX; cx++) {
      for (int cy = startY; cy <= endY; cy++) {
        final String key = cx + "," + cy;
        futures.add(pool.submit(() -> grid.getOrDefault(key, defaultReturn)));
      }
    }

    final Set<Client> nearClients = new HashSet<>(0);
    for (Future<Set<Client>> future : futures) {
      try {
        nearClients.addAll(future.get());
      } catch (ExecutionException e) {
        System.err.println("ExecutionException: " + e.getCause());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    nearClients.removeIf(client::isSamePos);

    return nearClients;
  }

  public Set<Client> findNearParallel(Client client, short radius) {
    return findNearParallel(new Vec2(client.getX(), client.getY()), radius);
  }

  public Set<Client> findNear(Vec2 client, short radius) {
    if (client == null || radius == 0) return new HashSet<>();

    final float cellSize = Settings.CELL_SIZE;
    final float cellX = client.x() / cellSize;
    final float cellY = client.y() / cellSize;
    final short cellRadius = (short) Math.ceil(radius / cellSize);

    final int startX = (int) Math.floor(cellX) - cellRadius;
    final int startY = (int) Math.floor(cellY) - cellRadius;
    final int endX = (int) Math.ceil(cellX) + cellRadius;
    final int endY = (int) Math.ceil(cellY) + cellRadius;

    final Set<Client> nearClients = new HashSet<>();
    final Set<Client> defaultReturn = new HashSet<>(0);

    for (int cx = startX; cx <= endX; cx++) {
      for (int cy = startY; cy <= endY; cy++) {
        final String key = cx + "," + cy;
        nearClients.addAll(grid.getOrDefault(key, defaultReturn));
      }
    }
    removeSame(client, nearClients);

    return nearClients;
  }

  /**
   * Copy of {@link java.util.Collection#removeIf() Collection#removeIf} but only for first match
   * @param client the client to test for matching position
   * @param c the {@link java.util.Collection collection} to remove from
   * @return {@code true} if an element is removed, {@code false} otherwise
   */
  private boolean removeSame(Vec2 client, Collection<Client> c) {
    final Iterator<Client> each = c.iterator();
    while (each.hasNext()) {
      if (client.isSamePos(each.next())) {
        each.remove();
        return true;
      }
    }
    return false;
  }

  public Set<Client> findNear(Client client, short radius) {
    return findNear(new Vec2(client.getX(), client.getY()), radius);
  }

  private String[] getHashKey(Client client) {
    final float cellSize = Settings.CELL_SIZE;
    final float x = client.getX() / cellSize;
    final float y = client.getY() / cellSize;
    return new String[] {
      ((int) Math.floor(x)) + "," + ((int) Math.floor(y)),
      ((int) Math.ceil(x)) + "," + ((int) Math.floor(y)),
      ((int) Math.floor(x)) + "," + ((int) Math.ceil(y)),
      ((int) Math.ceil(x)) + "," + ((int) Math.ceil(y)),
    };
  }

  private Stream<Set<Client>> getSet(Client client) {
    return Arrays
      .stream(getHashKey(client))
      .map(key -> grid.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet())
      );
  }

  private synchronized void cleanGrid() {
    if (System.currentTimeMillis() - preCleanTime < 1_000) return;

    for (
      Iterator<Entry<String, Set<Client>>> iter = grid.entrySet().iterator();
      iter.hasNext();
    ) {
      if (iter.next().getValue().isEmpty()) iter.remove();
    }

    preCleanTime = System.currentTimeMillis();
  }
}
