package ar.edu.itba.ss.brownian_motion.io;

import ar.edu.itba.ss.brownian_motion.models.BrownSystem;
import ar.edu.itba.ss.brownian_motion.models.Particle;
import ar.edu.itba.ss.g7.engine.io.TextFileSaver;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.io.IOException;
import java.io.Writer;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Octave {@link TextFileSaver}.
 */
public class OctaveFileTimeSeriesDataSaver extends TextFileSaver<BrownSystem.BrownSystemState> {

    /**
     * The simulation duration.
     */
    private final double duration;

    /**
     * The simulation time step.
     */
    private final double timeStep;

    /**
     * Constructor.
     *
     * @param filePath Path to the file to be saved.
     * @param duration The simulation duration.
     * @param timeStep The simulation time step.
     */
    public OctaveFileTimeSeriesDataSaver(final String filePath, double duration, double timeStep) {
        super(filePath);
        this.duration = duration;
        this.timeStep = timeStep;
    }

    @Override
    public void doSave(Writer writer, Queue<BrownSystem.BrownSystemState> queue) throws IOException {
        final String duration = "duration = " + this.duration + ";";
        final String timeStep = "dt = " + this.timeStep + ";";
        final String time = "t = 0:dt:duration;";

        final String collisions = "collisions = [" + queue.stream()
                .map(BrownSystem.BrownSystemState::getLastAmountOfCollisionsReported)
                .map(Object::toString)
                .collect(Collectors.joining(", ")) + "];";

        // This is saved as a matrix, where each row represents a state,
        // the first column, the 'x' axis, and the second column, the 'y' axis.
        final String bigParticleTrajectory = "bigParticleTrajectory = [" + queue.stream()
                .map(BrownSystem.BrownSystemState::getBigParticleState)
                .map(Particle.ParticleState::getPosition)
                .map(v -> v.getX() + ", " + v.getY())
                .collect(Collectors.joining("; ")) + "];";

        // This is saved as a matrix, where each column represents a particle, and each row represents a state
        final String velocitiesModules = "velocitiesModules = [" + queue.stream()
                .map(BrownSystem.BrownSystemState::getParticleStates)
                .map(list -> list.stream()
                        .map(Particle.ParticleState::getVelocity)
                        .map(Vector2D::getNorm)
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.joining("; ")) + "];";


        // Append results into the Writer
        writer
                .append(duration)
                .append("\n")
                .append(timeStep)
                .append("\n")
                .append(time)
                .append("\n")
                .append(collisions)
                .append("\n")
                .append(bigParticleTrajectory)
                .append("\n")
                .append(velocitiesModules)
                .append("\n")
        ;
    }
}
