import javax.swing.*;

public class MainBoard2 extends JFrame {
    private Board board;

    public MainBoard2() {
        setTitle("Chess Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        board = new Board();
        add(board);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainBoard2());
    }
}

