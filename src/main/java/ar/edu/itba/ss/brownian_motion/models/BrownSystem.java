package ar.edu.itba.ss.brownian_motion.models;

import ar.edu.itba.ss.brownian_motion.utils.ComponentsProvider;
import ar.edu.itba.ss.g7.engine.simulation.State;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A Brownian motion system.
 */
public class BrownSystem implements EventDrivenSystem<BrownSystem.BrownSystemState> {

    // ================================================================================================================
    // System stuff
    // ================================================================================================================

    /**
     * The {@link Particle}s in this system.
     */
    private final List<Particle> particles;

    /**
     * {@link List} holding the walls of the system.
     */
    private final List<Wall> walls;

    /**
     * The big particle (not final as the {@link #restart()} method can rebuild it).
     * This {@link Particle} is saved because its trajectory must be taken into account.
     */
    private Particle bigParticle;


    // ================================================================================================================
    // Update stuff
    // ================================================================================================================

    /**
     * Last moment at which this system was updated.
     */
    private double lastUpdated;

    /**
     * The total amount of collisions that occurred in the last output interval.
     */
    private int lastAmountOfCollisionsReported;


    // ================================================================================================================
    // Initialization stuff
    // ================================================================================================================

    /**
     * A {@link ComponentsProvider} that aids the task of building the system.
     */
    private final ComponentsProvider componentsProvider;

    /**
     * Indicates whether this room is clean (i.e can be used to perform the simulation from the beginning).
     */
    private boolean clean;


    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param length                 The length of te room's side.
     * @param bigParticleMass        The mass of the big particle.
     * @param bigParticleRadius      The radius of the big particle.
     * @param smallParticlesMass     The mass of the small particles.
     * @param smallParticlesRadius   The radius of the small particles.
     * @param amountOfSmallParticles The amount of small particles in the system.
     */
    public BrownSystem(final double length,
                       final double bigParticleMass, final double bigParticleRadius,
                       final double smallParticlesMass, final double smallParticlesRadius,
                       final int amountOfSmallParticles) {

        this.componentsProvider = new ComponentsProvider(length, bigParticleMass, bigParticleRadius,
                smallParticlesMass, smallParticlesRadius, amountOfSmallParticles);

        this.walls = componentsProvider.buildRoom();
        final ComponentsProvider.ParticlesHolder holder = componentsProvider.createParticles();
        this.particles = holder.getParticles();
        this.bigParticle = holder.getBigParticle();
        this.lastAmountOfCollisionsReported = 0;
        this.lastUpdated = 0;
        this.clean = true;
    }


    // ================================================================================================================
    // Interface stuff
    // ================================================================================================================

    @Override
    public void update(final double instant) {
        this.clean = false;
        if (Double.compare(instant, lastUpdated) <= 0) {
            return;
        }
        particles.forEach(particle -> particle.move(instant - lastUpdated));
        lastUpdated = instant;
    }

    @Override
    public void restart() {
        if (clean) {
            return;
        }
        this.particles.clear();
        final ComponentsProvider.ParticlesHolder holder = componentsProvider.createParticles();
        this.bigParticle = holder.getBigParticle();
        this.particles.addAll(holder.getParticles());
        this.lastAmountOfCollisionsReported = 0;
        this.lastUpdated = 0;
        this.clean = true;
    }

    @Override
    public List<CollisionEvent<? extends Collisionable>> nextCollisions(final double now) {
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
    public void reportAmountOfEvents(int amountOfEvents) {
        this.lastAmountOfCollisionsReported = amountOfEvents;
    }

    @Override
    public BrownSystemState outputState() {
        return new BrownSystemState(this);
    }


    // ================================================================================================================
    // Getters
    // ================================================================================================================

    /**
     * @return The particles in this system.
     */
    /* package */ List<Particle> getParticles() {
        return new LinkedList<>(particles);
    }

    /**
     * @return {@link List} holding the walls of the system.
     */
    /* package */ List<Wall> getWalls() {
        return new LinkedList<>(walls);
    }

    /**
     * @return The big particle.
     */
    /* package */ Particle getBigParticle() {
        return bigParticle;
    }

    /**
     * @return The total amount of collisions that occurred in the last output interval.
     */
    /* package */ int getLastAmountOfCollisionsReported() {
        return lastAmountOfCollisionsReported;
    }

    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Transforms the given params to {@link CollisionEvent}.
     *
     * @param collider The {@link Particle} colliding.
     * @param collided The {@link Collisionable} being collided.
     * @param time     The instant at which the event is happening.
     * @return The created {@link CollisionEvent}.
     */
    private static CollisionEvent<? extends Collisionable> getCollisionEvent(final Particle collider,
                                                                             final Collisionable collided,
                                                                             final double time) {

        if (collided instanceof Particle) {
            return new TwoParticlesCollisionEvent(collider, (Particle) collided, time);
        }
        if (collided instanceof Wall) {
            return new ParticleAndWallCollisionEvent(collider, (Wall) collided, time);
        }
        throw new IllegalArgumentException("Only particles or walls");
    }


    // ================================================================================================================
    // State
    // ================================================================================================================

    /**
     * {@link State} for a {@link BrownSystem}.
     */
    public final static class BrownSystemState implements State {

        /**
         * The {@link Particle}s' states.
         */
        private final List<Particle.ParticleState> particleStates;
        /**
         * The {@link Wall}s' states.
         */
        private final List<Wall.WallState> wallStates;

        /**
         * The big {@link Particle}'s state.
         */
        private final Particle.ParticleState bigParticleState;

        private final int lastAmountOfCollisionsReported;


        /**
         * Constructors
         *
         * @param brownSystem The {@link BrownSystem} owning this state.
         */
        /* package */ BrownSystemState(final BrownSystem brownSystem) {
            this.particleStates = brownSystem.getParticles().stream()
                    .map(Particle::outputState)
                    .collect(Collectors.toList());
            this.wallStates = brownSystem.getWalls().stream()
                    .map(Wall::outputState)
                    .collect(Collectors.toList());
            this.bigParticleState = brownSystem.getBigParticle().outputState();
            this.lastAmountOfCollisionsReported = brownSystem.getLastAmountOfCollisionsReported();
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

        /**
         * @return The big {@link Particle}'s state.
         */
        public Particle.ParticleState getBigParticleState() {
            return bigParticleState;
        }

        /**
         * @return The total amount of collisions that occurred in the last output interval.
         */
        public int getLastAmountOfCollisionsReported() {
            return lastAmountOfCollisionsReported;
        }
    }
}
