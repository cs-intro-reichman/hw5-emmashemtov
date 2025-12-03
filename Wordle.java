public class Wordle {

    // Reads all words from dictionary filename into a String array (uppercased)
    public static String[] readDictionary(String filename) {
        In fileIn = new In(filename);
        String[] raw = fileIn.readAllStrings();
        fileIn.close();
        for (int i = 0; i < raw.length; i++) raw[i] = raw[i].toUpperCase();
        return raw;
    }

    // Choose a random secret word from the dictionary
    public static String chooseSecretWord(String[] dict) {
        if (dict == null || dict.length == 0) {
            throw new IllegalArgumentException("Dictionary is empty");
        }
        int randomIndex = (int) (Math.random() * dict.length);
        return dict[randomIndex];
    }

    // Check if letter c appears anywhere in secret
    public static boolean containsChar(String secret, char c) {
        return secret.indexOf(c) != -1;
    }

    // Compute feedback for a guess into resultRow
    // G = exact, Y = wrong position, _ = not present
    public static void computeFeedback(String secret, String guess, char[] resultRow) {
        int length = secret.length();

        // First pass: mark exact matches
        for (int i = 0; i < length; i++) {
            if (guess.charAt(i) == secret.charAt(i)) resultRow[i] = 'G';
            else resultRow[i] = '-';
        }

        // Second pass: mark Y or _
        for (int i = 0; i < length; i++) {
            if (resultRow[i] == 'G') continue;
            char g = guess.charAt(i);
            if (containsChar(secret, g)) resultRow[i] = 'Y';
            else resultRow[i] = '_';
        }
    }

    // Store guess into guesses[row]
    public static void storeGuess(String guess, char[][] guesses, int row) {
        for (int col = 0; col < guess.length(); col++) {
            guesses[row][col] = guess.charAt(col);
        }
    }

    // Print game board up to currentRow
    public static void printBoard(char[][] guesses, char[][] results, int currentRow) {
        System.out.println("Current board:");
        for (int row = 0; row <= currentRow; row++) {
            System.out.print("Guess " + (row + 1) + ": ");
            for (int col = 0; col < guesses[row].length; col++) System.out.print(guesses[row][col]);
            System.out.print("   Result: ");
            for (int col = 0; col < results[row].length; col++) System.out.print(results[row][col]);
            System.out.println();
        }
        System.out.println();
    }

    // Returns true if all entries in resultRow are G
    public static boolean isAllGreen(char[] resultRow) {
        for (char c : resultRow) if (c != 'G') return false;
        return true;
    }

    public static void main(String[] args) {

        int WORD_LENGTH = 5;
        int MAX_ATTEMPTS = 6;

        String[] dict = readDictionary("dictionary.txt");
        String secret = chooseSecretWord(dict);

        char[][] guesses = new char[MAX_ATTEMPTS][WORD_LENGTH];
        char[][] results = new char[MAX_ATTEMPTS][WORD_LENGTH];

        In inp = new In();

        int attempt = 0;
        boolean won = false;

        while (attempt < MAX_ATTEMPTS && !won) {
            String guess = "";
            boolean valid = false;

            while (!valid) {
                System.out.print("Enter your guess (5-letter word): ");
                String line = inp.readLine();
                if (line == null) line = "";          // avoid NPE
                guess = line.toUpperCase();

                if (guess.length() != WORD_LENGTH) {
                    System.out.println("Invalid word. Please try again.");
                    continue;
                }

                boolean found = false;
                for (String word : dict) {
                    if (word.equals(guess)) { found = true; break; }
                }

                if (!found) System.out.println("Invalid word. Please try again.");
                else valid = true;
            }

            // Store guess and compute feedback before printing
            storeGuess(guess, guesses, attempt);
            computeFeedback(secret, guess, results[attempt]);
            printBoard(guesses, results, attempt);

            if (isAllGreen(results[attempt])) {
                System.out.println("Congratulations! You guessed the word in " + (attempt + 1) + " attempts.");
                won = true;
            }

            attempt++;
        }

        if (!won) {
            printBoard(guesses, results, attempt - 1);
            System.out.println("Sorry, you did not guess the word.");
            System.out.println("The secret word was: " + secret);
        }

        inp.close();
    }
}