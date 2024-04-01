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

        ThreadedTicTacToe game1 = new ThreadedTicTacToe(boardView,gameOutput,playerInput);
        game1.start();
    }
}
