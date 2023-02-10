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

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(30);
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
                if (!sizeToFreq.containsKey(freq))
                    sizeToFreq.put(freq, 1);
                else
                    sizeToFreq.put(freq, sizeToFreq.get(freq) + 1);
            }
        }
        int[] maxFreq = new int[2];
        for (Map.Entry entry : sizeToFreq.entrySet()) {
            if ((int) entry.getValue() > maxFreq[1]) {
                maxFreq[0] = (int) entry.getKey();
                maxFreq[1] = (int) entry.getValue();
            }
        }
        System.out.println("Самое частое количество повторений: " + maxFreq[0] + " (встретилось " + maxFreq[1] + " раз)\nДругие размеры:");
        sizeToFreq.forEach((o1, o2) -> System.out.println("- " + o1 + " (" + o2 + " раз)"));
    }
}
