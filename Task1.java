import java.util.*;

public class Task1 {
   
    static class MonitorLock {
        private boolean locked = false;
        
        public synchronized void lock() throws InterruptedException {
            while (locked) {
                wait();
            }
            locked = true;
        }
        
        public synchronized void unlock() {
            locked = false;
            notify();
        }
    }
    
    static class ReusableBarrier {
        private final int friends;
        private final Runnable task;
        
        private int completed = 0;
        private int current_pizza = 0;

        private final MonitorLock lock = new MonitorLock();
        private final Object condition = new Object(); 

        public ReusableBarrier(int friends) { this(friends, null); }

        public ReusableBarrier(int friends, Runnable task) {
            if (friends <= 0) throw new IllegalArgumentException("friends must be > 0");
            this.friends = friends;
            this.task = task;
        }

        public void await() throws InterruptedException {
            lock.lock();
            try {
                int pizza = current_pizza;
                completed++;
                
                if (completed == friends) {
                    if (task != null) {
                        task.run();
                    }
                    completed = 0;
                    current_pizza++;
                    synchronized(condition) {
                        condition.notifyAll();
                    }
                } else {

                    lock.unlock();
                    synchronized(condition) {
                        while (pizza == current_pizza) {
                            condition.wait();
                        }
                    }
                    return;
                }
            } finally {
                if (completed == 0) {
                    lock.unlock();
                }
            }
        }

    }
    
}
