package chess;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Board extends Group {

    final Color tan = Color.web("d2b48c");
    final Color tanDark = Color.web("a37a44");
    Tile[][] board = new Tile[8][8];
    Group pieces = new Group();
    double x;
    double y;

    Board(double x, double y, double size) {
        this.x = x;
        this.y = y;
        double tileWidth = size / 8;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Color tileColor;
                if ((i + j) % 2 == 0) {
                    tileColor = tan;
                } else {
                    tileColor = tanDark;
                }
                board[i][j] = new Tile(tileColor, (j * tileWidth) + x, (i * tileWidth) + y, tileWidth);
            }
        }
        this.getChildren().addAll(flatten());
        this.getChildren().add(pieces);
    }

    void setup() {
        //Clear the current pieces
        pieces.getChildren().clear();
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
        for (int i = 0; i < 2; i++) {
            board[0][5 * i + 1].setPiece(new Piece.Knight(false));
            board[7][5 * i + 1].setPiece(new Piece.Knight(true));
        }
        //Bishops
        for (int i = 0; i < 2; i++) {
            board[0][3 * i + 2].setPiece(new Piece.Bishop(false));
            board[7][3 * i + 2].setPiece(new Piece.Bishop(true));
        }
        //Kings
        board[0][3].setPiece(new Piece.King(false));
        board[7][3].setPiece(new Piece.King(true));
        //Queens
        board[0][4].setPiece(new Piece.Queen(false));
        board[7][4].setPiece(new Piece.Queen(true));
        //Add all to the board group
        pieces.getChildren().addAll(pieces());
    }

    Tile[] flatten() {
        Tile[] flatten = new Tile[64];
        for (int i = 0; i < 64; i++) {
            flatten[i] = board[i / 8][i - 8 * (i / 8)];
        }
        return flatten;
    }

    ArrayList<ImageView> pieces() {
        ArrayList<ImageView> sprites = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            ImageView sprite = board[i / 8][i - 8 * (i / 8)].piece;
            if (sprite != null) {
                sprites.add(sprite);
            }
        }
        return sprites;
    }
}
