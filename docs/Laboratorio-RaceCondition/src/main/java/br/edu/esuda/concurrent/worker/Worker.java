package br.edu.esuda.concurrent.worker;

import br.edu.esuda.concurrent.counter.Counter;

/**
 * Represents a worker thread responsible for incrementing a shared counter a specified number of
 * times.
 *
 * <p>
 * The worker depends only on the {@link Counter} interface, allowing different counter
 * implementations to be used transparently.
 * </p>
 *
 * @author ESUDA
 * @version 1.0
 */
// public class Worker extends Thread {
public class Worker implements Runnable {

    /**
     * Shared counter.
     */
    private final Counter counter;

    /**
     * Number of increments to perform.
     */
    private final int iterations;

    /**
     * Creates a new worker.
     *
     * @param counter shared counter
     * @param iterations number of increments
     */
    public Worker(Counter counter, int iterations) {
        this.counter = counter;
        this.iterations = iterations;
    }

    /**
     * Executes the worker task.
     */
    @Override
    public void run() {

        for (int i = 0; i < iterations; i++) {
            counter.increment();
        }

    }

}
