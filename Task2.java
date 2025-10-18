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

        public CoarseGrainedBlockingQueue(int capacity) {
            if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
            this.queue = new Object[capacity];
        }

        @Override
        public void enqueue(T item) throws InterruptedException {
            //TODO: Implement the function
        }

        @Override
        public T dequeue() throws InterruptedException {
            //TODO: Implement the function
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
        
        public FineGrainedBlockingQueue(int capacity) {
            if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
            this.queue = new Object[capacity];
        }

        @Override
        public void enqueue(T item) throws InterruptedException {
            //TODO: Implement the function
        }

        @Override
        public T dequeue() throws InterruptedException {
            //TODO: Implement the function
        }

        @Override
        public int size() { return size.get(); }

        @Override
        public int capacity() { return queue.length; }
    }
}