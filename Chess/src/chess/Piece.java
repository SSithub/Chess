package chess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public abstract class Piece extends ImageView {

    private static Image spriteSheet;
    boolean white;
    int row;
    int col;
    boolean notMoved = true;

    static {
        try {//IDE
            FileInputStream inputStream = new FileInputStream(System.getProperty("user.dir") + File.separator + "src" + File.separator + "chess" + File.separator + "Sprites.png");
            spriteSheet = new Image(inputStream);
        } catch (FileNotFoundException e) {//JAR
            spriteSheet = new Image(Piece.class.getResourceAsStream("Sprites.png"));
        }
    }

    public static Image spriteKing(boolean white) {
        return new WritableImage(spriteSheet.getPixelReader(), 0, y(white), 320, 320);
    }

    public static Image spriteQueen(boolean white) {
        return new WritableImage(spriteSheet.getPixelReader(), 320, y(white), 320, 320);
    }

    public static Image spriteBishop(boolean white) {
        return new WritableImage(spriteSheet.getPixelReader(), 640, y(white), 320, 320);
    }

    public static Image spriteKnight(boolean white) {
        return new WritableImage(spriteSheet.getPixelReader(), 960, y(white), 320, 320);
    }

    public static Image spriteRook(boolean white) {
        return new WritableImage(spriteSheet.getPixelReader(), 1280, y(white), 320, 320);
    }

    public static Image spritePawn(boolean white) {
        return new WritableImage(spriteSheet.getPixelReader(), 1600, y(white), 320, 320);
    }

    public void setPosAndSize(double x, double y, double size) {
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setFitWidth(size);
        this.setFitHeight(size);
        row = (int) (this.getTranslateY() / size);
        col = (int) (this.getTranslateX() / size);
    }

    private static int y(boolean white) {
        if (white) {
            return 0;
        } else {
            return 320;
        }
    }

    public boolean isWhite() {
        return white;
    }

    @Override
    public abstract Piece clone();

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object piece);

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.white ? 1 : 0);
        hash = 67 * hash + this.row;
        hash = 67 * hash + this.col;
        hash = 67 * hash + (this.notMoved ? 1 : 0);
        return hash;
    }

    public static class King extends Piece {

        King(boolean white) {
            this.white = white;
            this.setImage(spriteKing(white));
        }

        @Override
        public Piece clone() {
            Piece clone = new King(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }

        @Override
        public String toString() {
            final String color;
            if (white) {
                color = "White";
            } else {
                color = "Black";
            }
            return color + " King " + Moves.moveToString(new int[]{row, col});
        }

        @Override
        public boolean equals(Object piece) {
            if (piece instanceof King && piece.hashCode() == this.hashCode()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static class Queen extends Piece {

        Queen(boolean white) {
            this.white = white;
            this.setImage(spriteQueen(white));
        }

        @Override
        public Piece clone() {
            Piece clone = new Queen(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }

        @Override
        public String toString() {
            final String color;
            if (white) {
                color = "White";
            } else {
                color = "Black";
            }
            return color + " Queen " + Moves.moveToString(new int[]{row, col});
        }

        @Override
        public boolean equals(Object piece) {
            if (piece instanceof Queen && piece.hashCode() == this.hashCode()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static class Bishop extends Piece {

        Bishop(boolean white) {
            this.white = white;
            this.setImage(spriteBishop(white));
        }

        @Override
        public Piece clone() {
            Piece clone = new Bishop(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }

        @Override
        public String toString() {
            final String color;
            if (white) {
                color = "White";
            } else {
                color = "Black";
            }
            return color + " Bishop " + Moves.moveToString(new int[]{row, col});
        }

        @Override
        public boolean equals(Object piece) {
            if (piece instanceof Bishop && piece.hashCode() == this.hashCode()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static class Knight extends Piece {

        Knight(boolean white) {
            this.white = white;
            this.setImage(spriteKnight(white));
        }

        @Override
        public Piece clone() {
            Piece clone = new Knight(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }

        @Override
        public String toString() {
            final String color;
            if (white) {
                color = "White";
            } else {
                color = "Black";
            }
            return color + " Knight " + Moves.moveToString(new int[]{row, col});
        }

        @Override
        public boolean equals(Object piece) {
            if (piece instanceof Knight && piece.hashCode() == this.hashCode()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static class Rook extends Piece {

        Rook(boolean white) {
            this.white = white;
            this.setImage(spriteRook(white));
        }

        @Override
        public String toString() {
            final String color;
            if (white) {
                color = "White";
            } else {
                color = "Black";
            }
            return color + " Rook " + Moves.moveToString(new int[]{row, col});
        }

        @Override
        public Piece clone() {
            Piece clone = new Rook(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }

        @Override
        public boolean equals(Object piece) {
            if (piece instanceof Rook && piece.hashCode() == this.hashCode()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static class Pawn extends Piece {

        Pawn(boolean white) {
            this.white = white;
            this.setImage(spritePawn(white));
        }

        @Override
        public Piece clone() {
            Piece clone = new Pawn(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }

        @Override
        public String toString() {
            final String color;
            if (white) {
                color = "White";
            } else {
                color = "Black";
            }
            return color + " Pawn " + Moves.moveToString(new int[]{row, col});
        }

        @Override
        public boolean equals(Object piece) {
            if (piece instanceof Pawn && piece.hashCode() == this.hashCode()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
