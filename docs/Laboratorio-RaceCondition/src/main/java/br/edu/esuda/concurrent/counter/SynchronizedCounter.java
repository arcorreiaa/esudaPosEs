package br.edu.esuda.concurrent.counter;

/**
 * A thread-safe implementation of the {@link Counter} interface.
 *
 * <p>
 * This implementation uses the {@code synchronized} keyword to ensure that only one thread can
 * execute the increment operation at a time, preventing race conditions.
 * </p>
 *
 * <p>
 * This class is used to demonstrate how synchronization guarantees data consistency when multiple
 * threads access a shared resource.
 * </p>
 *
 * @author ESUDA
 * @version 1.0
 */
public class SynchronizedCounter implements Counter {

    /**
     * Shared counter value.
     */
    private int value;

    /**
     * Creates a new counter initialized with zero.
     */
    public SynchronizedCounter() {
        this.value = 0;
    }

    /**
     * Increments the counter in a thread-safe manner.
     *
     * <p>
     * The synchronized keyword guarantees that only one thread can execute this method at any given
     * time.
     * </p>
     */
    @Override
    public synchronized void increment() {
        value++;
    }

    /**
     * Returns the current counter value.
     *
     * @return current counter value
     */
    @Override
    public int getValue() {
        return value;
    }

    /**
     * Resets the counter to zero.
     */
    @Override
    public synchronized void reset() {
        value = 0;
    }

}
