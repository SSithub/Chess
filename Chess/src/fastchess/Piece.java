package fastchess;

public class Piece {

    public int type;
    public boolean white;
    public boolean moved;
    public int row;
    public int col;

    /**
     *
     * @param type 1 = king, 2 = queen, 3 = rook, 4 = bishop, 5 = knight, 6 =
     * pawn
     * @param white true = white, false = black
     */
    public Piece(int type, boolean white, int row, int col) {
        this.type = type;
        this.white = white;
        moved = false;
        setPos(row, col);
    }

    public void setPos(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean isKing() {
        return type == 1;
    }

    public boolean isQueen() {
        return type == 2;
    }

    public boolean isRook() {
        return type == 3;
    }

    public boolean isBishop() {
        return type == 4;
    }

    public boolean isKnight() {
        return type == 5;
    }

    public boolean isPawn() {
        return type == 6;
    }

    @Override
    public String toString() {
        final String color;
        if (white) {
            color = "w";
        } else {
            color = "b";
        }
        if (isKing()) {
            return color + "K";
        }
        if (isQueen()) {
            return color + "Q";
        }
        if (isRook()) {
            return color + "R";
        }
        if (isBishop()) {
            return color + "B";
        }
        if (isKnight()) {
            return color + "N";
        }
        if (isPawn()) {
            return color + "P";
        }
        return "?";
    }

    @Override
    public Piece clone() {
        Piece clone = new Piece(type, white, row, col);
        clone.moved = moved;
        return clone;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.type;
        hash = 17 * hash + (this.white ? 1 : 0);
        hash = 17 * hash + (this.moved ? 1 : 0);
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
        final Piece other = (Piece) obj;
        if (this.type != other.type) {
            return false;
        }
        if (this.white != other.white) {
            return false;
        }
        if (this.moved != other.moved) {
            return false;
        }
        return true;
    }
}
