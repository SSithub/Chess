package old;

//package chess;
//
//import java.util.ArrayList;
//import javafx.scene.Group;
//import javafx.scene.image.ImageView;
//import javafx.scene.paint.Color;
//
//public class Board_Old extends Group {
//
//    final Color tan = Color.web("D2B48C");
//    final Color tanDark = Color.web("A37A44");
//    Tile[][] board = new Tile[8][8];
//    ArrayList<Tile[][]> history = new ArrayList<>();
//    Group pieces = new Group();
//    double size;
//    Tile primaryTile = null;
//    boolean whiteTurn = true;
//    boolean check = false;
//
//    public Board_Old(double size) {
//        this.size = size;
//        double tileWidth = size / 8;
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                Tile tile = new Tile(j * tileWidth, i * tileWidth, tileWidth);
//                board[i][j] = tile;
//                board[i][j].setOnMouseClicked((t) -> {
//                    onActivation(tile);
//                });
//            }
//        }
//        this.getChildren().addAll(flatten());
//        this.getChildren().add(pieces);
//    }
//
//    void setupPieces() {
//        //Clear the current pieces
//        pieces.getChildren().clear();
//        //Pawns
//        for (int i = 0; i < 8; i++) {//Rows of 8 for each side
//            board[1][i].setPiece(new Piece.Pawn(false));//Black
//            board[6][i].setPiece(new Piece.Pawn(true));//White
//        }
//        //Rooks
//        for (int i = 0; i < 2; i++) {//2 corners for each side
//            board[0][i * 7].setPiece(new Piece.Rook(false));
//            board[7][i * 7].setPiece(new Piece.Rook(true));
//        }
//        //Knights
//        for (int i = 0; i < 2; i++) {
//            board[0][5 * i + 1].setPiece(new Piece.Knight(false));
//            board[7][5 * i + 1].setPiece(new Piece.Knight(true));
//        }
//        //Bishops
//        for (int i = 0; i < 2; i++) {
//            board[0][3 * i + 2].setPiece(new Piece.Bishop(false));
//            board[7][3 * i + 2].setPiece(new Piece.Bishop(true));
//        }
//        //Kings
//        board[0][4].setPiece(new Piece.King(false));
//        board[7][4].setPiece(new Piece.King(true));
//        //Queens
//        board[0][3].setPiece(new Piece.Queen(false));
//        board[7][3].setPiece(new Piece.Queen(true));
//        //Add all to the board group
//        pieces.getChildren().addAll(pieces());
//        //Add listeners to all pieces
//        for (int i = 0; i < pieces.getChildren().size(); i++) {
//            Piece piece = (Piece) pieces.getChildren().get(i);
//            piece.setOnMouseClicked((t) -> {
//                Tile tile = board[piece.row][piece.col];
//                onActivation(tile);
//            });
//        }
//        history.add(0, copyBoard(board));
//    }
//
//    void onActivation(Tile tile) {
//        if (tile.highlighted || tile.piece != null) {//Must be an activated square or there is a piece
//            if (primaryTile != null) {//Selected a primary piece
//                if (tile.piece == null) {//Empty tile
//                    tile.placePiece(primaryTile.piece);//Place piece into tile
//                    primaryTile.piece.notMoved = false;//Moved piece
//                    primaryTile.piece = null;//Clear piece from primary tile
//                    primaryTile = null;//Clear primary tile
//                    turnDone();
//                    if (tile.enpassant) {//Enpassant move
//                        tile.enpassant = false;
//                        Tile captureSquare;
//                        if (tile.piece.white) {
//                            captureSquare = board[tile.row + 1][tile.col];
//                        } else {
//                            captureSquare = board[tile.row - 1][tile.col];
//                        }
//                        captureSquare.piece.setVisible(false);
//                        captureSquare.piece = null;
//                    }
//                } else {//Unempty tile
//                    if (tile.piece.white == whiteTurn) {//Ally
//                        if (tile.castling) {//Castling move
//                            tile.castling = false;
//                            Piece rook = tile.piece;
//                            Piece king = primaryTile.piece;
//                            //Find which side the Rook is on relative to the King
//                            final int dirOne;
//                            if (king.col - rook.col < 0) {
//                                dirOne = 1;
//                            } else {
//                                dirOne = -1;
//                            }
//                            tile.piece = null;//Remove Rook from the old Tile
//                            board[king.row][king.col + dirOne].placePiece(rook);//Move Rook next to King
//                            primaryTile.piece = null;//Remove King from old Tile
//                            board[king.row][king.col + dirOne + dirOne].placePiece(king);//Move King past Rook
//                            turnDone();
//                        } else {//Ally that is not a Rook that can be used to castled and the King is not in check
//                            highlightAvailableMoves(tile);
//                        }
//                    } else {//Enemy
//                        if (tile.highlighted) {
//                            tile.piece.setVisible(false);
//                            tile.placePiece(primaryTile.piece);
//                            primaryTile.piece = null;
//                            primaryTile = null;
//                            turnDone();
//                        }
//                    }
//                }
//            } else {//No primary piece
//                if (tile.piece.white == whiteTurn) {//If it is an ally
//                    highlightAvailableMoves(tile);
//                }
//            }
//        }
//    }
//
//    void turnDone() {
//        whiteTurn = !whiteTurn;//Alternate turns
//        history.add(0, copyBoard(board));//Add the new board to the history of moves
//        checkForCheck(whiteTurn);
//        unactivateAll();
//    }
//
//    void highlightAvailableMoves(Tile tile) {
//        unactivateAll();
//        if (tile.check) {
//            tile.check();
//        } else {
//            tile.primary();
//        }
//        ArrayList<int[]> moves = tile.piece.availableMoves(history);
//        for (int k = 0; k < moves.size(); k++) {
//            int[] spot = moves.get(k);
//            board[spot[0]][spot[1]].highlight();
//            if (spot.length > 2) {
//                if (spot[2] == 1) {
//                    board[spot[0]][spot[1]].enpassant = true;
//                } else if (spot[2] == 2) {
//                    board[spot[0]][spot[1]].castling = true;
//                }
//            }
//        }
//        primaryTile = tile;//Store selected tile
//    }
//
//    Tile[] flatten() {
//        Tile[] flatten = new Tile[64];
//        for (int i = 0; i < 64; i++) {
//            flatten[i] = board[i / 8][i - 8 * (i / 8)];
//        }
//        return flatten;
//    }
//
//    ArrayList<ImageView> pieces() {
//        ArrayList<ImageView> sprites = new ArrayList<>();
//        for (int i = 0; i < 64; i++) {
//            ImageView sprite = board[i / 8][i - 8 * (i / 8)].piece;
//            if (sprite != null) {
//                sprites.add(sprite);
//            }
//        }
//        return sprites;
//    }
//
//    void unactivateAll() {
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                board[i][j].unborder();
//            }
//        }
//    }
//
//    void checkForCheck(boolean white) {//To be called after each turn, checks for a king with the specified color that is in check
//        boolean inCheck = false;
//        Piece king = null;
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                Piece piece = board[i][j].piece;
//                if (piece != null && piece instanceof Piece.King && piece.white == white) {
//                    king = board[i][j].piece;
//                }
//                board[i][j].check = false;//Reset the red highlight on the King when in check
//            }
//        }
//        //Loop through the board and find an enemy piece that can capture the king if the king doesn't move
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                Piece piece = board[i][j].piece;
//                if (piece != null && piece.white != white) {
//                    ArrayList<int[]> moves = piece.availableMoves(history);
//                    int size = moves.size();
//                    for (int k = 0; k < size; k++) {
//                        int[] move = moves.get(k);
//                        if (move[0] == king.row && move[1] == king.col) {
//                            inCheck = true;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        //Restrict board for the king in check
//        if (inCheck) {
//            Tile kingTile = board[king.row][king.col];
//            kingTile.check();
//        }
//    }
//
//    static Tile[][] copyBoard(Tile[][] board) {
//        Tile[][] copy = new Tile[8][8];
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                copy[i][j] = board[i][j].clone();
//            }
//        }
//        return copy;
//    }
//}
