package byow.Core;

import byow.TileEngine.TETile;

import java.awt.*;
import java.io.Serializable;

public class Bruiser extends Enemy {
    private int attack;
    private int health;
    private char symbol;
    private TETile icon;

    Bruiser() {
        attack = 2;
        health = 2;
        symbol = '웃';
        icon = new TETile(symbol, Color.white, Color.black, "Bruiser");
    }

    public TETile getTile() {
        return icon;
    }

    public void attack(Player p) {
        p.attacked(attack);
    }

    public void shot() {
        health--;
    }

    public int getHealth() {
        return health;
    }
}
