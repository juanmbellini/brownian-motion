package ar.edu.itba.ss.brownian_motion;

import ar.edu.itba.ss.brownian_motion.io.ProgramArguments;
import ar.edu.itba.ss.brownian_motion.models.BrownSystem;
import ar.edu.itba.ss.brownian_motion.simulation.EventDrivenSimulationEngine;
import ar.edu.itba.ss.g7.engine.io.DataSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class.
 */
@SpringBootApplication
public class BrownMotion implements CommandLineRunner {

    /**
     * The {@link Logger} object.
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(BrownMotion.class);

    private final BrownSystem brownSystem;

    private final EventDrivenSimulationEngine<BrownSystem.BrownSystemState> engine;

    private final double duration;

    private final DataSaver<BrownSystem.BrownSystemState> ovitoFileSaver;

    @Autowired
    public BrownMotion(ProgramArguments programArguments, DataSaver<BrownSystem.BrownSystemState> ovitoFileSaver) {
        this.brownSystem = new BrownSystem(programArguments.getWallLength(),
                programArguments.getAmountOfSmallParticles(),
                programArguments.getSmallParticlesRadius(),
                programArguments.getSmallParticlesMass(),
                programArguments.getBigParticlesRadius(),
                programArguments.getBigParticlesMass());
        this.engine = new EventDrivenSimulationEngine<>(brownSystem, programArguments.getOutputInterval());
        this.duration = programArguments.getDuration();
        this.ovitoFileSaver = ovitoFileSaver;
    }


    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Hello, Brownian Motion!");
        this.engine.initialize();
        this.engine.simulate(duration);
        this.ovitoFileSaver.save(this.engine.getAnimationFrames());
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
