# Multi-Threading Concurrency Assignment

## Overview
This assignment implements three classic concurrency problems using multi-threading, with focus on correctness, synchronization, and demonstrating understanding of concurrency control.

## Files Structure

### Implementation Files
- **Task1.java** - Barrier Synchronization (custom MonitorLock implementation)
- **Task2.java** - Concurrent Data Structures (Coarse-grained & Fine-grained blocking queues)
- **Task3.java** - Producer-Consumer Pattern (with poison pill termination)
- **Main.java** - Comprehensive test suite for all three tasks

### Documentation
- **REPORT.txt** - Detailed report answering all assignment questions with explanations

## How to Run

### Compile
```bash
javac *.java
```

### Run Tests
```bash
java Main
```

This will run all three tasks sequentially with output showing:
- Task 1: 5 friends making 3 pizzas with barrier synchronization
- Task 2: Testing both coarse-grained and fine-grained blocking queues
- Task 3: Producer-consumer with 2 producers and 2 consumers (20 pizzas total)

## Task Summaries

### Task 1: Barrier Synchronization ✅
- **Custom Lock**: MonitorLock (blocking mutex with wait/notify)
- **Why**: Efficient blocking for barrier waits, no CPU waste
- **Alternatives Considered**: TAS lock, TTAS lock (rejected due to spinning)
- **Result**: Reusable barrier that synchronizes 5 threads across 3 cycles

### Task 2: Concurrent Data Structure ✅
- **Coarse-Grained**: Single ReentrantLock, simple but lower concurrency
- **Fine-Grained**: Separate locks for enqueue/dequeue, higher concurrency
- **Lock Choice**: ReentrantLock with Conditions
- **Recommendation**: Fine-grained for pizzeria (better throughput)

### Task 3: Producer-Consumer ✅
- **Synchronization**: BlockingQueue (LinkedBlockingQueue)
- **Termination**: Poison pill pattern (-1 value)
- **Alternatives Discussed**: Volatile flag, AtomicBoolean, interrupts
- **Safety**: Prevents overwrite/underflow through bounded blocking queue

## Key Concepts Demonstrated

1. **Lock Types**: Monitor locks, ReentrantLock, conditions
2. **Synchronization Patterns**: Barriers, producer-consumer
3. **Concurrency Trade-offs**: Coarse vs. fine-grained locking
4. **Thread Safety**: Mutual exclusion, no race conditions, no deadlocks
5. **Graceful Shutdown**: Poison pill pattern
6. **Blocking vs. Spinning**: Appropriate lock selection

## Test Results
✅ All tests pass successfully  
✅ No race conditions detected  
✅ No deadlocks observed  
✅ Correct output for all scenarios  
✅ Graceful shutdown achieved  

## Report Contents
The REPORT.txt file contains detailed answers to all assignment questions:
- Lock implementations considered and chosen (Task 1)
- Comparison of coarse-grained vs fine-grained implementations (Task 2)
- Which option is better for pizzeria and why (Task 2)
- Alternative to poison pill and implementation (Task 3)
- How overwrite/underflow is prevented (Task 3)

## Author Notes
This implementation showcases understanding of:
- Multiple lock types from the concurrency course
- Trade-offs between different synchronization approaches
- Proper use of conditions and blocking mechanisms
- Real-world application of theoretical concepts
