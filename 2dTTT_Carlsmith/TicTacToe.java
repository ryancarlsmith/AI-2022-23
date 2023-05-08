/*
Ryan Carlsmith
Mr. Paige
Artifical Intelligence
2D TTT #1
 */

import java.util.Iterator;
import java.io.Console;

public class TicTacToe {

    public enum Player {
        ME('X') {
            @Override
            public Player other() {
                return Player.YOU;
            }
        },

        YOU('O') {
            @Override
            public Player other() {
                return Player.ME;
            }
        };

        private final char mark;

        Player(char mark) {
            this.mark = mark;
        }

        public char mark() {
            return this.mark;
        }

        public abstract Player other();
    }


    public static class Action {
        public int moveR;
        public int moveC;
        public Player movePlayer;

        public Action(int row, int col, Player player) {
            moveR = row;
            moveC = col;
            movePlayer = player;
        }
    }


    public static class State implements Game.State<Action> {

        public static final int N = 3;
        public Player[][] board;
        public Player turn;

        public State(Player player) {
            this.turn = player;
            this.board = new Player[N][N];
        }

        public State(State state, Action move) {
            Player[][] newBoard = copyBoard(state.board);
            board = newBoard;
            if (isValid(move.moveR, move.moveC) && isEmpty(move.moveR, move.moveC)) {
                newBoard[move.moveR][move.moveC] = move.movePlayer;
                board = newBoard;
                turn = move.movePlayer.other();
            } else {
                System.out.println("Attempting to make new state with invalid move, square taken or out of bounds");
            }
        }

        public Player[][] copyBoard(Player[][] board) {
            Player[][] newBoard = new Player[board.length][board[0].length];
            for (int r = 0; r < board.length; r++) {
                for (int c = 0; c < board[0].length; c++) {
                    newBoard[r][c] = board[r][c];
                }
            }
            return newBoard;
        }

        public int emptySquares() {
            int cnt = 0;
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    if (board[r][c] == null) {
                        cnt++;
                    }
                }
            }
            return cnt;
        }

        public boolean isValid(int row, int col) {
            if (row < 0 || row >= N) {
                return false;
            } else return col >= 0 && col < N;
        }

        public char get(int row, int col) {
            if (!isValid(row, col)) {
                return 'z';
            }
            Player p = this.board[row][col];
            if (p == null) {
                return ' ';
            }
            return p.mark();

        }

        public boolean isEmpty(int row, int col) {
            if (!isValid(row, col)) {
                return false;
            }
            return board[row][col] == null;
        }

        public boolean wins(Player player) {
            // Check each row
            for (int r = 0; r < N; r++) {
                int cnt = 0;
                for (int c = 0; c < N; c++) {
                    if (board[r][c] == player) {
                        cnt++;
                    }
                }
                if (cnt == N) {
                    return true;
                }
            }


            //Check each column
            for (int c = 0; c < N; c++) {
                int cnt = 0;
                for (int r = 0; r < N; r++) {
                    if (board[r][c] == player) {
                        cnt++;
                    }
                }
                if (cnt == N) {
                    return true;
                }
            }

            //Check diagonals

            //Down to the right diagonal
            int count = 0;
            for (int i = 0; i < N; i++) {
                if (board[i][i] == player) {
                    count++;
                }
            }
            if (count == N) {
                return true;
            }

            //Up and to the right diagonal
            count = 0;
            for (int i = 0; i < N; i++) {
                if (board[N - i - 1][i] == player) {
                    count++;
                }
            }
            return count == N;
        }

        @Override
        public boolean isTerminal() {
            //either player win or draw
            return wins(Player.ME) || wins(Player.YOU) || emptySquares() == 0;
        }

        @Override
        public int evaluate() {
            // TODO kind of
            // Positive or Negative infinity based on who wins, 0 if draw
            if (isTerminal()) {
                if (wins(Player.ME)) {
                    return Integer.MAX_VALUE;
                } else if (wins(Player.YOU)) {
                    return Integer.MIN_VALUE;
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        }

        @Override
        public State next(Action action) {
            return new State(this, action);
        }

        @Override
        public Iterable<Action> moves() {
            return new Iterable<Action>() {
                @Override
                public Iterator<Action> iterator() {
                    return State.this.new MoveIterator();
                }
            };
        }

        @Override
        public String toString() {
            String result = "";
            String separator = " ";
            for (int row = 0; row < N; row++) {
                if (row > 0) {
                    result += " \n";
                    result += "---+---+---\n";
                    separator = " ";
                }
                for (int col = 0; col < N; col++) {
                    result += separator;
                    result += get(row, col);
                    separator = " | ";
                }
            }
            result += " \n";
            return result;
        }

        public Player player()
            {
                return this.turn;
        }

        private class MoveIterator implements Iterator<Action> {

            private int row;
            private int col;

            public MoveIterator() {
                this.row = 0;
                this.col = 0;
            }

            @Override
            public boolean hasNext() {

                for (int r = row; r < N; r++) {
                    for (int c = col; c < N; c++) {
                        if (isEmpty(r, c)) {
                            this.row = r;
                            this.col = c;
                            return true;
                        }
                    }
                    this.col = 0; //reset to next column
                }
                return false;
            }

            @Override
            public Action next() {
                return new Action(this.row, this.col++, State.this.turn);
            }
        }
    }

    public static void main(String[] args) {
        TicTacToe.State state = new TicTacToe.State(Player.ME);
        Minimax<Action> minimax = new Minimax<>();
        Console console = System.console();

        while (state.emptySquares() > 0) {
            if (state.wins(Player.YOU)) {
                System.out.println("You won !!!");
                break;
            } else if (state.emptySquares() == 0) {
                System.out.println("Tie !!!");
                break;
            }

            if (args.length != 0) {
                if (args[0].equalsIgnoreCase("-first")) {
                    state = state.next(minimax.bestMove(state));
                    System.out.println(state);
                } else if (args[0].equalsIgnoreCase("-second")){
                    System.out.println(state);
                    state = state.next(minimax.bestMove(state));
                    //System.out.println(state);
                }
            } else {
                System.out.println(state);
                state = state.next(minimax.bestMove(state));
            }

            if (state.wins(Player.ME)) {
                System.out.println("I won !!!");
                break;
            } else if (state.emptySquares() == 0) {
                System.out.println("Tie !!!");
                break;
            }

            int row = 0;
            int col = 0;
            String line = "";
            do {
                do {
                    line = console.readLine("Row: ");
                    try {
                        row = Integer.parseInt(line) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid row: " + line);
                    }
                    if (row >= 0 && row < 3) break;
                    System.out.println("Invalid row: " + line);
                } while (true);

                do {
                    line = console.readLine("Col: ");
                    try {
                        col = Integer.parseInt(line) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid column: " + line);
                    }
                    if (col >= 0 && col < 3) break;
                    System.out.println("Invalid col: " + line);
                } while (true);

                if (state.isEmpty(row, col)) break;
                System.out.println("Square is not empty");
            } while (true);

            state = new State(state, new Action(row, col, Player.YOU));
        }
    }
}

