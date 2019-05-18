package byow.Core;

import java.io.Serializable;

public class Position implements Serializable {

    private int x;
    private int y;

    Position(int xCoor, int yCoor) {
        x = xCoor;
        y = yCoor;
    }

    public double distance(Position other) {
        return Math.sqrt(((x - other.x()) * (x - other.x())) + ((y - other.y()) * (y - other.y())));
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

}
