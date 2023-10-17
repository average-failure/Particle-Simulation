package simulation.body.object;

import simulation.body.particle.Particle;

interface Solid {
  void detectCollision(Particle p);
}
