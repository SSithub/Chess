package old;

//package chess;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.util.ArrayList;
//import java.util.Comparator;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.image.WritableImage;
//
//public abstract class Piece_Old extends ImageView {
//
//    static Image spriteSheet;
//    String type;
//    boolean white;
//    int row;
//    int col;
//    boolean notMoved;
//
//    public Piece_Old() {
//        try {
//            FileInputStream inputStream = new FileInputStream(System.getProperty("user.dir") + File.separator + "src" + File.separator + "chess" + File.separator + "Sprites.png");
//            spriteSheet = new Image(inputStream);
//        } catch (FileNotFoundException e) {
//        }
//        notMoved = true;
//    }
//
//    public void setPosAndSize(double x, double y, double size) {
//        this.setTranslateX(x);
//        this.setTranslateY(y);
//        this.setFitWidth(size);
//        this.setFitHeight(size);
//        row = (int) (this.getTranslateY() / size);
//        col = (int) (this.getTranslateX() / size);
//    }
//
//    private static int white(boolean white) {//For the sprite sheet
//        if (white) {
//            return 0;
//        } else {
//            return 320;
//        }
//    }
//
//    private static boolean inBounds(int row, int col) {//In bounds of the board
//        return (row < 8 && row >= 0 && col < 8 && col >= 0);
//    }
//
//    private static ArrayList<int[]> line(int row, int col, Tile[][] board, boolean white, int vertical, int horizontal, int limit) {
//        ArrayList<int[]> moves = new ArrayList<>();
//        int i = row;
//        int j = col;
//        int count = 0;
//        while (inBounds(i, j) && count < limit) {
//            count++;
//            if (vertical != -1 && vertical != 0 && vertical != 1) {
//                throw new IllegalArgumentException("Bad vertical direction");
//            }
//            if (horizontal != -1 && horizontal != 0 && horizontal != 1) {
//                throw new IllegalArgumentException("Bad horizontal direction");
//            }
//            i += vertical;
//            j += horizontal;
//            if (!inBounds(i, j)) {//Position inside board?
//                break;
//            } else if (board[i][j].piece == null) {//Empty? then valid move
//                moves.add(new int[]{i, j});
//            } else if (board[i][j].piece.white != white) {//If enemy, can't pass through but can capture
//                moves.add(new int[]{i, j});
//                break;
//            } else if (board[i][j].piece.white == white) {//If ally, can't pass or be on tile
//                break;
//            }
//        }
//        return moves;
//    }
//
//    private static ArrayList<int[]> straights(int row, int col, Tile[][] board, boolean white, int limit) {
//        ArrayList<int[]> moves = new ArrayList<>();
//        moves.addAll(line(row, col, board, white, -1, 0, limit));//Up
//        moves.addAll(line(row, col, board, white, 1, 0, limit));//Down
//        moves.addAll(line(row, col, board, white, 0, -1, limit));//Left
//        moves.addAll(line(row, col, board, white, 0, 1, limit));//Right
//        return moves;
//    }
//
//    private static ArrayList<int[]> diagonals(int row, int col, Tile[][] board, boolean white, int limit) {
//        ArrayList<int[]> moves = new ArrayList<>();
//        moves.addAll(line(row, col, board, white, -1, -1, limit));//Up Left
//        moves.addAll(line(row, col, board, white, -1, 1, limit));//Up Right
//        moves.addAll(line(row, col, board, white, 1, -1, limit));//Down Left
//        moves.addAll(line(row, col, board, white, 1, 1, limit));//Down Right
//        return moves;
//    }
//
//    static ArrayList<int[]> removeMovesCausingCheck(ArrayList<Tile[][]> history, Piece_Old primary, ArrayList<int[]> moves) {
//        Tile[][] board = history.get(0);
//        boolean white = primary.white;
//        King king = null;
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                Piece_Old piece = board[i][j].piece;
//                if (piece != null && piece instanceof King && piece.white == white) {
//                    king = (King) piece;
//                    break;
//                }
//            }
//        }
//        int movesSize = moves.size();
//        for (int i = 0; i < movesSize; i++) {
//            int[] move = moves.get(i);
//            //Simulate Move
//            Tile[][] simulation = Board_Old.copyBoard(board);
//            simulation[primary.row][primary.col].piece = null;
//            Tile tileForMove = simulation[move[0]][move[1]];
//            tileForMove.placePiece(primary);
//            //Check if any enemy can take the King
//            for (int j = 0; j < 8; j++) {
//                for (int k = 0; k < 8; k++) {
//                    Piece_Old piece = simulation[j][k].piece;
//                    if (piece.white != white) {
//                        //Check each enemy move for a King capture
//                        ArrayList<int[]> enemyMoves = piece.availableMoves(history);
//                        int enemyMovesSize = enemyMoves.size();
//                        for (int l = 0; l < enemyMovesSize; l++) {
//                            int[] enemyMove = enemyMoves.get(l);
//                        }
//                    }
//                }
//            }
//        }
//        return moves;
//    }
//
//    public abstract ArrayList<int[]> availableMoves(ArrayList<Tile[][]> history);
//
//    public abstract Piece_Old clone();
//
//    static class King extends Piece_Old {
//
//        King(boolean white) {
//            type = "king";
//            this.white = white;
//            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 0, white(white), 320, 320));
//        }
//
//        @Override
//        public ArrayList<int[]> availableMoves(ArrayList<Tile[][]> history) {
//            ArrayList<int[]> moves = new ArrayList<>();
//            Tile[][] current = history.get(0);
//            moves.addAll(kingMoves(current));
//            Tile[][] alteredBoard = Board_Old.copyBoard(current);
//            for (int i = 0; i < moves.size(); i++) {//Remove enemies that can be captured by the king for accurate danger squares
//                int[] move = moves.get(i);
//                alteredBoard[move[0]][move[1]].piece = null;
//            }
//            history.set(0, alteredBoard);//Replace current board with the altered board and change it back after checking for danger squares
//            ArrayList<int[]> captures = new ArrayList<>();//List of possible captures
//            for (int i = 0; i < 8; i++) {//Loop through enemy pieces and add to list of captures
//                for (int j = 0; j < 8; j++) {
//                    Piece_Old piece = current[i][j].piece;//Use the current board to make sure all pieces are accounted for
//                    if (piece != null && piece.white != this.white) {//Is an enemy piece
//                        if (piece instanceof Piece_Old.Pawn) {//Pawns have moves that are not captures
//                            Pawn pawn = (Pawn) piece;
//                            final int front;
//                            if (pawn.white) {
//                                front = -1;
//                            } else {
//                                front = 1;
//                            }
//                            captures.addAll(pawn.diagonalAttack(alteredBoard, front, true, false));
//                            captures.addAll(pawn.diagonalAttack(alteredBoard, front, false, false));
//                        } else if (piece instanceof Piece_Old.King) {//Prevent StackOverflow exception
//                            captures.addAll(((King) piece).kingMoves(alteredBoard));
//                        } else {//Rest of the pieces
//                            captures.addAll(piece.availableMoves(history));
//                        }
//                    }
//                }
//            }
//            history.set(0, current);//Replace altered board with the actual current board
//            //Remove from moves list that has the same location as a location in captures
//            ArrayList<Integer> toBeRemoved = new ArrayList<>();
//            for (int i = 0; i < moves.size(); i++) {//For each king move
//                for (int j = 0; j < captures.size(); j++) {//For each possible capture
//                    if (moves.get(i)[0] == captures.get(j)[0] && moves.get(i)[1] == captures.get(j)[1]) {//Indices match
//                        toBeRemoved.add(i);
//                        break;
//                    }
//                }
//            }
//            toBeRemoved.sort(Comparator.naturalOrder());
//            toBeRemoved.sort(Comparator.reverseOrder());//Remove moves going from last in the list to 
//            for (int i = 0; i < toBeRemoved.size(); i++) {
//                moves.remove((int) toBeRemoved.get(i));
//            }
//            //CASTLING
//            moves.addAll(castling(current));
//            return moves;
//        }
//
//        ArrayList<int[]> kingMoves(Tile[][] board) {
//            ArrayList<int[]> moves = new ArrayList<>();
//            moves.addAll(straights(row, col, board, white, 1));
//            moves.addAll(diagonals(row, col, board, white, 1));
//            return moves;
//        }
//
//        ArrayList<int[]> castling(Tile[][] board) {
//            ArrayList<int[]> moves = new ArrayList<>();
//            //Looking for Rook to the right
//            int i = row;
//            int j = col;
//            while (inBounds(i, ++j)) {
//                Piece_Old piece = board[i][j].piece;
//                if (piece != null && piece instanceof Rook && piece.notMoved) {
//                    moves.add(new int[]{i, j, 2});
//                } else if (piece != null) {
//                    break;
//                }
//            }
//            //Looking for Rook to the left
//            i = row;
//            j = col;
//            while (inBounds(i, --j)) {
//                Piece_Old piece = board[i][j].piece;
//                if (piece != null && piece instanceof Rook && piece.notMoved) {
//                    moves.add(new int[]{i, j, 2});
//                } else if (piece != null) {
//                    break;
//                }
//            }
//            return moves;
//        }
//
//        @Override
//        public Piece_Old clone() {
//            Piece_Old clone = new King(this.white);
//            clone.notMoved = this.notMoved;
//            clone.row = this.row;
//            clone.col = this.col;
//            return clone;
//        }
//    }
//
//    static class Queen extends Piece_Old {
//
//        Queen(boolean white) {
//            type = "queen";
//            this.white = white;
//            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 320, white(white), 320, 320));
//        }
//
//        @Override
//        public ArrayList<int[]> availableMoves(ArrayList<Tile[][]> history) {
//            ArrayList<int[]> moves = new ArrayList<>();
//            Tile[][] current = history.get(0);
//            moves.addAll(diagonals(row, col, current, white, 7));
//            moves.addAll(straights(row, col, current, white, 7));
//            return moves;
//        }
//
//        @Override
//        public Piece_Old clone() {
//            Piece_Old clone = new Queen(this.white);
//            clone.notMoved = this.notMoved;
//            clone.row = this.row;
//            clone.col = this.col;
//            return clone;
//        }
//    }
//
//    static class Bishop extends Piece_Old {
//
//        Bishop(boolean white) {
//            type = "bishop";
//            this.white = white;
//            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 640, white(white), 320, 320));
//        }
//
//        @Override
//        public ArrayList<int[]> availableMoves(ArrayList<Tile[][]> history) {
//            ArrayList<int[]> moves = new ArrayList<>();
//            moves.addAll(diagonals(row, col, history.get(0), white, 7));
//            return moves;
//        }
//
//        @Override
//        public Piece_Old clone() {
//            Piece_Old clone = new Bishop(this.white);
//            clone.notMoved = this.notMoved;
//            clone.row = this.row;
//            clone.col = this.col;
//            return clone;
//        }
//    }
//
//    static class Knight extends Piece_Old {
//
//        Knight(boolean white) {
//            type = "knight";
//            this.white = white;
//            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 960, white(white), 320, 320));
//        }
//
//        @Override
//        public ArrayList<int[]> availableMoves(ArrayList<Tile[][]> history) {
//            ArrayList<int[]> moves = new ArrayList<>();
//            Tile[][] current = history.get(0);
//            for (int i = 0; i < 8; i++) {
//                for (int j = 0; j < 8; j++) {
//                    if (current[i][j].piece != null && current[i][j].piece.white == white) {
//                        continue;
//                    }
//                    if (Math.abs(row - i) == 2 && Math.abs(col - j) == 1) {//Vertical L's
//                        moves.add(new int[]{i, j});
//                    } else if (Math.abs(row - i) == 1 && Math.abs(col - j) == 2) {//Horizontal L's
//                        moves.add(new int[]{i, j});
//                    }
//                }
//            }
//            return moves;
//        }
//
//        @Override
//        public Piece_Old clone() {
//            Piece_Old clone = new Knight(this.white);
//            clone.notMoved = this.notMoved;
//            clone.row = this.row;
//            clone.col = this.col;
//            return clone;
//        }
//    }
//
//    static class Rook extends Piece_Old {
//
//        Rook(boolean white) {
//            type = "rook";
//            this.white = white;
//            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 1280, white(white), 320, 320));
//        }
//
//        @Override
//        public ArrayList<int[]> availableMoves(ArrayList<Tile[][]> history) {
//            ArrayList<int[]> moves = new ArrayList<>();
//            moves.addAll(straights(row, col, history.get(0), white, 7));
//            return moves;
//        }
//
//        @Override
//        public Piece_Old clone() {
//            Piece_Old clone = new Rook(this.white);
//            clone.notMoved = this.notMoved;
//            clone.row = this.row;
//            clone.col = this.col;
//            return clone;
//        }
//    }
//
//    static class Pawn extends Piece_Old {
//
//        boolean canEnPassant = false;
//        boolean possibleEnPassantLeft = false;
//        boolean possibleEnPassantRight = false;
//
//        Pawn(boolean white) {
//            type = "pawn";
//            this.white = white;
//            this.setImage(new WritableImage(spriteSheet.getPixelReader(), 1600, white(white), 320, 320));
//        }
//
//        @Override
//        public ArrayList<int[]> availableMoves(ArrayList<Tile[][]> history) {
//            ArrayList<int[]> moves = new ArrayList<>();
//            moves.addAll(pawnMoves(history, white));
//            return moves;
//        }
//
//        private ArrayList<int[]> pawnMoves(ArrayList<Tile[][]> history, boolean white) {
//            ArrayList<int[]> moves = new ArrayList<>();
//            Tile[][] current = history.get(0);
//            final int front;
//            if (white) {
//                front = -1;
//            } else {
//                front = 1;
//            }
//            final int frontY = row + front;
//            final int frontY2 = row + front + front;
//            //ONE SQUARE
//            if (inBounds(frontY, col) && current[frontY][col].piece == null) {//Basic one square move, square must not be occupied
//                moves.add(new int[]{frontY, col});
//            }
//            //MOVE TWO SQUARES
//            if (notMoved && current[frontY][col].piece == null && current[frontY2][col].piece == null) {//Can move two squares on the first move, spaces must be free
//                moves.add(new int[]{frontY2, col});
//            }
//            //PAWN ATTACKING DIAGONALLY
//            moves.addAll(diagonalAttack(current, front, true, true));
//            moves.addAll(diagonalAttack(current, front, false, true));
//            //ENPASSANT
//            moves.addAll(enpassant(history, front, true));
//            moves.addAll(enpassant(history, front, false));
//            return moves;
//        }
//
//        ArrayList<int[]> diagonalAttack(Tile[][] board, int front, boolean right, boolean mustHaveEnemies) {
//            ArrayList<int[]> moves = new ArrayList<>();
//            final int direction;
//            if (right) {
//                direction = 1;
//            } else {
//                direction = -1;
//            }
//            final int y = row + front;
//            final int x = col + direction;
//            if (inBounds(y, x)) {//Check if the forward left is inside the board
//                Piece_Old piece = board[y][x].piece;
//                if (!mustHaveEnemies) {//For kings anticipating captures
//                    moves.add(new int[]{y, x});
//                } else if (piece != null && piece.white != white) {//If the diagonal square is an enemy
//                    moves.add(new int[]{y, x});
//                }
//            }
//            return moves;
//        }
//
//        ArrayList<int[]> enpassant(ArrayList<Tile[][]> history, int front, boolean right) {
//            ArrayList<int[]> moves = new ArrayList<>();
//            int offset;
//            if (right) {
//                offset = 1;
//            } else {
//                offset = -1;
//            }
//            final int y = row + front;
//            final int y2 = y + front;
//            final int x = col + offset;
//            if (inBounds(row, x) && history.size() > 2 && inBounds(y2, x)) {//Right / left must be inside the board, must have a last turn
//                Tile[][] current = history.get(0);
//                Tile[][] lastTurn = history.get(1);
//                Piece_Old piece = lastTurn[y2][x].piece;//Last turn piece in the square for en passant check
//                if (piece != null && piece.type.equals("pawn") && lastTurn[row][x].piece == null) {//En passant square has a piece amd is a pawn that has not moved, adjacent tile must be empty
//                    Piece_Old nextTo = current[row][x].piece;//This turn's piece in adjacent square
//                    if (nextTo != null && nextTo.type.equals("pawn")) {//There is a piece next to this pawn and it is a pawn,
//                        Piece_Old prevSquarePiece = current[y2][x].piece;
//                        if (prevSquarePiece == null) {//Square should be empty if it was a pawn that moved next to this pawn
//                            moves.add(new int[]{y, x, 1});
//                        }
//                    }
//                }
//            }
//            return moves;
//        }
//
//        @Override
//        public Piece_Old clone() {
//            Piece_Old clone = new Pawn(this.white);
//            clone.notMoved = this.notMoved;
//            clone.row = this.row;
//            clone.col = this.col;
//            return clone;
//        }
//    }
//}
