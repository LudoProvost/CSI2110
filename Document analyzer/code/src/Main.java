import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//DocumentAnalyzer
class Main {

    private static String readLine() {
        byte[] chars = new byte[2048];
        int car = -1;
        int i = 0;
        try {
            while (i < 2048) {
                car = System.in.read();
                if (car < 0 || car == '\n' || car == '\r') {
                    break;
                }
                chars[i++] += car;
            }
        } catch (IOException e) {
            return null;
        }
        return new String(chars, 0, i);
    }

    public static void main(String[] args) {

        String line = readLine();
        int numOfDocs = Integer.parseInt(line);

        HashMap<Integer, String> hashWord = new HashMap<>();
        HashMap<String, Integer> hashCount = new HashMap<>();

        // split into list of words
        for (int i = 1; i <= numOfDocs; i++) {
            int wordCount = 1;
            String doc = "";
            String word = "";

            hashWord.clear();
            hashCount.clear();

            Pattern r = Pattern.compile("[a-zA-Z]+");

            // create string of everything in each document
            while (!(line = readLine()).equals("END")) {
                Matcher m = r.matcher(line);
                while (m.find()) {
                    word = m.group();
                    hashWord.put(wordCount++, word);
                    hashCount.put(word, 0);
                }
            }

            int missingWords = hashCount.size(), p = 1;
            int bestP = 1, bestQ = hashWord.size();

            for (int q = 1; q <= hashWord.size(); q++) {
                String currentWord = hashWord.get(q);
                if (hashCount.get(currentWord) == 0) {
                    missingWords--;
                }
                hashCount.put(currentWord, hashCount.get(currentWord)+1);
                while (hashCount.get(hashWord.get(p)) > 1) {
                    hashCount.put(hashWord.get(p),hashCount.get(hashWord.get(p))-1);
                    p++;
                }
                if ((missingWords == 0) && (bestQ-bestP) > (q-p)) {
                    bestQ = q;
                    bestP = p;
                }
            }

            System.out.println("Document "+i+": "+bestP+" "+bestQ);

        }
    }
}
