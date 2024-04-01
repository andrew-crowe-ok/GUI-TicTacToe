import javax.swing.*;
import java.awt.event.*;

public class ThreadedTicTacToe extends Thread implements ActionListener {
    private final JTextPane boardOut;
    private final JTextArea gameOut;
    private final JTextField playerIn;
    private boolean fullBoard = false;
    private CellState player = CellState.O;

    @Override
    public void actionPerformed(ActionEvent e) {
        int row = -1;
        int col = -1;
        String s;

        try {
            s = e.getActionCommand();
            row = Integer.parseInt(s);
        } catch (NumberFormatException formatException) {
            makeMove(player, -1, -1);
        }

        gameOut.append("\nEnter column 1, 2, or 3.");

        try {
            s = e.getActionCommand();
            col = Integer.parseInt(s);
        } catch (NumberFormatException formatException) {
            makeMove(player, -1, -1);
        }

        playerIn.setText("");
        makeMove(player, row, col);
    }

    private enum CellState {X, O, EMPTY}
    private enum GameState {WIN, DRAW, CONTINUE}
    private final CellState[][] board = new CellState[3][3];

    public ThreadedTicTacToe(JTextPane boardView, JTextArea gameOutput, JTextField playerInput) {
        emptyBoard();
        this.boardOut = boardView;
        this.gameOut = gameOutput;
        this.playerIn = playerInput;
        playerIn.addActionListener(this);
    }


    // Handles game flow.
    @Override
    public void run() {
        printBoard();

        while (gameStatus(player).equals(GameState.CONTINUE)) {
            player = changePlayer(player);
            gameOut.setText("\nPlayer " + getCellText(player) + "'s turn.");

            gameOut.append("\nEnter row 1, 2, or 3.");

            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            /*
            try {
                gameOut.append("\nEnter row 1, 2, or 3.");
                String r = getTextFieldInput();
                gameOut.append("\nEnter column 1, 2, or 3.");
                String c = playerIn.getText();
                int row = Integer.parseInt(r);
                int col = Integer.parseInt(c);
                makeMove(player, row, col);
            } catch (NumberFormatException e) {
                makeMove(player, -1, -1);
            }

             */
            printBoard();
        }

        if (gameStatus(player).equals(GameState.DRAW)) {
            gameOut.setText("Draw. ");
        }

        if (
                gameStatus(player).equals(GameState.WIN)
                        || gameStatus(changePlayer(player)).equals(GameState.WIN)
        )
        {
            gameOut.setText("\nPlayer " + getCellText(player) + " wins!");
        }
        gameOut.append("\nGame over.");
    }



    // Tests validMove() and gameStatus().
    public void runTests() {
        int score;
        int scoreTotal = 0;
        CellState player = CellState.X;

        System.out.print("\nvalidMove tests:");

        if (!validMove(0,1)) {score = 1; scoreTotal++;} else {score = 0;}
        System.out.printf("\n  Correctly handles low-end out of bounds: %d/1", score);
        if (!validMove(1,4)) {score = 1; scoreTotal++;} else {score = 0;}
        System.out.printf("\n  Correctly handles high-end out of bounds: %d/1", score);
        if (!validMove(3,3)) {score = 1; scoreTotal++;} else {score = 0;}
        System.out.printf("\n  Correctly handles input to non-empty cell: %d/1", score);
        if (validMove(2,2)) {score = 1; scoreTotal++;} else {score = 0;}
        System.out.printf("\n  Correctly handles valid input: %d/1", score);

        System.out.print("\n\ngameStatus tests:");

        board[0][0] = board[0][1] = board[0][2] = player;

        if (gameStatus(player).equals(GameState.WIN)) {score = 1; scoreTotal++;} else {score = 0;}
        System.out.printf("\n  Correctly detects if a player wins: %d/1", score);

        board[0][0] = CellState.O; board[0][1] = CellState.O; board[0][2] = CellState.X;
        board[1][0] = CellState.X; board[1][1] = CellState.X; board[1][2] = CellState.O;
        board[2][0] = CellState.O; board[2][1] = CellState.O; board[2][2] = CellState.X;

        if (gameStatus(player).equals(GameState.DRAW)) {score = 1; scoreTotal++;} else {score = 0;}
        System.out.printf("\n  Correctly detects if the game is a draw: %d/1", score);

        board[0][0] = CellState.O; board[0][1] = CellState.O;     board[0][2] = CellState.X;
        board[1][0] = CellState.X; board[1][1] = CellState.EMPTY; board[1][2] = CellState.O;
        board[2][0] = CellState.O; board[2][1] = CellState.O;     board[2][2] = CellState.X;

        if (gameStatus(player).equals(GameState.CONTINUE)) {
            score = 1; scoreTotal++;
        } else {
            score = 0;
        }
        System.out.printf("\n  Correctly detects if the game is not over: %d/1", score);

        System.out.printf("\n\nTests passed: %d/7\n", scoreTotal);
    }

