package chess;

import java.util.Arrays;

public class BoardCompressed {

    private String[][] compressed = new String[8][8];

    BoardCompressed(Board board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile tile = board.getTile(i, j);
                if (tile.isEmpty()) {
                    compressed[i][j] = "empty";
                } else {
                    Piece piece = tile.getPiece();
                    final String type;
                    if (piece instanceof Piece.King) {
                        type = "king";
                    } else if (piece instanceof Piece.Queen) {
                        type = "queen";
                    } else if (piece instanceof Piece.Bishop) {
                        type = "bishop";
                    } else if (piece instanceof Piece.Knight) {
                        type = "knight";
                    } else if (piece instanceof Piece.Rook) {
                        type = "rook";
                    } else {
                        type = "pawn";
                    }
                    compressed[i][j] = type;
                    if (piece.white) {
                        compressed[i][j] += "white";
                    }
                    if (piece.notMoved) {
                        compressed[i][j] += "notMoved";
                    }
                }
            }
        }
    }

    public Board toBoard() {
        Board board = new Board();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String info = compressed[i][j];
                if (!info.contains("empty")) {
                    final boolean white;
                    if (info.contains("white")) {
                        white = true;
                    } else {
                        white = false;
                    }
                    Tile tile = board.getTile(i, j);
                    if (info.contains("king")) {
                        tile.setPiece(new Piece.King(white));
                    } else if (info.contains("queen")) {
                        tile.setPiece(new Piece.Queen(white));
                    } else if (info.contains("bishop")) {
                        tile.setPiece(new Piece.Bishop(white));
                    } else if (info.contains("knight")) {
                        tile.setPiece(new Piece.Knight(white));
                    } else if (info.contains("rook")) {
                        tile.setPiece(new Piece.Rook(white));
                    } else if (info.contains("pawn")) {
                        tile.setPiece(new Piece.Pawn(white));
                    }
                    if (!info.contains("notMoved")) {
                        tile.getPiece().notMoved = false;
                    }
                }
            }
        }
        return board;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Arrays.deepHashCode(this.compressed);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BoardCompressed other = (BoardCompressed) obj;
        if (!Arrays.deepEquals(this.compressed, other.compressed)) {
            return false;
        }
        return true;
    }
}
