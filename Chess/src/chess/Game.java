package chess;

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
    private final HashMap<Board, Integer> boardsFreq = new HashMap<>();
    private Tile primaryTile;
    private boolean whiteTurn;
    private boolean check;
    private int[] promotionLoc;
    private final Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(16), handler -> {
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
        int[] inputs = getInputs();
        if (inputs != null) {
            int row = inputs[0];
            int col = inputs[1];
            resetInputs();
            if (promotionLoc == null) {//For promotion, must make two actions
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
                        primaryTile = null;
                        if (history.get(0).getToBePromotedPawn(whiteTurn) == null) {
                            switchTurns();
                            System.out.println(moveHistory.get(0));
                        }
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
            } else {//For promotion, must make two actions
                int x = promotionLoc[1];
                if (col == promotionLoc[1]) {
                    int y = promotionLoc[0];
                    int distance = Math.abs(row - promotionLoc[0]);
                    if (distance < 4) {
                        Board lastBoard = history.get(1);
                        final int behind;
                        if (whiteTurn) {
                            behind = 1;
                        } else {
                            behind = -1;
                        }
                        int[] move = {y, x, 0, distance + 3};
                        history.set(0, Moves.applyMove(lastBoard.getTile(y + behind, x).getPiece(), move, lastBoard));
                        moveHistory.set(0, lastBoard.getTile(y + behind, x).getPiece() + " " + Moves.moveToString(move));
                        switchTurns();
                        System.out.println(moveHistory.get(0));
                        ui.showBoard(history.get(0));
                        ui.resetMisc();
                        promotionLoc = null;
                    }
                }
            }
        }
    }

    private void switchTurns() {
        if (whiteTurn) {
            whiteTurn = false;
            ui.acceptInputs(false);
        } else {
            whiteTurn = true;
            ui.acceptInputs(true);
        }
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
        Piece pawn = history.get(0).getToBePromotedPawn(whiteTurn);
        if (pawn != null) {
            promotionLoc = new int[]{pawn.row, pawn.col};
            Group promotionMenu = Graphics.promotion(whiteTurn);
            promotionMenu.setTranslateX(pawn.getTranslateX());
            if (pawn.white == false) {//If black pawn, move promotion menu halfway down
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

    private void gameOverStalemate() {
        ui.addToUI(Graphics.stalemate(), true);
        stalemate = true;
    }

    private void gameOverCheckmate() {
        ui.addToUI(Graphics.checkmate(!whiteTurn), true);
        checkmate = true;
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
        if (moveHistory.size() >= 50) {
            for (int i = 0; i < 50; i++) {
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
}
