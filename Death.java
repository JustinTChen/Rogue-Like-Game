package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.io.Serializable;

public class Death extends Enemy {
    private int attack;
    private int health;
    private char symbol;
    private TETile icon;

    Death() {
        attack = 2;
        health = 1;
        symbol = '☩';
        icon = new TETile(symbol, Color.white, Color.black, "Death");
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
