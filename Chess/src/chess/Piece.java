package chess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public abstract class Piece extends ImageView {

    static Image spriteSheet;
    boolean white;
    int row;
    int col;
    boolean notMoved;

    public Piece() {
        try {
            FileInputStream inputStream = new FileInputStream(System.getProperty("user.dir") + File.separator + "src" + File.separator + "chess" + File.separator + "Sprites.png");
            spriteSheet = new Image(inputStream);
        } catch (FileNotFoundException e) {
        }
        notMoved = true;
    }

    public void setPosAndSize(double x, double y, double size) {
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setFitWidth(size);
        this.setFitHeight(size);
        row = (int) (this.getTranslateY() / size);
        col = (int) (this.getTranslateX() / size);
    }

    private static int white(boolean white) {//For the sprite sheet
        if (white) {
            return 0;
        } else {
            return 320;
        }
    }

    public abstract Piece clone();

    static class King extends Piece {

        King(boolean white) {
            this.white = white;
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 0, white(white), 320, 320));
        }

        @Override
        public Piece clone() {
            Piece clone = new King(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }
    }

    static class Queen extends Piece {

        Queen(boolean white) {
            this.white = white;
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 320, white(white), 320, 320));
        }

        @Override
        public Piece clone() {
            Piece clone = new Queen(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }
    }

    static class Bishop extends Piece {

        Bishop(boolean white) {
            this.white = white;
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 640, white(white), 320, 320));
        }

        @Override
        public Piece clone() {
            Piece clone = new Bishop(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }
    }

    static class Knight extends Piece {

        Knight(boolean white) {
            this.white = white;
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 960, white(white), 320, 320));
        }

        @Override
        public Piece clone() {
            Piece clone = new Knight(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }
    }

    static class Rook extends Piece {

        Rook(boolean white) {
            this.white = white;
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 1280, white(white), 320, 320));
        }

        @Override
        public Piece clone() {
            Piece clone = new Rook(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }
    }

    static class Pawn extends Piece {

        boolean canEnPassant = false;
        boolean possibleEnPassantLeft = false;
        boolean possibleEnPassantRight = false;

        Pawn(boolean white) {
            this.white = white;
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 1600, white(white), 320, 320));
        }

        @Override
        public Piece clone() {
            Piece clone = new Pawn(this.white);
            clone.notMoved = this.notMoved;
            clone.row = this.row;
            clone.col = this.col;
            return clone;
        }
    }
}
