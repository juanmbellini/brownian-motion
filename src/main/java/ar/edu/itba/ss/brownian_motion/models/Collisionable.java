package ar.edu.itba.ss.brownian_motion.models;

import java.util.Optional;

/**
 * Defines behaviour for an object that can be collided.
 */
public interface Collisionable {

    /**
     * Calculates the time it will elapse till a collision occurs.
     *
     * @param particle The {@link Particle} colliding.
     * @return The estimated amount of time that will elapse till the collision.
     */
    Optional<Double> getCollisionTime(Particle particle);

    /**
     * Makes the given {@link Particle} collide with this {@link Collisionable}.
     *
     * @param particle The {@link Particle} colliding.
     */
    void collide(Particle particle);
}
