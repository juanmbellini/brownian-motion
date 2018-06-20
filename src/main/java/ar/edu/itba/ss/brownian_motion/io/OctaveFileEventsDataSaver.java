package ar.edu.itba.ss.brownian_motion.io;

import ar.edu.itba.ss.brownian_motion.models.CollisionEvent;
import ar.edu.itba.ss.g7.engine.io.FileHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An object in charge of outputting events stuff data (such as time between two events).
 */
public class OctaveFileEventsDataSaver {

    /**
     * The path to the file where to save.
     */
    private final String filePath;

    /**
     * Constructor.
     *
     * @param filePath The path to the file where to save.
     */
    public OctaveFileEventsDataSaver(final String filePath) {
        this.filePath = filePath;
    }

    /**
     * @param events A {@link List} containing the events to be outputted.
     */
    public void save(final List<CollisionEvent<?>> events) {
        FileHelper.save(filePath, path -> saveEvents(FileHelper.createFile(path), events));
    }

    /**
     * Performs the save operation.
     *
     * @param file   The {@link File} to which data will be written into.
     * @param events A {@link List} containing the events to be outputted.
     * @throws IOException In case any I/O error occurs while performing the operation.
     */
    private void saveEvents(final File file, final List<CollisionEvent<?>> events) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            final String collisionTimes = "collisionTimes = [" + events.stream()
                    .map(CollisionEvent::getEventInstant)
                    .map(Object::toString)
                    .collect(Collectors.joining(", ")) + "];";
            writer.append(collisionTimes)
                    .append("\n");
        }
    }
}
