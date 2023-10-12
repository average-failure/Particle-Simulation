package simulation.util.constructor;

import simulation.Settings;
import simulation.body.particle.ChargedParticle;
import simulation.util.MathUtils;
import simulation.util.Vec2;

public final record ParticleParams(
  Vec2 position,
  Vec2 velocity,
  short mass,
  short immortality,
  float initialLife,
  short strength,
  ChargedParticle.Charge charge
) {
  public ParticleParams(float x, float y) {
    this(new Vec2(x, y), new Vec2(0, 0));
  }

  public ParticleParams(Vec2 position, Vec2 velocity) {
    this(
      position,
      velocity,
      (short) MathUtils.randRange(
        Settings.get(Settings.Constants.MAX_MASS),
        Settings.get(Settings.Constants.MIN_MASS)
      ),
      (short) 0,
      MathUtils.randRange(10_000, 1_000)
    );
  }

  /**
   * Base particle constructor
   */
  public ParticleParams(
    Vec2 position,
    Vec2 velocity,
    short mass,
    short immortality,
    float initialLife
  ) {
    this(position, velocity, mass, immortality, initialLife, mass);
  }

  /**
   * Attractor/Repulser particles' constructor
   */
  public ParticleParams(
    Vec2 position,
    Vec2 velocity,
    short mass,
    short immortality,
    float initialLife,
    short strength
  ) {
    this(
      position,
      velocity,
      mass,
      immortality,
      initialLife,
      strength,
      ChargedParticle.Charge.random()
    );
  }

  /**
   * Charged particle constructor
   */
  public ParticleParams {
    position = new Vec2(position);
    velocity = new Vec2(velocity);
  }

  public ParticleParams withStrength(short strength) {
    return new ParticleParams(
      position,
      velocity,
      mass,
      immortality,
      initialLife,
      strength,
      charge
    );
  }

  public ParticleParams withCharge(ChargedParticle.Charge charge) {
    return new ParticleParams(
      position,
      velocity,
      mass,
      immortality,
      initialLife,
      strength,
      charge
    );
  }
}
