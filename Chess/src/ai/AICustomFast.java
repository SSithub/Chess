package ai;

import fastchess.*;
import ai.NNlib.*;
import java.util.ArrayList;

public class AICustomFast {

    private NN nn = ChessNN.nn;
    private final double exploration = .05;

    public AICustomFast() {
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
            nn.backpropagation(boardToInputs3d(history.get(i)), target);
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
            ArrayList<int[]> moves = Moves.getMoves(piece, history, false);
            int movesSize = moves.size();
            for (int j = 0; j < movesSize; j++) {//Test each move of the piece
                int[] move = moves.get(j);
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
                        float value = ((float[][]) nn.feedforward(boardToInputs3d(promotion)))[0][0];
//                        System.out.println(value);
                        if (value > max) {
                            selectedPiece = piece;
                            selectedMove = promotionMove;
                            max = value;
                        }
                    }
                } else {//No promotion
                    float value = ((float[][]) nn.feedforward(boardToInputs3d(simulated)))[0][0];
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
                        float value = ((float[][]) nn.feedforward(boardToInputs3d(promotion)))[0][0];
//                        System.out.println(value);
                        if (value < min) {
                            selectedPiece = piece;
                            selectedMove = promotionMove;
                            min = value;
                        }
                    }
                } else {//No promotion
                    float value = ((float[][]) nn.feedforward(boardToInputs3d(simulated)))[0][0];
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

    private float[][][] boardToInputs3d(Board board) {
        float[][][] inputs = new float[12][8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.pieces[i][j];
                if (piece != null) {
                    if (piece.white) {
                        if (piece.isKing()) {
                            inputs[0][i][j] = 1;
                        } else if (piece.isQueen()) {
                            inputs[1][i][j] = 1;
                        } else if (piece.isBishop()) {
                            inputs[2][i][j] = 1;
                        } else if (piece.isKnight()) {
                            inputs[3][i][j] = 1;
                        } else if (piece.isRook()) {
                            inputs[4][i][j] = 1;
                        } else if (piece.isPawn()) {
                            inputs[5][i][j] = 1;
                        }
                    } else {
                        if (piece.isKing()) {
                            inputs[6][i][j] = 1;
                        } else if (piece.isQueen()) {
                            inputs[7][i][j] = 1;
                        } else if (piece.isBishop()) {
                            inputs[8][i][j] = 1;
                        } else if (piece.isKnight()) {
                            inputs[9][i][j] = 1;
                        } else if (piece.isRook()) {
                            inputs[10][i][j] = 1;
                        } else if (piece.isPawn()) {
                            inputs[11][i][j] = 1;
                        }
                    }
                }
            }
        }
        return inputs;
    }

    private float[][] boardToInputs(Board board) {//Inputs into the network will be 1s and 0s, with each tile being represented by 6 numbers, for each of the possible pieces
        float[][] inputs = new float[1][768];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int indexInInputs = i * 96 + j * 12;
                for (int k = 0; k < 12; k++) {
                    inputs[0][indexInInputs + k] = 0;
                }
                Piece piece = board.pieces[i][j];
                if (piece != null) {
                    if (piece.white) {
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
                    } else {
                        if (piece.isKing()) {
                            inputs[0][indexInInputs + 6] = 1;
                        } else if (piece.isQueen()) {
                            inputs[0][indexInInputs + 7] = 1;
                        } else if (piece.isBishop()) {
                            inputs[0][indexInInputs + 8] = 1;
                        } else if (piece.isKnight()) {
                            inputs[0][indexInInputs + 9] = 1;
                        } else if (piece.isRook()) {
                            inputs[0][indexInInputs + 10] = 1;
                        } else if (piece.isPawn()) {
                            inputs[0][indexInInputs + 11] = 1;
                        }
                    }
                }
            }
        }
//        System.out.println("Board-------------------------------------------------------");
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                for (int k = 0; k < 12; k++) {
//                    System.out.print("[" + inputs[0][i * 96 + j * 12 + k] + "]");
//                }
//                System.out.println("");
//            }
//            System.out.println("next row");
//        }
//        System.out.println("End---------------------------------------------------------");
//        System.out.println("");
        return inputs;
    }
}
