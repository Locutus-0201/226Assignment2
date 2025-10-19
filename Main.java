import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Multi-Threading Assignment Tests ===\n");
        
        // Test Task 1
        testTask1();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Test Task 2
        testTask2();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Test Task 3
        testTask3();
    }
    
    private static void testTask1() throws InterruptedException {
        System.out.println("=== Task 1: Barrier Synchronization ===\n");
        
        final int numFriends = 5;
        final int numPizzas = 3;
        
        Task1.ReusableBarrier barrier = new Task1.ReusableBarrier(numFriends, 
            () -> System.out.println("All friends finished! Time to bake the pizza!\n"));
        
        String[] names = {"You", "Jen", "Steve", "Mark", "Jessica"};
        String[] tasks = {"make the dough", "knead the dough", "spread the sauce", 
                         "add the cheese", "add toppings"};
        
        Thread[] threads = new Thread[numFriends];
        
        for (int i = 0; i < numFriends; i++) {
            final int friendId = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int pizza = 1; pizza <= numPizzas; pizza++) {
                        System.out.println(names[friendId] + " is working on pizza " + pizza 
                            + " (" + tasks[friendId] + ")");
                        Thread.sleep((long)(Math.random() * 100 + 50)); // Simulate work
                        System.out.println(names[friendId] + " finished pizza " + pizza);
                        barrier.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
        
        // Wait for all threads to complete
        for (Thread t : threads) {
            t.join();
        }
        
        System.out.println("Task 1 completed successfully!");
    }
    
    private static void testTask2() throws InterruptedException {
        System.out.println("=== Task 2: Concurrent Data Structure ===\n");
        
        // Test Coarse-Grained Queue
        System.out.println("--- Testing Coarse-Grained Queue ---");
        testQueue(new Task2.CoarseGrainedBlockingQueue<>(5), "Coarse-Grained");
        
        System.out.println();
        
        // Test Fine-Grained Queue
        System.out.println("--- Testing Fine-Grained Queue ---");
        testQueue(new Task2.FineGrainedBlockingQueue<>(5), "Fine-Grained");
        
        System.out.println("\nTask 2 completed successfully!");
    }
    
    private static void testQueue(Task2.MyBlockingQueue<Integer> queue, String type) 
            throws InterruptedException {
        final int numWaiters = 3;
        final int numCustomers = 3;
        final int pizzasPerWaiter = 5;
        
        Thread[] waiters = new Thread[numWaiters];
        Thread[] customers = new Thread[numCustomers];
        
        // Create waiter threads (producers)
        for (int i = 0; i < numWaiters; i++) {
            final int waiterId = i;
            waiters[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < pizzasPerWaiter; j++) {
                        int pizzaId = waiterId * 100 + j;
                        queue.enqueue(pizzaId);
                        System.out.println("[" + type + "] Waiter " + waiterId 
                            + " placed pizza " + pizzaId + " on counter");
                        Thread.sleep((long)(Math.random() * 50));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        
        // Create customer threads (consumers)
        for (int i = 0; i < numCustomers; i++) {
            final int customerId = i;
            customers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < pizzasPerWaiter; j++) {
                        int pizzaId = queue.dequeue();
                        System.out.println("[" + type + "] Customer " + customerId 
                            + " picked up pizza " + pizzaId + " from counter");
                        Thread.sleep((long)(Math.random() * 50));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        
        // Start all threads
        for (Thread t : waiters) t.start();
        for (Thread t : customers) t.start();
        
        // Wait for all threads to complete
        for (Thread t : waiters) t.join();
        for (Thread t : customers) t.join();
    }
    
    private static void testTask3() throws Exception {
        System.out.println("=== Task 3: Producer-Consumer ===\n");
        
        final int numProducers = 2;
        final int numConsumers = 2;
        final int queueCapacity = 5;
        
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(queueCapacity);
        
        ExecutorService executor = Executors.newFixedThreadPool(numProducers + numConsumers);
        List<Future<List<Integer>>> producerFutures = new ArrayList<>();
        List<Future<List<Integer>>> consumerFutures = new ArrayList<>();
        
        // Start producers
        for (int i = 0; i < numProducers; i++) {
            Task3.Producer producer = new Task3.Producer(i, queue);
            producerFutures.add(executor.submit(producer));
        }
        
        // Start consumers
        for (int i = 0; i < numConsumers; i++) {
            Task3.Consumer consumer = new Task3.Consumer(i, queue);
            consumerFutures.add(executor.submit(consumer));
        }
        
        // Wait for all producers to finish
        int totalProduced = 0;
        for (Future<List<Integer>> future : producerFutures) {
            List<Integer> produced = future.get();
            totalProduced += produced.size();
        }
        
        System.out.println("\nAll producers finished. Total pizzas produced: " + totalProduced);
        
        // Send poison pills to consumers
        for (int i = 0; i < numConsumers; i++) {
            queue.put(Task3.POISON_PILL);
        }
        
        // Wait for all consumers to finish
        int totalConsumed = 0;
        for (Future<List<Integer>> future : consumerFutures) {
            List<Integer> consumed = future.get();
            totalConsumed += consumed.size();
        }
        
        System.out.println("All consumers finished. Total pizzas consumed: " + totalConsumed);
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("\nTask 3 completed successfully!");
    }
}