package ar.edu.itba.ss.brownian_motion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class.
 */
@SpringBootApplication
public class Main implements CommandLineRunner {

    /**
     * The {@link Logger} object.
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);


    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Hello, Brownian Motion!");
    }

    /**
     * Entry point.
     *
     * @param args Program arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
