
package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.lang.management.PlatformLoggingMXBean;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Map implements Serializable {
    private boolean valid;

    private int worldWidth;
    private int worldLength;
    private Random RANDOM;
    private TETile[][] world;
    private int roomWidthLim;
    private int roomHeightLim;

    private Position initPos;
    private Position currPos;
    private Position door;
    private HashMap<String, Enemy> enemies;
    private HashMap<Integer, Enemy> badGuys;
    private HashSet<String> baddies;

    Map(boolean fail) {
        if (fail) {
            valid = false;
        }
    }

    /** Initializes the worldmap. Starts completely empty. */
    Map(int width, int length, Random random) {
        valid = true;
        worldWidth = width;
        worldLength = length;
        RANDOM = random;
        roomWidthLim = (worldWidth / 5) - 3;
        roomHeightLim = (worldLength / 5) - 3;

        //TERenderer ter = new TERenderer();
        //ter.initialize(worldWidth, worldLength);
        world = new TETile[worldWidth][worldLength];
        badGuys = new HashMap<>();
        enemies = new HashMap<>();
        enemies.put("Bruiser", new Bruiser());
        enemies.put("Munchkin", new Munchkin());
        enemies.put("Death", new Death());
        enemies.put("Trap", new Trap());
        baddies = new HashSet<>();
        baddies.add("Bruiser");
        baddies.add("Munchkin");
        baddies.add("Death");
        baddies.add("Trap");

        //Initializes entire world to empty space
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldLength; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    /** Generates random rooms by randomly selecting a starting top left corner (x,y).
     *  Then randomly selects a room width and height, and draws the room accordingly.
     *  Walls first, then fills in the room with floors.
     * @param first
     */
    public void generateRoom(boolean first) {
        int width = RANDOM.nextInt(roomWidthLim) + 3;
        int height = RANDOM.nextInt(roomHeightLim) + 3;
        int x = RANDOM.nextInt(worldWidth - 2);
        int y = RANDOM.nextInt(worldLength - 2);
        Room room = new Room(new Position(x, y), width, height, worldWidth, worldLength);
        Position centroid =  new Position(((room.getTopLeft().x() + room.getTopRight().x()) / 2),
                ((room.getTopLeft().y() + room.getBottomLeft().y()) / 2));

        /** Designates the starting position for the user (middle of first room) */
        if (first) {
            initPos = centroid;
            currPos = initPos;
        }

        if (!intersects(room)) {
            //Draws top wall
            for (int xPos = x; xPos <= room.getTopRight().x(); xPos++) {
                if (world[xPos][y].equals(Tileset.NOTHING)) {
                    world[xPos][y] = Tileset.WALL;
                }
            }

            //Draws left wall
            for (int yPos = y; yPos <= room.getBottomLeft().y(); yPos++) {
                if (world[x][yPos].equals(Tileset.NOTHING)) {
                    world[x][yPos] = Tileset.WALL;
                }
            }

            //Draws right wall
            for (int yPos = y; yPos <= room.getBottomLeft().y(); yPos++) {
                if (world[room.getTopRight().x()][yPos].equals(Tileset.NOTHING)) {
                    world[room.getTopRight().x()][yPos] = Tileset.WALL;
                }
            }

            //Draws bottom wall
            for (int xPos = x; xPos <= room.getTopRight().x(); xPos++) {
                if (world[xPos][room.getBottomLeft().y()].equals(Tileset.NOTHING)) {
                    world[xPos][room.getBottomLeft().y()] = Tileset.WALL;
                }
            }

            //Draw Floor
            for (int yPos = y + 1; yPos <= room.getBottomLeft().y() - 1; yPos++) {
                for (int xPos = x + 1; xPos <= room.getTopRight().x() - 1; xPos++) {
                    world[xPos][yPos] = Tileset.FLOOR;
                }
            }

            /** Draws multiple hallways per each room generated. Guarantees rooms are connected. */
            for (int i = 0; i < 50; i++) {
                generateHallway(centroid);
            }
        }
    }

    public void generateHallway(Position start) {
        int orientation = RANDOM.nextInt(2);
        int width;
        int height;
        if (orientation == 0) {
            width = 3;
            height = RANDOM.nextInt(worldLength / 2);
        } else {
            height = 3;
            width = RANDOM.nextInt(worldWidth / 2);
        }
        Room hallway = new Room(start, width, height, worldWidth, worldLength);

        //Draws top wall
        for (int xPos = start.x(); xPos <= hallway.getTopRight().x(); xPos++) {
            if (world[xPos][start.y()].equals(Tileset.NOTHING)) {
                world[xPos][start.y()] = Tileset.WALL;
            }
        }

        //Draws left wall
        for (int yPos = start.y(); yPos <= hallway.getBottomLeft().y(); yPos++) {
            if (world[start.x()][yPos].equals(Tileset.NOTHING)) {
                world[start.x()][yPos] = Tileset.WALL;
            }
        }

        //Draws right wall
        for (int yPos = start.y(); yPos <= hallway.getBottomLeft().y(); yPos++) {
            if (world[hallway.getTopRight().x()][yPos].equals(Tileset.NOTHING)) {
                world[hallway.getTopRight().x()][yPos] = Tileset.WALL;
            }
        }

        //Draws bottom wall
        for (int xPos = start.x(); xPos <= hallway.getTopRight().x(); xPos++) {
            if (world[xPos][hallway.getBottomLeft().y()].equals(Tileset.NOTHING)) {
                world[xPos][hallway.getBottomLeft().y()] = Tileset.WALL;
            }
        }

        //Draw Floor
        for (int yPos = start.y() + 1; yPos <= hallway.getBottomLeft().y() - 1; yPos++) {
            for (int xPos = start.x() + 1; xPos <= hallway.getTopRight().x() - 1; xPos++) {
                world[xPos][yPos] = Tileset.FLOOR;
            }
        }
    }

    public Player moveW(Player p) {
        int currX = currPos.x();
        int currY = currPos.y();
        String temp = world[currX][currY + 1].description();
        String target = temp.toLowerCase();
        p = checkDeath(currX, currY + 1, p);
        if (target.equals("locked door")) {
            world[currX][currY + 1] = Tileset.UNLOCKED_DOOR;
            p.action("win");
        } else if (target.equals("wall")) {
            p.action("fail");
        } else if (baddies.contains(temp)) {
            enemies.get(temp).attack(p);
            p.action("fail");
        } else {
            world[currX][currY + 1] = Tileset.AVATAR;
            world[currX][currY] = Tileset.FLOOR;
            currPos = new Position(currX, currY + 1);
            if (target.equals("flower")) {
                p.heal();
            }
            p.action("move");
        }
        return p;
    }

    public Player moveS(Player p) {
        int currX = currPos.x();
        int currY = currPos.y();
        String temp = world[currX][currY - 1].description();
        String target = temp.toLowerCase();
        p = checkDeath(currX, currY - 1, p);
        if (target.equals("locked door")) {
            world[currX][currY - 1] = Tileset.UNLOCKED_DOOR;
            p.action("win");
        } else if (target.equals("wall")) {
            p.action("fail");

        } else if (baddies.contains(temp)) {
            enemies.get(temp).attack(p);
            p.action("fail");
        } else {
            world[currX][currY - 1] = Tileset.AVATAR;
            world[currX][currY] = Tileset.FLOOR;
            currPos = new Position(currX, currY - 1);
            if (target.equals("flower")) {
                p.heal();
            }
            p.action("move");
        }
        return p;
    }

    public Player moveA(Player p) {
        int currX = currPos.x();
        int currY = currPos.y();
        String temp = world[currX - 1][currY].description();
        String target = temp.toLowerCase();
        p = checkDeath(currX - 1, currY, p);
        if (target.equals("locked door")) {
            world[currX - 1][currY] = Tileset.UNLOCKED_DOOR;
            p.action("win");
        } else if (target.equals("wall")) {
            p.action("fail");
        } else if (baddies.contains(temp)) {
            enemies.get(temp).attack(p);
            p.action("fail");
        } else {
            world[currX - 1][currY] = Tileset.AVATAR;
            world[currX][currY] = Tileset.FLOOR;
            currPos = new Position(currX - 1, currY);
            if (target.equals("flower")) {
                p.heal();
            }
            p.action("move");
        }
        return p;
    }

    public Player moveD(Player p) {
        int currX = currPos.x();
        int currY = currPos.y();
        String temp = world[currX + 1][currY].description();
        String target = temp.toLowerCase();
        p = checkDeath(currX + 1, currY, p);
        if (target.equals("locked door")) {
            world[currX + 1][currY] = Tileset.UNLOCKED_DOOR;
            p.action("win");
        } else if (target.equals("wall")) {
            p.action("fail");
        } else if (baddies.contains(temp)) {
            enemies.get(temp).attack(p);
            p.action("fail");
        } else {
            world[currX + 1][currY] = Tileset.AVATAR;
            world[currX][currY] = Tileset.FLOOR;
            currPos = new Position(currX + 1, currY);
            if (target.equals("flower")) {
                p.heal();
            }
            p.action("move");
        }
        return p;
    }

    public Player jump(Player p) {
        int currX = currPos.x();
        int currY = currPos.y();
        if (currY + 2 >= worldLength && world[currX][currY + 2].description().equals("wall")) {
            currY--;
        }
        String temp = world[currX][currY + 2].description();
        String target = temp.toLowerCase();
        p = checkDeath(currX, currY + 2, p);
        if (target.equals("locked door")) {
            world[currX][currY + 2] = Tileset.UNLOCKED_DOOR;
            p.action("win");
        } else if (target.equals("wall")) {
            p.action("fail");
        } else {
            if (baddies.contains(temp)) {
                enemies.get(temp).jumped();
            }
            world[currX][currY + 2] = Tileset.AVATAR;
            world[currX][currY] = Tileset.FLOOR;
            currPos = new Position(currX, currY + 2);
            if (target.equals("flower")) {
                p.heal();
            }
            p.action("move");
        }
        return p;
    }

    public void shot(Position p) {
        Enemy e = badGuys.get(p.x() * 100 + p.y());
        e.shot();
        if (e.getHealth() == 0) {
            world[p.x()][p.y()] = Tileset.FLOOR;
        }
    }

    private Player checkDeath(int x, int y, Player p) {
        Death death = new Death();
        if (validRanger(x + 1, y - 1) && world[x + 1][y - 1].description().equals("Death")) {
            /** Assumes Top Left Position */
            death.attack(p);
        } else if (validRanger(x + 1, y) && world[x + 1][y].description().equals("Death")) {
            /** Assumes Middle Left Position */
            death.attack(p);
        } else if (validRanger(x + 1, y + 1) && world[x + 1][y + 1].description().equals("Death")) {
            /** Assumes Bottom Left Position */
            death.attack(p);
        } else if (validRanger(x, y + 1) && world[x][y + 1].description().equals("Death")) {
            /** Assumes Bottom Middle Position */
            death.attack(p);
        } else if (validRanger(x - 1, y + 1) && world[x - 1][y + 1].description().equals("Death")) {
            /** Assumes Bottom Right Position */
            death.attack(p);
        } else if (validRanger(x - 1, y) && world[x - 1][y].description().equals("Death")) {
            /** Assumes Middle Right Position */
            death.attack(p);
        } else if (validRanger(x - 1, y - 1) && world[x - 1][y - 1].description().equals("Death")) {
            /** Assumes Top Right Position */
            death.attack(p);
        } else if (validRanger(x, y - 1) && world[x][y - 1].description().equals("Death")) {
            /** Assumes Top Middle Position */
            death.attack(p);
        }
        return p;
     }

    private boolean validRanger(int x, int y) {
        if (x >= worldWidth || x < 0) {
            return false;
        }
        if (y >= worldLength || y < 0) {
            return false;
        }
        return true;
    }

    private void genBad(TETile t, Enemy guy) {
        int x = RANDOM.nextInt(worldWidth - 1);
        int y = RANDOM.nextInt(worldLength - 1);
        String temp = world[x][y].description().toLowerCase();
        while (!temp.equals("floor")) {
            x = RANDOM.nextInt(worldWidth - 1);
            y = RANDOM.nextInt(worldLength - 1);
            temp = world[x][y].description().toLowerCase();
        }
        world[x][y] = t;
        badGuys.put(x * 100 + y, guy);
    }

    private void generate(TETile t) {
        int x = RANDOM.nextInt(worldWidth - 1);
        int y = RANDOM.nextInt(worldLength - 1);
        String temp = world[x][y].description().toLowerCase();
        while (!temp.equals("floor")) {
            x = RANDOM.nextInt(worldWidth - 1);
            y = RANDOM.nextInt(worldLength - 1);
            temp = world[x][y].description().toLowerCase();
        }
        world[x][y] = t;
    }

    public void randFlowers() {
        generate(Tileset.FLOWER);
    }

    public void randBaddies() {
        int traps = RANDOM.nextInt(worldWidth * worldLength / 800) + 3;
        int Ms = RANDOM.nextInt(worldWidth * worldLength / 800) + 4;
        int Bs = RANDOM.nextInt(worldWidth * worldLength / 800) + 2;
        int Ds = RANDOM.nextInt(worldWidth * worldLength / 800) + 2;
        for (int i = 0; i < traps; i++) {
            Trap t = new Trap();
            genBad(t.getTile(), t);
        }
        for (int i = 0; i < Ms; i++) {
            Munchkin m = new Munchkin();
            genBad(m.getTile(), m);
        }
        for (int i = 0; i < Bs; i++) {
            Bruiser b = new Bruiser();
            genBad(b.getTile(), b);
        }
        for (int i = 0; i < Ds; i++) {
            Death d = new Death();
            genBad(d.getTile(), d);
        }
    }

    public void randDoor() {
        int x = RANDOM.nextInt(worldWidth - 1);
        int y = RANDOM.nextInt(worldLength - 1);
        Position temp = new Position(x, y);
        if (world[x][y].equals(Tileset.WALL) && validDoor(x, y)) {
            door = temp;
        } else {
            randDoor();
        }
    }

    private boolean validDoor(int x, int y) {
        boolean validHorizontal = true;
        boolean validVertical = true;
        if (x == 0 || x == worldWidth) {
            validHorizontal = false;
        }
        if (y == 0 || y == worldWidth) {
            validVertical = false;
        }
        if (validVertical && world[x][y + 1].equals(Tileset.WALL)
                && world[x][y - 1].equals(Tileset.WALL)) {
            if ((x > 0 && world[x - 1][y].equals(Tileset.FLOOR))
                    || (x < worldWidth && world[x + 1][y].equals(Tileset.FLOOR))) {
                return true;
            }
        } else if (validHorizontal && world[x + 1][y].equals(Tileset.WALL)
                   && world[x - 1][y].equals(Tileset.WALL)) {
            if ((y > 0 && world[x][y - 1].equals(Tileset.FLOOR))
                    || (y < worldLength && world[x][y + 1].equals(Tileset.FLOOR))) {
                return true;
            }
        }
        return false;
    }

    private boolean intersects(Room room) {
        int y = room.getBottomRight().y();
        if (world[room.getBottomLeft().x()][room.getBottomLeft().y()].equals(Tileset.NOTHING)
                && world[room.getTopRight().x()][room.getTopRight().y()].equals(Tileset.NOTHING)
                && world[room.getBottomRight().x()][y].equals(Tileset.NOTHING)
                && world[room.getTopLeft().x()][room.getTopLeft().y()].equals(Tileset.NOTHING)) {
            return false;
        }
        return true;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public Position getInitPos() {
        return initPos;
    }

    public Position getPos() {
        return currPos;
    }

    public Position getDoor() {
        return door;
    }

    public int getWidthLim() {
        return worldWidth;
    }

    public int getHeightLim() {
        return worldLength;
    }

    public boolean isValid() {
        return valid;
    }

    public Random getRANDOM() {
        return RANDOM;
    }
}

