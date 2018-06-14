package ar.edu.itba.ss.brownian_motion.models;

/**
 * Represents a collision event between two {@link Particle}s.
 */
public class TwoParticlesCollisionEvent extends CollisionEvent<Particle> {

    /**
     * The amount of collisions the {@link super#collided} {@link Particle} has at the moment of creating this event.
     */
    private final int collidedCollisionsAmount;

    /**
     * Constructor.
     *
     * @param collider     The {@link Particle} that is colliding against the {@link #collided} {@link Collisionable}.
     * @param collided     The {@link Particle} being collided.
     * @param eventInstant The instant at which this event happens.
     */
    public TwoParticlesCollisionEvent(final Particle collider, final Particle collided, final double eventInstant) {
        super(collider, collided, eventInstant);
        this.collidedCollisionsAmount = collided.getCollisionsAmount();
    }

    @Override
    public boolean isValid() {
        return getCollided().getCollisionsAmount() == this.collidedCollisionsAmount && colliderHasSameAmount();
    }
}
