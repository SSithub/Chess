package ai;

import chess.*;
import ai.NNLib.*;
import static ai.NNLib.*;
import java.util.ArrayList;

public class AI_Custom {

    private NN nn = new NN("chessAI", 0, 0, LossFunction.QUADRATIC(.5), Optimizer.ADADELTA,
            new Layer.Dense(384, 192, ActivationFunction.LEAKYRELU, Initializer.HE),
            new Layer.Dense(192, 96, ActivationFunction.LEAKYRELU, Initializer.HE),
            new Layer.Dense(96, 48, ActivationFunction.LEAKYRELU, Initializer.HE),
            new Layer.Dense(48, 24, ActivationFunction.LEAKYRELU, Initializer.HE),
            new Layer.Dense(24, 12, ActivationFunction.LEAKYRELU, Initializer.HE),
            new Layer.Dense(12, 6, ActivationFunction.LEAKYRELU, Initializer.HE),
            new Layer.Dense(6, 3, ActivationFunction.LEAKYRELU, Initializer.HE),
            new Layer.Dense(3, 1, ActivationFunction.LEAKYRELU, Initializer.HE));
    private boolean white;

    AI_Custom(boolean white) {
        this.white = white;
    }

    public Board getActionBoard(ArrayList<Board> history) {
        if (white) {
            return maxValuedAction(history);
        } else {
            return minValuedAction(history);
        }
    }

    public void learn(int result, ArrayList<Board> history) {//1 = White win, -1 = Black win, 0 = stalemate/draw
        
    }

    private Board maxValuedAction(ArrayList<Board> history) {
        Board current = history.get(0);
        ArrayList<Piece> pieces = current.getPieces(white);
        float max = Float.MIN_VALUE;
        Board maxBoard = null;
        int piecesSize = pieces.size();
        for (int i = 0; i < piecesSize; i++) {//Test each piece
            Piece piece = pieces.get(i);
            ArrayList<int[]> moves = Moves.getMoves(piece, history, false);
            int movesSize = moves.size();
            for (int j = 0; j < movesSize; j++) {//Test each move of the piece
                int[] move = moves.get(j);
                Board simulated = Moves.applyMove(piece, move, current);
                if (simulated.getToBePromotedPawn(white) != null) {//If promotion
                    for (int k = 0; k < 4; k++) {//Test each promotion
                        move[3] = k + 3;//Promotion moves start at 3;
                        Board promotion = Moves.applyMove(piece, move, current);
                        if (nn.feedforward(boardToInputs(promotion))[0][0] > max) {
                            maxBoard = promotion;
                        }
                    }
                } else {//No promotion
                    if (nn.feedforward(boardToInputs(simulated))[0][0] > max) {
                        maxBoard = simulated;
                    }
                }
            }
        }
        return maxBoard;
    }

    private Board minValuedAction(ArrayList<Board> history) {
        Board current = history.get(0);
        ArrayList<Piece> pieces = current.getPieces(white);
        float min = Float.MAX_VALUE;
        Board maxBoard = null;
        int piecesSize = pieces.size();
        for (int i = 0; i < piecesSize; i++) {//Test each piece
            Piece piece = pieces.get(i);
            ArrayList<int[]> moves = Moves.getMoves(piece, history, false);
            int movesSize = moves.size();
            for (int j = 0; j < movesSize; j++) {//Test each move of the piece
                int[] move = moves.get(j);
                Board simulated = Moves.applyMove(piece, move, current);
                if (simulated.getToBePromotedPawn(white) != null) {//If promotion
                    for (int k = 0; k < 4; k++) {//Test each promotion
                        move[3] = k + 3;//Promotion moves start at 3;
                        Board promotion = Moves.applyMove(piece, move, current);
                        if (nn.feedforward(boardToInputs(promotion))[0][0] < min) {
                            maxBoard = promotion;
                        }
                    }
                } else {//No promotion
                    if (nn.feedforward(boardToInputs(simulated))[0][0] < min) {
                        maxBoard = simulated;
                    }
                }
            }
        }
        return maxBoard;
    }

    private float[][] boardToInputs(Board board) {//Inputs into the network will be 1s and 0s, with each tile being represented by 6 numbers, for each of the possible pieces
        float[][] inputs = {{}};
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                float[][] tileInfo = {{0, 0, 0, 0, 0, 0}};
                Tile tile = board.getTile(i, j);
                if (!tile.isEmpty()) {
                    Piece piece = tile.getPiece();
                    if (piece instanceof Piece.King) {
                        tileInfo[0][0] = 1;
                    } else if (piece instanceof Piece.Queen) {
                        tileInfo[1][0] = 1;
                    } else if (piece instanceof Piece.Bishop) {
                        tileInfo[2][0] = 1;
                    } else if (piece instanceof Piece.Knight) {
                        tileInfo[3][0] = 1;
                    } else if (piece instanceof Piece.Rook) {
                        tileInfo[4][0] = 1;
                    } else if (piece instanceof Piece.Pawn) {
                        tileInfo[5][0] = 1;
                    }
                }
                inputs = append(inputs, tileInfo);
            }
        }
        return inputs;
    }
}
