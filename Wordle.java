public class Wordle {

    // Reads all words from dictionary filename into a String array.
    public static String[] readDictionary(String filename) {
		In fileIn = new In(filename);
        String[] dict = fileIn.readAllStrings();
        fileIn.close(); 
        return dict;
    }

    // Choose a random secret word from the dictionary. 
    // Hint: Pick a random index between 0 and dict.length (not including) using Math.random()
    public static String chooseSecretWord(String[] dict) {
		if (dict == null || dict.length == 0) {
            throw new IllegalArgumentException("error");
        }
        int randomIndex = (int) (Math.random() * dict.length);
        return dict[randomIndex];
    }

    // Simple helper: check if letter c appears anywhere in secret (true), otherwise
    // return false.
    public static boolean containsChar(String secret, char c) {
		return secret.indexOf(c) != -1;
    }

    // Compute feedback for a single guess into resultRow.
    // G for exact match, Y if letter appears anywhere else, _ otherwise.
    public static void computeFeedback(String secret, String guess, char[] resultRow) {
        int length = secret.length();
        
        for (int i = 0; i < length; i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                resultRow[i] = 'G'; 
            } else {
                resultRow[i] = '-'; // Temporary placeholder for non-matches
            }
        }
        
        // PASS 2: Identify Yellow ('Y') and Misses ('_').
        for (int i = 0; i < length; i++) {
            // Skip letters already confirmed as Green
            if (resultRow[i] == 'G') {
                continue; 
            }
		// you may want to use containsChar in your implementation
        char currentGuessChar = guess.charAt(i);
            
            // Use the containsChar helper
            if (containsChar(secret, currentGuessChar)) {
                resultRow[i] = 'Y'; // Yellow: Correct letter, wrong position
            } else {
                resultRow[i] = '_'; // Miss: Letter not in the secret word
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
		for (char entry : resultRow ) {
            if (entry != 'G') {
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

        // Prepare 2D arrays for guesses and results
        char[][] guesses = new char[MAX_ATTEMPTS][WORD_LENGTH];
        char[][] results = new char[MAX_ATTEMPTS][WORD_LENGTH];

        // Prepare to read from the standart input 
        In inp = new In();

        int attempt = 0;
        boolean won = false;

        while (attempt < MAX_ATTEMPTS && !won) {

            String guess = "";
            boolean valid = false;

            // Loop until you read a valid guess
            while (!valid) {
                System.out.print("Enter your guess (5-letter word): ");
                guess = inp.readLine().toUpperCase();

                boolean wordFoundInDictionary = false;
                for (String word : dict) {
                    if (word.equals(guess)) {
                        wordFoundInDictionary = true;
                        break; // Stop searching once found
                }
           
                if (guess.length() != WORD_LENGTH || !wordFoundInDictionary) {
                    System.out.println("Invalid word. Please try again.");
                } else {
                    valid = true;
                }
            }

            // Store guess and compute feedback
            // ... use storeGuess and computeFeedback

            // Print board
            printBoard(guesses, results, attempt);

            // Check win
            if (isAllGreen(results[attempt])) {
                System.out.println("Congratulations! You guessed the word in " + (attempt + 1) + " attempts.");
                won = true;
            }

            attempt++;
        }

        if (!won) {
            printBoard(guesses, results, attempt);

            System.out.println("Sorry, you did not guess the word.");
            System.out.println("The secret word was: " + secret);
        }

        inp.close();
    }
}
}
