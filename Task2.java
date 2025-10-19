import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Task2 {
    /*
     * TASK 2: CONCURRENT DATA STRUCTURE - BLOCKING QUEUE IMPLEMENTATIONS
     * ===================================================================
     * 
     * Two implementations of thread-safe bounded blocking queues:
     * 
     * 1. COARSE-GRAINED LOCKING:
     *    - Single ReentrantLock protects all operations
     *    - Simple to reason about - one lock guards everything
     *    - Lower concurrency: only ONE operation at a time
     *    - Uses two Conditions: notFull (producers wait), notEmpty (consumers wait)
     * 
     * 2. FINE-GRAINED LOCKING:
     *    - Separate locks: enqueueLock (tail) and dequeueLock (head)
     *    - Higher concurrency: producers and consumers can work simultaneously
     *    - More complex: requires AtomicInteger for size, cross-lock signaling
     *    - Uses volatile indices and careful coordination between locks
     * 
     * LOCK CHOICES:
     * - ReentrantLock: Provides explicit lock()/unlock() and Condition variables
     * - Better than synchronized: supports multiple conditions, fairer, more flexible
     * - Conditions (await/signal): Block threads efficiently when queue is full/empty
     */
    
    public interface MyBlockingQueue<T> {
        void enqueue(T item) throws InterruptedException;
        T dequeue() throws InterruptedException;
        int size();
        int capacity();
    }

    public static class CoarseGrainedBlockingQueue<T> implements MyBlockingQueue<T> {
        private final Object[] queue;
        private int head = 0, tail = 0, count = 0;

        //add any lock and conditions here
        // COARSE-GRAINED: Single lock guards all queue state
        // Analogy: One cashier manages the entire counter (Option 1 from assignment)
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();   // Waiters wait here when counter is full
        private final Condition notEmpty = lock.newCondition();  // Customers wait here when counter is empty

        public CoarseGrainedBlockingQueue(int capacity) {
            if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
            this.queue = new Object[capacity];
        }

        @Override
        public void enqueue(T item) throws InterruptedException {
            lock.lock();
            try {
                while (count == queue.length) {
                    notFull.await();
                }
                queue[tail] = item;
                tail = (tail + 1) % queue.length;
                count++;
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public T dequeue() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0) {
                    notEmpty.await();
                }
                @SuppressWarnings("unchecked")
                T item = (T) queue[head];
                queue[head] = null;
                head = (head + 1) % queue.length;
                count--;
                notFull.signal();
                return item;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int size() {
            lock.lock();
            try { return count; } finally { lock.unlock(); }
        }

        @Override
        public int capacity() { return queue.length; }
    }

    public static class FineGrainedBlockingQueue<T> implements MyBlockingQueue<T> {
        private final Object[] queue;
        private volatile int head = 0, tail = 0;  // Volatile for visibility across threads
        private final AtomicInteger size = new AtomicInteger(0);  // Atomic for thread-safe size tracking

        //add any lock and conditions here
        // FINE-GRAINED: Separate locks for each end of the queue
        // Analogy: Two cashiers - one for kitchen side (enqueue), one for customer side (dequeue) (Option 2)
        private final ReentrantLock enqueueLock = new ReentrantLock();  // Protects tail operations
        private final ReentrantLock dequeueLock = new ReentrantLock();  // Protects head operations
        private final Condition notFull = enqueueLock.newCondition();   // Waiters wait on enqueue lock
        private final Condition notEmpty = dequeueLock.newCondition();  // Customers wait on dequeue lock
        
        public FineGrainedBlockingQueue(int capacity) {
            if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
            this.queue = new Object[capacity];
        }

        @Override
        public void enqueue(T item) throws InterruptedException {
            enqueueLock.lock();
            try {
                while (size.get() == queue.length) {
                    notFull.await();
                }
                queue[tail] = item;
                tail = (tail + 1) % queue.length;
                int currentSize = size.incrementAndGet();
                
                // Signal the dequeue side that there's an item available
                if (currentSize == 1) {
                    dequeueLock.lock();
                    try {
                        notEmpty.signal();
                    } finally {
                        dequeueLock.unlock();
                    }
                }
                
                // If there's still space, signal another enqueuing thread
                if (currentSize < queue.length) {
                    notFull.signal();
                }
            } finally {
                enqueueLock.unlock();
            }
        }

        @Override
        public T dequeue() throws InterruptedException {
            dequeueLock.lock();
            try {
                while (size.get() == 0) {
                    notEmpty.await();
                }
                @SuppressWarnings("unchecked")
                T item = (T) queue[head];
                queue[head] = null;
                head = (head + 1) % queue.length;
                int currentSize = size.decrementAndGet();
                
                // Signal the enqueue side that there's space available
                if (currentSize == queue.length - 1) {
                    enqueueLock.lock();
                    try {
                        notFull.signal();
                    } finally {
                        enqueueLock.unlock();
                    }
                }
                
                // If there are still items, signal another dequeuing thread
                if (currentSize > 0) {
                    notEmpty.signal();
                }
                
                return item;
            } finally {
                dequeueLock.unlock();
            }
        }

        @Override
        public int size() { return size.get(); }

        @Override
        public int capacity() { return queue.length; }
    }
}