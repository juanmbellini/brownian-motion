package ar.edu.itba.ss.brownian_motion.simulation;

import ar.edu.itba.ss.brownian_motion.models.CollisionEvent;
import ar.edu.itba.ss.brownian_motion.models.Collisionable;
import ar.edu.itba.ss.brownian_motion.models.EventDrivenSystem;
import ar.edu.itba.ss.g7.engine.simulation.State;

import java.util.*;

/**
 * An event driven simulation engine.
 * This engine performs simulations using the event driven approach.
 * Intervals between simulation steps are variable,
 * being these the moments at which something interesting is happening the system.
 */
public class EventDrivenSimulationEngine<S extends State> {

    /**
     * The {@link EventDrivenSystem} to which the simulation will be performed.
     */
    private final EventDrivenSystem<S> system;

    /**
     * Holds the outputted animation frames through which the system passes
     * (i.e those that are outputted every dt2 interval)
     */
    private final Queue<S> animationFrames;

    /**
     * {@link Queue} that contains the events to be processed by the engine.
     */
    private final Queue<CollisionEvent<? extends Collisionable>> events;

    /**
     * A flag indicating whether the engine is initialized.
     */
    private boolean initialized;

    /**
     * Indicates the interval of time between two animation frames outputs
     * (i.e amount of time between that must elapse between call to the {@link #saveAnimationFrame()} method,
     * also known as dt2).
     */
    private final double dt2;

    private final Queue<CollisionEvent> processedEvents;

    private final List<Integer> eventFrequencies;

    /**
     * Constructor.
     *
     * @param system The system to which the simulation will be performed.
     * @param dt2    Indicates the interval of time between two animation frames outputs (i.e also known as dt2).
     */
    public EventDrivenSimulationEngine(EventDrivenSystem<S> system, double dt2) {
        this.system = system;
        this.dt2 = dt2;
        this.animationFrames = new LinkedList<>();
        this.events = new PriorityQueue<>();
        this.initialized = false;
        this.eventFrequencies = new LinkedList<>();
        this.processedEvents = new LinkedList<>();
    }

    /**
     * Initializes this engine.
     */
    public void initialize() {
        clear();
        this.initialized = true;
    }

    /**
     * Performs the simulation.
     *
     * @param duration The duration the simulation will last (in double format).
     */
    public void simulate(final double duration) {
        validateState();
        final double startingInstant = 0;
        double outputInstant = startingInstant;
        double now = startingInstant;
        final double finishingInstant = startingInstant + duration;
        while (now < finishingInstant) {
            events.addAll(system.nextCollisions(now));
            final Optional<CollisionEvent<? extends Collisionable>> eventOptional = processEventsQueue();
            if (!eventOptional.isPresent()) {
                // This should not happen, but just in case...
                throw new IllegalStateException("No more valid events before end of simulation");
            }
            final CollisionEvent<? extends Collisionable> event = eventOptional.get();
            final double eventInstant = event.getEventInstant();
            double nextOutputInstant = outputInstant + dt2;
            // In case the event happens after the next output instant, evolve system at an outputInterval rate.
            while (nextOutputInstant < eventInstant) {
                outputInstant = nextOutputInstant;
                system.update(nextOutputInstant);
                saveAnimationFrame();
                nextOutputInstant = nextOutputInstant +  dt2;

                // TODO: move to another method
                final int lastFrequency = eventFrequencies.stream().reduce(0, (o1, o2) -> o1 + o2);
                eventFrequencies.add(this.processedEvents.size() - lastFrequency);
            }
            system.update(eventInstant);
            event.executeEvent();
            this.processedEvents.offer(event);
            now = eventInstant;
        }
    }

    /**
     * Outputs the simulation results.
     *
     * @return The simulation results.
     */
    public Queue<S> getAnimationFrames() {
        return new LinkedList<>(animationFrames); // Copy queue to avoid change of state from outside.
    }

    /**
     * Clears this engine
     * (i.e removes all {@link State}s from the {@link Queue}).
     *
     * @throws IllegalStateException In case this engine is now simulating.
     */
    public void clear() throws IllegalStateException {
        this.animationFrames.clear();
        this.events.clear();
        this.system.restart();
        this.eventFrequencies.clear();
        this.processedEvents.clear();
        saveAnimationFrame();
    }

    /**
     * Processes the {@link #events} {@link Queue},
     * returning an {@link Optional} with the next valid {@link CollisionEvent},
     * or an empty {@link Optional} in case there are no more valid events.
     *
     * @return An {@link Optional} with the next valid {@link CollisionEvent},
     * or an empty {@link Optional} in case there are no more valid events.
     * @implNote The {@link Queue} will be emptied from invalid events.
     */
    private Optional<CollisionEvent<? extends Collisionable>> processEventsQueue() {
        CollisionEvent<? extends Collisionable> event = events.poll();
        while (event != null && !event.isValid()) {
            event = events.poll();
        }
        return Optional.ofNullable(event);
    }


    /**
     * Saves the actual state in the {@code animationFrames} {@link Queue}.
     */
    private void saveAnimationFrame() {
        this.animationFrames.offer(this.system.outputState());
    }


    /**
     * Validates the state of this engine.
     *
     * @throws IllegalStateException If the state is not valid.
     */
    private void validateState() throws IllegalStateException {
        if (!this.initialized) {
            throw new IllegalStateException("Engine not initialized. Must call #initialize method first!");
        }
    }


}
