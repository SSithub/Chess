package chess;

import static chess.Chess.inputs;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.util.Duration;

public class Game {

    private Chess ui;
    private ArrayList<Board> history = new ArrayList<>();
    private ArrayList<String> moveHistory = new ArrayList<>();
    private HashMap<Board, Integer> boardsFreq = new HashMap<>();
    private Tile primaryTile;
    private boolean whiteTurn;
    private boolean check;
    private int[] promotionLoc;
    private Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(16), handler -> {
        update();
    }));
    private boolean stalemate;
    private boolean checkmate;

    public Game(Chess ui) {
        this.ui = ui;
        resetGame();
        gameLoop.setCycleCount(-1);
        gameLoop.play();
    }

    private void update() {
        if (inputs != null) {
            int row = inputs[0];
            int col = inputs[1];
            inputs = null;
            if (promotionLoc == null) {
                Tile selectTile = history.get(0).getTile(row, col);
                if (primaryTile == null && !selectTile.isEmpty() && selectTile.getPiece().white == whiteTurn) {//Nothing selected and clicked on ally piece
                    primaryTile = selectTile;
                    primaryTile.setPrimary();
                    highlightMoves(Moves.getMoves(primaryTile.getPiece(), history, false));
                } else if (primaryTile != null) {//Selected a piece
                    if (selectTile.isHighlightColored()) {//Is an available move
                        int[] move = selectTile.getMove();
                        Piece primaryPiece = primaryTile.getPiece();
                        Board newBoard = Moves.applyMove(primaryPiece, move, history.get(0));
                        moveHistory.add(0, primaryPiece + " " + Moves.moveToString(move));
                        clearHighlights(history.get(0));
                        history.add(0, newBoard);
                        ui.showBoard(history.get(0));
                        storeFreqOfBoard(history.get(0));
                        whiteTurn = !whiteTurn;
                        primaryTile = null;
                        System.out.println(moveHistory.get(0));
                    } else if (!selectTile.isEmpty() && selectTile.getPiece().white == whiteTurn) {//Selected another ally piece
                        clearHighlights(history.get(0));
                        primaryTile = selectTile;
                        primaryTile.setPrimary();
                        highlightMoves(Moves.getMoves(primaryTile.getPiece(), history, false));
                    }
                }
                check();
                gameOverCheck();
                promotion();
            } else {
                int x = promotionLoc[1];
                if (col == promotionLoc[1]) {
                    int y = promotionLoc[0];
                    int distance = Math.abs(row - promotionLoc[0]);
                    if (distance < 4) {
                        Board lastBoard = history.get(1);
                        final int behind;
                        if (whiteTurn) {//Last turn would be black's
                            behind = -1;
                        } else {//Last turn would be white's
                            behind = 1;
                        }
                        int[] move = {y, x, 0, distance + 3};
                        history.set(0, Moves.applyMove(lastBoard.getTile(y + behind, x).getPiece(), move, lastBoard));
                        moveHistory.set(0, lastBoard.getTile(y + behind, x).getPiece() + " " + Moves.moveToString(move));
                        System.out.println(moveHistory.get(0));
                        ui.showBoard(history.get(0));
                        ui.resetMisc();
                        promotionLoc = null;
                    }
                }
            }
        }
    }

    private void storeFreqOfBoard(Board board) {
        if (boardsFreq.containsKey(board)) {
            boardsFreq.put(board, boardsFreq.get(board) + 1);
        } else {
            boardsFreq.put(board, 1);
        }
    }

    private void highlightMoves(ArrayList<int[]> moves) {
        int length = moves.size();
        for (int i = 0; i < length; i++) {
            int[] move = moves.get(i);
            int row = move[0];
            int col = move[1];
            Tile tile = history.get(0).getTile(row, col);
            tile.setHighlight();
            tile.setMove(move);
        }
    }

    private void clearHighlights(Board board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.getTile(i, j).clearTemps();
            }
        }
    }

    public void resetGame() {
        stalemate = false;
        checkmate = false;
        promotionLoc = null;
        history.clear();
        history.add(new Board(Chess.SIZE));
        primaryTile = null;
        whiteTurn = true;
        check = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile tile = history.get(0).getTile(i, j);
                tile.clearTemps();
                tile.clearPiece();
            }
        }
        history.get(0).setupPieces();
        storeFreqOfBoard(history.get(0));
        try {
            ui.showBoard(history.get(0));
            ui.resetMisc();
        } catch (Exception e) {

        }
    }

    private void promotion() {
        Piece pawn = history.get(0).getToBePromotedPawn(!whiteTurn);
        if (pawn != null) {
            promotionLoc = new int[]{pawn.row, pawn.col};
            Group promotionMenu = Graphics.promotion(!whiteTurn);//Called after the turn is swapped
            promotionMenu.setTranslateX(pawn.getTranslateX());
            if (pawn.white == false) {//If black pawn
                promotionMenu.setTranslateY(pawn.getFitHeight() * 4);
            }
            ui.addToUI(promotionMenu, false);
        }
    }

    private void check() {
        Board board = history.get(0);
        if (Moves.isInCheck(whiteTurn, board)) {
            Piece king = board.getKing(whiteTurn);
            board.getTile(king.row, king.col).setCheck();
            check = true;
        } else {
            check = false;
        }
    }

    private void gameOverCheck() {
        if (fivefoldRepetition()) {
            ui.addToUI(Graphics.stalemate(), true);
            stalemate = true;
        } else {
            boolean noMoves = false;
            Board board = history.get(0);
            ArrayList<Piece> pieces = board.getPieces(whiteTurn);
            int piecesSize = pieces.size();
            for (int i = 0; i < piecesSize; i++) {
                if (Moves.getMoves(pieces.get(i), history, false).size() > 0) {
                    noMoves = false;
                    break;
                } else {
                    noMoves = true;
                }
            }
            if (noMoves) {
                if (check) {//Checkmate
                    ui.addToUI(Graphics.checkmate(!whiteTurn), true);
                    checkmate = true;
                } else {//Stalemate
                    ui.addToUI(Graphics.stalemate(), true);
                    stalemate = true;
                }
            }
        }
    }

    private boolean fivefoldRepetition() {
        return boardsFreq.containsValue(5);
    }

    public Board getCurrentBoard() {
        return history.get(0);
    }

//    public ArrayList<int[]> getActions() {
//        ArrayList<int[]> actions = new ArrayList<>();
//        ArrayList<int[]> highlightedTiles = new ArrayList<>();
//        if (highlightedTiles.size() == 0) {//Either can select own pieces or 
//            for (int i = 0; i < 8; i++) {
//                for (int j = 0; j < 8; j++) {
//                    actions.add(new int[]{i, j});
//                }
//            }
//        } else {
//            return highlightedTiles;
//        }
//        return actions;
//    }
}
