package chess;

import java.util.ArrayList;

public class Moves {

    public static ArrayList<int[]> getMoves(Piece piece, ArrayList<Board> history, boolean vulnerableKing) {
        ArrayList<int[]> moves = new ArrayList<>();
        Board board = history.get(0);
        if (piece instanceof Piece.King) {
            moves.addAll(straights(piece, board, 1));
            moves.addAll(diagonals(piece, board, 1));
            //Castling
            if (!vulnerableKing) {//not needed when removing dangerous moves for the king
                if (piece.notMoved) {
                    for (int dir = -1; dir <= 1; dir += 2) {
                        int i = piece.row;
                        int j = piece.col;
                        while (inBounds(i, j += dir)) {
                            Tile tile = board.getTile(i, j);
                            if (Math.abs(j - piece.col) < 2 && spotUnderAttack(new int[]{i, j}, board, piece.white)) {
                                break;
                            } else if (!tile.isEmpty() && !(tile.getPiece() instanceof Piece.Rook)) {
                                break;
                            } else if (tile.getPiece() instanceof Piece.Rook && tile.getPiece().notMoved) {
                                moves.add(new int[]{i, j, 0, 1});
                            }
                        }
                    }
                }
            }
        } else if (piece instanceof Piece.Queen) {
            moves.addAll(straights(piece, board, 7));
            moves.addAll(diagonals(piece, board, 7));
        } else if (piece instanceof Piece.Bishop) {
            moves.addAll(diagonals(piece, board, 7));
        } else if (piece instanceof Piece.Knight) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Tile tile = board.getTile(i, j);
                    if (tile.getPiece() != null && tile.getPiece().white == piece.white) {
                        continue;
                    }
                    if (Math.abs(piece.row - i) == 2 && Math.abs(piece.col - j) == 1) {//Vertical L's
                        if (tile.isEmpty()) {
                            moves.add(new int[]{i, j, 0});
                        } else {
                            moves.add(new int[]{i, j, 1});
                        }
                    } else if (Math.abs(piece.row - i) == 1 && Math.abs(piece.col - j) == 2) {//Horizontal L's
                        if (tile.isEmpty()) {
                            moves.add(new int[]{i, j, 0});
                        } else {
                            moves.add(new int[]{i, j, 1});
                        }
                    }
                }
            }
        } else if (piece instanceof Piece.Rook) {
            moves.addAll(straights(piece, board, 7));
        } else if (piece instanceof Piece.Pawn) {
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
                if (inBounds(frontY, piece.col) && board.getTile(frontY, piece.col).isEmpty()) {
                    moves.add(new int[]{frontY, piece.col, 0});
                    if (piece.notMoved && board.getTile(frontY2, piece.col).isEmpty()) {//TWO SQUARES
                        moves.add(new int[]{frontY2, piece.col, 0});
                    }
                }
            }
            //PAWN ATTACKING DIAGONALLY
            for (int dir = -1; dir <= 1; dir += 2) {
                int x = piece.col + dir;
                if (inBounds(frontY, x)) {
                    if ((!board.getTile(frontY, x).isEmpty() && board.getTile(frontY, x).getPiece().white != piece.white) || vulnerableKing) {
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
                            Tile tile = last.getTile(frontY2, x);
                            if (!tile.isEmpty() && tile.getPiece() instanceof Piece.Pawn && tile.getPiece().white != piece.white && tile.getPiece().notMoved) {
                                Tile adjacent = board.getTile(piece.row, x);
                                if (board.getTile(frontY2, x).isEmpty() && !adjacent.isEmpty() && adjacent.getPiece() instanceof Piece.Pawn && adjacent.getPiece().white != piece.white) {
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
        if (move.length == 3) {//[row, col]
            Tile moveTile = newBoard.getTile(move[0], move[1]);
            Tile pieceTile = newBoard.getTile(piece.row, piece.col);
            if (!moveTile.isEmpty()) {
                moveTile.getPiece().setVisible(false);
            }
            moveTile.placePiece(pieceTile.getPiece());
            pieceTile.clearPiece();
        } else if (move.length == 4) {//[row, col, type of special move] 
            int special = move[3];
            if (special == 1) {//Castle
                final int dir;
                if (piece.col - move[1] > 0) {
                    dir = -1;
                } else {
                    dir = 1;
                }
                Tile rookTile = newBoard.getTile(move[0], move[1]);
                Tile kingTile = newBoard.getTile(piece.row, piece.col);
                newBoard.getTile(piece.row, piece.col + dir).placePiece(rookTile.getPiece());
                newBoard.getTile(piece.row, piece.col + dir + dir).placePiece(kingTile.getPiece());
                rookTile.clearPiece();
                kingTile.clearPiece();
            } else if (special == 2) {//En Passant
                Tile moveTile = newBoard.getTile(move[0], move[1]);
                Tile pawnTile = newBoard.getTile(piece.row, piece.col);
                final int front;
                if (piece.white) {
                    front = -1;
                } else {
                    front = 1;
                }
                Tile enemyPawnTile = newBoard.getTile(move[0] - front, move[1]);
                moveTile.placePiece(piece);
                pawnTile.clearPiece();
                enemyPawnTile.getPiece().setVisible(false);
                enemyPawnTile.clearPiece();
            } else if (special > 2 && special <= 6) {
                Tile moveTile = newBoard.getTile(move[0], move[1]);
                newBoard.getTile(piece.row, piece.col).clearPiece();
                piece.setVisible(false);
                Piece promotedPiece;
                if (special == 3) {//Queen
                    promotedPiece = new Piece.Queen(piece.white);
                } else if (special == 4) {//Knight
                    promotedPiece = new Piece.Knight(piece.white);
                } else if (special == 5) {//Rook
                    promotedPiece = new Piece.Rook(piece.white);
                } else {//Bishop
                    promotedPiece = new Piece.Bishop(piece.white);
                }
                moveTile.placePiece(promotedPiece);
            }
        }
        return newBoard;
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
            } else if (board.getTile(i, j).getPiece() == null) {//Empty? then valid move
                moves.add(new int[]{i, j, 0});
            } else if (board.getTile(i, j).getPiece().white != white) {//If enemy, can't pass through but can capture
                moves.add(new int[]{i, j, 1});
                break;
            } else if (board.getTile(i, j).getPiece().white == white) {//If ally, can't pass or be on tile
                break;
            }
        }
        return moves;
    }

    public static String moveToString(int[] move) {
        if (move.length == 2) {
            String x = String.valueOf(Character.toChars(97 + move[1]));//Lowercase letters start at ascii code 97
            String y = String.valueOf(8 - move[0]);//Reversing the computer coordinates to fit the chess coordinates for the y axis
            return x + y;
        } else if (move.length == 3) {
            String x = String.valueOf(Character.toChars(97 + move[1]));//Lowercase letters start at ascii code 97
            String y = String.valueOf(8 - move[0]);//Reversing the computer coordinates to fit the chess coordinates for the y axis
            String capture = "";
            if (move[2] == 1) {
                capture = "Capture ";
            }
            return capture + x + y;
        } else if (move.length == 4) {
            int special = move[3];
            if (special == 1) {
                if (move[1] == 0) {
                    return "Castles Left";
                } else {
                    return "Castles Right";
                }
            } else if (special == 2) {
                String x = String.valueOf(Character.toChars(97 + move[1]));
                String y = String.valueOf(8 - move[0]);
                return "Capture Enpassant " + x + y;
            } else if (special > 2 && special <= 6) {
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
