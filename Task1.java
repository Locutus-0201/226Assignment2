import java.util.*;

public class Task1 {
    static class ReusableBarrier {
        private final int friends;
        private final Runnable task;
        
        private int completed = 0;
        private int current_pizza = 0;

        //add lock here

        public ReusableBarrier(int friends) { this(friends, null); }

        public ReusableBarrier(int friends, Runnable task) {
            if (friends <= 0) throw new IllegalArgumentException("friends must be > 0");
            this.friends = friends;
            this.task = task;
        }

        public void await() throws InterruptedException {
            //TODO: Implement function
        }

    }
    
}

