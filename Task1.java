public class Task1 {
    /*
     * LOCK IMPLEMENTATION REPORT FOR TASK 1
     * =====================================
     * 
     * Custom MonitorLock (Blocking Mutex using Java Monitors)
     *    - Uses synchronized keyword with wait()/notify()
     *    - Threads BLOCK (not spin) when waiting for lock
     *    - Best for: Long critical sections or when threads should yield CPU
     *    - Advantage: No CPU waste, threads sleep until lock is available
     * 
     * WHY THIS LOCK WAS CHOSEN:
     * Barrier synchronization requires threads to wait (potentially for
     * varying amounts of time) until all friends finish their tasks. Spinning
     * would waste CPU cycles. MonitorLock blocks threads efficiently and
     * supports the wait()/notify() pattern needed for condition synchronization.
     * 
     * The barrier uses TWO synchronization mechanisms:
     * - MonitorLock: Protects critical section (incrementing counters)
     * - Condition Object: Allows threads to wait until all friends arrive
     */
    
    // Custom Blocking Mutex using Monitor pattern (wait/notify)
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

        //add lock here
        // Using custom MonitorLock for mutual exclusion
        // Supports blocking wait() which is essential for barrier synchronization
        private final MonitorLock lock = new MonitorLock();
        private final Object condition = new Object();  // Separate condition variable for barrier synchronization

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
                    // Last thread to arrive - execute task and reset
                    if (task != null) {
                        task.run();
                    }
                    completed = 0;
                    current_pizza++;
                    // Wake up all waiting threads
                    synchronized(condition) {
                        condition.notifyAll();
                    }
                } else {
                    // Release the lock and wait for barrier to complete
                    lock.unlock();
                    synchronized(condition) {
                        while (pizza == current_pizza) {
                            condition.wait();
                        }
                    }
                    // Don't need to reacquire lock since we're done
                    return;
                }
            } finally {
                // Only unlock if we're the last thread (didn't return early)
                if (completed == 0) {
                    lock.unlock();
                }
            }
        }

    }
    
}
