package chess;

import static chess.Chess.inputs;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Logic {

    ArrayList<Tile[][]> history = new ArrayList<>();
    Tile primaryTile = null;
    boolean whiteTurn = true;
    boolean check = false;
    Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(16), handler -> {
        if (inputs != null) {
            int y = inputs[0];
            int x = inputs[1];
            
        }
    }));

    Logic() {
        gameLoop.setCycleCount(-1);
        gameLoop.play();
    }
}
