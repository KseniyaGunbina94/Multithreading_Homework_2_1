import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
    public static int[] findMaxFreq (Map<Integer, Integer> map) {
        int[] maxFreq = new int[2];
        for (Map.Entry entry : sizeToFreq.entrySet()) {
            if ((int) entry.getValue() > maxFreq[1]) {
                maxFreq[0] = (int) entry.getKey();
                maxFreq[1] = (int) entry.getValue();
            }
        }
        return maxFreq;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(30);
        Thread maxFreqInf = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    synchronized (sizeToFreq) {
                        sizeToFreq.wait();
                        System.out.println("Time: " + new Date() + "  Самое частое количество повторений: " + findMaxFreq(sizeToFreq)[0] + " (встретилось " + findMaxFreq(sizeToFreq)[1] + " раз)");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        maxFreqInf.start();

        for (int i = 0; i < 1000;  i++) {
            int freq = threadPool.submit(() -> {
                char[] routeElements = generateRoute("RLRFR", 100).toCharArray();
                Integer maxFreq = 0;
                for (char element : routeElements) {
                    if (Character.toString(element).equals("R"))
                        maxFreq++;
                }
                return maxFreq;
            }).get();
            synchronized (sizeToFreq) {
                if (!sizeToFreq.containsKey(freq)) {
                    sizeToFreq.put(freq, 1);
                    sizeToFreq.notify();
                }
                else {
                    sizeToFreq.put(freq, sizeToFreq.get(freq) + 1);
                    sizeToFreq.notify();
                }
            }
        }

        System.out.println("Самое частое количество повторений: " + findMaxFreq(sizeToFreq)[0] + " (встретилось " + findMaxFreq(sizeToFreq)[1] + " раз)\nДругие размеры:");
        sizeToFreq.forEach((o1, o2) -> System.out.println("- " + o1 + " (" + o2 + " раз)"));
    }
}
