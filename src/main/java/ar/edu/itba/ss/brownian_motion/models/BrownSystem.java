package ar.edu.itba.ss.brownian_motion.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A Brownian motion system.
 */
public class BrownSystem implements EventDrivenSystem<BrownSystem.BrownSystemState> {

    /**
     * The particles in this system.
     */
    private final List<Particle> particles;

    /**
     * List holding the walls of the system.
     */
    private final List<Wall> walls;

    /**
     * The length of te room's side.
     */
    private final double length;

    /**
     * The amount of small particles in the system.
     */
    private final int amountOfSmallParticles;

    /**
     * The radius of the small particles.
     */
    private final double smallParticlesRadius;

    /**
     * The mass of the small particles.
     */
    private final double smallParticlesMass;

    /**
     * The big particle.
     */
    private final Particle bigParticle;

    /**
     * Last moment at which this system was updated.
     */
    private double lastUpdated;


    /**
     * Constructor.
     *
     * @param length                 The length of te room's side.
     * @param amountOfSmallParticles The amount of small particles in the system.
     * @param smallParticlesRadius   The radius of the small particles.
     * @param smallParticlesMass     The mass of the small particles.
     * @param bigParticlesRadius     The radius of the big particle.
     * @param bigParticlesMass       The mass of the big particle.
     */
    public BrownSystem(double length, int amountOfSmallParticles,
                       double smallParticlesRadius, double smallParticlesMass,
                       double bigParticlesRadius, double bigParticlesMass) {
        this.particles = new LinkedList<>();
        this.lastUpdated = 0;

        this.length = length;
        final Wall leftWall = Wall.getVertical(0, 0, length);
        final Wall bottomWall = Wall.getHorizontal(0, 0, length);
        final Vector2D rightInitialPoint = bottomWall.getFinalPoint();
        final Vector2D topInitialPoint = leftWall.getFinalPoint();
        final Wall rightWall = Wall.getVertical(rightInitialPoint.getX(), rightInitialPoint.getY(), length);
        final Wall topWall = Wall.getHorizontal(topInitialPoint.getX(), topInitialPoint.getY(), length);
        this.walls = Stream.of(leftWall, bottomWall, rightWall, topWall).collect(Collectors.toList());

        this.amountOfSmallParticles = amountOfSmallParticles;
        this.smallParticlesRadius = smallParticlesRadius;
        this.smallParticlesMass = smallParticlesMass;
        Optional<Particle> bigParticle;
        do {
            bigParticle = provideParticle(bigParticlesMass, bigParticlesRadius, length, 0.0, 0.0, this.walls);
        } while (!bigParticle.isPresent());
        this.bigParticle = bigParticle.get();

    }

    @Override
    public void update(double instant) {
        if (Double.compare(instant, lastUpdated) <= 0) {
            return;
        }
        particles.forEach(particle -> particle.move(instant - lastUpdated));
        lastUpdated = instant;
    }

    private Optional<Particle> provideParticle(double mass, double radius, double length,
                                               double xVelocity, double yVelocity,
                                               List<Wall> walls) {
        final double xPosition = radius + new Random().nextDouble() * (length - 2 * radius);
        final double yPosition = radius + new Random().nextDouble() * (length - 2 * radius);
        return Optional.of(new Particle(mass, radius, xPosition, yPosition, xVelocity, yVelocity))
                .filter(particle -> walls.stream().filter(wall -> this.isOverlapped(particle, wall)).count() == 0);

    }

    @Override
    public void restart() {
        this.particles.clear();
        this.lastUpdated = 0;
        this.particles.add(bigParticle);
        while (particles.size() < amountOfSmallParticles + 1) {
            final Optional<Particle> newParticle = provideParticle(smallParticlesMass, smallParticlesRadius, length,
                    -0.1 + new Random().nextDouble() * 0.2,
                    -0.1 + new Random().nextDouble() * 0.2,
                    walls);
            newParticle.ifPresent(particle -> {
                if (particles.stream().filter(others -> this.isOverlapped(particle, others)).count() == 0) {
                    particles.add(particle);
                }
            });
        }
    }

