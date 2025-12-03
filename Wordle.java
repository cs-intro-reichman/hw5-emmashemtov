public class Wordle {

    // Reads all words from dictionary filename into a String array.
    public static String[] readDictionary(String filename) {
        In fileIn = new In(filename);
        String[] raw = fileIn.readAllStrings();
        fileIn.close();
        // Uppercase all words so we can compare easily with user input
        for (int i = 0; i < raw.length; i++) raw[i] = raw[i].toUpperCase();
        return raw;
    }

    // Choose a random secret word from the dictionary.
    public static String chooseSecretWord(String[] dict) {
        if (dict == null || dict.length == 0) {
            throw new IllegalArgumentException("Dictionary is empty");
        }
        int randomIndex = (int) (Math.random() * dict.length);
        return dict[randomIndex];
    }

    // Simple helper: check if letter c appears anywhere in secret (true), otherwise false.
    public static boolean containsChar(String secret, char c) {
        return secret.indexOf(c) != -1;
    }

    // Compute feedback for a single guess into resultRow.
    // G for exact match, Y if letter appears anywhere else, _ otherwise.
    public static void computeFeedback(String secret, String guess, char[] resultRow) {
        int length = secret.length();

        // First pass: mark exact matches as 'G', others temporarily '-'
        for (int i = 0; i < length; i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                resultRow[i] = 'G';
            } else {
                resultRow[i] = '-';
            }
        }

        // Second pass: for non-G positions, mark 'Y' if letter appears anywhere, else '_'
        for (int i = 0; i < length; i++) {
            if (resultRow[i] == 'G') continue;
            char g = guess.charAt(i);
            if (containsChar(secret, g)) resultRow[i] = 'Y';
            else resultRow[i] = '_';
        }
    }

    // Store guess string (chars) into the given row of guesses 2D array.
    public static void storeGuess(String guess, char[][] guesses, int row) {
        for (int col = 0; col < guess.length(); col++) {
            guesses[row][col] = guess.charAt(col);
        }
    }

    // Prints the game board up to currentRow (inclusive).
    public static void printBoard(char[][] guesses, char[][] results, int currentRow) {
        System.out.println("Current board:");
        for (int row = 0; row <= currentRow; row++) {
            System.out.print("Guess " + (row + 1) + ": ");
            for (int col = 0; col < guesses[row].length; col++) {
                System.out.print(guesses[row][col]);
            }
            System.out.print("   Result: ");
            for (int col = 0; col < results[row].length; col++) {
                System.out.print(results[row][col]);
            }
            System.out.println();
        }
        System.out.println();
    }

    // Returns true if all entries in resultRow are 'G'.
    public static boolean isAllGreen(char[] resultRow) {
        for (char entry : resultRow) {
            if (entry != 'G') return false;
        }
        return true;
    }

    public static void main(String[] args) {

        int WORD_LENGTH = 5;
        int MAX_ATTEMPTS = 6;

        // Read dictionary and choose secret (dictionary words uppercased)
        String[] dict = readDictionary("dictionary.txt");
        String secret = chooseSecretWord(dict);

        // Prepare 2D arrays for guesses and results (initialized with '\0' chars)
        char[][] guesses = new char[MAX_ATTEMPTS][WORD_LENGTH];
        char[][] results = new char[MAX_ATTEMPTS][WORD_LENGTH];

        // Prepare to read from standard input
        In inp = new In();

        int attempt = 0;
        boolean won = false;

        while (attempt < MAX_ATTEMPTS && !won) {

            String guess = "";
            boolean valid = false;

            // Loop until you read a valid guess
            while (!valid) {
                System.out.print("Enter your guess (5-letter word): ");
                String line = inp.readLine();
                if (line == null) line = "";            // avoid NPE during tests
                guess = line.toUpperCase();

                boolean wordFoundInDictionary = false;
                for (String word : dict) {
                    if (word.equals(guess)) {
                        wordFoundInDictionary = true;
                        break;
                    }
                }

                if (guess.length() != WORD_LENGTH || !wordFoundInDictionary) {
                    System.out.println("Invalid word. Please try again.");
                } else {
                    valid = true;
                }
            }

            // Store guess and compute feedback BEFORE printing the board
            storeGuess(guess, guesses, attempt);
            computeFeedback(secret, guess, results[attempt]);

            // Print board up to current attempt
            printBoard(guesses, results, attempt);

            // Check win
            if (isAllGreen(results[attempt])) {
                System.out.println("Congratulations! You guessed the word in " + (attempt + 1) + " attempts.");
                won = true;
            }

            attempt++;
        }

        if (!won) {
            // attempt has been incremented after last try; print up to last attempt index
            printBoard(guesses, results, attempt - 1);
            System.out.println("Sorry, you did not guess the word.");
            System.out.println("The secret word was: " + secret);
        }

        inp.close();
    }
}