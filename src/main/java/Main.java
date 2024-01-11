import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        List<Future<Integer>> futures = new ArrayList<>();
        ExecutorService es = Executors.newFixedThreadPool(4);

        // start time
        long startTs = System.currentTimeMillis();

        for (String text : texts) {
            final Callable<Integer> task = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };

            Future<Integer> fut = es.submit(task);
            futures.add(fut);
        }


        int maxSize = futures.stream()
                .map(Main::getFutureValue)
                .reduce(Math::max)
                .get();


        long endTs = System.currentTimeMillis();

        System.out.println("Max interval: " + maxSize);
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static int getFutureValue(Future<Integer> integerFuture) {
        try {
            return integerFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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