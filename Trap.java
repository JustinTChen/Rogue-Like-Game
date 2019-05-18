package byow.Core;

import byow.TileEngine.TETile;

import java.awt.*;

public class Trap extends Enemy {
    private int attack;
    private int health;
    private char symbol;
    private TETile icon;

    Trap() {
        attack = 1;
        health = 1;
        symbol = '︷';
        icon = new TETile(symbol, Color.white, Color.black, "Trap");
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
