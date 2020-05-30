package chess;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class Tile extends Rectangle {

    final Color tan = Color.web("D2B48C");
    final Color tanDark = Color.web("A37A44");
    Piece piece = null;
    int row;
    int col;
    boolean highlighted;
    final Color highlightColor = Color.web("3FA7D6");
    final Color primaryColor = Color.web("04E762");
    final Color checkColor = Color.web("FF3C38");
    boolean enpassant = false;
    boolean castling = false;
    boolean check = false;

    public Tile(double x, double y, double size) {
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setWidth(size);
        this.setHeight(size);
        this.setStroke(highlightColor);
        this.setStrokeType(StrokeType.INSIDE);
        row = (int) (this.getTranslateY() / size);
        col = (int) (this.getTranslateX() / size);
        if ((row + col) % 2 == 0) {
            this.setFill(tan);
        } else {
            this.setFill(tanDark);
        }
        unborder();
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

    Piece getPiece() {
        return piece;
    }

    boolean isEmpty() {
        return this.piece == null;
    }

    void primary() {
        this.setBorder(primaryColor);
    }

    void highlight() {
        this.setBorder(highlightColor);
        highlighted = true;
    }

    void unborder() {
        if (!check) {
            this.setStrokeWidth(0);
            highlighted = false;
        }
    }

    void check() {
        this.setBorder(checkColor);
        check = true;
    }

    private void setBorder(Color color) {
        this.setStroke(color);
        this.setStrokeWidth(this.getWidth() / 15);
    }

    @Override
    public Tile clone() {
        Tile clone = new Tile(this.getTranslateX(), this.getTranslateY(), this.getWidth());
        if (!this.isEmpty()) {
            clone.setPiece(getPiece().clone());
        }
        return clone;
    }
}
