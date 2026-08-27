public class Bishop extends Piece {

    public Bishop(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, isWhite, "bishop");
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs(this.col - col) == Math.abs(this.row - row);
    }

    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        // left  up
        if (this.col > col && this.row > row) {
            for (int i = 1; i < Math.abs(this.col - col); i++) {
                if (board.getPiece(this.col - i, this.row - i) != null) {
                    return true;
                }
            }
        }
        // rigth up
        if (this.col < col && this.row > row) {
            for (int i = 1; i < Math.abs(this.col - col); i++) {
                if (board.getPiece(this.col + i, this.row - i) != null) {
                    return true;
                }
            }
        }
        // down left
        if (this.col > col && this.row < row) {
            for (int i = 1; i < Math.abs(this.col - col); i++) {
                if (board.getPiece(this.col - i, this.row + i) != null) {
                    return true;
                }
            }
        }
        // rigth down
        if (this.col < col && this.row < row) {
            for (int i = 1; i < Math.abs(this.col - col); i++) {
                if (board.getPiece(this.col + i, this.row + i) != null) {
                    return true;
                }
            }
        }

        return false;
    }
}

