public class Rook extends Piece {

    public Rook(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, isWhite, "rook");
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return this.col == col || this.row == row;
    }

    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        if (this.col > col) {
            for (int c = this.col - 1; c > col; c--) {
                if (board.getPiece(c, this.row) != null) {
                    return true;
                }
            }
        }
        if (this.col < col) {
            for (int c = this.col + 1; c < col; c++) {
                if (board.getPiece(c, this.row) != null) {
                    return true;
                }
            }
        }
        if (this.row > row) {
            for (int r = this.row - 1; r > row; r--) {
                if (board.getPiece(this.col, r) != null) {
                    return true;
                }
            }
        }
        if (this.row < row) {
            for (int r = this.row + 1; r < row; r++) {
                if (board.getPiece(this.col, r) != null) {
                    return true;
                }
            }
        }

        return false;
    }
}

