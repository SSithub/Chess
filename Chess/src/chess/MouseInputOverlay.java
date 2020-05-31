package chess;

import javafx.scene.shape.Rectangle;
import static chess.Chess.inputs;

public class MouseInputOverlay extends Rectangle {

    MouseInputOverlay(double size) {
        this.setWidth(size);
        this.setHeight(size);
        this.setOpacity(0);
        this.setOnMouseClicked((t) -> {
            int x = (int) (t.getSceneX() / size * 8);
            int y = (int) (t.getSceneY() / size * 8);
            System.out.println(y);
            System.out.println(x);
            inputs = new int[]{y, x};
        });
    }
}
