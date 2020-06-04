package chess;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.Group;

public class Board {

    private Tile[][] board = new Tile[8][8];
    private double size;

    public Board(double size) {
        this.size = size;
        double tileWidth = size / 8;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Tile(j * tileWidth, i * tileWidth, tileWidth);
            }
        }
    }

    public void setupPieces() {//Only pieces that depends on the notMoved boolean are set to make tracking repetitions consistent.
        //Pawns
        for (int i = 0; i < 8; i++) {//Rows of 8 for each side
            board[1][i].setPiece(new Piece.Pawn(false));//Black
            board[6][i].setPiece(new Piece.Pawn(true));//White
        }
        //Rooks
        for (int i = 0; i < 2; i++) {//2 corners for each side
            board[0][i * 7].setPiece(new Piece.Rook(false));
            board[7][i * 7].setPiece(new Piece.Rook(true));
        }
        //Knights
        for (int i = 0; i < 2; i++) {//1 away from each corner
            board[0][5 * i + 1].placePiece(new Piece.Knight(false));
            board[7][5 * i + 1].placePiece(new Piece.Knight(true));
        }
        //Bishops
        for (int i = 0; i < 2; i++) {//2 away from each corner
            board[0][3 * i + 2].placePiece(new Piece.Bishop(false));
            board[7][3 * i + 2].placePiece(new Piece.Bishop(true));
        }
        //Kings
        board[0][4].setPiece(new Piece.King(false));
        board[7][4].setPiece(new Piece.King(true));
        //Queens
        board[0][3].placePiece(new Piece.Queen(false));
        board[7][3].placePiece(new Piece.Queen(true));
    }

    public void print() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.println(board[i][j].getPiece());
            }
        }
    }

    public ArrayList<int[]> getHighlighted() {
        ArrayList<int[]> coordinates = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].isHighlightColored()) {
                    coordinates.add(new int[]{i, j});
                }
            }
        }
        return coordinates;
    }

    public Piece getToBePromotedPawn(boolean white) {
        if (white) {
            for (int i = 0; i < 8; i++) {
                Tile tile = board[0][i];//The 8th rank
                if (!tile.isEmpty() && tile.getPiece() instanceof Piece.Pawn) {
                    return (Piece.Pawn) tile.getPiece();
                }
            }
            return null;
        } else {
            for (int i = 0; i < 8; i++) {
                Tile tile = board[7][i];//The 1st rank
                if (!tile.isEmpty() && tile.getPiece() instanceof Piece.Pawn) {
                    return (Piece.Pawn) tile.getPiece();
                }
            }
            return null;
        }
    }

    public Piece getKing(boolean white) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile tile = board[i][j];
                if (!tile.isEmpty() && tile.getPiece() instanceof Piece.King && tile.getPiece().white == white) {
                    return (Piece.King) tile.getPiece();
                }
            }
        }
        return null;
    }

    public Tile getTile(int row, int col) {
        return board[row][col];
    }

    public ArrayList<Piece> getPieces(boolean white) {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile tile = board[i][j];
                if (!tile.isEmpty() && tile.getPiece().white == white) {
                    pieces.add(tile.getPiece());
                }
            }
        }
        return pieces;
    }

    public Group getPieceList() {
        Group pieces = new Group();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile tile = board[i][j];
                if (!tile.isEmpty()) {
                    pieces.getChildren().add(tile.getPiece());
                }
            }
        }
        return pieces;
    }

    public Group getTileList() {
        Group tiles = new Group();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tiles.getChildren().add(board[i][j]);
            }
        }
        return tiles;
    }

    public void setTile(Tile tile, int row, int col) {
        board[row][col] = tile;
    }

    @Override
    public Board clone() {
        Board clone = new Board(size);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                clone.setTile(board[i][j].clone(), i, j);
            }
        }
        return clone;
    }

    @Override
    public boolean equals(Object board) {
        if (board instanceof Board) {
            Board cast = (Board) board;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Tile boardTile = cast.getTile(i, j);
                    Tile thisTile = this.board[i][j];
                    if (thisTile.isEmpty() && boardTile.isEmpty()) {
                        continue;
                    } else if (!thisTile.isEmpty() && !boardTile.isEmpty() && thisTile.getPiece().equals(boardTile.getPiece())) {
                        continue;
                    }
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        int deepHash = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile tile = board[i][j];
                if (!tile.isEmpty()) {
                    deepHash += board[i][j].getPiece().hashCode();
                }
            }
        }
        hash = 47 * hash + deepHash;
        return hash;
    }
}
