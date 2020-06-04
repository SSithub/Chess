package chess;

import javafx.scene.shape.Rectangle;
import static chess.Chess.inputs;

public class Player extends Rectangle {

    Player(double size) {
        this.setWidth(size);
        this.setHeight(size);
        this.setOpacity(0);
        this.setOnMousePressed((t) -> {
            int x = (int) (t.getSceneX() / size * 8);
            int y = (int) (t.getSceneY() / size * 8);
            inputs = new int[]{y, x};
        });
    }
}
