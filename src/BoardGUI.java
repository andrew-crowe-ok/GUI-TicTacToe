import javax.swing.*;

public class BoardGUI extends JFrame {
    private JTextPane boardView;
    private JTextArea gameOutput;
    private JTextField playerInput;
    private JPanel mainPanel;

    public BoardGUI() {
        setContentPane(mainPanel);
        setTitle("TicTacToe");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();

        TicTacToe game1 = new TicTacToe(boardView,gameOutput,playerInput);
        game1.startGame();
    }
}
