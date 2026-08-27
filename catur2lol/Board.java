import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Board extends JPanel {
    public int tileSize = 85;
    public int cols = 8;
    public int rows = 8;

    public JButton[][] buttons = new JButton[8][8];
    ArrayList<Piece> pieceList = new ArrayList<>();
    public Piece selectedPiece;
    Input input = new Input(this);

    public int enPassantTile = -1;
    public CheckScanner checkScanner = new CheckScanner(this);

    public boolean isWhiteToMove = true;
    public boolean isGameOver = false;

    public String startingPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private Process botProcess;
    private BufferedWriter botWriter;
    private BufferedReader botReader;

    public Board() {
        this.setLayout(new GridLayout(8, 8));
        this.setPreferredSize(new Dimension(cols * tileSize, rows * tileSize));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                buttons[r][c] = new JButton();
                buttons[r][c].setFocusable(false);

                Color bg = (c + r) % 2 == 0 ? new Color(235, 235, 208) : new Color(119, 148, 85);
                buttons[r][c].setBackground(bg);
                buttons[r][c].addActionListener(input);

                this.add(buttons[r][c]);
            }
        }

        loadPositionFromFen(startingPosition);
        updateBoardUI();
        initBotProcess();
    }

    private void initBotProcess() {
        try {
            ProcessBuilder pb = new ProcessBuilder("mono", "/data/data/com.termux/files/home/catur2lol_csharp/Bridge.exe");
            botProcess = pb.start();
            botWriter = new BufferedWriter(new OutputStreamWriter(botProcess.getOutputStream()));
            botReader = new BufferedReader(new InputStreamReader(botProcess.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeBotMove() {
        if (isGameOver || isWhiteToMove || botWriter == null) return;

        new Thread(() -> {
            try {
                botWriter.write("THINK\n");
                botWriter.flush();

                String line = botReader.readLine();
                if (line != null && line.contains(",")) {
                    String[] parts = line.split(",");
                    int startCol = Integer.parseInt(parts[0]);
                    int startRow = Integer.parseInt(parts[1]);
                    int targetCol = Integer.parseInt(parts[2]);
                    int targetRow = Integer.parseInt(parts[3]);

                    Piece p = getPiece(startCol, startRow);
                    if (p != null) {
                        Piece cap = getPiece(targetCol, targetRow);
                        Move botMove = new Move(this, p, targetCol, targetRow);
                        botMove.capture = cap;

                        SwingUtilities.invokeLater(() -> {
                            makeMove(botMove);
                            updateBoardUI();
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void loadPositionFromFen(String fenString) {
        pieceList.clear();

        String[] parts = fenString.split(" ");

        String fenBoard = parts[0];
        int row = 0;
        int col = 0;

        for (int i = 0; i < fenBoard.length(); i++) {
            char c = fenBoard.charAt(i);

            if (c == '/') {
                row++;
                col = 0;
            } else if (Character.isDigit(c)) {
                col += Character.getNumericValue(c);
            } else {
                boolean isWhite = Character.isUpperCase(c);
                char pieceChar = Character.toLowerCase(c);

                switch (pieceChar) {
                    case 'r':
                        pieceList.add(new Rook(this, col, row, isWhite));
                        break;
                    case 'n':
                        pieceList.add(new Knight(this, col, row, isWhite));
                        break;
                    case 'b':
                        pieceList.add(new Bishop(this, col, row, isWhite));
                        break;
                    case 'q':
                        pieceList.add(new Queen(this, col, row, isWhite));
                        break;
                    case 'k':
                        pieceList.add(new King(this, col, row, isWhite));
                        break;
                    case 'p':
                        pieceList.add(new Pawn(this, col, row, isWhite));
                        break;
                }
                col++;
            }
        }

        isWhiteToMove = parts[1].equalsIgnoreCase("w");

        boolean whiteKingSide = parts[2].contains("K");
        boolean whiteQueenSide = parts[2].contains("Q");
        boolean blackKingSide = parts[2].contains("k");
        boolean blackQueenSide = parts[2].contains("q");

        Piece whiteKing = findKing(true);
        if (whiteKing != null) {
            whiteKing.isFirstMove = whiteKingSide || whiteQueenSide;
        }

        Piece blackKing = findKing(false);
        if (blackKing != null) {
            blackKing.isFirstMove = blackKingSide || blackQueenSide;
        }

        Piece whiteRookK = getPiece(7, 7);
        if (whiteRookK != null && whiteRookK.name.equalsIgnoreCase("rook")) {
            whiteRookK.isFirstMove = whiteKingSide;
        }

        Piece whiteRookQ = getPiece(0, 7);
        if (whiteRookQ != null && whiteRookQ.name.equalsIgnoreCase("rook")) {
            whiteRookQ.isFirstMove = whiteQueenSide;
        }

        Piece blackRookK = getPiece(7, 0);
        if (blackRookK != null && blackRookK.name.equalsIgnoreCase("rook")) {
            blackRookK.isFirstMove = blackKingSide;
        }

        Piece blackRookQ = getPiece(0, 0);
        if (blackRookQ != null && blackRookQ.name.equalsIgnoreCase("rook")) {
            blackRookQ.isFirstMove = blackQueenSide;
        }

        if (parts[3].equals("-")) {
            enPassantTile = -1;
        } else {
            enPassantTile = (parts[3].charAt(0) - 'a') + (8 - Character.getNumericValue(parts[3].charAt(1))) * 8;
        }
    }

    public Piece findKing(boolean isWhite) {
        for (Piece piece : pieceList) {
            if (isWhite == piece.isWhite && piece.name.equalsIgnoreCase("king")) {
                return piece;
            }
        }
        return null;
    }

    public int getTileNum(int col, int row) {
        return row * cols + col;
    }

    public Piece getPiece(int col, int row) {
        for (Piece piece : pieceList) {
            if (piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    public void makeMove(Move move) {
        if (isWhiteToMove && botWriter != null) {
            try {
                botWriter.write("MOVE:" + move.piece.col + "," + move.piece.row + "," + move.newCol + "," + move.newRow + "\n");
                botWriter.flush();
            } catch (Exception e) {}
        }

        if (move.piece.name.equalsIgnoreCase("pawn")) {
            movePawn(move);
        } else if (move.piece.name.equalsIgnoreCase("king")) {
            enPassantTile = -1;
            moveKing(move);
        } else {
            enPassantTile = -1;

            move.piece.col = move.newCol;
            move.piece.row = move.newRow;
            move.piece.xPos = move.newCol * tileSize;
            move.piece.yPos = move.newRow * tileSize;

            move.piece.isFirstMove = false;
            capture(move.capture);
        }

        isWhiteToMove = !isWhiteToMove;
        updateGameState();

        if (!isWhiteToMove && !isGameOver) {
            makeBotMove();
        }
    }

    private void updateGameState() {
        Piece king = findKing(isWhiteToMove);

        if (checkScanner.isGameOver(king)) {
            if (checkScanner.isKingChecked(new Move(this, king, king.col, king.row))) {
                System.out.println(isWhiteToMove ? "Black wins" : "White wins");
            } else {
                System.out.println("Stalemate");
            }
            isGameOver = true;
        } else if (insufficientMaterial(true) && insufficientMaterial(false)) {
            System.out.println("Insufficient Material");
            isGameOver = true;
        }
    }

    private boolean insufficientMaterial(boolean isWhite) {
        ArrayList<String> names = pieceList.stream()
                .filter(p -> p.isWhite == isWhite)
                .map(p -> p.name)
                .collect(Collectors.toCollection(ArrayList::new));

        for (String name : names) {
            if (name.equalsIgnoreCase("queen") || name.equalsIgnoreCase("pawn") || name.equalsIgnoreCase("rook")) {
                return false;
            }
        }

        return names.size() < 3;
    }

    private void moveKing(Move move) {
        if (Math.abs(move.piece.col - move.newCol) == 2) {
            Piece rook;

            if (move.piece.col < move.newCol) {
                rook = getPiece(7, move.piece.row);
                if (rook != null) {
                    rook.col = 5;
                    rook.xPos = rook.col * tileSize;
                    rook.isFirstMove = false;
                }
            } else {
                rook = getPiece(0, move.piece.row);
                if (rook != null) {
                    rook.col = 3;
                    rook.xPos = rook.col * tileSize;
                    rook.isFirstMove = false;
                }
            }
        }

        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.xPos = move.newCol * tileSize;
        move.piece.yPos = move.newRow * tileSize;

        move.piece.isFirstMove = false;
        capture(move.capture);
    }

    private void movePawn(Move move) {
        int colorIndex = move.piece.isWhite ? 1 : -1;

        if (getTileNum(move.newCol, move.newRow) == enPassantTile) {
            move.capture = getPiece(move.newCol, move.newRow + colorIndex);
        }

        if (Math.abs(move.piece.row - move.newRow) == 2) {
            enPassantTile = getTileNum(move.newCol, move.newRow + colorIndex);
        } else {
            enPassantTile = -1;
        }

        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.xPos = move.newCol * tileSize;
        move.piece.yPos = move.newRow * tileSize;

        move.piece.isFirstMove = false;
        capture(move.capture);

        int promotionRow = move.piece.isWhite ? 0 : 7;
        if (move.newRow == promotionRow) {
            promotePawn(move);
        }
    }

    private void promotePawn(Move move) {
        pieceList.add(new Queen(this, move.newCol, move.newRow, move.piece.isWhite));
        capture(move.piece);
    }

    public void capture(Piece piece) {
        if (piece != null) {
            pieceList.remove(piece);
        }
    }

    public boolean isValidMove(Move move) {
        if (isGameOver) {
            return false;
        }
        if (move.piece.isWhite != isWhiteToMove) {
            return false;
        }
        if (sameTeam(move.piece, move.capture)) {
            return false;
        }
        if (!move.piece.isValidMovement(move.newCol, move.newRow)) {
            return false;
        }
        if (move.piece.moveCollidesWithPiece(move.newCol, move.newRow)) {
            return false;
        }
        if (checkScanner.isKingChecked(move)) {
            return false;
        }

        return true;
    }

    public boolean sameTeam(Piece p1, Piece p2) {
        if (p1 == null || p2 == null) {
            return false;
        }
        return p1.isWhite == p2.isWhite;
    }

    public void highlightValidMoves() {
        if (selectedPiece != null) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (isValidMove(new Move(this, selectedPiece, c, r))) {
                        buttons[r][c].setBackground(new Color(145, 205, 110));
                    }
                }
            }
        }
    }

    public void resetBoardColors() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Color bg = (c + r) % 2 == 0 ? new Color(235, 235, 208) : new Color(119, 148, 85);
                buttons[r][c].setBackground(bg);
            }
        }
    }

    public void updateBoardUI() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Piece p = getPiece(c, r);
                if (p != null) {
                    buttons[r][c].setIcon(p.icon);
                } else {
                    buttons[r][c].setIcon(null);
                }
            }
        }
    }
}
