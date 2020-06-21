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
    private static Group inputOverlay = new Group();
    private static Group miscUI = new Group();
    public static int[] whiteInputs = null;
    public static int[] blackInputs = null;
    private Menu menu;
    private Player whitePlayer;
    private Player blackPlayer;
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
        root.getChildren().addAll(board.getTileList(), Graphics.ooordinates(), board.getPieceList(), miscUI);
    }
    
    public void showBoard(Board board) {
        root.getChildren().set(0, board.getTileList());
        root.getChildren().set(2, board.getPieceList());
    }
    
    private void initVars() {
        //Graphics
        menu = new Menu();
        whitePlayer = new Player(true);
        blackPlayer = new Player(false);
        inputOverlay.getChildren().addAll(whitePlayer, blackPlayer);
        resetMisc();
        //Start game
        game = new Game(this);
    }
    
    public void acceptInputs(boolean white) {
        if (!white) {
            whitePlayer.setVisible(false);
            blackPlayer.setVisible(true);
        } else {
            whitePlayer.setVisible(true);
            blackPlayer.setVisible(false);
        }
    }
    
    public void resetMisc() {
        miscUI.getChildren().clear();
        miscUI.getChildren().addAll(inputOverlay, menu);
    }
    
    public void addToUI(Node node, boolean overInputs) {
        if (overInputs) {
            miscUI.getChildren().add(1, node);
        } else {
            miscUI.getChildren().add(0, node);
        }
    }
    
    public void incrementScores(int stat) {
        menu.incrementScore(stat);
    }
    
    @Override
    public void start(Stage primaryStage) {
        initVars();
        acceptInputs(true);
        addGraphics(game.getCurrentBoard());
        menu.getButtons().get(0).setOnMouseClicked((t) -> {
            game.setHumanVsHuman();
            menu.setVisible(false);
        });
        menu.getButtons().get(1).setOnMouseClicked((t) -> {
            game.setHumanVsAI(false);
            menu.setVisible(false);
        });
        menu.getButtons().get(2).setOnMouseClicked((t) -> {
            game.setAIVsAI();
            menu.setVisible(false);
        });
        menu.getSlider().valueProperty().addListener((ov, t, t1) -> {
            game.setLoopRate(t1.doubleValue());
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
