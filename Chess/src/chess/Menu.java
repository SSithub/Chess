package chess;

import static chess.Chess.SIZE;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Menu extends Group {

    private final double WIDTH;
    private final double GAP;
    private final double SCALE;
    private VBox buttons;
    private ArrayList<Text> gameScores = new ArrayList<>();
    private Slider slider;

    public Menu() {
        WIDTH = SIZE / 3;
        GAP = SIZE / 50;
        SCALE = SIZE / 400;
        buttons = new VBox(GAP);
        buttons.setAlignment(Pos.CENTER);
        buttons.setTranslateX((SIZE - WIDTH) / 2);
        buttons.setTranslateY(GAP * 15);
        buttons.getChildren().addAll(newGameButton(), playAIButton(), trainAIButton(), quitButton());
        this.getChildren().addAll(overlay(), buttons, gameSpeedSlider(), scores());
        this.setVisible(false);
    }

    public ObservableList<Node> getButtons() {
        return buttons.getChildren();
    }

    public Slider getSlider() {
        return slider;
    }

    private Rectangle overlay() {
        Rectangle overlay = new Rectangle(SIZE, SIZE);
        overlay.setOpacity(.6);
        return overlay;
    }

    private StackPane newGameButton() {
        StackPane newGameButton = createButton("New Game");
        return newGameButton;
    }

    private StackPane playAIButton() {
        StackPane playAIButton = createButton("Play AI");
        return playAIButton;
    }

    private StackPane trainAIButton() {
        StackPane trainAIButton = createButton("Train AI");
        return trainAIButton;
    }

    private StackPane quitButton() {
        StackPane quitButton = createButton("Quit");
        quitButton.setOnMouseClicked((event) -> {
            System.exit(0);
        });
        return quitButton;
    }

    private Group scores() {
        VBox white = new VBox();
        Text whiteLabel = new Text("White Wins:");
        Text whiteScore = new Text("0");
        style(whiteLabel);
        style(whiteScore);
        white.getChildren().addAll(whiteLabel, whiteScore);
        VBox stalemate = new VBox();
        Text stalemateLabel = new Text("Stalemates:");
        Text stalemateScore = new Text("0");
        style(stalemateLabel);
        style(stalemateScore);
        stalemate.getChildren().addAll(stalemateLabel, stalemateScore);
        stalemate.setTranslateX(SIZE / 4);
        VBox black = new VBox();
        Text blackLabel = new Text("Black Wins:");
        Text blackScore = new Text("0");
        style(blackLabel);
        style(blackScore);
        black.getChildren().addAll(blackLabel, blackScore);
        black.setTranslateX(SIZE / 2);
        scale(white, SCALE * .6);
        scale(black, SCALE * .6);
        scale(stalemate, SCALE * .6);
        gameScores.add(whiteScore);
        gameScores.add(blackScore);
        gameScores.add(stalemateScore);
        Group scores = new Group();
        scores.getChildren().addAll(white, stalemate, black);
        scores.setTranslateX(SIZE / 3);
        scores.setTranslateY(SIZE / 25);
        return scores;
    }

    public void incrementScore(int stat) {//1 = white, 2 = black, 3 = stalemate
        Text text = gameScores.get(stat - 1);
        text.setText("" + (Integer.decode(text.getText()) + 1));
    }

    private VBox gameSpeedSlider() {
        slider = new Slider();
        slider.setPrefWidth(WIDTH / 4);
        slider.setMin(1);
        slider.setMax(20);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setBlockIncrement(.5);
        Text label = new Text("Game Update Slider");
        style(label);
        scale(label, SCALE * .55);
        VBox gameSpeedSlider = new VBox();
        gameSpeedSlider.getChildren().addAll(label, slider);
        gameSpeedSlider.setTranslateX(SIZE / 20);
        gameSpeedSlider.setTranslateY(SIZE / 30);
        return gameSpeedSlider;
    }

    private StackPane createButton(String buttonLabel) {
        Rectangle shape = new Rectangle();
        shape.setWidth(WIDTH);
        shape.setHeight(SIZE / 8);
        shape.setArcWidth(SIZE / 10);
        shape.setArcHeight(SIZE / 10);
        shape.setFill(Color.ALICEBLUE);
        Text label = new Text(buttonLabel);
        scale(label, SCALE);
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

    private static void style(Text text) {
        text.setFont(Font.font("MS PGothic"));
        text.setFill(Color.ALICEBLUE);
    }

    private static void scale(Node node, double scale) {
        node.setScaleX(scale);
        node.setScaleY(scale);
    }
}
