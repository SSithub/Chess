package chess;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class Chess extends Application {

    final double SIZE = 1000;
    Group root = new Group();
    Board board = new Board(SIZE);
    Menu menu = new Menu(SIZE);
    MouseInputOverlay mouseInputOverlay = new MouseInputOverlay(SIZE);
    public static int[] inputs = null;

    void setupListeners(Scene scene) {
        scene.setOnKeyPressed((t) -> {
            if (t.getCode().equals(KeyCode.ESCAPE)) {
                if (menu.isVisible()) {
                    menu.setVisible(false);
                } else {
                    menu.setVisible(true);
                }
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        board.setupPieces();
        root.getChildren().addAll(board.tiles);
        root.getChildren().addAll(board.pieces);
        root.getChildren().addAll(mouseInputOverlay);
        root.getChildren().addAll(menu);
        Scene scene = new Scene(root, SIZE, SIZE);
        setupListeners(scene);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.setTitle("JavaFX Chess");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
