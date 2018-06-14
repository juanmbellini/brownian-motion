package ar.edu.itba.ss.brownian_motion.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Optional;


/**
 * Represents a particle in the system.
 */
public class Particle implements Collisionable, StateHolder<Particle.ParticleState> {

    /**
     * The particle's mass.
     */
    private double mass;

    /**
     * The particle's radius.
     */
    private double radius;

    /**
     * The particle's position (represented as a 2D vector).
     */
    private Vector2D position;

    /**
     * The particle's velocity (represented as a 2D vector).
     */
    private Vector2D velocity;

    /**
     * The amount of collisions this particle has.
     */
    private int collisionAmount;

    /**
     * Constructor.
     *
     * @param mass     The particle's mass.
     * @param radius   The particle's radius.
     * @param position The particle's position.
     * @param velocity The particle's radius.
     */
    public Particle(final double mass, final double radius, final Vector2D position, final Vector2D velocity) {
        this.mass = mass;
        this.radius = radius;
        this.position = position;
        this.velocity = velocity;
        this.collisionAmount = 0;
    }

    /**
     * @return The particle's mass.
     */
    public double getMass() {
        return this.mass;
    }

    /**
     * @return The particle's radius.
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * @return The particle's position (represented as a 2D vector).
     */
    public Vector2D getPosition() {
        return this.position;
    }

    /**
     * @return The particle's velocity (represented as a 2D vector).
     */
    public Vector2D getVelocity() {
        return this.velocity;
    }

    /**
     * @return The particle's kinetic energy.
     */
    public double getKineticEnergy() {
        return 0.5 * mass * velocity.getNormSq();
    }

    /**
     * @return The amount of collisions this particle has.
     */
    public int getCollisionsAmount() {
        return collisionAmount;
    }

    /**
     * Moves this particle according to the linear motion equation, using the given {@code deltaTime}.
     *
     * @param deltaTime The time variable of the linear motion equation.
     */
    public void move(final double deltaTime) {
        this.position = this.position.add(deltaTime, this.velocity);
    }

    /**
     * Sets a new velocity for the particle.
     *
     * @param newVelocity The new velocity.
     */
    public void setVelocity(final Vector2D newVelocity) {
        this.velocity = newVelocity;
    }

    /**
     * Adds a collision to this particle.
     */
    public void addCollision() {
        this.collisionAmount++;
    }

    @Override
    public Optional<Double> getCollisionTime(final Particle particle) {
        final Vector2D deltaR = getDeltaR(particle);
        final Vector2D deltaV = getDeltaV(particle);

        final double deltaVByDeltaR = deltaV.dotProduct(deltaR);
        if (Double.compare(deltaVByDeltaR, 0.0) >= 0) {

            return Optional.empty();
        }

        final double deltaVSquare = deltaV.dotProduct(deltaV);
        final double deltaRSquare = deltaR.dotProduct(deltaR);
        final double sigma = getSigma(particle);

        final double d = Math.pow(deltaVByDeltaR, 2) - deltaVSquare * (deltaRSquare - Math.pow(sigma, 2));
        if (Double.compare(d, 0.0) < 0) {
            return Optional.empty();
        }
        final double deltaTime = -(deltaVByDeltaR + Math.sqrt(d)) / deltaVSquare;
        return Optional.of(deltaTime).filter(time -> Double.compare(time, 0.0) > 0);
    }

    @Override
    public void collide(final Particle other) {
        final Vector2D deltaR = getDeltaR(other);
        final Vector2D deltaV = getDeltaV(other);
        final double deltaVByDeltaR = deltaV.dotProduct(deltaR);
        final double sigma = getSigma(other);
        final double impulse = (2 * this.mass * other.mass * deltaVByDeltaR) / (sigma * (this.mass + other.mass));
        final Vector2D impulseVector = deltaR.scalarMultiply(impulse / sigma);

        this.setVelocity(this.velocity.add(impulseVector.scalarMultiply(1 / this.mass)));
        other.setVelocity(other.velocity.subtract(impulseVector.scalarMultiply(1 / other.mass)));
        this.addCollision();
        other.addCollision();
    }

    /**
     * Calculates position difference between this particle and the {@code other} particle.
     *
     * @param other The other particle.
     * @return A {@link Vector2D} with the difference of positions.
     */
    private Vector2D getDeltaR(final Particle other) {
        return other.position.subtract(this.position);
    }

    /**
     * Calculates velocity difference between this particle and the {@code other} particle.
     *
     * @param other The other particle.
     * @return A {@link Vector2D} with the difference of positions.
     */
    private Vector2D getDeltaV(final Particle other) {
        return other.velocity.subtract(this.velocity);
    }

    /**
     * Calculates the distance between the mass center of this particle and the {@code other} particle.
     *
     * @param other The other particle.
     * @return A {@link Vector2D} with the difference of positions.
     */
    private double getSigma(final Particle other) {
        return this.radius + other.radius;
    }


    @Override
    public ParticleState outputState() {
        return new ParticleState(this);
    }

    /**
     * Represents the state of a given particle.o
     */
    public static final class ParticleState implements State {

        /**
         * The {@link Particle}'s mass.
         */
        private final double mass;

        /**
         * The {@link Particle}'s radius.
         */
        private final double radius;

        /**
         * The {@link Particle}'s position (represented as a 2D vector).
         */
        private final Vector2D position;

        /**
         * The {@link Particle}'s velocity (represented as a 2D vector).
         */
        private final Vector2D velocity;

        /**
         * Constructor.
         *
         * @param particle The {@link Particle}'s whose state will be represented.
         */
        private ParticleState(Particle particle) {
            this.mass = particle.getMass();
            this.radius = particle.getRadius();
            this.position = particle.getPosition(); // The Vector2D class is unmodifiable.
            this.velocity = particle.getVelocity(); // The Vector2D class is unmodifiable.
        }

        /**
         * The {@link Particle}'s mass.
         */
        public double getMass() {
            return mass;
        }

        /**
         * The {@link Particle}'s radius.
         */
        public double getRadius() {
            return radius;
        }

        /**
         * The {@link Particle}'s position (represented as a 2D vector).
         */
        public Vector2D getPosition() {
            return position;
        }

        /**
         * The {@link Particle}'s velocity (represented as a 2D vector).
         */
        public Vector2D getVelocity() {
            return velocity;
        }
    }
}
