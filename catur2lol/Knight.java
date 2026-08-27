public class Knight extends Piece {

    public Knight(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, isWhite, "knight");
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
    }
}

