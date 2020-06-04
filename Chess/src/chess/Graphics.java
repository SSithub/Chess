package chess;

import static chess.Chess.SIZE;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Graphics {

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
//        rect.setStrokeWidth(SIZE / 50);
        //Message
        Text msg = new Text(message);
        msg.setScaleX(5);
        msg.setScaleY(5);
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
