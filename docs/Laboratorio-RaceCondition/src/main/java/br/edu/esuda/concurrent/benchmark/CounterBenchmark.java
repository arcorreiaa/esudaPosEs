package br.edu.esuda.concurrent.benchmark;

import br.edu.esuda.concurrent.counter.Counter;
import br.edu.esuda.concurrent.counter.SharedCounter;
import br.edu.esuda.concurrent.counter.SynchronizedCounter;
import br.edu.esuda.concurrent.worker.Worker;

/**
 * Executes the concurrency benchmark using different counter implementations.
 *
 * @author ESUDA
 * @version 1.0
 */
public class CounterBenchmark {

    /**
     * Number of worker threads.
     */
    private static final int NUMBER_OF_THREADS = 50;

    /**
     * Number of increments performed by each thread.
     */
    private static final int ITERATIONS_PER_THREAD = 10_000;

    /**
     * Executes all benchmark tests.
     */
    public void execute() {

        runBenchmark("Shared Counter (Without Synchronization)", new SharedCounter());

        runBenchmark("Synchronized Counter", new SynchronizedCounter());

    }

    /**
     * Executes a benchmark using the specified counter implementation.
     *
     * @param title   benchmark title
     * @param counter counter implementation
     */
    private void runBenchmark(String title, Counter counter) {

        System.out.println(title);
        System.out.println();

        Thread[] workers = new Thread[NUMBER_OF_THREADS];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {

            workers[i] = new Thread(new Worker(counter, ITERATIONS_PER_THREAD));

            workers[i].start();
        }

        for (Thread worker : workers) {

            try {
                worker.join();
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                System.err.println("Execution interrupted.");

                return;
            }

        }

        long endTime = System.currentTimeMillis();

        int expected = NUMBER_OF_THREADS * ITERATIONS_PER_THREAD;

        printResults(expected, counter.getValue(), endTime - startTime);

    }

    /**
     * Prints benchmark results.
     *
     * @param expected      expected counter value
     * @param obtained      obtained counter value
     * @param executionTime execution time in milliseconds
     */
    private void printResults(int expected, int obtained, long executionTime) {

        System.out.printf("Expected value : %,d%n", expected);

        System.out.printf("Obtained value : %,d%n", obtained);

        System.out.printf("Execution time : %d ms%n", executionTime);

        if (expected == obtained) {

            System.out.println("Result         : SUCCESS");

        } else {

            System.out.println("Result         : RACE CONDITION DETECTED");

        }

        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println();

    }

}
