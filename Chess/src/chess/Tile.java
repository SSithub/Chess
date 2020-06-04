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
    private int[] move = null;

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

    public void setPiece(Piece piece) {
        this.piece = piece;
        piece.setPosAndSize(this.getTranslateX(), this.getTranslateY(), this.getWidth());
    }

    public void placePiece(Piece piece) {
        this.piece = piece;
        piece.notMoved = false;
        piece.setPosAndSize(this.getTranslateX(), this.getTranslateY(), this.getWidth());
    }

    public Piece getPiece() {
        return this.piece;
    }

    public boolean isEmpty() {
        return this.piece == null;
    }

    public boolean isPrimaryColored() {
        return this.primaryColored;
    }

    public boolean isHighlightColored() {
        return this.highlightColored;
    }

    public boolean isCheckColored() {
        return this.checkColored;
    }

    public void setPrimary() {
        this.setBorder(primaryColor);
        primaryColored = true;
    }

    public void setHighlight() {
        this.setBorder(highlightColor);
        highlightColored = true;
    }

    public void setCheck() {
        this.setBorder(checkColor);
        checkColored = true;
    }

    public void setBorderOff() {
        this.setStrokeWidth(0);
        primaryColored = false;
        highlightColored = false;
        checkColored = false;
    }

    private void setBorder(Color color) {
        this.setStroke(color);
        this.setStrokeWidth(this.getWidth() / 15);
    }

    public void setMove(int[] move) {
        this.move = move;
    }

    public int[] getMove() {
        return move;
    }

    public void clearTemps() {
        setBorderOff();
        move = null;
    }

    public void clearPiece() {
        piece = null;
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