    @Override
    public List<CollisionEvent<? extends Collisionable>> nextCollisions(double now) {
        final List<Collisionable> collisionableList = Stream.concat(particles.stream(), walls.stream())
                .collect(Collectors.toList());
        return particles.stream()
                .map(particle1 -> collisionableList.stream()
                        .filter(particle2 -> !particle1.equals(particle2))
                        .map(collisionable ->
                                collisionable
                                        .getCollisionTime(particle1)
                                        .map(time -> getCollisionEvent(particle1, collisionable, time + now)))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    @Override
    public BrownSystemState outputState() {
        return new BrownSystemState(this);
    }

    /**
     * Transforms the given params to {@link CollisionEvent}.
     *
     * @param collider The {@link Particle} colliding.
     * @param collided The {@link Collisionable} being collided.
     * @param time     The instant at which the event is happening.
     * @return The created {@link CollisionEvent}.
     */
    private static CollisionEvent<? extends Collisionable> getCollisionEvent(Particle collider,
                                                                             Collisionable collided,
                                                                             double time) {

        if (collided instanceof Particle) {
            return new TwoParticlesCollisionEvent(collider, (Particle) collided, time);
        }
        if (collided instanceof Wall) {
            return new ParticleAndWallCollisionEvent(collider, (Wall) collided, time);
        }
        throw new IllegalArgumentException("Only particles or walls");
    }


    /**
     * Indicates if the given {@code particle} overlaps with the given {@code other} particle.
     *
     * @param particle One particle.
     * @param other    The other particle.
     * @return {@code true} if they overlap, or {@code false} otherwise.
     */
    public boolean isOverlapped(Particle particle, Particle other) {
        return particle.getPosition().distance(other.getPosition()) <= particle.getRadius() + other.getRadius();
    }

    /**
     * Indicates if the given {@code particle} overlaps with the given {@code wall}.
     *
     * @param particle The particle.
     * @param wall     The wall.
     * @return {@code true} if they overlap, or {@code false} otherwise.
     * @implNote Uses Heron's formula.
     */
    public boolean isOverlapped(Particle particle, Wall wall) {
        // The wall's points and the particle's positions makes up a triangle.
        // We must find the distance between the particle's position and the wall,
        // which is the same as getting the triangle's height (if the wall is the base).
        // We use Heron's formula
        final double base = wall.getInitialPoint().distance(wall.getFinalPoint());
        final double triangleSide1 = wall.getInitialPoint().distance(particle.getPosition());
        final double triangleSide2 = wall.getFinalPoint().distance(particle.getPosition());
        final double s = (base + triangleSide1 + triangleSide2) / 2;
        final double distance = Math.sqrt(s * (s - base) * (s - triangleSide1) * (s - triangleSide2)) * 2 / base;

        return distance <= particle.getRadius();
    }

    /**
     * {@link State} for a {@link BrownSystem}.
     */
    public final static class BrownSystemState implements State {

        /**
         * The room's length.
         */
        private final double length;
        /**
         * The {@link Particle}s' states.
         */
        private final List<Particle.ParticleState> particleStates;
        /**
         * The {@link Wall}s' states.
         */
        private final List<Wall.WallState> wallStates;

        /**
         * Constructors
         *
         * @param brownSystem The {@link BrownSystem} owning this state.
         */
        public BrownSystemState(BrownSystem brownSystem) {
            this.length = brownSystem.length;
            this.particleStates = brownSystem.particles.stream().map(Particle::outputState).collect(Collectors.toList());
            this.wallStates = brownSystem.walls.stream().map(Wall::outputState).collect(Collectors.toList());
        }

        /**
         * @return The room's length.
         */
        public double getLength() {
            return length;
        }

        /**
         * @return The {@link Particle}s' states.
         */
        public List<Particle.ParticleState> getParticleStates() {
            return particleStates;
        }

        /**
         * @return The {@link Wall}s' states.
         */
        public List<Wall.WallState> getWallStates() {
            return wallStates;
        }
    }

}
