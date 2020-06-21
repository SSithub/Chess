package fastchess;

import java.util.ArrayList;
import java.util.Arrays;

public class Board {

    Piece[][] pieces;

    public Board() {
        pieces = new Piece[8][8];
    }

    public void setup() {
        //Pawns
        for (int i = 0; i < 8; i++) {//Rows of 8 for each side
            pieces[1][i] = new Piece(6, false, 1, i);
            pieces[6][i] = new Piece(6, true, 6, i);
        }
        //Rooks
        for (int i = 0; i < 2; i++) {//2 corners for each side
            int col = i * 7;
            pieces[0][col] = new Piece(3, false, 0, col);
            pieces[7][col] = new Piece(3, true, 7, col);
        }
        //Knights
        for (int i = 0; i < 2; i++) {//1 away from each corner
            int col = 5 * i + 1;
            pieces[0][col] = new Piece(5, false, 0, col);
            pieces[7][col] = new Piece(5, true, 7, col);
        }
        //Bishops
        for (int i = 0; i < 2; i++) {//2 away from each corner
            int col = 3 * i + 2;
            pieces[0][col] = new Piece(4, false, 0, col);
            pieces[7][col] = new Piece(4, true, 7, col);
        }
        //Kings
        pieces[0][4] = new Piece(1, false, 0, 4);
        pieces[7][4] = new Piece(1, true, 7, 4);
        //Queens
        pieces[0][3] = new Piece(2, false, 0, 3);
        pieces[7][3] = new Piece(2, true, 7, 3);
    }

    public Piece getToBePromotedPawn(boolean white) {
        final int promoLine;
        if (white) {
            promoLine = 0;
        } else {
            promoLine = 7;
        }
        for (int i = 0; i < 8; i++) {
            Piece temp = pieces[promoLine][i];
            if (temp != null && temp.isPawn()) {
                return temp;
            }
        }
        return null;
    }

    public ArrayList<Piece> getPieces(boolean white) {
        ArrayList<Piece> pieceList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = pieces[i][j];
                if (piece != null && piece.white == white) {
                    pieceList.add(piece);
                }
            }
        }
        return pieceList;
    }

    public Piece getKing(boolean white) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = pieces[i][j];
                if (piece != null && piece.isKing() && piece.white == white) {
                    return piece;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String board = "";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = pieces[i][j];
                board += "|";
                if (piece == null) {
                    board += "  ";
                } else {
                    board += pieces[i][j].toString();
                }
            }
            board += "|\n";
        }
        return board;
    }

    @Override
    public Board clone() {
        Board clone = new Board();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] != null) {
                    clone.pieces[i][j] = pieces[i][j].clone();
                }
            }
        }
        return clone;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Arrays.deepHashCode(this.pieces);
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
        final Board other = (Board) obj;
        if (!Arrays.deepEquals(this.pieces, other.pieces)) {
            return false;
        }
        return true;
    }
}
