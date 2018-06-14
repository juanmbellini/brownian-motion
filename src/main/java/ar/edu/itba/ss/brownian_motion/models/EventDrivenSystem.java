package ar.edu.itba.ss.brownian_motion.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;

import java.time.Instant;
import java.util.List;

/**
 * Defines behaviour for an object that can act like a system that is event driven
 * (i.e is updatable up to a given moment where an interesting event happens).
 */
public interface EventDrivenSystem<S extends State> extends StateHolder<S> {

    /**
     * Updates the system up to the given {@link Instant}.
     *
     * @param instant instant to which the system will be taken.
     */
    void update(final double instant);

    /**
     * Restarts the system.
     */
    void restart();

    /**
     * Calculates the next collisions.
     *
     * @return The {@link List} of {@link CollisionEvent}s that will happen in the future.
     */
    List<CollisionEvent<? extends Collisionable>> nextCollisions(final double now);
}
