public class Wordle {

    // Reads all words from dictionary filename into a String array.
    public static String[] readDictionary(String filename) {
        In in = new In(filename); 

        // The In class provides readAll() to read the entire text of the file as one
        // string.
        // We split this string by whitespace to get individual words.
        String text = in.readAll();
        String[] dict = text.split("\\s+"); // Split by any sequence of whitespace characters

        // Note: The dictionary.txt contains one word per line, but text.split("\\s+")
        // handles both spaces and newlines efficiently.

        return dict;
    }

    // Choose a random secret word from the dictionary.
    // Hint: Pick a random index between 0 and dict.length (not including) using
    // Math.random()
    public static String chooseSecretWord(String[] dict) {
        // Generate a random index between 0 (inclusive) and dict.length (exclusive)
        int randomIndex = (int) (Math.random() * dict.length);
        return dict[randomIndex];
    }

    // Simple helper: check if letter c appears anywhere in secret (true), otherwise
    // return false.
    public static boolean containsChar(String secret, char c) {
        return secret.indexOf(c) >= 0;

    }

    // Compute feedback for a single guess into resultRow.
    // G for exact match, Y if letter appears anywhere else, _ otherwise.
    public static void computeFeedback(String secret, String guess, char[] resultRow) {
        // The length of the secret and guess should be 5 (WORD_LENGTH)
        for (int i = 0; i < secret.length(); i++) {
            char guessedChar = guess.charAt(i);

            // 1. Check for Green ('G'): Correct letter in correct position [cite: 1473]
            if (guessedChar == secret.charAt(i)) {
                resultRow[i] = 'G';
            }
            // 2. Check for Yellow ('Y'): Correct letter in wrong position [cite: 1474]
            // else if the letter appears anywhere in the secret word [cite: 1528]
            else if (containsChar(secret, guessedChar)) {
                resultRow[i] = 'Y';
            }
            // 3. Check for Gray ('_'): Letter not in the word at all [cite: 1475, 1503]
            else {
                resultRow[i] = '_';
            }
        }
    }

    // Store guess string (chars) into the given row of guesses 2D array.
    // For example, of guess is HELLO, and row is 2, then after this function
    // guesses should look like:
    // guesses[2][0] // 'H'
    // guesses[2][1] // 'E'
    // guesses[2][2] // 'L'
    // guesses[2][3] // 'L'
    // guesses[2][4] // 'O'
    public static void storeGuess(String guess, char[][] guesses, int row) {
        // We iterate through the columns (letter positions)
        for (int col = 0; col < guess.length(); col++) {
            // Store each character of the guess string into the corresponding cell
            // in the current row of the 2D array [cite: 1542]
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
        // The assignment's win condition is "all Gs"[cite: 1545].
        for (char feedback : resultRow) {
            if (feedback != 'G') {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        int WORD_LENGTH = 5;
        int MAX_ATTEMPTS = 6;

        // Read dictionary
        String[] dict = readDictionary("dictionary.txt");

        // Choose secret word
        String secret = chooseSecretWord(dict);
        // You can optionally print the secret word for testing purposes here (e.g.,
        // System.out.println(secret);)

        // Prepare 2D arrays for guesses and results
        // Initialize a 2D array: MAX_ATTEMPTS rows, WORD_LENGTH columns [cite: 1542]
        char[][] guesses = new char[MAX_ATTEMPTS][WORD_LENGTH];
        char[][] results = new char[MAX_ATTEMPTS][WORD_LENGTH];

        // Prepare to read from the standard input (In in = new In() is a common usage
        // for standard input)
        In inp = new In(); // An In object for reading standard input [cite: 786, 810]

        int attempt = 0;
        boolean won = false;

        while (attempt < MAX_ATTEMPTS && !won) {

            String guess = "";
            boolean valid = false;

            // Loop until you read a valid guess
            while (!valid) {
                System.out.print("Enter your guess (5-letter word): ");
                // Read the next token (word) from standard input [cite: 786]
                guess = inp.readString();

                // Check if the guess is valid (must have exactly 5 letters) [cite: 1539]
                if (guess.length() != WORD_LENGTH) {
                    System.out.println("Invalid word. Please try again.");
                } else {
                    valid = true;
                }
            }

            // Store guess and compute feedback
            storeGuess(guess, guesses, attempt);
            computeFeedback(secret, guess, results[attempt]); // results[attempt] is the char[] for the current row
            // The diagram below shows the abstract structure you are filling with each
            // guess.

            // Print board
            printBoard(guesses, results, attempt); // printBoard will print up to the current row (inclusive)

            // Check win
            if (isAllGreen(results[attempt])) {
                System.out.println("Congratulations! You guessed the word in " + (attempt + 1) + " attempts.");
                won = true;
            }

            attempt++;
        }

        if (!won) {
            // Player used all 6 attempts without guessing, print loss message [cite: 1546]
            System.out.println("Sorry, you did not guess the word.");
            System.out.println("The secret word was: " + secret);
        }

        // It's good practice to close the standard input object, although not strictwly
        // necessary for this simple program
        inp.close();
    }
}