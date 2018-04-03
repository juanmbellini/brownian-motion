package ar.edu.itba.ss.brownian_motion.models;

/**
 * Represents a collision event.
 */
public abstract class CollisionEvent<C extends Collisionable> implements Comparable<CollisionEvent<C>> {

    /**
     * The {@link Particle} that is colliding against the {@link #collided} {@link Collisionable}.
     */
    private final Particle collider;

    /**
     * The {@link Collisionable} being collided.
     */
    private final C collided;

    /**
     * The amount of collisions the {@link #collider} {@link Particle} has at the moment of creating this event.
     */
    private final int collidedCollisionsAmount;

    /**
     * The instant at which this event happens.
     */
    private final double eventInstant;

    /**
     * Constructor.
     *
     * @param collider     The {@link Particle} that is colliding against the {@link #collided} {@link Collisionable}.
     * @param collided     The {@link Collisionable} being collided.
     * @param eventInstant The instant at which this event happens.
     */
    public CollisionEvent(Particle collider, C collided, double eventInstant) {
        this.collider = collider;
        this.collided = collided;
        this.collidedCollisionsAmount = collider.getCollisionsAmount();
        this.eventInstant = eventInstant;
    }

    /**
     * @return The {@link Particle} that is colliding against the {@link #collided} {@link Collisionable}.
     */
    public Particle getCollider() {
        return collider;
    }

    /**
     * @return The {@link Collisionable} being collided.
     */
    public C getCollided() {
        return collided;
    }

    /**
     * @return The instant at which this event happens.
     */
    public double getEventInstant() {
        return eventInstant;
    }

    /**
     * Indicates whether the {@link #getCollider()} {@link Particle} has the same amount of collisions
     * than the amount it had at the moment this event instance was created.
     *
     * @return {@code true} if it has the same amount, or {@code false} otherwise.
     */
    /* package */ boolean colliderHasSameAmount() {
        return collidedCollisionsAmount == collider.getCollisionsAmount();
    }

    /**
     * Indicates whether this event instance is valid (i.e not stale).
     *
     * @return {@code true} if it is valid, or {@code false} otherwise.
     */
    public abstract boolean isValid();

    /**
     * Executes this event.
     */
    public void executeEvent() {
        collided.collide(collider);
    }


    @Override
    public int compareTo(CollisionEvent<C> o) {
        return Double.compare(this.eventInstant, o.eventInstant);
    }
}
