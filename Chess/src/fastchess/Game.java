package fastchess;

import ai.AICustomFast;
import static fastchess.FastChess.BOARDPRINTING;
import static fastchess.FastChess.GAMEOVERPRINTING;
import static fastchess.FastChess.TRAINING;
import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    public AICustomFast ai = new AICustomFast();
    public ArrayList<Board> history = new ArrayList<>();
    public HashMap<Board, Integer> boardFreq = new HashMap<>();
    public ArrayList<String> moveHistory = new ArrayList<>();
    public boolean whiteTurn;
    public boolean check;
    public int whiteWins = 0;
    public int blackWins = 0;
    public int stalemates = 0;
    public boolean gameOver = false;

    public void newGame() {
        reset();
        if (BOARDPRINTING) {
            System.out.println(history.get(0));
        }
        while (!gameOver) {
            AITurn();
        }
    }

    public void reset() {
        history.clear();
        boardFreq.clear();
        moveHistory.clear();
        Board start = new Board();
        start.setup();
        history.add(start);
        whiteTurn = true;
        check = false;
        gameOver = false;
    }

    public void AITurn() {
        Object[] action = ai.getAction(history, whiteTurn);
        Piece selectedPiece = (Piece) action[0];
        int[] selectedMove = (int[]) action[1];
        Board newBoard = Moves.applyMove(selectedPiece, selectedMove, history.get(0));
        history.add(0, newBoard);
        storeFreqOfBoard(newBoard);
        moveHistory.add(0, selectedPiece + " " + Moves.moveToString(selectedMove));
        if (BOARDPRINTING) {
            System.out.println(moveHistory.get(0));
            System.out.println(history.get(0));
        }
        endTurn();
    }

    private void storeFreqOfBoard(Board board) {
        if (boardFreq.containsKey(board)) {
            boardFreq.put(board, boardFreq.get(board) + 1);
        } else {
            boardFreq.put(board, 1);
        }
    }

    private void endTurn() {
        whiteTurn = !whiteTurn;
        check();
        gameOverCheck();
    }

    private void check() {
        Board board = history.get(0);
        if (Moves.isInCheck(whiteTurn, board)) {
            check = true;
        } else {
            check = false;
        }
    }

    private void gameOverCheck() {
        if (noMoves(whiteTurn)) {//Basic gameover rules
            if (check) {//Checkmate
                gameOverCheckmate();
            } else {//Stalemate
                if (GAMEOVERPRINTING) {
                    System.out.print("No Valid Moves ");
                }
                gameOverStalemate();
            }
        } else if (fivefoldRepetitionRule()) {
            if (GAMEOVERPRINTING) {
                System.out.print("Fivefold Repetition ");
            }
            gameOverStalemate();
        } else if (fiftyMoveRule()) {
            if (GAMEOVERPRINTING) {
                System.out.print("Fifty moves without captures or moved pawns ");
            }
            gameOverStalemate();
        } else if (insufficientMaterial()) {
            if (GAMEOVERPRINTING) {
                System.out.print("Insufficient material ");
            }
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

    private void gameOverCheckmate() {
        if (!whiteTurn) {
            whiteWins++;
            if (GAMEOVERPRINTING) {
                System.out.println("WHITE WINS");
            }
            if (TRAINING) {
                ai.learn(1, history);
            }
        } else {
            blackWins++;
            if (GAMEOVERPRINTING) {
                System.out.println("BLACK WINS");
            }
            if (TRAINING) {
                ai.learn(-1, history);
            }
        }
        gameOver = true;
    }

    private void gameOverStalemate() {
        if (GAMEOVERPRINTING) {
            System.out.println("STALEMATE");
        }
        stalemates++;
        if (TRAINING) {
            ai.learn(0, history);
        }
        gameOver = true;
    }

    private boolean fivefoldRepetitionRule() {
        return boardFreq.containsValue(5);
    }

    private boolean fiftyMoveRule() {
        if (moveHistory.size() >= 100) {
            for (int i = 0; i < 100; i++) {
                String move = moveHistory.get(i);
                if (move.contains("Capture") || move.contains("P")) {
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
            if (white.get(0).isKing() && black.get(0).isKing()) {//King vs King
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
                if (pieces1.get(0).isKing()
                        && (pieces2.get(0).isKing() && pieces2.get(1).isBishop())
                        || (pieces2.get(0).isKing() && pieces2.get(1).isBishop())) {
                    return true;
                } //King vs King and Knight
                else if (pieces1.get(0).isKing()
                        && (pieces2.get(0).isKing() && pieces2.get(1).isKnight())
                        || (pieces2.get(1).isKing() && pieces2.get(0).isKnight())) {
                    return true;
                }
            }
        }
        if (white.size() == 2 && black.size() == 2) {
            //King and Bishop against King and Bishop, Bishops on same color squares
            if ((white.get(0).isKing() && white.get(1).isBishop())
                    || (white.get(1).isKing() && white.get(0).isBishop())
                    && (black.get(0).isKing() && black.get(1).isBishop())
                    || (black.get(1).isKing() && black.get(0).isBishop())) {
                Piece whiteBishop;
                Piece blackBishop;
                if (white.get(0).isBishop()) {
                    whiteBishop = white.get(0);
                } else {
                    whiteBishop = white.get(1);
                }
                if (black.get(0).isBishop()) {
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

}
