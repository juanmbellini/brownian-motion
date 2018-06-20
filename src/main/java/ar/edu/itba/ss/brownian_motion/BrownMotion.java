package ar.edu.itba.ss.brownian_motion;

import ar.edu.itba.ss.brownian_motion.io.OctaveFileEventsDataSaver;
import ar.edu.itba.ss.brownian_motion.io.OctaveFileTimeSeriesDataSaver;
import ar.edu.itba.ss.brownian_motion.io.OvitoFileSaverImpl;
import ar.edu.itba.ss.brownian_motion.io.ProgramArguments;
import ar.edu.itba.ss.brownian_motion.models.BrownSystem;
import ar.edu.itba.ss.brownian_motion.simulation.EventDrivenSimulationEngine;
import ar.edu.itba.ss.g7.engine.io.DataSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.LinkedList;

/**
 * Main class.
 */
@SpringBootApplication
public class BrownMotion implements CommandLineRunner, InitializingBean {

    /**
     * The {@link Logger} object.
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(BrownMotion.class);


    /**
     * The simulation engine.
     */
    private final EventDrivenSimulationEngine<BrownSystem.BrownSystemState> engine;

    /**
     * The duration of the simulation.
     */
    private final double duration;

    /**
     * The {@link DataSaver} that will output data for ovito.
     */
    private final DataSaver<BrownSystem.BrownSystemState> ovitoFileSaver;

    /**
     * The {@link DataSaver} that will output the Octave file that contains time series data.
     */
    private final DataSaver<BrownSystem.BrownSystemState> octaveFileTimeSeriesDataSaver;

    /**
     * An {@link OctaveFileEventsDataSaver} that will output events stuff.
     */
    private final OctaveFileEventsDataSaver octaveFileEventsDataSaver;

    /**
     * Constructor.
     *
     * @param programArguments The program arguments.
     */
    @Autowired
    public BrownMotion(ProgramArguments programArguments) {
        final BrownSystem brownSystem = new BrownSystem(programArguments.getWallLength(),
                programArguments.getBigParticlesMass(), programArguments.getBigParticlesRadius(),
                programArguments.getSmallParticlesMass(), programArguments.getSmallParticlesRadius(),
                programArguments.getAmountOfSmallParticles());
        this.engine = new EventDrivenSimulationEngine<>(brownSystem, programArguments.getOutputInterval());
        this.duration = programArguments.getDuration();
        this.ovitoFileSaver = new OvitoFileSaverImpl(programArguments.getOvitoFilePath());
        this.octaveFileTimeSeriesDataSaver =
                new OctaveFileTimeSeriesDataSaver(programArguments.getOctaveTimeSeriesFilePath(),
                        duration, programArguments.getOutputInterval());
        this.octaveFileEventsDataSaver = new OctaveFileEventsDataSaver(programArguments.getOctaveEventsFilePath());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.engine.initialize();
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Hello, BrownMotion!");
        // First, simulate
        simulate();
        // Then, save
        save();
        LOGGER.info("Bye-bye!");
        System.exit(0);
    }

    /**
     * Performs the simulation phase of the program.
     */
    private void simulate() {
        LOGGER.info("Starting simulation...");
        this.engine.simulate(duration);
        LOGGER.info("Finished simulation");
    }

    /**
     * Performs the save phase of the program.
     */
    private void save() {
        LOGGER.info("Saving outputs...");
        ovitoFileSaver.save(new LinkedList<>(this.engine.getStates()));
        octaveFileTimeSeriesDataSaver.save(new LinkedList<>(this.engine.getStates()));
        octaveFileEventsDataSaver.save(new LinkedList<>(this.engine.getProcessedEvents()));
        LOGGER.info("Finished saving output in all formats.");
    }

    /**
     * Entry point.
     *
     * @param args Program arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(BrownMotion.class, args);
    }
}
