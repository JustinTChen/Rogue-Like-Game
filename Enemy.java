package byow.Core;

import byow.TileEngine.TETile;

import java.io.Serializable;

public class Enemy implements Serializable {
    private int attack;
    private int health;
    private char symbol;
    private TETile icon;

    public int getAttack() {
        return attack;
    }

    public int getHealth() {
        return health;
    }

    public void shot() {
        health--;
    }

    public void jumped() {
        health = 0;
    }

    public void attack(Player p) {
        p.attacked(attack);
    }
}
