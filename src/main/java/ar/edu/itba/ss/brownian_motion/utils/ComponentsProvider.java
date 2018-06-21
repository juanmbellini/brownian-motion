package ar.edu.itba.ss.brownian_motion.utils;

import ar.edu.itba.ss.brownian_motion.models.Particle;
import ar.edu.itba.ss.brownian_motion.models.Wall;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Juan Marcos Bellini on 14/6/18.
 */
public class ComponentsProvider {

    /**
     * The max. amount of consecutive failed tries of adding a {@link Particle}
     * into the returned {@link List} of {@link Particle} by {@link #createParticles()} method.
     */
    private static final int MAX_AMOUNT_OF_TRIES = 3000;

    /**
     * A {@link Logger} instance that will log a warning message in case the amount of
     * required particles could not be built.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentsProvider.class);

    /**
     * Length of the room (i.e it is a square room).
     */
    private final double length;

    /**
     * The mass of the big particle.
     */
    private final double bigParticleMass;

    /**
     * The radius of the big particle.
     */
    private final double bigParticleRadius;

    /**
     * The mass of the small particles.
     */
    private final double smallParticlesMass;

    /**
     * The radius of the small particles.
     */
    private final double smallParticlesRadius;

    /**
     * The amount of small particles to be built.
     */
    private final int amountOfSmallParticles;


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
    public ComponentsProvider(final double length,
                              final double bigParticleMass, final double bigParticleRadius,
                              final double smallParticlesMass, final double smallParticlesRadius,
                              final int amountOfSmallParticles) {
        this.length = length;
        this.bigParticleMass = bigParticleMass;
        this.bigParticleRadius = bigParticleRadius;
        this.smallParticlesMass = smallParticlesMass;
        this.smallParticlesRadius = smallParticlesRadius;
        this.amountOfSmallParticles = amountOfSmallParticles;
    }

    /**
     * Builds the room to be used.
     *
     * @return A {@link List} containing the {@link Wall}s of the room.
     */
    public List<Wall> buildRoom() {
        final Wall leftWall = Wall.getVertical(0, 0, length);
        final Wall bottomWall = Wall.getHorizontal(0, 0, length);
        final Vector2D rightInitialPoint = bottomWall.getFinalPoint();
        final Vector2D topInitialPoint = leftWall.getFinalPoint();
        final Wall rightWall = Wall.getVertical(rightInitialPoint.getX(), rightInitialPoint.getY(), length);
        final Wall topWall = Wall.getHorizontal(topInitialPoint.getX(), topInitialPoint.getY(), length);
        return Arrays.asList(leftWall, bottomWall, rightWall, topWall);
    }

    /**
     * Creates the {@link Particle}s for the system.
     *
     * @return a {@link ParticlesHolder} instance with the {@link Particle}s.
     */
    public ParticlesHolder createParticles() {
        final Particle bigParticle = createBigParticle();
        final List<Particle> particles = new LinkedList<>();
        particles.add(bigParticle);

        int tries = 0; // Will count the amount of consecutive failed tries of adding randomly a particle into the list.
        while (tries < MAX_AMOUNT_OF_TRIES && particles.size() < amountOfSmallParticles + 1) {

            final Particle newParticle = new Particle(smallParticlesMass, smallParticlesRadius,
                    buildPosition(smallParticlesRadius), buildSmallParticleVelocity());

            if (particles.stream().noneMatch(other -> areOverlapped(newParticle, other))) {
                particles.add(newParticle);
                tries = 0;
            } else {
                tries++;
            }
        }
        if (particles.size() < amountOfSmallParticles) {
            LOGGER.warn("Could not build the required amount of particles");
        }
        return new ParticlesHolder(bigParticle, particles);
    }

    /**
     * Creates the big {@link Particle}.
     *
     * @return The big {@link Particle}.
     */
    private Particle createBigParticle() {
        return new Particle(bigParticleMass, bigParticleRadius, buildPosition(bigParticleRadius), Vector2D.ZERO);
    }


    /**
     * Builds a {@link Vector2D} instance that represents a position.
     *
     * @param radius The radius of the {@link Particle} that will use this position.
     * @return The created instance.
     * @apiNote This method does not check if a position overlaps.
     */
    private Vector2D buildPosition(final double radius) {
        final double factor = length - 2 * radius;
        final double xPosition = radius + new Random().nextDouble() * factor;
        final double yPosition = radius + new Random().nextDouble() * factor;
        return new Vector2D(xPosition, yPosition);
    }

    /**
     * Builds a {@link Vector2D} instance that represents a small {@link Particle} starting velocity
     * (i.e has both components being a random number with uniform distributions between -0.1 m/s and 0.1 m/s).
     *
     * @return The created instance.
     */
    private Vector2D buildSmallParticleVelocity() {
        return new Vector2D(-0.1 + new Random().nextDouble() * 0.2, -0.1 + new Random().nextDouble() * 0.2);
    }

    /**
     * Indicates if the given {@code particle1} overlaps with the given {@code particle2} particle.
     *
     * @param particle1 One particle.
     * @param particle2 The other particle.
     * @return {@code true} if they overlap, or {@code false} otherwise.
     */
    private static boolean areOverlapped(Particle particle1, Particle particle2) {
        return particle1.getPosition().distance(particle2.getPosition()) <= particle1.getRadius() + particle2.getRadius();
    }

    /**
     * Class wrapping a {@link List} of {@link Particle}s, holding a pointer to a big {@link Particle}.
     */
    public final static class ParticlesHolder {
        /**
         * The big {@link Particle}.
         */
        private final Particle bigParticle;
        /**
         * A small {@link Particle}.
         */
        private final Particle smallParticle;
        /**
         * The {@link List} of {@link Particle}s
         */
        private final List<Particle> particles;

        /**
         * Constructor.
         *
         * @param bigParticle The big {@link Particle}.
         * @param particles   The {@link List} of {@link Particle}s
         */
        private ParticlesHolder(final Particle bigParticle, final List<Particle> particles) {
            this.bigParticle = bigParticle;
            this.particles = particles;
            this.smallParticle = particles.stream()
                    .filter(p -> p != bigParticle)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("This should not happen"));
        }

        /**
         * @return The big {@link Particle}.
         */
        public Particle getBigParticle() {
            return bigParticle;
        }

        /**
         * @return The {@link List} of {@link Particle}s
         */
        public List<Particle> getParticles() {
            return particles;
        }

        /**
         * @return A small {@link Particle}.
         */
        public Particle getSmallParticle() {
            return smallParticle;
        }
    }
}
