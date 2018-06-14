package ar.edu.itba.ss.brownian_motion.io;

import ar.edu.itba.ss.brownian_motion.models.BrownSystem;
import ar.edu.itba.ss.g7.engine.io.OvitoFileSaver;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The ovito file saver that will be used to output data that can be read from ovito to display an animation.
 */

public class OvitoFileSaverImpl extends OvitoFileSaver<BrownSystem.BrownSystemState> {

    /**
     * Constructor.
     *
     * @param filePath Path to the file to be saved.
     */
    public OvitoFileSaverImpl(@Value("${custom.output.ovito}") final String filePath) {
        super(filePath);
    }

    @Override
    public void saveState(final Writer writer, final BrownSystem.BrownSystemState state, final int frame)
            throws IOException {

        final Stream<StringBuilder> particles = state.getParticleStates().stream()
                .map(particle -> new StringBuilder()
                        .append(particle.getPosition().getX())
                        .append(" ")
                        .append(particle.getPosition().getY())
                        .append(" ")
                        .append(particle.getVelocity().getX())
                        .append(" ")
                        .append(particle.getVelocity().getY())
                        .append(" ")
                        .append(particle.getRadius())
                );
        final Stream<StringBuilder> walls = state.getWallStates().stream()
                .map(wall -> new StringBuilder()
                        .append(wall.getInitialPoint().getX())
                        .append(" ")
                        .append(wall.getInitialPoint().getY())
                        .append(" ")
                        .append(0.0)
                        .append(" ")
                        .append(0.0)
                        .append(" ")
                        .append(0.0001)
                        .append(" ")
                        .append(wall.getFinalPoint().getX())
                        .append(" ")
                        .append(wall.getFinalPoint().getY())
                        .append(" ")
                        .append(0.0)
                        .append(" ")
                        .append(0.0)
                        .append(" ")
                        .append(0.0001)
                );

        final List<String> finalList = Stream.concat(particles, walls)
                .map(StringBuilder::toString)
                .collect(Collectors.toList());
        final String stringRepresentation = finalList.stream().collect(Collectors.joining("\n"));
        writer.append(Integer.toString(finalList.size())).append("\n")
                .append(Integer.toString(frame)).append("\n")
                .append(stringRepresentation).append("\n");
    }
}
