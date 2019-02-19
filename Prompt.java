

import java.util.Scanner;

public class Prompt {
	
	// Get a number from the user in the range [i,j]
	public static int promptNumb(int i, int j) {
		Scanner scan = new Scanner(System.in);
		int input = 0;
		while (true) {
			boolean parsed = true;
			System.out.print("Choice [" + i + "," + j + "]: ");
			String in = scan.next();
			System.out.println();
			try {
				input = Integer.parseInt(in);
			} catch (NumberFormatException nfe) {
				System.out.println("You entered " + in + ". Please enter a number [" + i + "," + j + "].");
				parsed = false;
			}
			if (parsed && (input < i || input > j)) {
				System.out.println("You entered " + in + ". Please enter a number [" + i + "," + j + "].");
			} else if (parsed)
				break;
		}
		return input;
	}
	
	//prompts user for input, creates instance of game and runs it.
	public static void Menu() {
		System.out.println("Choose a game:\n0. Tiny 3x3x3 Connect-Three\n1. Standard 6x7x4 Connect-Four");
		int input = Prompt.promptNumb(0, 1);
		
		int difficulty = 10;
		if(input == 1) {
			System.out.println("Enter a difficulty (depth limit). A depth of 10 is recommended.");
			difficulty = Prompt.promptNumb(1, 13);
		}

		System.out.println("Enter 0 to go first or 1 to go second.");
		int turn = Prompt.promptNumb(0, 1);

		MNK game;

		if (input == 0) {
			game = new MNK(3, 3, 3, turn, 10);
		} else {
			game = new MNK(6, 7, 4, turn, difficulty);
		}

		game.play();
	}

}
