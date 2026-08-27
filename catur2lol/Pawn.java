import javax.swing.*;

public class Pawn extends Piece {

    public Pawn(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, isWhite, "Pawn");
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        int colorIndex = isWhite ? -1 : 1;

        if (this.col == col && row == this.row + colorIndex && board.getPiece(col, row) == null) {
            return true;
        }

        boolean isFirstRow = this.row == (isWhite ? 6 : 1);
        if (isFirstRow && this.col == col && row == this.row + colorIndex * 2 &&
            board.getPiece(col, row) == null && board.getPiece(col, this.row + colorIndex) == null) {
            return true;
        }

        if (Math.abs(this.col - col) == 1 && row == this.row + colorIndex) {
            Piece pieceToCapture = board.getPiece(col, row);
            if (pieceToCapture != null && pieceToCapture.isWhite != this.isWhite) {
                return true;
            }
        }

        if (Math.abs(this.col - col) == 1 && row == this.row + colorIndex) {
            if (board.getTileNum(col, row) == board.enPassantTile) {
                return true;
            }
        }

        return false;
    }
}

