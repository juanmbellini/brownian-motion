package ar.edu.itba.ss.brownian_motion.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * Represents a wall in the system.
 */
public class Wall implements Collisionable, StateHolder<Wall.WallState> {

    /**
     * The wall's initial position.
     */
    private final Vector2D initialPoint;

    /**
     * The wall's final position.
     */
    private final Vector2D finalPoint;

    /**
     * The wall's layout.
     */
    private final WallLayout wallLayout;

    /**
     * Constructor.
     *
     * @param initialPoint The wall's initial position.
     * @param finalPoint   The wall's final position.
     */
    private Wall(final Vector2D initialPoint, final Vector2D finalPoint, final WallLayout wallLayout) {
        this.initialPoint = initialPoint;
        this.finalPoint = finalPoint;
        this.wallLayout = wallLayout;
    }


    /**
     * @return The wall's initial position.
     */
    public Vector2D getInitialPoint() {
        return initialPoint;
    }

    /**
     * @return The wall's final position.
     */
    public Vector2D getFinalPoint() {
        return finalPoint;
    }

    @Override
    public Optional<Double> getCollisionTime(final Particle particle) {
        switch (wallLayout) {
            case HORIZONTAL: {
                final double yVelocity = particle.getVelocity().getY();
                if (yVelocity == 0) {
                    return Optional.empty();
                }
                final double yParticlePosition = particle.getPosition().getY();
                final double wallYPosition = this.initialPoint.getY();

                if ((Double.compare(yVelocity, 0) > 0 && Double.compare(wallYPosition, yParticlePosition) < 0)
                        || (Double.compare(yVelocity, 0) < 0 && Double.compare(wallYPosition, yParticlePosition) > 0)) {
                    return Optional.empty();
                }
                final double deltaR = (yVelocity > 0)
                        ? wallYPosition - particle.getRadius() - yParticlePosition
                        : wallYPosition + particle.getRadius() - yParticlePosition;
                return Optional.of(deltaR / yVelocity).filter(time -> Double.compare(time, 0.0) >= 0);
            }
            case VERTICAL: {
                final double xVelocity = particle.getVelocity().getX();
                if (xVelocity == 0) {
                    return Optional.empty();
                }
                final double xParticlePosition = particle.getPosition().getX();
                final double wallXPosition = this.initialPoint.getX();
                if ((Double.compare(xVelocity, 0) > 0 && Double.compare(wallXPosition, xParticlePosition) < 0)
                        || (Double.compare(xVelocity, 0) < 0 && Double.compare(wallXPosition, xParticlePosition) > 0)) {
                    return Optional.empty();
                }
                final double deltaR = (xVelocity > 0)
                        ? wallXPosition - particle.getRadius() - xParticlePosition
                        : wallXPosition + particle.getRadius() - xParticlePosition;
                return Optional.of(deltaR / xVelocity).filter(time -> Double.compare(time, 0.0) >= 0);

            }
            case OTHER:
            default:
                throw new RuntimeException("Only horizontal or vertical");
        }
    }


    @Override
    public void collide(final Particle particle) {
        final Vector2D velocity = particle.getVelocity();
        particle.addCollision();
        switch (wallLayout) {
            case HORIZONTAL:
                particle.setVelocity(new Vector2D(velocity.getX(), -velocity.getY()));
                break;
            case VERTICAL:
                particle.setVelocity(new Vector2D(-velocity.getX(), velocity.getY()));
                break;
            case OTHER:
            default:
                throw new RuntimeException("Only horizontal or vertical");
        }
    }

    @Override
    public WallState outputState() {
        return new WallState(this);
    }

    /**
     * Creates an horizontal wall (i.e having both initial and final point with the same 'y' component).
     *
     * @param xInitialPosition The 'x' component of the initial point position.
     * @param yInitialPosition The 'y' component of the initial point position.
     * @param length           The length of the wall (must be positive).
     * @return The created wall.
     * @throws IllegalArgumentException In case the length is not positive.
     */
    public static Wall getHorizontal(final double xInitialPosition, final double yInitialPosition, final double length)
            throws IllegalArgumentException {
        validateLength(length);
        final Vector2D initialPosition = new Vector2D(xInitialPosition, yInitialPosition);
        final Vector2D finalPosition = new Vector2D(xInitialPosition + length, yInitialPosition);
        return new Wall(initialPosition, finalPosition, WallLayout.HORIZONTAL);
    }

    /**
     * Creates a vertical wall (i.e having both initial and final point with the same 'x' component).
     *
     * @param xInitialPosition The 'x' component of the initial point position.
     * @param yInitialPosition The 'y' component of the initial point position.
     * @param length           The length of the wall (must be positive).
     * @return The created wall.
     * @throws IllegalArgumentException In case the length is not positive.
     */
    public static Wall getVertical(final double xInitialPosition, final double yInitialPosition, final double length)
            throws IllegalArgumentException {
        validateLength(length);
        final Vector2D initialPosition = new Vector2D(xInitialPosition, yInitialPosition);
        final Vector2D finalPosition = new Vector2D(xInitialPosition, yInitialPosition + length);
        return new Wall(initialPosition, finalPosition, WallLayout.VERTICAL);
    }

    /**
     * Validates the given {@code length}.
     *
     * @param length The length value to be validated.
     * @throws IllegalArgumentException In case the value is not valid (i.e is not positive).
     */
    private static void validateLength(final double length) throws IllegalArgumentException {
        Assert.isTrue(length > 0, "The length of the wall must be positive");
    }

    /**
     * Enum containing the wall layouts (i.e horizontal and vertical).
     */
    private enum WallLayout {
        /**
         * Indicates that the wall is horizontal (i.e having both initial and final point with the same 'y' component).
         */
        HORIZONTAL,
        /**
         * Indicates that the wall is vertical (i.e having both initial and final point with the same 'x' component).
         */
        VERTICAL,
        /**
         * Non vertical and non horizontal.
         */
        OTHER
    }

    /**
     * Represents the state of a {@link Wall}.
     */
    public final static class WallState implements State {
        /**
         * The wall's initial state.
         */
        private final Vector2D initialPoint;
        /**
         * The wall's final state.
         */
        private final Vector2D finalPoint;

        /**
         * Constructor.
         *
         * @param wall The {@link Wall} owning this state.
         */
        private WallState(final Wall wall) {
            this.initialPoint = wall.initialPoint;
            this.finalPoint = wall.finalPoint;
        }

        /**
         * @return The wall's initial state.
         */
        public Vector2D getInitialPoint() {
            return initialPoint;
        }

        /**
         * @return The wall's final state.
         */
        public Vector2D getFinalPoint() {
            return finalPoint;
        }
    }
}
