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
            
            for (int i = 0; i < 5; i++) {
                int pizzaId = id * 100 + i;
                queue.put(pizzaId);
                produced.add(pizzaId);
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
                
                if (pizzaId == POISON_PILL) {
                    break;
                }
                
                consumed.add(pizzaId);
                Thread.sleep(random.nextInt(100));
            }
            
            return consumed;
        }
    }
}