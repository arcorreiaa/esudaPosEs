package br.edu.esuda.concurrent.counter;

/**
 * A non-thread-safe implementation of the {@link Counter} interface.
 *
 * <p>
 * This class intentionally does not use any synchronization mechanism. When accessed concurrently
 * by multiple threads, it may produce inconsistent results due to race conditions.
 * </p>
 *
 * <p>
 * This implementation is used for educational purposes to demonstrate the problems caused by
 * concurrent access to shared resources.
 * </p>
 *
 * @author ESUDA
 * @version 1.0
 */
public class SharedCounter implements Counter {

    /**
     * Shared counter value.
     */
    private int value;

    /**
     * Creates a new counter initialized with zero.
     */
    public SharedCounter() {
        this.value = 0;
    }

    /**
     * Increments the counter.
     *
     * <p>
     * This method is intentionally not synchronized in order to demonstrate race conditions.
     * </p>
     */
    @Override
    public void increment() {
        // Isso fará ser mais lento de propósito
        int currentValue = value;

        try {
            Thread.sleep(0, 1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        value = currentValue + 1;
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
    public void reset() {
        value = 0;
    }

}
