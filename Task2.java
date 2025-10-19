import java.util.concurrent.locks.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Task2 {
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
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

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
        private volatile int head = 0, tail = 0; 
        private final AtomicInteger size = new AtomicInteger(0);

        //add any lock and conditions here
        private final ReentrantLock enqueueLock = new ReentrantLock();
        private final ReentrantLock dequeueLock = new ReentrantLock();
        private final Condition notFull = enqueueLock.newCondition();
        private final Condition notEmpty = dequeueLock.newCondition();
        
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