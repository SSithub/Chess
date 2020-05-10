package chess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public abstract class Piece extends ImageView {

    static Image spriteSheet;

    Piece() {
        try {
            FileInputStream inputStream = new FileInputStream(System.getProperty("user.dir") + File.separator + "src" + File.separator + "chess" + File.separator + "Sprites.png");
            spriteSheet = new Image(inputStream);
        } catch (FileNotFoundException e) {
        }
    }

    void setPos(double x, double y) {
        this.setTranslateX(x);
        this.setTranslateY(y);
    }

    void setSize(double size) {
        this.setFitWidth(size);
        this.setFitHeight(size);
    }

    private static int white(boolean white) {
        if (white) {
            return 0;
        } else {
            return 320;
        }
    }

    static class Queen extends Piece {

        Queen(boolean white) {
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 0, white(white), 320, 320));
        }
    }

    static class King extends Piece {

        King(boolean white) {
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 320, white(white), 320, 320));
        }
    }

    static class Bishop extends Piece {

        Bishop(boolean white) {
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 640, white(white), 320, 320));
        }
    }

    static class Knight extends Piece {

        Knight(boolean white) {
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 960, white(white), 320, 320));
        }
    }

    static class Rook extends Piece {

        Rook(boolean white) {
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 1280, white(white), 320, 320));
        }
    }

    static class Pawn extends Piece {

        Pawn(boolean white) {
            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 1600, white(white), 320, 320));
        }
    }
}
