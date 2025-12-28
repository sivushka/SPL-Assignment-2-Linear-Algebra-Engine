package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        this.workers = new TiredThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            double fatigueFactor = 0.5 + Math.random();
            workers[i] = new TiredThread(i, fatigueFactor);
            workers[i].start(); 
            idleMinHeap.add(workers[i]); 
        }
    }

    public void submit(Runnable task) {
        // TODO
        try {
            TiredThread worker = idleMinHeap.take();
            inFlight.incrementAndGet();
            
            Runnable wrappedTask = new Runnable() {
                @Override
                public void run() {
                    try {
                        task.run(); 
                    } 
                    finally {
                        synchronized (inFlight) {
                            if (inFlight.decrementAndGet() == 0) {
                                inFlight.notifyAll(); 
                            } 
                        }
                        idleMinHeap.add(worker);
                    }
                }
            };
                worker.newTask(wrappedTask);
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for (Runnable t : tasks) submit(t);

        synchronized (inFlight) {
            while (inFlight.get() > 0) {
                try {
                    inFlight.wait(); 
                } 
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    public void shutdown() throws InterruptedException {
        // TODO
        for (TiredThread w : workers) w.shutdown();
        for (TiredThread w : workers) w.join();
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        StringBuilder report = new StringBuilder("--- Worker Stats ---\n");
        for (TiredThread w : workers) {
            report.append(String.format("ID: %d | Fatigue: %.2f | Used: %d ns | Idle: %d ns\n",
                    w.getWorkerId(), w.getFatigue(), w.getTimeUsed(), w.getTimeIdle()));
        }
        return report.toString();
    }
}
