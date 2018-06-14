package ar.edu.itba.ss.brownian_motion.io;

import ar.edu.itba.ss.brownian_motion.models.BrownSystem;
import ar.edu.itba.ss.brownian_motion.models.Particle;
import ar.edu.itba.ss.brownian_motion.models.Wall;
import ar.edu.itba.ss.g7.engine.io.OvitoFileSaver;

import java.io.IOException;
import java.io.Writer;

/**
 * The ovito file saver that will be used to output data that can be read from ovito to display an animation.
 */

public class OvitoFileSaverImpl extends OvitoFileSaver<BrownSystem.BrownSystemState> {

    /**
     * Constructor.
     *
     * @param filePath Path to the file to be saved.
     */
    public OvitoFileSaverImpl(final String filePath) {
        super(filePath);
    }


    @Override
    public void saveState(final Writer writer, final BrownSystem.BrownSystemState state, final int frame)
            throws IOException {

        final StringBuilder data = new StringBuilder();
        // First, headers
        data.append(state.getParticleStates().size() + 2 * state.getWallStates().size())
                .append("\n")
                .append(frame)
                .append("\n");
        // Save particles
        for (Particle.ParticleState particle : state.getParticleStates()) {
            saveParticle(data, particle);
        }
        // Save walls
        for (Wall.WallState wall : state.getWallStates()) {
            saveWall(data, wall);
        }

        // Append data into the Writer
        writer.append(data);

    }

    /**
     * Saves a {@link ar.edu.itba.ss.brownian_motion.models.Particle.ParticleState}
     * into the {@code data} {@link StringBuilder}.
     *
     * @param data     The {@link StringBuilder} that is collecting data.
     * @param particle The {@link ar.edu.itba.ss.brownian_motion.models.Particle.ParticleState} with the data.
     */
    private void saveParticle(final StringBuilder data, final Particle.ParticleState particle) {
        data.append("")
                .append(particle.getPosition().getX())
                .append(" ")
                .append(particle.getPosition().getY())
                .append(" ")
                .append(particle.getVelocity().getX())
                .append(" ")
                .append(particle.getVelocity().getY())
                .append(" ")
                .append(particle.getRadius())
                .append(" ")
                .append(calculateRedForParticle(particle)) // Red
                .append(" ")
                .append(calculateGreenForParticle(particle)) // Green
                .append(" ")
                .append(calculateBlueForParticle(particle)) // Blue
                .append("\n");
    }

    /**
     * Saves a {@link ar.edu.itba.ss.brownian_motion.models.Wall.WallState}
     * into the {@code data} {@link StringBuilder}.
     *
     * @param data The {@link StringBuilder} that is collecting data.
     * @param wall The {@link ar.edu.itba.ss.brownian_motion.models.Wall.WallState} with the data.
     */
    private void saveWall(final StringBuilder data, final Wall.WallState wall) {
        data.append("")
                .append(wall.getInitialPoint().getX())
                .append(" ")
                .append(wall.getInitialPoint().getY())
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0.0001)  // Radius
                .append(" ")
                .append(1) // Red
                .append(" ")
                .append(1) // Green
                .append(" ")
                .append(1) // Blue
                .append("\n")
                .append(wall.getFinalPoint().getX())
                .append(" ")
                .append(wall.getFinalPoint().getY())
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0.0001) // Radius
                .append(" ")
                .append(1) // Red
                .append(" ")
                .append(1) // Green
                .append(" ")
                .append(1) // Blue
                .append("\n");
    }

    /**
     * Provides the red component for the given particle.
     *
     * @param particleState The particle whose red color component is going to be calculated.
     * @return The red component for the particle.
     */
    private double calculateRedForParticle(final Particle.ParticleState particleState) {
        return 1d;
    }

    /**
     * Provides the green component for the given particle.
     *
     * @param particleState The particle whose green color component is going to be calculated.
     * @return The green component for the particle.
     */
    private double calculateGreenForParticle(final Particle.ParticleState particleState) {
        return 1d;
    }

    /**
     * Provides the blue component for the given particle.
     *
     * @param particleState The particle whose blue color component is going to be calculated.
     * @return The blue component for the particle.
     */
    private double calculateBlueForParticle(final Particle.ParticleState particleState) {
        return 1d;
    }
}
