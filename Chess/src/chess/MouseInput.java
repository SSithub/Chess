package chess;

import javafx.scene.shape.Rectangle;

public class MouseInput extends Rectangle {
    
    MouseInput(double windowSize) {
        this.setWidth(windowSize);
        this.setHeight(windowSize);
        this.setOnMouseClicked((t) -> {
            System.out.println(t.getSceneX());
            System.out.println(t.getSceneY());
        });
    }
}
