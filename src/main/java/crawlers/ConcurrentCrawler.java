package crawlers;

import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ConcurrentCrawler {

    private final ExecutorService executor;
    private final BlockingQueue<String> frontier;
    private final Set<String> visited;

    private final Function<String, Set<String>> fetchFunction;

    private final Semaphore rateLimiter;
    private final int maxPages;

    private final AtomicInteger processed = new AtomicInteger();

    public ConcurrentCrawler(
            int workerCount,
            int maxPages,
            int maxConcurrentRequests,
            Function<String, Set<String>> fetchFunction
    ) {
        this.executor = Executors.newFixedThreadPool(workerCount);
        this.frontier = new LinkedBlockingQueue<>();
        this.visited = ConcurrentHashMap.newKeySet();
        this.fetchFunction = fetchFunction;
        this.rateLimiter = new Semaphore(maxConcurrentRequests);
        this.maxPages = maxPages;
    }

    public void start(String startUrl) throws InterruptedException {
        visited.add(startUrl);
        frontier.add(startUrl);

        for (int i = 0; i < ((ThreadPoolExecutor) executor).getCorePoolSize(); i++) {
            executor.submit(this::workerLoop);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
    }

    private void workerLoop() {
        try {
            while (true) {

                if (processed.get() >= maxPages) {
                    return;
                }

                String url = frontier.poll(2, TimeUnit.SECONDS);
                if (url == null) {
                    return;
                }

                processed.incrementAndGet();

                try {
                    rateLimiter.acquire();  // 🔒 limit concurrent HTTP calls
                    Set<String> discovered;
                    try {
                        discovered = fetchFunction.apply(url);
                    } finally {
                        rateLimiter.release();
                    }

                    for (String link : discovered) {
                        if (visited.add(link)) {
                            frontier.offer(link);
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Failed: " + url);
                }
            }
        } catch (InterruptedException ignored) {
        }
    }
}