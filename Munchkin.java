package byow.Core;

import byow.TileEngine.TETile;

import java.awt.*;
import java.io.Serializable;

public class Munchkin extends Enemy {
    private int attack;
    private int health;
    private char symbol;
    private TETile icon;

    Munchkin() {
        attack = 1;
        health = 1;
        symbol = '㋡';
        icon = new TETile(symbol, Color.white, Color.black, "Munchkin");
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
