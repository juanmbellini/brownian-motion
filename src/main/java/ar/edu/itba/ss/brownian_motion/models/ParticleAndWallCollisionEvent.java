package ar.edu.itba.ss.brownian_motion.models;

/**
 * Represents a collision event between a {@link Particle} and a {@link Wall}.
 */
public class ParticleAndWallCollisionEvent extends CollisionEvent<Wall> {

    /**
     * Constructor.
     *
     * @param collider     The {@link Particle} that is colliding against the {@link #collided} {@link Collisionable}.
     * @param collided     The {@link Wall} being collided.
     * @param eventInstant The instant at which this event happens.
     */
    public ParticleAndWallCollisionEvent(Particle collider, Wall collided, double eventInstant) {
        super(collider, collided, eventInstant);
    }

    @Override
    public boolean isValid() {
        return colliderHasSameAmount();
    }
}
