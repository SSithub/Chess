package chess;

import static chess.Chess.SIZE;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Graphics {

    public static Group ooordinates() {
        Group coords = new Group();
        //Letters
        for (int i = 0; i < 8; i++) {
            Text letter = new Text(String.valueOf(Character.toChars(97 + i)));
            letter.setScaleX(SIZE / 600);
            letter.setScaleY(SIZE / 600);
            letter.setTranslateY(SIZE - (SIZE / 9) + (SIZE / 200));
            letter.setTranslateX(SIZE / 8 * i + (SIZE / 9));
            letter.setFill(Color.ALICEBLUE);
            if (i % 2 == 1) {
                letter.setFill(((Color) letter.getFill()).invert());
            }
            letter.setFont(Font.font("MS GOTHIC"));
            coords.getChildren().add(letter);
        }
        //Numbers
        for (int i = 0; i < 8; i++) {
            Text number = new Text(8 - i + "");
            number.setScaleX(SIZE / 600);
            number.setScaleY(SIZE / 600);
            number.setTranslateX(SIZE / 150);
            number.setTranslateY(SIZE / 8 * i + (SIZE / 50));
            number.setFill(Color.ALICEBLUE);
            if (i % 2 == 0) {
                number.setFill(((Color) number.getFill()).invert());
            }
            number.setFont(Font.font("MS GOTHIC"));
            coords.getChildren().add(number);
        }
        return coords;
    }

    public static Group checkmate(boolean whiteWinner) {
        final Group checkmate;
        if (whiteWinner) {
            checkmate = createMessageBoxOverlay("White Wins!");
        } else {
            checkmate = createMessageBoxOverlay("Black Wins!");
        }
        return checkmate;
    }

    public static Group stalemate() {
        Group stalemate = createMessageBoxOverlay("Stalemate!");
        return stalemate;
    }

    public static Group promotion(boolean white) {
        Rectangle box = new Rectangle(SIZE / 8, SIZE / 2);
        box.setFill(Color.ALICEBLUE);
        ImageView Queen = new ImageView(Piece.spriteQueen(white));
        Queen.setFitWidth(SIZE / 8);
        Queen.setFitHeight(SIZE / 8);
        ImageView Knight = new ImageView(Piece.spriteKnight(white));
        Knight.setFitWidth(SIZE / 8);
        Knight.setFitHeight(SIZE / 8);
        ImageView Rook = new ImageView(Piece.spriteRook(white));
        Rook.setFitWidth(SIZE / 8);
        Rook.setFitHeight(SIZE / 8);
        ImageView Bishop = new ImageView(Piece.spriteBishop(white));
        Bishop.setFitWidth(SIZE / 8);
        Bishop.setFitHeight(SIZE / 8);
        VBox pieces;
        if (white) {
            pieces = new VBox(Queen, Knight, Rook, Bishop);
        } else {
            pieces = new VBox(Bishop, Rook, Knight, Queen);
        }
        return new Group(box, pieces);
    }

    private static Group createMessageBoxOverlay(String message) {
        Group messageOverlay = new Group();
        //Obscure background
        Rectangle filter = new Rectangle(SIZE, SIZE);
        filter.setOpacity(.8);
        //Message box
        //Rectangle
        Rectangle rect = new Rectangle(SIZE * 7 / 12, SIZE / 5);
        rect.setFill(Color.ALICEBLUE);
        rect.setArcWidth(SIZE / 20);
        rect.setArcHeight(SIZE / 20);
        //Message
        Text msg = new Text(message);
        msg.setScaleX(SIZE / 200);
        msg.setScaleY(SIZE / 200);
        msg.setFont(Font.font("MS PGothic"));
        msg.setFill(Color.GRAY.darker().darker());
        //Stack the nodes
        StackPane box = new StackPane(rect, msg);
        box.setTranslateX((SIZE - rect.getWidth()) / 2);
        box.setTranslateY((SIZE - rect.getHeight()) / 2);
        //Add to the group
        messageOverlay.getChildren().addAll(filter, box);
        return messageOverlay;
    }
}
