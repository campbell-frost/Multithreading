import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SearchForWord {
    private static int count = 0;
    private static String[] searchWords;

    public static void main(String[] args) throws FileNotFoundException {

        Scanner text = new Scanner(new FileInputStream("C:\\Users\\Campbell\\Documents\\WarAndPeace.txt"));

        Scanner in = new Scanner(System.in);
        System.out.println("Enter 6 words to search for:");

        // Create new array to hold inputed strings
        searchWords = new String[6];
        for (int i = 0; i < 6; i++) {
            System.out.print("Word " + (i + 1) + ": ");
            searchWords[i] = in.next();
        }
        in.close();

        // Create six threads
        Thread[] threads = new Thread[6];
        for (int i = 0; i < 6; i++) {
            threads[i] = new Thread(new SearchForWordTask(text, "Thread " + (i + 1), searchWords[i]));
        }

        long startTime = System.currentTimeMillis();

        // Start the threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = (endTime - startTime);
        System.out.println("Total count: " + count + " and took " + totalTime + "ms" );
    }

    // Create a Runnable task for searching the word
    static class SearchForWordTask implements Runnable {
        private final Scanner text;
        private final String threadName;
        private final String searchWord;

        public SearchForWordTask(Scanner text, String threadName, String searchWord) {
            this.text = text;
            this.threadName = threadName;
            this.searchWord = searchWord;
        }

        @Override
        public void run() {
            int localCount = 0; // Local count for this thread

            while (true) {
                String line;
                synchronized (text) {
                    if (text.hasNextLine()) {
                        line = text.nextLine();
                    } else {
                        break; // No more lines to read
                    }
                }

                if (line.contains(searchWord)) {
                    localCount++;
                    System.out.println(threadName + ": " + localCount);
                }
            }

            System.out.println(threadName + ": Number of occurrences for " + searchWord + " is " + localCount);
            synchronized (SearchForWord.class) {
                count += localCount; // Update the global count
            }
        }
    }
}
