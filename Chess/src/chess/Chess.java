package chess;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Chess extends Application {

    final double WINDOWSIZE = 1000;
    Group root = new Group();
    Board board = new Board(WINDOWSIZE);
    int[] inputs = null;
    Menu menu = new Menu(WINDOWSIZE);
    Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(16), handler -> {
        if (inputs != null) {
            
        }
    }));

    @Override
    public void start(Stage primaryStage) throws Exception {
        menu.setVisible(false);
        gameLoop.setCycleCount(-1);
        gameLoop.play();
        root.getChildren().addAll(board.tiles);
        board.setupPieces();
        root.getChildren().addAll(board.pieces);
        Scene scene = new Scene(root, WINDOWSIZE, WINDOWSIZE);
        scene.setOnMouseClicked((t) -> {
            int x = (int) (t.getSceneX() / WINDOWSIZE * 8);
            int y = (int) (t.getSceneY() / WINDOWSIZE * 8);
            System.out.println(y);
            System.out.println(x);
            inputs = new int[]{y, x};
        });
        scene.setOnKeyPressed((t) -> {
            if (t.getCode().equals(KeyCode.ESCAPE)) {
                if (menu.isVisible()) {
                    menu.setVisible(false);
                } else {
                    menu.setVisible(true);
                }
            }
        });
        root.getChildren().add(menu);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Chess");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
