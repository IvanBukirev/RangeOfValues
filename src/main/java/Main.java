import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];

        final ExecutorService threadPool = Executors.newFixedThreadPool(25);
        List<Future> futures = new ArrayList<>();

        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);

            String text = texts[i];

            Callable<Integer> callable = () -> {
                int maxSize = 0;
                for (int s = 0; s < text.length(); s++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (s >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = s; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - s) {
                            maxSize = j - s;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };

            futures.add(threadPool.submit(callable));
        }

        long startTs = System.currentTimeMillis();

        int maxRange = 0;
        for (Future<Integer> future : futures) {
            maxRange = Math.max(maxRange, future.get());
        }
        System.out.printf("Максимальный интервал значений: %d%n%n", maxRange);

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");

        threadPool.shutdown();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}