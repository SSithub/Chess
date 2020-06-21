package fastchess;

import fastchess.NNLib.*;
import java.util.ArrayList;

public class AI_Custom {

    private NN nn = new NN("chessAI", 7777, .0001, LossFunction.QUADRATIC(.5), Optimizer.AMSGRAD,
            new Layer.Dense(384, 128, ActivationFunction.TANH, Initializer.XAVIER),
            new Layer.Dense(128, 64, ActivationFunction.TANH, Initializer.XAVIER),
            new Layer.Dense(64, 1, ActivationFunction.TANH, Initializer.XAVIER));
    private final double exploration = .5;

    public AI_Custom() {
//        NNLib.showInfo(NNLib.infoLayers, nn);
//        NNLib.setInfoUpdateRate(100000);
        nn.load();
    }

    public Object[] getAction(ArrayList<Board> history, boolean white) {
        if (nn.getRandom().nextDouble() < exploration) {
            return randomAction(history, white);
        } else {
            if (white) {
                return maxValuedAction(history, white);
            } else {
                return minValuedAction(history, white);
            }
        }
    }

    public void learn(int result, ArrayList<Board> history) {//1 = White win, -1 = Black win, 0 = stalemate/draw
        int historySize = history.size();
        float[][] target;
        if (result == 1) {
            target = new float[][]{{1}};
        } else if (result == 0) {
            target = new float[][]{{0}};
        } else {
            target = new float[][]{{-1}};
        }
        for (int i = 0; i < historySize; i++) {
            nn.backpropagation(boardToInputs(history.get(i)), target);
        }
        nn.save();
    }

    private Object[] randomAction(ArrayList<Board> history, boolean white) {
        Board current = history.get(0);
        ArrayList<Piece> pieces = current.getPieces(white);
        ArrayList<Object[]> availableMoves = new ArrayList<>();
        int piecesSize = pieces.size();
        for (int i = 0; i < piecesSize; i++) {//Test each piece
            Piece piece = pieces.get(i);
//            System.out.println(piece + Moves.locToString(new int[]{piece.row, piece.col}));
            ArrayList<int[]> moves = Moves.getMoves(piece, history, false);
            int movesSize = moves.size();
            for (int j = 0; j < movesSize; j++) {//Test each move of the piece
                int[] move = moves.get(j);
//                System.out.println(Moves.moveToString(move));
                Board simulated = Moves.applyMove(piece, move, current);
                if (simulated.getToBePromotedPawn(white) != null) {//If promotion
                    for (int k = 0; k < 4; k++) {//Test each promotion
                        int[] promotionMove = {move[0], move[1], move[2], k + 3};//Promotion moves start at 3
                        availableMoves.add(new Object[]{piece, promotionMove});
                    }
                } else {//No promotion
                    availableMoves.add(new Object[]{piece, move});
                }
            }
        }
        return availableMoves.get(nn.getRandom().nextInt(availableMoves.size()));
    }

    private Object[] maxValuedAction(ArrayList<Board> history, boolean white) {
        Board current = history.get(0);
        ArrayList<Piece> pieces = current.getPieces(white);
        float max = -2;
        Piece selectedPiece = null;
        int[] selectedMove = null;
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
                        int[] promotionMove = {move[0], move[1], move[2], k + 3};//Promotion moves start at 3
                        Board promotion = Moves.applyMove(piece, promotionMove, current);
                        float value = nn.feedforward(boardToInputs(promotion))[0][0];
//                        System.out.println(value);
                        if (value > max) {
                            selectedPiece = piece;
                            selectedMove = promotionMove;
                            max = value;
                        }
                    }
                } else {//No promotion
                    float value = nn.feedforward(boardToInputs(simulated))[0][0];
//                    System.out.println(value);
                    if (value > max) {
                        selectedPiece = piece;
                        selectedMove = move;
                        max = value;
                    }
                }
            }
        }
        Object[] selected = {selectedPiece, selectedMove};
        return selected;
    }

    private Object[] minValuedAction(ArrayList<Board> history, boolean white) {
        Board current = history.get(0);
        ArrayList<Piece> pieces = current.getPieces(white);
        float min = 2;
        Piece selectedPiece = null;
        int[] selectedMove = null;
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
                        int[] promotionMove = {move[0], move[1], move[2], k + 3};//Promotion moves start at 3
                        Board promotion = Moves.applyMove(piece, promotionMove, current);
                        float value = nn.feedforward(boardToInputs(promotion))[0][0];
//                        System.out.println(value);
                        if (value < min) {
                            selectedPiece = piece;
                            selectedMove = promotionMove;
                            min = value;
                        }
                    }
                } else {//No promotion
                    float value = nn.feedforward(boardToInputs(simulated))[0][0];
//                    System.out.println(value);
                    if (value < min) {
                        selectedPiece = piece;
                        selectedMove = move;
                        min = value;
                    }
                }
            }
        }
        Object[] selected = {selectedPiece, selectedMove};
        return selected;
    }

    private float[][] boardToInputs(Board board) {//Inputs into the network will be 1s and 0s, with each tile being represented by 6 numbers, for each of the possible pieces
        float[][] inputs = new float[1][384];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int indexInInputs = i * 48 + j * 6;
                for (int k = 0; k < 6; k++) {
                    inputs[0][indexInInputs + k] = 0;
                }
                Piece piece = board.pieces[i][j];
                if (piece != null) {
                    if (piece.isKing()) {
                        inputs[0][indexInInputs] = 1;
                    } else if (piece.isQueen()) {
                        inputs[0][indexInInputs + 1] = 1;
                    } else if (piece.isBishop()) {
                        inputs[0][indexInInputs + 2] = 1;
                    } else if (piece.isKnight()) {
                        inputs[0][indexInInputs + 3] = 1;
                    } else if (piece.isRook()) {
                        inputs[0][indexInInputs + 4] = 1;
                    } else if (piece.isPawn()) {
                        inputs[0][indexInInputs + 5] = 1;
                    }
                }
            }
        }
//        NNLib.print(inputs);
        return inputs;
    }
}
