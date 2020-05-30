package chess;

import java.util.ArrayList;

public class Logic {

    ArrayList<Tile[][]> history = new ArrayList<>();
    Tile primaryTile = null;
    boolean whiteTurn = true;
    boolean check = false;
}
