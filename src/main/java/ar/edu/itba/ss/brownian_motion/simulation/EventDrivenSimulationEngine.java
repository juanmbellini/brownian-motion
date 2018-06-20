package ar.edu.itba.ss.brownian_motion.simulation;

import ar.edu.itba.ss.brownian_motion.models.CollisionEvent;
import ar.edu.itba.ss.brownian_motion.models.Collisionable;
import ar.edu.itba.ss.brownian_motion.models.EventDrivenSystem;
import ar.edu.itba.ss.g7.engine.simulation.State;
import org.springframework.util.Assert;

import java.util.*;

/**
 * An event driven simulation engine.
 * This engine performs simulations using the event driven approach.
 * Intervals between simulation steps are variable,
 * being these the moments at which something interesting is happening in the system.
 */
public class EventDrivenSimulationEngine<S extends State> {

    /**
     * The {@link EventDrivenSystem} to which the simulation will be performed.
     */
    private final EventDrivenSystem<S> system;

    /**
     * Holds the outputted states through which the system passes
     * (i.e those that are outputted every dt2 interval)
     */
    private final Queue<S> states;

    /**
     * {@link Queue} that contains the events to be processed by the engine.
     */
    private final Queue<CollisionEvent<? extends Collisionable>> events;

    /**
     * A flag indicating whether the engine is initialized.
     */
    private boolean initialized;

    /**
     * Indicates the interval of time between two outputted states
     * (i.e amount of time between that must elapse between call to the {@link #saveState()} method,
     * also known as dt2).
     */
    private final double dt2;

    /**
     * A {@link List} containing the {@link CollisionEvent} that has been processed by this engine.
     */
    private final List<CollisionEvent<?>> processedEvents;


    /**
     * Constructor.
     *
     * @param system The system to which the simulation will be performed.
     * @param dt2    Indicates the interval of time between two animation frames outputs (i.e also known as dt2).
     */
    public EventDrivenSimulationEngine(final EventDrivenSystem<S> system, final double dt2) {
        this.system = system;
        this.dt2 = dt2;
        this.states = new LinkedList<>();
        this.events = new PriorityQueue<>();
        this.processedEvents = new LinkedList<>();
        this.initialized = false;
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
        int eventsByTimeUnit = 0; // This variable holds how many events had happened in a given output interval
        while (now < finishingInstant) {
            events.addAll(system.nextCollisions(now));
            final Optional<CollisionEvent<? extends Collisionable>> eventOptional = processEventsQueue();

            // This should not happen, but just in case...
            Assert.state(eventOptional.isPresent(), "No more valid events before end of simulation");

            // Here we know that there is a valid event to be processed
            final CollisionEvent<? extends Collisionable> event = eventOptional.get();
            final double eventInstant = event.getEventInstant();
            double nextOutputInstant = outputInstant + dt2;

            // In case the event happens after the next output instant, evolve system at an outputInterval rate.
            while (nextOutputInstant < eventInstant) {
                outputInstant = nextOutputInstant;
                system.update(nextOutputInstant);
                system.reportAmountOfEvents(eventsByTimeUnit);
                saveState();
                nextOutputInstant = nextOutputInstant + dt2;
                eventsByTimeUnit = 0;

            }

            // When reached here we know that the event is ready to be processed
            eventsByTimeUnit++;
            system.update(eventInstant);
            event.executeEvent();
            saveProcessedEvent(event);
            now = eventInstant;
        }
    }

    /**
     * Outputs the simulation results.
     *
     * @return The simulation results.
     */
    public Queue<S> getStates() {
        return new LinkedList<>(states); // Copy queue to avoid change of state from outside.
    }

    /**
     * Outputs a {@link List} of {@link CollisionEvent}s that have been processed by this engine.
     *
     * @return The {@link List} of {@link CollisionEvent}s that have been processed by this engine.
     */
    public List<CollisionEvent<?>> getProcessedEvents() {
        return new LinkedList<>(processedEvents); // Copy queue to avoid change of state from outside.
    }

    /**
     * Clears this engine
     * (i.e removes all {@link State}s from the {@link Queue}).
     *
     * @throws IllegalStateException In case this engine is now simulating.
     */
    public void clear() throws IllegalStateException {
        this.states.clear();
        this.processedEvents.clear();
        this.events.clear();
        this.system.restart();
        saveState();
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
     * Saves the actual state in the {@code states} {@link Queue}.
     */
    private void saveState() {
        this.states.offer(this.system.outputState());
    }

    /**
     * Saves the given {@code event} in the {@link List} of processed events.
     *
     * @param event The {@link CollisionEvent} to be saved.
     */
    private void saveProcessedEvent(final CollisionEvent<?> event) {
        this.processedEvents.add(event);
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