    // Displays the TicTacToe board in its current state.
    public void printBoard() {
        String[] cells = {
                getCellText(board[0][0]),getCellText(board[0][1]),getCellText(board[0][2]),
                getCellText(board[1][0]),getCellText(board[1][1]),getCellText(board[1][2]),
                getCellText(board[2][0]),getCellText(board[2][1]),getCellText(board[2][2])
        };

        // https://docs.oracle.com/en/java/javase/15/text-blocks/index.html
        String board = ("\n" +
                "              |              |\n" +
                "           %s  |       %s      |   %s\n" +
                "_______|_______|_______\n" +
                "              |              |\n" +
                "           %s  |       %s     |   %s\n" +
                "_______|_______|_______\n" +
                "              |              |\n" +
                "           %s  |       %s      |   %s\n" +
                "              |              |\n").formatted(
                cells[0],cells[1],cells[2],
                cells[3],cells[4],cells[5],
                cells[6],cells[7],cells[8]
        );
        boardOut.setText(board);
    }

    // Determines if a move is acceptable.
    private boolean validMove(int row, int col) {
        if (row <= 0 || row >= 4) {return false;}
        else if (col <= 0 || col >= 4) {return false;}
        else return board[row - 1][col - 1].equals(CellState.EMPTY);
    }

    // Determines the current status of the game.
    private GameState gameStatus(CellState player) {
        if (
                rowCheck(player).equals(GameState.WIN)
                        || colCheck(player).equals(GameState.WIN)
                        || diagCheck(player).equals(GameState.WIN)
        )
        {
            return GameState.WIN;
        }
        if (fullBoard) {return GameState.DRAW;}
        return GameState.CONTINUE;
    }

    // Checks rows for a winner and determines if the board is full.
    private GameState rowCheck(CellState player) {
        CellState[] row1 = new CellState[]
                {board[0][0], board[0][1], board[0][2]};
        CellState[] row2 = new CellState[]
                {board[1][0], board[1][1], board[1][2]};
        CellState[] row3 = new CellState[]
                {board[2][0], board[2][1], board[2][2]};

        if (
                threeInARow(row1, player)
                        || threeInARow(row2, player)
                        || threeInARow(row3, player)
        )
        {
            return GameState.WIN;
        }

        fullBoard = !hasEmpty(row1)
                && !hasEmpty(row2)
                && !hasEmpty(row3);

        return GameState.CONTINUE;
    }

    // Checks columns for a winner.
    private GameState colCheck(CellState player) {
        CellState[] col1 = new CellState[]
                {board[0][0], board[1][0], board[2][0]};
        CellState[] col2 = new CellState[]
                {board[0][1], board[1][1], board[2][1]};
        CellState[] col3 = new CellState[]
                {board[0][2], board[1][2], board[2][2]};

        if (
                threeInARow(col1, player)
                        || threeInARow(col2, player)
                        || threeInARow(col3, player)
        )
        {
            return GameState.WIN;
        }

        return GameState.CONTINUE;
    }

    // Checks diagonals for a winner.
    private GameState diagCheck(CellState player) {
        CellState[] diag1 = new CellState[]
                {board[0][0], board[1][1], board[2][2]};
        CellState[] diag2 = new CellState[]
                {board[0][2], board[1][1], board[2][0]};

        if (
                threeInARow(diag1, player)
                        || threeInARow(diag2, player)
        )
        {
            return GameState.WIN;
        }

        return GameState.CONTINUE;
    }

    // Determines if a player got three in a row.
    private boolean threeInARow(CellState[] rowColDiag, CellState player) {
        return rowColDiag[0] == player
                && rowColDiag[1] == player
                && rowColDiag[2] == player;
    }

    // Determines whether there are empty spaces on the board.
    private boolean hasEmpty(CellState[] rowColDiag) {
        return rowColDiag[0] == CellState.EMPTY
                || rowColDiag[1] == CellState.EMPTY
                || rowColDiag[2] == CellState.EMPTY;
    }

    // Returns strings that correspond to CellState.
    private String getCellText(CellState cell) {
        if (cell == CellState.X) {
            return "X";
        }
        if (cell == CellState.O) {
            return "O";
        }
        return " ";
    }

    // Sets the board to an empty state.
    private void emptyBoard() {
        this.board[0][0] = CellState.EMPTY; this.board[0][1] = CellState.EMPTY; this.board[0][2] = CellState.EMPTY;
        this.board[1][0] = CellState.EMPTY; this.board[1][1] = CellState.X; this.board[1][2] = CellState.EMPTY;
        this.board[2][0] = CellState.EMPTY; this.board[2][1] = CellState.EMPTY; this.board[2][2] = CellState.EMPTY;
    }

    // Switches between player X and O.
    private CellState changePlayer(CellState player) {
        if (player.equals(CellState.X)) {
            return CellState.O;
        }
        return CellState.X;
    }

    // Loops until a valid move is played and applies valid move to board.
    private void makeMove(CellState player, int row, int col) {

        while (!validMove(row, col)) {
            try {
                gameOut.setText("\nInvalid move.");
                gameOut.append("\nEnter row 1, 2, or 3.");
                String r = playerIn.getText();
                gameOut.append("\nEnter column 1, 2, or 3.");
                String c = playerIn.getText();
                row = Integer.parseInt(r);
                col = Integer.parseInt(c);
            } catch (NumberFormatException e) {
                row = col = -1;
            }
        }
        this.board[row-1][col-1] = player;
    }

    public String getTextFieldInput() {
        return playerIn.getText();
    }

    public String processInput() {
        String text = getTextFieldInput();
        return text;
    }

    /*
    private static class EnterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

     */
}