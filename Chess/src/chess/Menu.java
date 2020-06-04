package chess;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Menu extends Group {

    private final double size;
    private final double WIDTH;
    private final double GAP;
    private VBox buttons;

    public Menu(double size) {
        this.size = size;
        WIDTH = size / 3;
        GAP = size / 50;
        buttons = new VBox(GAP);
        buttons.setAlignment(Pos.CENTER);
        buttons.setTranslateX((size - WIDTH) / 2);
        buttons.setTranslateY(GAP * 15);
        buttons.getChildren().addAll(newGameButton(), quitButton());
        this.getChildren().addAll(overlay(), buttons);
        this.setVisible(false);
    }

    public ObservableList<Node> getButtons() {
        return buttons.getChildren();
    }

    private Rectangle overlay() {
        Rectangle overlay = new Rectangle(size, size);
        overlay.setOpacity(.6);
        return overlay;
    }

    private StackPane newGameButton() {
        StackPane newGameButton = createButton("New Game");
        return newGameButton;
    }

    private StackPane quitButton() {
        StackPane quitButton = createButton("Quit");
        quitButton.setOnMouseClicked((event) -> {
            System.exit(0);
        });
        return quitButton;
    }

    private StackPane createButton(String buttonLabel) {
        Rectangle shape = new Rectangle();
        shape.setWidth(WIDTH);
        shape.setHeight(size / 8);
        shape.setArcWidth(size / 10);
        shape.setArcHeight(size / 10);
        shape.setFill(Color.ALICEBLUE);
        Text label = new Text(buttonLabel);
        label.setScaleX(3);
        label.setScaleY(3);
        label.setFont(Font.font("MS PGothic"));
        label.setFill(Color.GRAY.darker().darker());
        StackPane button = new StackPane();
        button.getChildren().addAll(shape, label);
        button.setOnMousePressed((event) -> {
            shape.setFill(((Color) shape.getFill()).darker());
        });
        button.setOnMouseReleased((event) -> {
            shape.setFill(((Color) shape.getFill()).brighter());
        });
        return button;
    }
}
