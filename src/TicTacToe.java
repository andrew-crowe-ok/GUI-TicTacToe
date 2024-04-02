import javax.swing.*;
import java.awt.event.*;

public class TicTacToe implements ActionListener {
    private final JTextPane boardOut;
    private final JTextArea gameOut;
    private final JTextField playerIn;

    private enum GameState {WIN, DRAW, CONTINUE}

    private enum CellState {X, O, EMPTY}
    private CellState player = CellState.X;
    private final CellState[][] board = new CellState[3][3];

    private int row = -1;
    private int col = -1;
    private boolean fullBoard = false;

    public TicTacToe(JTextPane boardView, JTextArea gameOutput, JTextField playerInput) {
        resetBoard();
        this.boardOut = boardView;
        this.gameOut = gameOutput;
        this.playerIn = playerInput;
        playerIn.addActionListener(this);
    }

    // Controls game flow.
    @Override
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        boolean gameOver = getGameStatus().equals(GameState.WIN) || getGameStatus().equals(GameState.DRAW);

        if (gameOver && s.equals("go")) {
            resetBoard();
            startGame();
        }
        else if (gameOver && s.equals("end")) {
            System.exit(0);
        }
        else if (row == -1) {
            gameOut.append(s);
            try {
                row = Integer.parseInt(s);
            }
            catch (NumberFormatException formatException) {
                invalidMove();
                return;
            }
            gameOut.append("\nEnter column 1, 2, or 3: ");
        }
        else if (col == -1) {
            gameOut.append(s);
            try {
                col = Integer.parseInt(s);
            }
            catch (NumberFormatException formatException) {
                invalidMove();
                return;
            }
            if (validMove()) {
                makeMove();
            }
            else {
                invalidMove();
            }
        }
        playerIn.setText("");
    }

    // Outputs text for the beginning of a match.
    public void startGame() {
        printBoard();
        gameOut.setText("\nPlayer " + getCellText(player) + "'s turn.");
        gameOut.append("\nEnter row 1, 2, or 3: ");
    }

    // Displays the board in its current state.
    public void printBoard() {
        String[] cells = {
                getCellText(board[0][0]),getCellText(board[0][1]),getCellText(board[0][2]),
                getCellText(board[1][0]),getCellText(board[1][1]),getCellText(board[1][2]),
                getCellText(board[2][0]),getCellText(board[2][1]),getCellText(board[2][2])
        };

        // https://docs.oracle.com/en/java/javase/15/text-blocks/index.html
        String board = ("""

                              |              |
                           %s  |       %s      |   %s
                _______|_______|_______
                              |              |
                           %s  |       %s     |   %s
                _______|_______|_______
                              |              |
                           %s  |       %s      |   %s
                              |              |
                """).formatted(
                cells[0],cells[1],cells[2],
                cells[3],cells[4],cells[5],
                cells[6],cells[7],cells[8]
        );
        boardOut.setText(board);
    }

    // Determines if a move is acceptable.
    private boolean validMove() {
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

    // Returns the current status of the game.
    private GameState getGameStatus() {
        return gameStatus(player);
    }

    // Outputs text for invalid move.
    private void invalidMove() {
        gameOut.append("\nInvalid move. Try again.");
        gameOut.append("\n\nEnter row 1, 2, or 3: ");
        row = col = -1;
    }
    
    // Outputs text for the end of a match.
    private void postGame() {
        gameOut.append("\nGame over.");
        gameOut.append("\nTo play again, type \"go\" and press the Enter key.");
        gameOut.append("\nTo exit, type \"end\" and press the Enter key.");
    }

    // Resets board.
    private void resetBoard() {
        row = col = -1;
        
        this.board[0][0] = CellState.EMPTY; this.board[0][1] = CellState.EMPTY; this.board[0][2] = CellState.EMPTY;
        this.board[1][0] = CellState.EMPTY; this.board[1][1] = CellState.EMPTY; this.board[1][2] = CellState.EMPTY;
        this.board[2][0] = CellState.EMPTY; this.board[2][1] = CellState.EMPTY; this.board[2][2] = CellState.EMPTY;
    }

    // Switches between player X and O.
    private CellState changePlayer(CellState player) {
        if (player.equals(CellState.X)) {
            return CellState.O;
        }
        return CellState.X;
    }

    // Applies valid move to board, continues game flow.
    private void makeMove() {
        this.board[row-1][col-1] = player;
        printBoard();
        row = col = -1;

        if (getGameStatus().equals(GameState.CONTINUE)) {
            player = changePlayer(player);
            gameOut.setText("\nPlayer " + getCellText(player) + "'s turn.");
            gameOut.append("\nEnter column 1, 2, or 3: ");
        }
        else if (
                getGameStatus().equals(GameState.WIN)
                || gameStatus(changePlayer(player)).equals(GameState.WIN)
        )
        {
            gameOut.setText("\nPlayer " + getCellText(player) + " wins!");
            postGame();
        }
        else {
            gameOut.setText("Draw. ");
            postGame();
        }
    }
}