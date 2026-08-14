public class ContadorThreads {

    // Contador compartilhado
    static int contador = 0;

    // Quantidade de threads
    static final int NUM_THREADS = 10;

    // Quantidade de incrementos por thread
    static final int INCREMENTOS = 100_000;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== TESTE SEM SINCRONIZAÇÃO ===");
        testarSemSincronizacao();

        System.out.println();

        System.out.println("=== TESTE COM SINCRONIZAÇÃO ===");
        testarComSincronizacao();
    }

    // ----------------------------
    // SEM SINCRONIZAÇÃO
    // ----------------------------
    static void testarSemSincronizacao() throws InterruptedException {
        contador = 0;

        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTOS; j++) {
                    contador++; // acesso sem proteção
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        int esperado = NUM_THREADS * INCREMENTOS;

        System.out.println("Valor esperado: " + esperado);
        System.out.println("Valor obtido : " + contador);
    }

    // ----------------------------
    // COM SINCRONIZAÇÃO
    // ----------------------------
    static final Object lock = new Object();

    static void incrementarSincronizado() {
        synchronized (lock) {
            contador++;
        }
    }

    static void testarComSincronizacao() throws InterruptedException {
        contador = 0;

        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTOS; j++) {
                    incrementarSincronizado();
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        int esperado = NUM_THREADS * INCREMENTOS;

        System.out.println("Valor esperado: " + esperado);
        System.out.println("Valor obtido : " + contador);
    }
}