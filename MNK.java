

import java.util.*;

public class MNK {

	//Row, column, and number needed in a row to win
	private int m;
	private int n;
	private int k;

	private int turn;
	private int difficulty;

	public MNK(int m, int n, int k, int turn, int difficulty) {
		this.m = m;
		this.n = n;
		this.k = k;
		this.turn = turn;
		this.difficulty = difficulty;
	}

	private class State {
		private int[][] board;
		private int turn;

		private State() {
			this.board = new int[m][n];
			this.turn = 1;
		}

		private State(State s) {
			this.board = new int[m][n];
			for (int i = 0; i < m; i++)
				for (int j = 0; j < n; j++)
					this.board[i][j] = s.board[i][j];
			this.turn = s.turn;
		}

		//Check if a move is valid
		private boolean isValid(int x) {
			if (x < 0 || x >= n)
				return false;
			return board[m - 1][x] == 0;
		}

		// returns 2 if not terminal, 1 if player 1 wins, -1 if player -1 wins, or 0 if
		// there is a tie.
		private int utility() {
			boolean full = true;
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					int cur = board[i][j];
					if (cur != 0) {
						boolean vert = true, side = true, diag1 = true, diag2 = true;
						for (int l = 1; l < k; l++) {
							if (i + k > m || board[i + l][j] != cur)
								vert = false;
							if (j + k > n || board[i][j + l] != cur)
								side = false;
							if (i + k > m || j + k > n || board[i + l][j + l] != cur)
								diag1 = false;
							if (i - k < -1 || j + k > n || board[i - l][j + l] != cur)
								diag2 = false;
						}
						if (vert || side || diag1 || diag2)
							return cur;
					} else
						full = false;
				}
			}
			if (full)
				return 0;

			return 2;
		}

		//Calculate heuristic for a given state
		private double heuristic() {
			int util = this.utility();
			if (util != 2)
				return util;
			int X = 0;
			int O = 0;
			int total = 1;

			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					int cur = board[i][j];
					if (cur != 0) {
						if (i + k < m) {
							for (int l = 1; l < k; l++) {
								if (i + k < m && board[i + l][j] == cur)
									total = cur == 1 ? X++ : O++;
								else if (i + k < m && board[i + l][j] != 0) {
									if (cur == 1)
										X = X - l + 1;
									else
										O -= (l - 1);
									break;
								}
							}
						}
						if (j + k < n) {
							for (int l = 1; l < k; l++) {
								if (j + k < n && board[i][j + l] == cur)
									total = cur == 1 ? X++ : O++;
								else if (j + k <= n && board[i][j + l] != 0) {
									if (cur == 1)
										X = X - l + 1;
									else
										O -= (l - 1);
									break;
								}

							}
						}

						if (i + k < m && j + k <= n) {
							for (int l = 1; l < k; l++) {
								if (i + k < m && j + k <= n && board[i + l][j + l] == cur)
									total = cur == 1 ? X++ : O++;
								else if (i + k < m && j + k < n && board[i + l][j + l] != 0) {
									if (cur == 1)
										X = X - l + 1;
									else
										O -= (l - 1);
									break;
								}

							}
						}
						if (i - k >= 0 && j + k <= n) {
							for (int l = 1; l < k; l++) {
								if (i - k >= 0 && j + k <= n && board[i - l][j + l] == cur)
									total = cur == 1 ? X++ : O++;
								else if (i - k >= 0 && j + k < n && board[i - l][j + l] != 0) {
									if (cur == 1)
										X = X - l + 1;
									else
										O -= (l - 1);
									break;
								}

							}
						}

					}
				}
			}
			return 2 / (1 + Math.exp(O - X)) - 1;
		}

		//Return a new state that is the same, except a given move is made.
		private State makeMove(int x) {
			State ret = new State(this);

			for (int i = 0; i < m; i++) {
				if (ret.board[i][x] == 0) {
					ret.board[i][x] = ret.turn;
					break;
				}
			}

			ret.turn *= -1;
			return ret;
		}

		//Print state (Pritning board)
		private void print() {
			for (int i = m - 1; i >= -1; i--) {
				for (int j = 0; j < n; j++) {
					if (i == -1) {
						if (j == 0)
							System.out.print("  " + j + " ");
						else
							System.out.print(j + " ");
					} else {
						if (j == 0)
							System.out.print("| ");
						if (board[i][j] == 1)
							System.out.print("X ");
						else if (board[i][j] == -1)
							System.out.print("O ");
						else
							System.out.print("  ");
						if (j == n - 1)
							System.out.print("|");
					}
				}
				System.out.println();
			}
		}

	}

	//Basic alphabeta pruning minimax
	public double abh_minimax(State s, int depth, double alpha, double beta) {
		if (depth == 0 || s.utility() != 2)
			return s.heuristic();

		if (s.turn == 1) {
			double ret = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < n; i++) {
				if (s.isValid(i)) {
					ret = Math.max(ret, abh_minimax(s.makeMove(i), depth - 1, alpha, beta));
					alpha = Math.max(alpha, ret);
					if (alpha >= beta)
						break;
				}
			}
			return ret;
		} else {
			double ret = Double.POSITIVE_INFINITY;
			for (int i = 0; i < n; i++) {
				if (s.isValid(i)) {
					ret = Math.min(ret, abh_minimax(s.makeMove(i), depth - 1, alpha, beta));
					beta = Math.min(beta, ret);
					if (alpha >= beta)
						break;
				}
			}
			return ret;
		}
	}
	

	//Handles the turn taking
	public void play() {
		State state = new State();
		Scanner reader = new Scanner(System.in);

		if (turn == 0) {
			state.print();
			System.out.print("Your move: ");
			int x = reader.nextInt();
			while (!state.isValid(x)) {
				System.out.println("That is not a move try again.");
				x = reader.nextInt();
			}
			state = state.makeMove(x);
			state.print();
		}

		while (true) {
			System.out.println("\n Thinking...\n");
			
			//Look at the minimax value of each node below the current state. Finds the minimum/maximum depending on if AI is min or max.
			int move = 0;
			double util;
			if(turn == 0) util = 1;
			else util = -1;
			for (int i = 0; i < n; i++)
				if (state.isValid(i)) {
					double value = abh_minimax(state.makeMove(i), difficulty, Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
					if ((turn == 0 && util > value) || (turn == 1 && util < value)) {
						
						util = value;
						move = i;
					}
				}
			state = state.makeMove(move);
			state.print();
			
			if (state.utility() != 2) {
				if(state.utility() == 0) System.out.print("Tie! ");
				else System.out.print("AI wins! ");
				System.out.println("Game over.\n\n");
				return;
			}
			
			System.out.print("\nYour move: ");
			int x = reader.nextInt();

			System.out.println();
			while (!state.isValid(x)) {
				System.out.println("That is not a move try again.");
				x = reader.nextInt();
			}
			
			state = state.makeMove(x);
			state.print();

			if (state.utility() != 2) {
				if(state.utility() == 0) System.out.print("Tie! ");
				else System.out.print("You win! ");
				System.out.println("Game Over.\n\n");
				break;
			}
			

		}
	}

}
