package chess;

import ai.AICustom;
import static chess.Chess.SIZE;
import static chess.Chess.whiteInputs;
import static chess.Chess.blackInputs;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.util.Duration;

public final class Game {
    
    private final Chess ui;
    private final ArrayList<Board> history = new ArrayList<>();
    private final ArrayList<String> moveHistory = new ArrayList<>();
    private final HashMap<BoardCompressed, Integer> boardsFreq = new HashMap<>();
    private Tile primaryTile;
    private boolean whiteTurn;
    private boolean check;
    private int[] promotionPawnLoc;
    private int[] promotionLoc;
    private Timeline gameLoop;
    private AICustom ai = new AICustom();
    private boolean aiTurn;
    private int aiTimer = 0;
    private final int AIDELAY = 30;//In frames
    private double loopRate = 1;
    private boolean trainingAI;
    
    public Game(Chess ui) {
        this.ui = ui;
        resetGame();
        setHumanVsHuman();
    }
    
    private void update(int mode) {//1 = humans, 2 = human vs ai, 3 = ai vs ai
        if (mode == 3) {
            if (aiTimer >= AIDELAY) {
                aiTimer = 0;
                AITurn();
            } else {
                aiTimer++;
            }
        } else {
            int[] inputs = getInputs();
            if (inputs != null && !aiTurn) {
                int row = inputs[0];
                int col = inputs[1];
                resetInputs();
                Board current = history.get(0);
                if (promotionLoc == null) {//For promotion, must make two actions
                    Tile selectTile = current.getTile(row, col);
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
                            addHistory(newBoard);
                            ui.showBoard(newBoard);
                            storeFreqOfBoard(newBoard);
                            Piece promotionCheck = newBoard.getToBePromotedPawn(whiteTurn);
                            if (promotionCheck == null) {
                                endTurn();
                                if (mode == 2) {
                                    aiTurn = true;
                                }
                            } else {//There is a promotion to be made
                                Piece originalPawn = primaryTile.getPiece();
                                promotionPawnLoc = new int[]{originalPawn.row, originalPawn.col};
                                promotionLoc = new int[]{promotionCheck.row, promotionCheck.col};
                                Group promotionMenu = Graphics.promotion(whiteTurn);
                                promotionMenu.setTranslateX(newBoard.getTile(promotionLoc[0], promotionLoc[1]).getTranslateX());
                                if (originalPawn.white == false) {//If black pawn, move promotion menu halfway down
                                    promotionMenu.setTranslateY(SIZE / 2);
                                }
                                ui.addToUI(promotionMenu, false);
                            }
                            primaryTile = null;
                        } else if (!selectTile.isEmpty() && selectTile.getPiece().white == whiteTurn) {//Selected another ally piece
                            clearHighlights(current);
                            primaryTile = selectTile;
                            primaryTile.setPrimary();
                            highlightMoves(Moves.getMoves(primaryTile.getPiece(), history, false));
                        }
                    }
                } else {//For promotion, must make two actions
                    int moveX = promotionLoc[1];
                    if (col == promotionLoc[1]) {
                        int moveY = promotionLoc[0];
                        int distance = Math.abs(row - promotionLoc[0]);
                        if (distance < 4) {
                            int pawnX = promotionPawnLoc[1];
                            int pawnY = promotionPawnLoc[0];
                            Board lastBoard = history.get(1);
                            Piece originalPawn = lastBoard.getTile(pawnY, pawnX).getPiece();
                            int[] move = {moveY, moveX, 0, distance + 3};
                            if (!lastBoard.getTile(moveY, moveX).isEmpty()) {//Capture
                                move[2] = 1;
                            }
                            history.set(0, Moves.applyMove(originalPawn, move, lastBoard));
                            moveHistory.set(0, originalPawn + " " + Moves.moveToString(move));
                            endTurn();
                            ui.showBoard(history.get(0));
                            ui.resetMisc();
                            promotionLoc = null;
                            if (mode == 2) {
                                aiTurn = true;
                            }
                        }
                    }
                }
            } else if (aiTurn) {
                if (aiTimer >= AIDELAY) {
                    aiTimer = 0;
                    AITurn();
                } else {
                    aiTimer++;
                }
            }
        }
    }
    
    private void AITurn() {
        Object[] action = ai.getAction(history, whiteTurn);
        Piece selectedPiece = (Piece) action[0];
        int[] selectedMove = (int[]) action[1];
        Board newBoard = Moves.applyMove(selectedPiece, selectedMove, history.get(0));
        addHistory(newBoard);
        storeFreqOfBoard(newBoard);
        moveHistory.add(0, selectedPiece + " " + Moves.moveToString(selectedMove));
        endTurn();
        aiTurn = false;
        ui.showBoard(newBoard);
    }
    
    public void setHumanVsHuman() {
        resetGame();
        trainingAI = false;
        try {
            gameLoop.stop();
        } catch (Exception e) {
            
        }
        gameLoop = new Timeline(new KeyFrame(Duration.millis(16), handler -> {
            update(1);
        }));
        gameLoop.setCycleCount(-1);
        gameLoop.setRate(loopRate);
        gameLoop.play();
    }
    
    public void setHumanVsAI(boolean white) {
        resetGame();
        trainingAI = false;
        try {
            gameLoop.stop();
        } catch (Exception e) {
            
        }
        if (white) {
            AITurn();
        }
        gameLoop = new Timeline(new KeyFrame(Duration.millis(16), handler -> {
            update(2);
        }));
        gameLoop.setCycleCount(-1);
        gameLoop.setRate(loopRate);
        gameLoop.play();
    }
    
    public void setAIVsAI() {
        resetGame();
        trainingAI = true;
        try {
            gameLoop.stop();
        } catch (Exception e) {
            
        }
        gameLoop = new Timeline(new KeyFrame(Duration.millis(16), handler -> {
            update(3);
        }));
        gameLoop.setCycleCount(-1);
        gameLoop.setRate(loopRate);
        gameLoop.play();
    }
    
    private void endTurn() {
        if (whiteTurn) {
            whiteTurn = false;
            ui.acceptInputs(false);
        } else {
            whiteTurn = true;
            ui.acceptInputs(true);
        }
        System.out.println(moveHistory.get(0));
        check();
        gameOverCheck();
    }
    
    private void addHistory(Board board) {
//        if (history.size() > 2) {
//            history.remove(history.size() - 1);
//        }
        history.add(0, board);
    }
    
    private int[] getInputs() {
        if (whiteTurn) {
            return whiteInputs;
        } else {
            return blackInputs;
        }
    }
    
    private void resetInputs() {
        whiteInputs = null;
        blackInputs = null;
    }
    
    private void storeFreqOfBoard(Board board) {
        BoardCompressed compressed = board.compress();
        if (boardsFreq.containsKey(compressed)) {
            boardsFreq.put(compressed, boardsFreq.get(compressed) + 1);
        } else {
            boardsFreq.put(compressed, 1);
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
        promotionLoc = null;
        promotionPawnLoc = null;
        boardsFreq.clear();
        moveHistory.clear();
        history.clear();
        history.add(new Board());
        primaryTile = null;
        whiteTurn = true;
        ui.acceptInputs(whiteTurn);
        check = false;
        aiTurn = false;
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
    
    private void gameOverStalemate() {
        ui.addToUI(Graphics.stalemate(), true);
        ui.incrementScores(3);
        gameLoop.stop();
        if (trainingAI) {
            ai.learn(0, history);
            setAIVsAI();
        }
    }
    
    private void gameOverCheckmate() {
        ui.addToUI(Graphics.checkmate(!whiteTurn), true);
        if (!whiteTurn) {
            ui.incrementScores(1);
        } else {
            ui.incrementScores(2);
        }
        gameLoop.stop();
        if (trainingAI) {
            if (whiteTurn) {
                ai.learn(-1, history);
            } else {
                ai.learn(1, history);
            }
            setAIVsAI();
        }
    }
    
    private void gameOverCheck() {
        if (noMoves(whiteTurn)) {//Basic gameover rules
            if (check) {//Checkmate
                gameOverCheckmate();
            } else {//Stalemate
                System.out.println("No Valid Moves");
                gameOverStalemate();
            }
        } else if (fivefoldRepetitionRule()) {
            System.out.println("Fivefold Repetition");
            gameOverStalemate();
        } else if (fiftyMoveRule()) {
            System.out.println("Fifty moves without captures or moved pawns");
            gameOverStalemate();
        } else if (insufficientMaterial()) {
            System.out.println("Insufficient material");
            gameOverStalemate();
        }
    }
    
    private boolean noMoves(boolean white) {
        Board board = history.get(0);
        ArrayList<Piece> pieces = board.getPieces(white);
        int piecesSize = pieces.size();
        for (int i = 0; i < piecesSize; i++) {
            if (Moves.getMoves(pieces.get(i), history, false).size() > 0) {
                return false;
            }
        }
        return true;
    }
    
    private boolean fivefoldRepetitionRule() {
        return boardsFreq.containsValue(5);
    }
    
    private boolean fiftyMoveRule() {
        if (moveHistory.size() >= 100) {
            for (int i = 0; i < 100; i++) {
                String move = moveHistory.get(i);
                if (move.contains("Capture") || move.contains("Pawn")) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean insufficientMaterial() {
        Board board = history.get(0);
        ArrayList<Piece> white = board.getPieces(true);
        ArrayList<Piece> black = board.getPieces(false);
        if (white.size() == 1 && black.size() == 1) {//1 vs 1
            if (white.get(0) instanceof Piece.King && black.get(0) instanceof Piece.King) {//King vs King
                return true;
            }
        }
        for (int i = 0; i < 2; i++) {
            ArrayList<Piece> pieces1;
            ArrayList<Piece> pieces2;
            if (i == 0) {
                pieces1 = white;
                pieces2 = black;
            } else {
                pieces1 = black;
                pieces2 = white;
            }
            int pieces1Size = pieces1.size();
            int pieces2Size = pieces2.size();
            if (pieces1Size == 1 && pieces2Size == 2) {//1 vs 2
                //King vs King and Bishop
                if (pieces1.get(0) instanceof Piece.King
                        && (pieces2.get(0) instanceof Piece.King && pieces2.get(1) instanceof Piece.Bishop)
                        || (pieces2.get(0) instanceof Piece.King && pieces2.get(1) instanceof Piece.Bishop)) {
                    return true;
                } //King vs King and Knight
                else if (pieces1.get(0) instanceof Piece.King
                        && (pieces2.get(0) instanceof Piece.King && pieces2.get(1) instanceof Piece.Knight)
                        || (pieces2.get(1) instanceof Piece.King && pieces2.get(0) instanceof Piece.Knight)) {
                    return true;
                }
            }
        }
        if (white.size() == 2 && black.size() == 2) {
            //King and Bishop against King and Bishop, Bishops on same color squares
            if ((white.get(0) instanceof Piece.King && white.get(1) instanceof Piece.Bishop)
                    || (white.get(1) instanceof Piece.King && white.get(0) instanceof Piece.Bishop)
                    && (black.get(0) instanceof Piece.King && black.get(1) instanceof Piece.Bishop)
                    || (black.get(1) instanceof Piece.King && black.get(0) instanceof Piece.Bishop)) {
                Piece whiteBishop;
                Piece blackBishop;
                if (white.get(0) instanceof Piece.Bishop) {
                    whiteBishop = white.get(0);
                } else {
                    whiteBishop = white.get(1);
                }
                if (black.get(0) instanceof Piece.Bishop) {
                    blackBishop = black.get(0);
                } else {
                    blackBishop = black.get(1);
                }
                if ((whiteBishop.row + whiteBishop.col) % 2 == (blackBishop.row + blackBishop.col) % 2) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Board getCurrentBoard() {
        return history.get(0);
    }
    
    public void setLoopRate(double rate) {
        gameLoop.setRate(rate);
        loopRate = rate;
    }
}
