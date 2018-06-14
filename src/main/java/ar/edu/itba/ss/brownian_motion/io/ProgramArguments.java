package ar.edu.itba.ss.brownian_motion.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Bean class holding the program execution arguments.
 */
@Component
public class ProgramArguments {

    /**
     * The wall's length.
     */
    private final double wallLength;

    /**
     * The amount of small particles.
     */
    private final int amountOfSmallParticles;

    /**
     * The small particles' radius.
     */
    private final double smallParticlesRadius;

    /**
     * The small particles' mass.
     */
    private final double smallParticlesMass;

    /**
     * The big particle's radius.
     */
    private final double bigParticlesRadius;

    /**
     * The big particle's mass.
     */
    private final double bigParticlesMass;

    /**
     * The duration of the simulation.
     */
    private final double duration;

    /**
     * The output interval.
     */
    private final double outputInterval;

    /**
     * Path to Ovito file.
     */
    private final String ovitoFilePath;

    /**
     * Constructor.
     *
     * @param wallLength             The wall's length.
     * @param amountOfSmallParticles The amount of small particles.
     * @param smallParticlesRadius   The small particles' radius.
     * @param smallParticlesMass     The small particles' mass.
     * @param bigParticlesRadius     The big particle's radius.
     * @param bigParticlesMass       The big particle's mass.
     * @param duration               The duration of the simulation.
     * @param outputInterval         The output interval
     * @param ovitoFilePath          Path to Ovito file.
     */
    @Autowired
    public ProgramArguments(@Value("${custom.system.wall-length}") final double wallLength,
                            @Value("${custom.system.particles-amount}") final int amountOfSmallParticles,
                            @Value("${custom.system.small-particles-radius}") final double smallParticlesRadius,
                            @Value("${custom.system.small-particles-mass}") final double smallParticlesMass,
                            @Value("${custom.system.big-particle-radius}") final double bigParticlesRadius,
                            @Value("${custom.system.big-particle-mass}") final double bigParticlesMass,
                            @Value("${custom.simulation.duration}") final double duration,
                            @Value("${custom.simulation.output-interval}") final double outputInterval,
                            @Value("${custom.output.ovito}") final String ovitoFilePath) {

        this.wallLength = wallLength;
        this.amountOfSmallParticles = amountOfSmallParticles;
        this.smallParticlesRadius = smallParticlesRadius;
        this.smallParticlesMass = smallParticlesMass;
        this.bigParticlesRadius = bigParticlesRadius;
        this.bigParticlesMass = bigParticlesMass;
        this.duration = duration;
        this.outputInterval = outputInterval;
        this.ovitoFilePath = ovitoFilePath;
    }

    /**
     * @return The wall's length.
     */
    public double getWallLength() {
        return wallLength;
    }

    /**
     * @return The amount of small particles.
     */
    public int getAmountOfSmallParticles() {
        return amountOfSmallParticles;
    }

    /**
     * @return The small particles' radius.
     */
    public double getSmallParticlesRadius() {
        return smallParticlesRadius;
    }

    /**
     * @return The small particles' mass.
     */
    public double getSmallParticlesMass() {
        return smallParticlesMass;
    }

    /**
     * @return The big particle's radius.
     */
    public double getBigParticlesRadius() {
        return bigParticlesRadius;
    }

    /**
     * @return The big particle's mass.
     */
    public double getBigParticlesMass() {
        return bigParticlesMass;
    }

    /**
     * @return The duration of the simulation.
     */
    public double getDuration() {
        return duration;
    }

    /**
     * @return The output interval.
     */
    public double getOutputInterval() {
        return outputInterval;
    }

    /**
     * @return Path to Ovito file.
     */
    public String getOvitoFilePath() {
        return ovitoFilePath;
    }
}
