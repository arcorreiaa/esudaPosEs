package br.edu.esuda.concurrent.counter;

/**
 * Defines the contract for a shared counter.
 *
 * <p>
 * This interface is used to demonstrate different synchronization strategies in concurrent
 * applications. Each implementation provides its own mechanism for handling concurrent access.
 * </p>
 *
 * <p>
 * Implementations used in this laboratory:
 * </p>
 * <ul>
 * <li>SharedCounter (without synchronization)</li>
 * <li>SynchronizedCounter (using synchronized)</li>
 * </ul>
 *
 * @author ESUDA
 * @version 1.0
 */
public interface Counter {

    /**
     * Increments the counter by one.
     */
    void increment();

    /**
     * Returns the current counter value.
     *
     * @return current value
     */
    int getValue();

    /**
     * Resets the counter to zero.
     */
    void reset();

}
