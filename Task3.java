import java.util.*;
import java.util.concurrent.*;

public class Task3 {
    public static final int POISON_PILL = -1;

    static class Producer implements Callable<List<Integer>> {
        private final int id;
        private final BlockingQueue<Integer> queue;

        public Producer(int id, BlockingQueue<Integer> queue) {
            this.id = id;
            this.queue = queue;
        }

        public List<Integer> call() throws Exception {
            List<Integer> produced = new ArrayList<>();
            Random random = new Random();
            
            // Produce 10 pizzas
            for (int i = 0; i < 10; i++) {
                int pizzaId = id * 100 + i; // Unique pizza ID based on producer ID
                queue.put(pizzaId);
                produced.add(pizzaId);
                System.out.println("Producer " + id + " produced pizza: " + pizzaId);
                
                // Sleep for random interval (0-100ms)
                Thread.sleep(random.nextInt(100));
            }
            
            return produced;
        }
    }

    static class Consumer implements Callable<List<Integer>> {
        private final int id;
        private final BlockingQueue<Integer> queue;

        public Consumer(int id, BlockingQueue<Integer> queue) {
            this.id = id;
            this.queue = queue;
        }

        public List<Integer> call() throws Exception {
            List<Integer> consumed = new ArrayList<>();
            Random random = new Random();
            
            while (true) {
                int pizzaId = queue.take();
                
                // Check for poison pill
                if (pizzaId == POISON_PILL) {
                    System.out.println("Consumer " + id + " received poison pill. Stopping.");
                    break;
                }
                
                consumed.add(pizzaId);
                System.out.println("Consumer " + id + " consumed pizza: " + pizzaId);
                
                // Sleep for random interval (0-100ms)
                Thread.sleep(random.nextInt(100));
            }
            
            return consumed;
        }
    }
}