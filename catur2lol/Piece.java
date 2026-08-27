import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Piece {
    public int col, row;
    public int xPos, yPos;
    public boolean isWhite;
    public String name;
    public int value;
    public boolean isFirstMove = true;
    protected Board board;
    public ImageIcon icon;

    public Piece(Board board, int col, int row, boolean isWhite, String name) {
        this.board = board;
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        this.isWhite = isWhite;
        this.name = name;
        this.icon = loadIcon();
    }

    private ImageIcon loadIcon() {
        try {
            BufferedImage sheet = ImageIO.read(new File("pieces.png"));
            int sheetWidth = sheet.getWidth();
            int sheetHeight = sheet.getHeight();
            int pieceWidth = sheetWidth / 6;
            int pieceHeight = sheetHeight / 2;

            int index = getIndex();
            int x = index * pieceWidth;
            int y = isWhite ? 0 : pieceHeight;

            BufferedImage subImage = sheet.getSubimage(x, y, pieceWidth, pieceHeight);
            Image scaledImage = subImage.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            return null;
        }
    }

    private int getIndex() {
        if (name.equalsIgnoreCase("king")) return 0;
        if (name.equalsIgnoreCase("queen")) return 1;
        if (name.equalsIgnoreCase("bishop")) return 2;
        if (name.equalsIgnoreCase("knight")) return 3;
        if (name.equalsIgnoreCase("rook")) return 4;
        if (name.equalsIgnoreCase("pawn")) return 5;
        return 0;
    }

    public boolean isValidMovement(int col, int row) {
        return true;
    }

    public boolean moveCollidesWithPiece(int col, int row) {
        return false;
    }
}

