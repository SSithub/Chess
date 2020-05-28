package chess;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Chess extends Application {
    
    final double windowSize = 1000;
    static Group root = new Group();
    Board board = new Board(windowSize);

    @Override
    public void start(Stage primaryStage) throws Exception {
        root.getChildren().add(board);
        board.setupPieces();
        Scene scene = new Scene(root, windowSize, windowSize);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Chess");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
