package byow.Core;

import java.io.Serializable;

public class Room implements Serializable {
    private Position topLeft;
    private Position topRight;
    private Position bottomLeft;
    private Position bottomRight;
    private int width;
    private int length;
    private boolean first = false;


    Room(Position tlCorner, int wid, int leng, int wWidth, int wHeight) {
        topLeft = tlCorner;
        this.width = wid;
        length = leng;

        int newX = topLeft.x() + width - 1;
        int newY = topLeft.y() + length - 1;
        if (newX >= wWidth) {
            newX = wWidth - 1;
        }
        if (newY >= wHeight) {
            newY = wHeight - 1;
        }
        topRight = new Position(newX, topLeft.y());
        bottomLeft = new Position(topLeft.x(), newY);
        bottomRight = new Position(newX, newY);
    }

    Room(Position tlCorner, int wid, int leng, int wWidth, int wHeight, boolean first) {
        topLeft = tlCorner;
        this.width = wid;
        length = leng;
        this.first = first;

        int newX = topLeft.x() + width - 1;
        int newY = topLeft.y() + length - 1;
        if (newX >= wWidth) {
            newX = wWidth - 1;
        }
        if (newY >= wHeight) {
            newY = wHeight - 1;
        }
        topRight = new Position(newX, topLeft.y());
        bottomLeft = new Position(topLeft.x(), newY);
        bottomRight = new Position(newX, newY);
    }

    public Position getTopLeft() {
        return topLeft;
    }

    public Position getTopRight() {
        return topRight;
    }

    public Position getBottomLeft() {
        return bottomLeft;
    }

    public Position getBottomRight() {
        return bottomRight;
    }
}
