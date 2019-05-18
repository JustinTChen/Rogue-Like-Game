package byow.Core;

import java.io.Serializable;

public class Player implements Serializable {
    private int health;
    private String what;

    Player() {
        health = 3;
    }

    public void attacked(int attack) {
        health -= attack;
    }

    public void heal() {
        health++;
        if (health > 3) {
            health = 3;
        }
    }

    public int getHealth() {
        return health;
    }

    public void action(String s) {
        what = s;
    }

    public String result() {
        return what;
    }
}
