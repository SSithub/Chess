package fastchess;

import java.util.ArrayList;

public class Moves {

    public static ArrayList<int[]> getMoves(Piece piece, ArrayList<Board> history, boolean vulnerableKing) {
        ArrayList<int[]> moves = new ArrayList<>();
        Board board = history.get(0);
        if (piece.isKing()) {
            moves.addAll(straights(piece, board, 1));
            moves.addAll(diagonals(piece, board, 1));
            //Castling
            if (!vulnerableKing) {//not needed when removing dangerous moves for the king
                if (!piece.moved) {
                    for (int dir = -1; dir <= 1; dir += 2) {
                        int i = piece.row;
                        int j = piece.col;
                        if (!spotUnderAttack(new int[]{i, j}, board, piece.white)) {
                            while (inBounds(i, j += dir)) {
                                Piece temp = board.pieces[i][j];
                                if (Math.abs(j - piece.col) < 2 && spotUnderAttack(new int[]{i, j}, board, piece.white)) {
                                    break;
                                } else if (temp != null && !temp.isRook()) {
                                    break;
                                } else if (temp != null && temp.isRook() && !temp.moved) {
                                    moves.add(new int[]{i, j, 0, 1});
                                }
                            }
                        }
                    }
                }
            }
        } else if (piece.isQueen()) {
            moves.addAll(straights(piece, board, 7));
            moves.addAll(diagonals(piece, board, 7));
        } else if (piece.isBishop()) {
            moves.addAll(diagonals(piece, board, 7));
        } else if (piece.isKnight()) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Piece temp = board.pieces[i][j];
                    if (temp != null && temp.white == piece.white) {
                        continue;
                    }
                    if (Math.abs(piece.row - i) == 2 && Math.abs(piece.col - j) == 1) {//Vertical L's
                        if (temp == null) {
                            moves.add(new int[]{i, j, 0});
                        } else {
                            moves.add(new int[]{i, j, 1});
                        }
                    } else if (Math.abs(piece.row - i) == 1 && Math.abs(piece.col - j) == 2) {//Horizontal L's
                        if (temp == null) {
                            moves.add(new int[]{i, j, 0});
                        } else {
                            moves.add(new int[]{i, j, 1});
                        }
                    }
                }
            }
        } else if (piece.isRook()) {
            moves.addAll(straights(piece, board, 7));
        } else if (piece.isPawn()) {
            final int front;
            if (piece.white) {
                front = -1;
            } else {
                front = 1;
            }
            final int frontY = piece.row + front;
            final int frontY2 = piece.row + front + front;
            if (!vulnerableKing) {
                //ONE SQUARE
                if (inBounds(frontY, piece.col) && board.pieces[frontY][piece.col] == null) {
                    moves.add(new int[]{frontY, piece.col, 0});
                    if (!piece.moved && board.pieces[frontY2][piece.col] == null) {//TWO SQUARES
                        moves.add(new int[]{frontY2, piece.col, 0});
                    }
                }
            }
            //PAWN ATTACKING DIAGONALLY
            for (int dir = -1; dir <= 1; dir += 2) {
                int x = piece.col + dir;
                if (inBounds(frontY, x)) {
                    if ((board.pieces[frontY][x] != null && board.pieces[frontY][x].white != piece.white) || vulnerableKing) {
                        moves.add(new int[]{frontY, x, 1});
                    }
                }
            }
            if (!vulnerableKing) {//not needed when removing dangerous moves for the king
                //ENPASSANT
                if (history.size() > 1) {
                    Board last = history.get(1);
                    for (int dir = -1; dir <= 1; dir += 2) {
                        int x = piece.col + dir;
                        //Check last turn for an enemy pawn in the forward 2, right or left 1 square
                        if (inBounds(frontY2, x)) {
                            Piece temp = last.pieces[frontY2][x];
                            if (temp != null && temp.isPawn() && temp.white != piece.white && !temp.moved) {
                                Piece adjacent = board.pieces[piece.row][x];
                                if (board.pieces[frontY2][x] == null && adjacent != null && adjacent.isPawn() && adjacent.white != piece.white) {
                                    moves.add(new int[]{frontY, x, 1, 2});
                                }
                            }
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid Piece");
        }
        if (!vulnerableKing) {//Should not be called when checking for moves of enemy pieces that can capture king
            return removeVulnerableMoves(piece, moves, history);
        }
        return moves;
    }

    public static Board applyMove(Piece piece, int[] move, Board board) {
        Board newBoard = board.clone();
        Piece newPiece = newBoard.pieces[piece.row][piece.col];
        if (move.length == 3) {
            move(newBoard, newPiece, move);
        } else if (move.length == 4) {//[row, col, type of special move] 
            int special = move[3];
            if (special == 1) {//Castle
                final int dir;
                if (piece.col - move[1] > 0) {
                    dir = -1;
                } else {
                    dir = 1;
                }
                Piece rook = newBoard.pieces[move[0]][move[1]];
                move(newBoard, rook, new int[]{piece.row, piece.col + dir});
                move(newBoard, newPiece, new int[]{piece.row, piece.col + dir + dir});
            } else if (special == 2) {//En Passant
                move(newBoard, newPiece, move);
                final int front;
                if (piece.white) {
                    front = -1;
                } else {
                    front = 1;
                }
                newBoard.pieces[move[0] - front][move[1]] = null;
            } else if (special > 2 && special <= 6) {
                move(newBoard, newPiece, move);
                if (special == 3) {//Queen
                    newPiece.type = 2;
                } else if (special == 4) {//Knight
                    newPiece.type = 5;
                } else if (special == 5) {//Rook
                    newPiece.type = 3;
                } else {//Bishop
                    newPiece.type = 4;
                }
            }
        }
        return newBoard;
    }

    private static void move(Board board, Piece piece, int[] move) {
        board.pieces[move[0]][move[1]] = piece;
        board.pieces[piece.row][piece.col] = null;
        piece.setPos(move[0], move[1]);
        piece.moved = true;
    }

    private static ArrayList<int[]> removeVulnerableMoves(Piece piece, ArrayList<int[]> moves, ArrayList<Board> history) {
        ArrayList<int[]> newMoves = new ArrayList<>();
        final int movesSize = moves.size();
        for (int i = 0; i < movesSize; i++) {//For each move of the piece
            Board outcome = applyMove(piece.clone(), moves.get(i), history.get(0));//Do the move
            if (!isInCheck(piece.white, outcome)) {
                newMoves.add(moves.get(i));//Add move if the king is not in check
            }
        }
        return newMoves;
    }

    public static boolean isInCheck(boolean white, Board board) {
        boolean inCheck = false;
        Piece king = board.getKing(white);
        ArrayList<Piece> enemies = board.getPieces(!white);
        int enemiesSize = enemies.size();
        ArrayList<Board> history = new ArrayList<>();
        history.add(board);
        enemies:
        for (int i = 0; i < enemiesSize; i++) {
            ArrayList<int[]> enemyMoves = getMoves(enemies.get(i), history, true);
            int enemyMovesSize = enemyMoves.size();
            for (int j = 0; j < enemyMovesSize; j++) {
                int[] move = enemyMoves.get(j);
                if (move[0] == king.row && move[1] == king.col) {
                    inCheck = true;
                    break enemies;
                }
            }
        }
        return inCheck;
    }

    public static boolean spotUnderAttack(int[] spot, Board board, boolean white) {
        ArrayList<Piece> enemies = board.getPieces(!white);
        ArrayList<Board> history = new ArrayList<>();
        history.add(board);
        int enemiesSize = enemies.size();
        for (int i = 0; i < enemiesSize; i++) {
            ArrayList<int[]> moves = Moves.getMoves(enemies.get(i), history, true);
            int movesSize = moves.size();
            for (int j = 0; j < movesSize; j++) {
                int[] move = moves.get(j);
                if (move[0] == spot[0] && move[1] == spot[1]) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ArrayList<int[]> straights(Piece piece, Board board, int limit) {
        ArrayList<int[]> moves = new ArrayList<>();
        moves.addAll(line(piece, board, -1, 0, limit));//Up
        moves.addAll(line(piece, board, 1, 0, limit));//Down
        moves.addAll(line(piece, board, 0, -1, limit));//Left
        moves.addAll(line(piece, board, 0, 1, limit));//Right
        return moves;
    }

    private static ArrayList<int[]> diagonals(Piece piece, Board board, int limit) {
        ArrayList<int[]> moves = new ArrayList<>();
        moves.addAll(line(piece, board, -1, -1, limit));//Up Left
        moves.addAll(line(piece, board, -1, 1, limit));//Up Right
        moves.addAll(line(piece, board, 1, -1, limit));//Down Left
        moves.addAll(line(piece, board, 1, 1, limit));//Down Right
        return moves;
    }

    private static boolean inBounds(int row, int col) {//In bounds of the board
        return (row < 8 && row >= 0 && col < 8 && col >= 0);
    }

    private static ArrayList<int[]> line(Piece piece, Board board, int vertical, int horizontal, int limit) {
        ArrayList<int[]> moves = new ArrayList<>();
        int i = piece.row;
        int j = piece.col;
        boolean white = piece.white;
        int count = 0;
        while (inBounds(i, j) && count < limit) {
            count++;
            if (vertical != -1 && vertical != 0 && vertical != 1) {
                throw new IllegalArgumentException("Bad vertical direction");
            }
            if (horizontal != -1 && horizontal != 0 && horizontal != 1) {
                throw new IllegalArgumentException("Bad horizontal direction");
            }
            i += vertical;
            j += horizontal;
            if (!inBounds(i, j)) {//Position inside board?
                break;
            } else if (board.pieces[i][j] == null) {//Empty? then valid move
                moves.add(new int[]{i, j, 0});
            } else if (board.pieces[i][j].white != white) {//If enemy, can't pass through but can capture
                moves.add(new int[]{i, j, 1});
                break;
            } else if (board.pieces[i][j].white == white) {//If ally, can't pass or be on tile
                break;
            }
        }
        return moves;
    }

    public static String locToString(int[] loc) {
        String x = String.valueOf(Character.toChars(97 + loc[1]));//Lowercase letters start at ascii code 97
        String y = String.valueOf(8 - loc[0]);//Reversing the computer coordinates to fit the chess coordinates for the y axis
        return x + y;
    }

    public static String moveToString(int[] move) {
        if (move.length == 2) {
            return locToString(move);
        } else if (move.length == 3) {
            String capture = "";
            if (move[2] == 1) {
                capture = "Capture ";
            }
            return capture + locToString(move);
        } else if (move.length == 4) {
            int special = move[3];
            if (special == 1) {
                if (move[1] == 0) {
                    return "Castles Left";
                } else {
                    return "Castles Right";
                }
            } else if (special == 2) {
                return "Capture Enpassant " + locToString(move);
            } else if (special >= 3 && special <= 6) {
                if (special == 3) {
                    return "Promotion to Queen";
                } else if (special == 4) {
                    return "Promotion to Knight";
                } else if (special == 5) {
                    return "Promotion to Rook";
                } else if (special == 6) {
                    return "Promotion to Bishop";
                }
            }
        }
        return "?";
    }
}
