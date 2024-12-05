import java.util.Comparator;

import components.map.Map;
import components.map.Map.Pair;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Program that reads a text file and then generates an HTML file with words'
 * occurrence in the file.
 *
 * @author Zhuoyang Li
 *
 */

public final class WordCounter {
    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private WordCounter() {
    }

    /**
     * Generate a HTML page with words' occurrence in the file.
     *
     * @param in
     *            the input stream
     * @param out
     *            the output stream
     * @param word
     *            the map of words and their occurrences
     * @param wordQueue
     *            the queue of words
     * @param fileName
     *            the name of the input file
     * @clear {@code wordQueue}, {@code word}
     * @ensures <pre>
     * {@code wordQueue = < >}
     * {@code word = < >}
     * </pre>
     */

    private static void html(SimpleReader in, SimpleWriter out,
            Map<String, Integer> word, Queue<String> wordQueue,
            String fileName) {
        int numberOfWords = 0;
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Word Counter : " + fileName + "</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Word Counter " + fileName + "</h2>");
        out.println("<hr />");
        out.println("<table border=\"1\">");
        out.println("<tr>");
        out.println("<th>Words</th>");
        out.println("<th>Counts</th>");
        out.println("</tr>");
        //print the words and their occurrences in the table
        while (!(wordQueue.length() == 0)) {
            String word1 = wordQueue.dequeue();
            Pair<String, Integer> pair = word.remove(word1);
            out.println("<tr>");
            out.println("<td>" + pair.key() + "</td>");
            out.println("<td>" + pair.value() + "</td>");
            out.println("</tr>");
            numberOfWords += pair.value();
        }
        out.println("</table>");
        out.println("<hr />");
        out.println("<p> Total number of words: " + numberOfWords + "</p>");
        out.println("</body>");
        out.println("</html>");

    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    private static void generateElements(String str, Set<Character> charSet) {
        assert str != null : "Violation of: str is not null";
        assert charSet != null : "Violation of: charSet is not null";

        for (int i = 0; i < str.length(); i++) {
            if (!charSet.contains(str.charAt(i))) {
                charSet.add(str.charAt(i));
            }
        }

    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        //
        String result = "";
        boolean isSeparator = separators.contains(text.charAt(position));
        int i = position;

        while (i < text.length()
                && (isSeparator == separators.contains(text.charAt(i)))) {
            result += text.charAt(i);
            i++;
        }

        return result;

    }

    /**
     * Generate a map of words and their occurrences.
     *
     * @param file
     *            the input stream
     * @param word
     *            the map of words and their occurrences
     * @updates {@code word}
     * @ensures <pre>
     * {@code wordQueue = < >}
     * </pre>
     */
    public static void mapGenerate(SimpleReader file,
            Map<String, Integer> word) {
        assert file.isOpen() : "Violation of: file is open";
        assert word != null : "Violation of: word is not null";

        word.clear();
        int i = 0; //position
        Set<Character> separatorSet = new Set1L<Character>();

        //common separators
        String separators = " ,.?!;:-()[]{}'\"/\\@#$%^&*";
        generateElements(separators, separatorSet);

        while (!file.atEOS()) {
            String line = file.nextLine();
            i = 0;
            while (i < line.length()) {
                String word1 = nextWordOrSeparator(line, i, separatorSet);
                if (!separatorSet.contains(word1.charAt(0))) {
                    if (word.hasKey(word1)) {
                        // word1 is in the set
                        int value = word.value(word1);
                        word.replaceValue(word1, value + 1);
                    } else {
                        // word1 is not in the set
                        word.add(word1, 1);
                    }
                }
                i += word1.length(); //update position for the next nextWordOrSeparator
            }
        }

    }

    /**
     * Compare {@code String}s in alphabetical order.
     */
    public static class WordComparator implements Comparator<String> {

        /**
         * Compare {@code String}s in alphabetical order.
         *
         * @param o1
         *            the first {@code String} to compare
         * @param o2
         *            the second {@code String} to compare
         * @return -1 if o1 is less than o2, 1 if o1 is greater than o2, 0 if o1
         *         is equal to o2
         */
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    /**
     * Sort the map of words and their occurrences in alphabetical order.
     *
     * @param word
     *            the map of words and their occurrences
     * @param order
     *            the comparator of {@code String}s
     * @param wordQueue
     *            the queue of words
     * @updates {@code keyQueue}
     * @clear {@code words}
     * @ensures <pre>
     * {@code words = < >}
     * </pre>
     */
    public static void sortMap(Map<String, Integer> word,
            Comparator<String> order, Queue<String> wordQueue) {
        assert word != null : "Violation of: words is not null";
        assert order != null : "Violation of: order is not null";
        assert wordQueue != null : "Violation of: wordQueue is not null";

        Map<String, Integer> temp = word.newInstance();
        Queue<String> tempQueue = wordQueue.newInstance();
        wordQueue.clear();

        while (word.iterator().hasNext()) {
            Pair<String, Integer> pair = word.removeAny();// get one word
            //add the pair to the temp map
            temp.add(pair.key(), pair.value());
            //create a queue of words with same order as the map
            wordQueue.enqueue(pair.key());
        }

        wordQueue.sort(order);
        while (wordQueue.iterator().hasNext()) {
            //get one word by alphabetical order
            String word1 = wordQueue.dequeue();
            //get the pair of the word from the map
            Pair<String, Integer> pair = temp.remove(word1);
            word.add(pair.key(), pair.value());
            tempQueue.enqueue(word1);
        }
        wordQueue.transferFrom(tempQueue);
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        Map<String, Integer> word = new Map1L<String, Integer>();
        Queue<String> wordQueue = new Queue1L<String>();
        Comparator<String> order = new WordComparator();

        out.print("Please enter the name of the input file(included file): ");
        String fileName = in.nextLine();
        SimpleReader file = new SimpleReader1L(fileName);
        //users should type "data/" and ".txt" in the input file name
        out.print("Please enter the name of the output file: ");
        SimpleWriter output = new SimpleWriter1L(in.nextLine());

        mapGenerate(file, word);
        sortMap(word, order, wordQueue);
        html(file, output, word, wordQueue, fileName);

        in.close();
        out.close();
        file.close();
        output.close();

    }
}
