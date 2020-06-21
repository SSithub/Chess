package fastchess;

import java.util.Scanner;

public class FastChess {

    public static boolean running = true;
    public static Game game = new Game();
    public static long iterations = 0;

    public static void training() {
        while (running) {
            game.newGame();
            iterations++;
            if (iterations % 100 == 0) {
                System.out.println("iterations: " + iterations);
                System.out.println("White wins: " + game.whiteWins);
                System.out.println("Black wins: " + game.blackWins);
                System.out.println("Stalemates: " + game.stalemates);
                System.out.println("% of iterations that are stalemates: " + (double) game.stalemates / (double) iterations);
                System.out.println("");
            }
        }
    }

    public static void main(String[] args) {
        Thread t = new Thread(() -> training());
        t.start();
        Scanner s = new Scanner(System.in);
        while (!s.next().equals("stop"));
        running = false;
        t.interrupt();
    }
}