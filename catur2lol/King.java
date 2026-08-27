public class King extends Piece {

    public King(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, isWhite, "king");
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs(col - this.col) * Math.abs(row - this.row) == 1 ||
               Math.abs(col - this.col) + Math.abs(row - this.row) == 1 ||
               canCastle(col, row);
    }

    private boolean canCastle(int col, int row) {
        if (this.row == row) {
            if (col == 6) {
                Piece rook = board.getPiece(7, row);
                if (rook != null && rook.isFirstMove && this.isFirstMove) {
                    return board.getPiece(5, row) == null &&
                           board.getPiece(6, row) == null &&
                           !board.checkScanner.isKingChecked(new Move(board, this, 5, row));
                }
            } else if (col == 2) {
                Piece rook = board.getPiece(0, row);
                if (rook != null && rook.isFirstMove && this.isFirstMove) {
                    return board.getPiece(3, row) == null &&
                           board.getPiece(2, row) == null &&
                           board.getPiece(1, row) == null &&
                           !board.checkScanner.isKingChecked(new Move(board, this, 3, row));
                }
            }
        }
        return false;
    }
}

