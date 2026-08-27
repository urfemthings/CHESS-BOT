import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class Input implements ActionListener {
    Board board;

    public Input(Board board) {
        this.board = board;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        int clickedCol = -1;
        int clickedRow = -1;

        for (int r = 0; r < board.rows; r++) {
            for (int c = 0; c < board.cols; c++) {
                if (board.buttons[r][c] == clickedButton) {
                    clickedCol = c;
                    clickedRow = r;
                    break;
                }
            }
        }

        if (board.selectedPiece == null) {
            Piece pieceXY = board.getPiece(clickedCol, clickedRow);
            if (pieceXY != null) {
                board.selectedPiece = pieceXY;
                board.highlightValidMoves();
            }
        } else {
            Move move = new Move(board, board.selectedPiece, clickedCol, clickedRow);

            if (move.capture == board.selectedPiece) {
                move.capture = null;
            }

            if (board.isValidMove(move)) {
                board.makeMove(move);
            }

            board.selectedPiece = null;
            board.resetBoardColors();
            board.updateBoardUI();
        }
    }
}

