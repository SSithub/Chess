package chess;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class Chess extends Application {
    
    final static double SIZE = 1000;
    private static Group root = new Group();
    private static Group miscUI = new Group();
    public static int[] inputs = null;
    private Menu menu;
    private Player player;
    private Game game;
    
    private void setupListeners(Scene scene) {
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
    
    private void addGraphics(Board board) {
        root.getChildren().addAll(board.getTileList(), board.getPieceList(), miscUI);
    }
    
    public void showBoard(Board board) {
        root.getChildren().set(0, board.getTileList());
        root.getChildren().set(1, board.getPieceList());
    }
    
    private void initVars() {
        game = new Game(this);
        //Graphics
        menu = new Menu(SIZE);
        player = new Player(SIZE);
        miscUI.getChildren().addAll(player, menu);
    }
    
    public void resetMisc() {
        miscUI.getChildren().clear();
        miscUI.getChildren().addAll(player, menu);
    }
    
    public void addToUI(Node node, boolean overInputs) {
        if (overInputs) {
            miscUI.getChildren().add(1, node);
        } else {
            miscUI.getChildren().add(0, node);
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        initVars();
        addGraphics(game.getCurrentBoard());
        menu.getButtons().get(0).setOnMouseClicked((t) -> {
            game.resetGame();
            menu.setVisible(false);
        });
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
