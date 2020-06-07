package chess;

import static chess.Chess.SIZE;
import javafx.scene.shape.Rectangle;
import static chess.Chess.whiteInputs;
import static chess.Chess.blackInputs;

public class Player extends Rectangle {

    Player(boolean white) {
        this.setWidth(SIZE);
        this.setHeight(SIZE);
        this.setOpacity(0);
        if (white) {
            this.setOnMousePressed((t) -> {
                int x = (int) (t.getSceneX() / SIZE * 8);
                int y = (int) (t.getSceneY() / SIZE * 8);
                whiteInputs = new int[]{y, x};
            });
        } else {
            this.setOnMousePressed((t) -> {
                int x = (int) (t.getSceneX() / SIZE * 8);
                int y = (int) (t.getSceneY() / SIZE * 8);
                blackInputs = new int[]{y, x};
            });
        }
    }
}
