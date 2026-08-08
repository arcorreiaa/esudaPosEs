package br.edu.esuda.concurrent;

import br.edu.esuda.concurrent.benchmark.CounterBenchmark;

/**
 * Entry point of the Java Concurrency Laboratory.
 *
 * @author ESUDA
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {

        printHeader();

        CounterBenchmark benchmark = new CounterBenchmark();

        benchmark.execute();

        printFooter();

    }

    /**
     * Prints the application header.
     */
    private static void printHeader() {

        System.out.println("============================================================");
        System.out.println("        JAVA CONCURRENCY LABORATORY");
        System.out.println("============================================================");
        System.out.println();
        System.out.println("Institution : ESUDA");
        System.out.println("Course      : Distributed Systems and Cloud Computing");
        System.out.println("Topic       : Race Condition and Synchronization");
        System.out.println("Java        : " + Runtime.version().feature());
        System.out.println();
        System.out.println("============================================================");
        System.out.println();

    }

    /**
     * Prints the application footer.
     */
    private static void printFooter() {

        System.out.println();
        System.out.println("=========================================================");
        System.out.println("Laboratory completed.");
        System.out.println("=========================================================");

    }

}
