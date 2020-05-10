package chess;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    Piece piece;

    Tile(Color color, double x, double y, double size) {
        this.setFill(color);
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setWidth(size);
        this.setHeight(size);
        this.setOnMouseClicked((event) -> {
            
        });
    }

    void setPiece(Piece piece) {
        this.piece = piece;
        piece.setSize(this.getWidth());
        piece.setPos(this.getTranslateX(), this.getTranslateY());
    }
}
