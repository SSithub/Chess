package chess;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Board extends Group {

    final Color tan = Color.web("D2B48C");
    final Color tanDark = Color.web("A37A44");
    Tile[][] board = new Tile[8][8];
    ArrayList<Tile[][]> history = new ArrayList<>();
    Group pieces = new Group();
    double size;
    Tile primaryTile = null;
    boolean whiteTurn = true;

    public Board(double size) {
        this.size = size;
        double tileWidth = size / 8;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Color tileColor;
                if ((i + j) % 2 == 0) {//Every other tile
                    tileColor = tan;
                } else {
                    tileColor = tanDark;
                }
                Tile tile = new Tile(tileColor, (j * tileWidth), (i * tileWidth), tileWidth);
                board[i][j] = tile;
                board[i][j].setOnMouseClicked((t) -> {
                    onActivation(tile);
                });
            }
        }
        this.getChildren().addAll(flatten());
        this.getChildren().add(pieces);
    }

    void setupPieces() {
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
        board[0][4].setPiece(new Piece.King(false));
        board[7][4].setPiece(new Piece.King(true));
        //Queens
        board[0][3].setPiece(new Piece.Queen(false));
        board[7][3].setPiece(new Piece.Queen(true));
        //Add all to the board group
        pieces.getChildren().addAll(pieces());
        //Add listeners to all pieces
        for (int i = 0; i < pieces.getChildren().size(); i++) {
            Piece piece = (Piece) pieces.getChildren().get(i);
            piece.setOnMouseClicked((t) -> {
                Tile tile = board[piece.row][piece.col];
                onActivation(tile);
            });
        }
        history.add(0, copyBoard(board));
    }

    void onActivation(Tile tile) {
        if (tile.highlighted || tile.piece != null) {//Must be an activated square or there is a piece
            if (primaryTile != null) {//Selected a primary piece
                if (tile.piece == null) {//Empty tile
                    tile.placePiece(primaryTile.piece);//Place piece into tile
                    primaryTile.piece.notMoved = false;//Moved piece
                    primaryTile.piece = null;//Clear piece from primary tile
                    primaryTile = null;//Clear primary tile
                    unactivateAll();//Clear all highlighted squares
                    whiteTurn = !whiteTurn;//Alternate turns
                    history.add(0, copyBoard(board));
                    //Enpassant move
                    if (tile.enpassantSquare) {
                        tile.enpassantSquare = false;
                        Tile captureSquare;
                        if (tile.piece.white) {
                            captureSquare = board[tile.row + 1][tile.col];
                        } else {
                            captureSquare = board[tile.row - 1][tile.col];
                        }
                        captureSquare.piece.setVisible(false);
                        captureSquare.piece = null;
                    }
                } else {//Unempty tile
                    if (tile.piece.white == whiteTurn) {//Ally
                        highlightAvailableMoves(tile);
                    } else {//Enemy
                        if (tile.highlighted) {
                            tile.piece.setVisible(false);
                            tile.placePiece(primaryTile.piece);
                            primaryTile.piece = null;
                            primaryTile = null;
                            unactivateAll();
                            whiteTurn = !whiteTurn;
                            history.add(0, copyBoard(board));
                        }
                    }
                }
            } else {//No primary piece
                if (tile.piece.white == whiteTurn) {//If it is an ally
                    highlightAvailableMoves(tile);
                }
            }
        }
    }

    void highlightAvailableMoves(Tile tile) {
        unactivateAll();
        tile.primary();
        ArrayList<int[]> moves = tile.piece.availableMoves(history);
        for (int k = 0; k < moves.size(); k++) {
            int[] spot = moves.get(k);
            board[spot[0]][spot[1]].highlight();
            if (spot.length > 2 && spot[2] == 1) {
                board[spot[0]][spot[1]].enpassantSquare = true;
            }
        }
        primaryTile = tile;//Store selected tile
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

    void unactivateAll() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j].unhighlight();
            }
        }
    }

    static Tile[][] copyBoard(Tile[][] board) {
        Tile[][] copy = new Tile[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copy[i][j] = board[i][j].clone();
            }
        }
        return copy;
    }
}
