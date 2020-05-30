package chess;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class Tile extends Rectangle {

    private final Color tan = Color.web("D2B48C");
    private final Color tanDark = Color.web("A37A44");
    private Piece piece = null;
    private int row;
    private int col;
    private final Color highlightColor = Color.web("3FA7D6");
    private final Color primaryColor = Color.web("04E762");
    private final Color checkColor = Color.web("FF3C38");
    private boolean highlightColored = false;
    private boolean checkColored = false;
    private boolean primaryColored = false;

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
        setBorderOff();
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
        return this.piece;
    }

    boolean isEmpty() {
        return this.piece == null;
    }

    boolean isPrimaryColored() {
        return this.primaryColored;
    }

    boolean isHighlightColored() {
        return this.highlightColored;
    }

    boolean isCheckColored() {
        return this.checkColored;
    }

    void setPrimary() {
        this.setBorder(primaryColor);
    }

    void setHighlight() {
        this.setBorder(highlightColor);
    }

    void setCheck() {
        this.setBorder(checkColor);
    }

    void setBorderOff() {
        this.setStrokeWidth(0);
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
