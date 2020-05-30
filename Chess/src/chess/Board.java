package chess;

import javafx.scene.Group;

public class Board {

    Tile[][] board = new Tile[8][8];
    double size;
    Group tiles = new Group();
    Group pieces = new Group();

    public Board(double size) {
        this.size = size;
        double tileWidth = size / 8;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile tile = new Tile(j * tileWidth, i * tileWidth, tileWidth);
                board[i][j] = tile;
                tiles.getChildren().add(tile);
            }
        }
    }

    public void setupPieces() {
        //Pawns
        for (int i = 0; i < 8; i++) {//Rows of 8 for each side
            board[1][i].setPiece(new Piece.Pawn(false));//Black
            board[6][i].setPiece(new Piece.Pawn(true));//White
            pieces.getChildren().add(board[1][i].getPiece());
            pieces.getChildren().add(board[6][i].getPiece());
        }
        //Rooks
        for (int i = 0; i < 2; i++) {//2 corners for each side
            board[0][i * 7].setPiece(new Piece.Rook(false));
            board[7][i * 7].setPiece(new Piece.Rook(true));
            pieces.getChildren().add(board[0][i * 7].getPiece());
            pieces.getChildren().add(board[7][i * 7].getPiece());
        }
        //Knights
        for (int i = 0; i < 2; i++) {//1 away from each corner
            board[0][5 * i + 1].setPiece(new Piece.Knight(false));
            board[7][5 * i + 1].setPiece(new Piece.Knight(true));
            pieces.getChildren().add(board[0][5 * i + 1].getPiece());
            pieces.getChildren().add(board[7][5 * i + 1].getPiece());
        }
        //Bishops
        for (int i = 0; i < 2; i++) {//2 away from each corner
            board[0][3 * i + 2].setPiece(new Piece.Bishop(false));
            board[7][3 * i + 2].setPiece(new Piece.Bishop(true));
            pieces.getChildren().add(board[0][3 * i + 2].getPiece());
            pieces.getChildren().add(board[7][3 * i + 2].getPiece());
        }
        //Kings
        board[0][4].setPiece(new Piece.King(false));
        board[7][4].setPiece(new Piece.King(true));
        pieces.getChildren().add(board[0][4].getPiece());
        pieces.getChildren().add(board[7][4].getPiece());
        //Queens
        board[0][3].setPiece(new Piece.Queen(false));
        board[7][3].setPiece(new Piece.Queen(true));
        pieces.getChildren().add(board[0][3].getPiece());
        pieces.getChildren().add(board[7][3].getPiece());
    }
}
