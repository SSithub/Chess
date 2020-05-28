package chess;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class Tile extends Rectangle {

    Piece piece = null;
    int row;
    int col;
    boolean highlighted;
    final Color highlight = Color.web("3FA7D6");
    final Color primary = Color.web("04E762");
    boolean enpassantSquare = false;

    public Tile(Color color, double x, double y, double size) {
        this.setFill(color);
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setWidth(size);
        this.setHeight(size);
        this.setStroke(highlight);
        this.setStrokeType(StrokeType.INSIDE);
        row = (int) (this.getTranslateY() / size);
        col = (int) (this.getTranslateX() / size);
        unhighlight();
    }

    void setPiece(Piece piece) {
        this.piece = piece;
        piece.setPosAndSize(this.getTranslateX(), this.getTranslateY(), this.getWidth());
    }

    void placePiece(Piece piece) {
        this.piece = piece;
        piece.notMoved = false;
        piece.setPosAndSize(this.getTranslateX(), this.getTranslateY(), this.getWidth());
    }

    void toggleHighlight() {
        if (highlighted) {
            unhighlight();
        } else {
            highlight();
        }
    }

    void primary() {
        this.setStroke(primary);
        this.setStrokeWidth(getWidth() / 15);
        highlighted = true;
    }

    void highlight() {
        this.setStroke(highlight);
        this.setStrokeWidth(getWidth() / 15);
        highlighted = true;
    }

    void unhighlight() {
        this.setStrokeWidth(0);
        highlighted = false;
    }

    @Override
    public Tile clone() {
        Tile clone = new Tile((Color) this.getFill(), this.getTranslateX(), this.getTranslateY(), this.getWidth());
        if (this.piece != null) {
            clone.piece = this.piece.clone();
        }
        return clone;
    }
}
