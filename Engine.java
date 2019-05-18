
package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Random;

import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.awt.Font;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;

    private Map gameWorld;
    private static Map lastSave;
    private HashSet<String> enemies;

    private long seed;
    private Random RANDOM;
    private TETile[][] finalWorldFrame;
    private boolean fail = false;
    private Player player = new Player();
    private int AMMO = 5;
    private int steps = 0;
    private int lastStep = 0;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT + 4, 0, 0);

        menu();
        if (!fail) {
            initEnemies();
            initiate();
            initDoor();
            initiatePlayer();
            initFlowers();
            initBaddies();
            //ter.renderFrame(gameWorld.getWorld());
            ter.renderFrame(flashlight());
            play();
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        finalWorldFrame = new TETile[WIDTH][HEIGHT];
        seed = 0;
        if (Character.toLowerCase(input.charAt(0)) == 'n') {
            getSeed(input);
            RANDOM = new Random(seed);
            initiate();
            initDoor();
            initiatePlayer();
        } else if (Character.toLowerCase(input.charAt(0)) == 'l') {
            loadGame();
        }
        TETile[][] temp = flashlight();
        ter.renderFrame(temp);
        int index = input.toLowerCase().indexOf("s") + 1;
        if (Character.toLowerCase(input.charAt(0)) == 'l') {
            index = 1;
        }
        while (index < input.length() && input.charAt(index) != ':') {
            char c = Character.toLowerCase(input.charAt(index));
            if (c == 'w') {
                player = gameWorld.moveW(player);
                ter.renderFrame(flashlight());
                if (player.result().equals("win")) {
                    System.out.println("Congratulations! You won.");
                    break;
                } else if (player.result().equals("fail")) {
                    index++;
                    continue;
                }
            } else if (c == 's') {
                player = gameWorld.moveS(player);
                ter.renderFrame(flashlight());
                if (player.result().equals("win")) {
                    System.out.println("Congratulations! You won.");
                    break;
                } else if (player.result().equals("fail")) {
                    index++;
                    continue;
                }
            } else if (c == 'a') {
                player = gameWorld.moveA(player);
                ter.renderFrame(flashlight());
                if (player.result().equals("win")) {
                    System.out.println("Congratulations! You won.");
                    break;
                } else if (player.result().equals("fail")) {
                    index++;
                    continue;
                }
            } else if (c == 'd') {
                player = gameWorld.moveD(player);
                ter.renderFrame(flashlight());
                if (player.result().equals("win")) {
                    System.out.println("Congratulations! You won.");
                    break;
                } else if (player.result().equals("fail")) {
                    index++;
                    continue;
                }
            }
            index++;
        }
        try {
            if (input.charAt(index) == ':') {
                if (Character.toLowerCase(input.charAt(index + 1)) == 'q') {
                    save(gameWorld);
                }
                finalWorldFrame = gameWorld.getWorld();
                return temp;
            }
        } catch (StringIndexOutOfBoundsException e) {
            finalWorldFrame = gameWorld.getWorld();
            return temp;
        }
        finalWorldFrame = gameWorld.getWorld();
        return temp;
    }

    private void menu() {
        fail = false;
        //StdDraw.setCanvasSize(WIDTH * TILESIZE, HEIGHT * TILESIZE + 4 );
        //StdDraw.setXscale(0, WIDTH);
        //StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 50));
        //StdDraw.text(0.5, 0.8, "CS61B: THE GAME");
        StdDraw.text(WIDTH / 2, HEIGHT / 4 *  3, "CS61B: THE GAME");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        //StdDraw.text(0.5, 0.6, "New Game(N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "New Game(N)");
        //StdDraw.text(0.5, 0.4, "Load Game(L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 3, "Load Game(L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "Guide(G)");
        //StdDraw.text(0.5, 0.2, "Quit(Q)");
        StdDraw.text(WIDTH / 2, HEIGHT / 8, "Quit(Q)");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'n') {
                    enterSeed();
                    return;
                } else if (c == 'l') {
                    loadGame();
                    loadInit();
                    play();
                    break;
                } else if (c == 'q') {
                    quit();
                    return;
                } else if (c == 'g') {
                    guide();
                }
            }
            if (StdDraw.isMousePressed()) {
                //CHANGE DIMENSIONS AS NECESSARY
                if (validClick(HEIGHT / 2 - 4, HEIGHT / 2 + 4)) {
                //if (validClick(0.55, 0.65)) {
                    enterSeed();
                    return;
                } else if (validClick(HEIGHT / 8 * 3 - 4, HEIGHT / 8 * 3 + 4)) {
                //} else if (validClick(0.35, 0.45)) {
                    loadGame();
                    loadInit();
                    initEnemies();
                    play();
                    break;
                } else if (validClick(HEIGHT / 8 - 4, HEIGHT / 8 + 4)) {
                //} else if (validClick(0.15, 0.25)) {
                    quit();
                    return;
                } else if (validClick(HEIGHT / 4 - 4, HEIGHT / 4 + 4)) {
                    guide();
                }
            }
        }
    }

    private TETile[][] flashlight() {
        TETile[][] temp = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                temp[i][j] = Tileset.NOTHING;
            }
        }
        int startX;
        int startY;
        int endX;
        int endY;
        if (gameWorld.getPos().x() - 4 < 0) {
            startX = 0;
        } else {
            startX = gameWorld.getPos().x() - 4;
        }
        if (gameWorld.getPos().y() - 4 < 0) {
            startY = 0;
        } else {
            startY = gameWorld.getPos().y() - 4;
        }
        if (gameWorld.getPos().x() + 4 > WIDTH - 1) {
            endX = WIDTH - 1;
        } else {
            endX = gameWorld.getPos().x() + 4;
        }
        if (gameWorld.getPos().y() + 4 > HEIGHT - 1) {
            endY = HEIGHT - 1;
        } else {
            endY = gameWorld.getPos().y() + 4;
        }
        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                temp[i][j] = gameWorld.getWorld()[i][j];
            }
        }
        return temp;
    }

    /** Draws the menu, updates every keystroke. Takes input seed */
    private void enterSeed() {
        StringBuilder string = new StringBuilder();
        seed = 0;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c <= '9' && c >= '0') {
                    seed = seed * 10 + Character.getNumericValue(c);
                    string.append(c);
                } else if (Character.toLowerCase(c) == 's') {
                    this.seed = seed;
                    RANDOM = new Random(this.seed);
                    return;
                } else {
                    fail = true;
                    error();
                    return;
                }

            }
            //StdDraw.setCanvasSize(WIDTH * TILESIZE, HEIGHT * TILESIZE + 4);
            StdDraw.clear(Color.black);
            //StdDraw.setXscale(0, WIDTH);
            //StdDraw.setYscale(0, HEIGHT);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 50));
            //StdDraw.text(0.5, 0.8, "CS61B: THE GAME");
            StdDraw.text(WIDTH / 2, HEIGHT / 4 * 3, "CS61B: THE GAME");
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 35));
            //StdDraw.text(0.5, 0.5, "Enter SEED:");
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "Enter SEED:");
            //StdDraw.text(0.5, 0.3, string.toString());
            StdDraw.text(WIDTH / 2, HEIGHT / 4, string.toString());
            StdDraw.enableDoubleBuffering();
            StdDraw.show();
        }
    }

    private void play() {
        while (true) {
            hud();
            checkDie();
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'w') {
                    player = gameWorld.moveW(player);
                    ter.renderFrame(flashlight());
                    if (player.result().equals("win")) {
                        ter.renderFrame(gameWorld.getWorld());
                        win();
                        break;
                    } else if (player.result().equals("fail")) {
                        continue;
                    } else {
                        ter.renderFrame(flashlight());
                    }
                    steps++;
                } else if (c == 's') {
                    player = gameWorld.moveS(player);
                    ter.renderFrame(flashlight());
                    if (player.result().equals("win")) {
                        ter.renderFrame(gameWorld.getWorld());
                        win();
                        break;
                    } else if (player.result().equals("fail")) {
                        continue;
                    } else {
                        ter.renderFrame(flashlight());
                    }
                    steps++;
                } else if (c == 'a') {
                    player = gameWorld.moveA(player);
                    ter.renderFrame(flashlight());
                    if (player.result().equals("win")) {
                        ter.renderFrame(gameWorld.getWorld());
                        win();
                        break;
                    } else if (player.result().equals("fail")) {
                        continue;
                    } else {
                        ter.renderFrame(flashlight());
                    }
                    steps++;
                } else if (c == 'd') {
                    player = gameWorld.moveD(player);
                    ter.renderFrame(flashlight());
                    if (player.result().equals("win")) {
                        ter.renderFrame(gameWorld.getWorld());
                        win();
                        break;
                    } else if (player.result().equals("fail")) {
                        continue;
                    } else {
                        ter.renderFrame(flashlight());
                    }
                    steps++;
                } else if (c == 'f') {
                    shoot();
                    ter.renderFrame(flashlight());
                } else if (c == ' ') {
                    player = gameWorld.jump(player);
                    ter.renderFrame(flashlight());
                    if (player.result().equals("win")) {
                        ter.renderFrame(gameWorld.getWorld());
                        win();
                        break;
                    } else if (player.result().equals("fail")) {
                        continue;
                    } else {
                        //ter.renderFrame(gameWorld.getWorld());
                        ter.renderFrame(flashlight());
                    }
                    steps++;
                } else if (c == ':') {
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char save = Character.toLowerCase(StdDraw.nextKeyTyped());
                            if (save == 'q') {
                                save(gameWorld);
                                quit();
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void shoot() {
        int y = gameWorld.getPos().y();
        int x = gameWorld.getPos().x();
        finalWorldFrame = gameWorld.getWorld();
        if (y + 1 < HEIGHT && AMMO > 0 &&
                enemies.contains(finalWorldFrame[x][y + 1].description())) {
            gameWorld.shot(new Position(x, y + 1));
        }
        AMMO--;
        if (AMMO < 0) {
            AMMO = 0;
        }
    }

    /** Creates the world using Map class and its random map generator. */
    private void initiate() {
        gameWorld = new Map(WIDTH, HEIGHT, RANDOM);

        for (int i = 0; i < RANDOM.nextInt(((WIDTH * HEIGHT) / 20)) + (WIDTH * HEIGHT) / 50; i++) {
            if (i == 0) {
                gameWorld.generateRoom(true);
            }
            gameWorld.generateRoom(false);
        }
        finalWorldFrame = gameWorld.getWorld();
    }

    private void initFlowers() {
        int num = RANDOM.nextInt(WIDTH * HEIGHT / 800) + 4;
        for (int i = 0; i < num; i++) {
            gameWorld.randFlowers();
        }
    }

    private void initBaddies() {
        gameWorld.randBaddies();
    }

    /** Initializes the final locked door to get to. */
    private void initDoor() {
        gameWorld.randDoor();
        Position door = gameWorld.getDoor();
        finalWorldFrame[door.x()][door.y()] = Tileset.LOCKED_DOOR;
    }

    /** Initializes the origin player spawn point (middle of the first room created). */
    private void initiatePlayer() {
        Position init = gameWorld.getInitPos();
        finalWorldFrame[init.x()][init.y()] = Tileset.AVATAR;
    }

    private void initEnemies() {
        enemies = new HashSet<>();
        enemies.add("Bruiser");
        enemies.add("Munchkin");
        enemies.add("Death");
        enemies.add("Trap");
        //gameWorld.randEnemies();
    }

    /** Results in a pop-up error box w/ message. */
    private void error() {
        //StdDraw.setCanvasSize(WIDTH * TILESIZE, HEIGHT * TILESIZE + 4);
        StdDraw.clear(Color.black);
        //StdDraw.setXscale(0, WIDTH);
        //StdDraw.setYscale(0, HEIGHT);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        //StdDraw.text(0.5, 0.7, "ERROR: Please enter a valid sequence.");
        StdDraw.text(WIDTH / 2, HEIGHT / 4 * 3, "ERROR: Please enter a valid sequence.");
        //StdDraw.text(0.5, 0.4, "Press (M) to return to the Menu.");
        StdDraw.text(WIDTH / 2, HEIGHT / 3, "Press (M) to return to the Menu.");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.toLowerCase(c) == 'm') {
                    interactWithKeyboard();
                }
            }
        }
    }

    private void win() {
        //StdDraw.setCanvasSize(WIDTH * TILESIZE, HEIGHT * TILESIZE + 4);
        StdDraw.clear(Color.black);
        //StdDraw.setXscale(0, WIDTH);
        //StdDraw.setYscale(0, HEIGHT);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
        //StdDraw.text(0.5, 0.8, "CONGRATULATIONS!");
        StdDraw.text(WIDTH / 2, HEIGHT / 4 * 3, "CONGRATULATIONS!");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 35));
        //StdDraw.text(0.5, 0.5, "You Made It Out!");
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "You made it to the ENDGAME!");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        //StdDraw.text(0.5, 0.3, "Press (M) to return to the Menu.");
        StdDraw.text(WIDTH / 2, HEIGHT / 8, "Press (M) to return to the Menu.");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.toLowerCase(c) == 'm') {
                    interactWithKeyboard();
                }
            }
        }
    }

    private void die() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
        StdDraw.text(WIDTH / 2, HEIGHT / 4 * 3, "YOU DIED!");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 35));
        //StdDraw.text(0.5, 0.5, "You Made It Out!");
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "You ran out of lives.");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        StdDraw.text(WIDTH / 2, HEIGHT / 8, "Press (M) to return to the Menu.");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.toLowerCase(c) == 'm') {
                    interactWithKeyboard();
                }
            }
        }
    }

    private void checkDie() {
        if (player.getHealth() == 0) {
            die();
        }
    }

    private void controls() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        StdDraw.text(WIDTH / 2, HEIGHT, "Avoid/Shoot enemies, don't die, and get to the door!");
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 7, "Forward: W");
        StdDraw.text(WIDTH / 2, HEIGHT / 4 * 3, "Left: A");
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 5, "Down: S");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Right: D");
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 3, "Jump Forward(Skip one tile/Attack " +
                "unless it's a wall): Space");
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "Shoot (Forward Only): F");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        StdDraw.text(WIDTH / 2, HEIGHT / 8, "Press (B) to Go Back.");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.toLowerCase(c) == 'b') {
                    guide();
                }
            }
        }
    }

    private void manual() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 7, "Trap (︷): Stepping on one results in " +
                "losing one life. Shoot once or jump on to kill.");
        StdDraw.text(WIDTH / 2, HEIGHT / 4 * 3, "Bruiser (웃): Running into one results in " +
                "losing two lives. Shoot twice or jump on to kill.");
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 5, "Munchkin (㋡): Running into one results in " +
                "losing one life. Shoot once or jump on to kill.");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Death (☩): Going within one tile of it " +
                "results in losing two lives. Shoot twice or jump on to kill.");
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 3, "Careful: Your special abilities are " +
                "shooting one tile forward or jumping two tiles forward.");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 15));
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "If there's a wall two steps ahead, then " +
                "you can't jump. 10 steps recharges one ammo, eating a flower regenerates one life. ");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        StdDraw.text(WIDTH / 2, HEIGHT / 8, "Press (B) to Go Back.");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.toLowerCase(c) == 'b') {
                    guide();
                }
            }
        }
    }

    private void lore() {
        StdDraw.clear(new Color(239, 218, 179));
        //StdDraw.picture(WIDTH / 2, HEIGHT / 2 + 2, "./background.png",
                //WIDTH, HEIGHT + 4);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setFont(new Font("American Typewriter", Font.BOLD, 20));
        StdDraw.text(WIDTH / 2, HEIGHT, "THIS IS THE UNTOLD STORY OF ENDGAME:" +
                "THE LEGEND OF THE RAT");
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 7, "It is the year 2023. Thanos snapped his " +
                "fingers, and half the universe is gone.");
        StdDraw.text(WIDTH / 2, HEIGHT / 4 * 3, "With this, Earth has been opened up" +
                " to to new threats as its defense is gone.");
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 5, "An Alien force has come and taken over," +
                " essentially policing Earth.");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Unintentionally, the event gave you " +
                "the undiscovered power to shapeshift...");
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 3, "You are wandering for food, when the Aliens " +
                "come. Instinctively, you turn into a rat. Suddenly, you are sucked into a vision.");
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "An incredible, unfathomable sequence of " +
                "events flashes before your eyes. When you come to, you don't remember much.");
        StdDraw.text(WIDTH / 2, HEIGHT / 8, "You have a faint memory of a van, and an " +
                "impulse to act. You know exactly what to do.");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        StdDraw.text(WIDTH / 2, HEIGHT / 8 - 4, "Good Luck! Press (B) to Go Back.");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.toLowerCase(c) == 'b') {
                    guide();
                }
            }
        }
    }

    private void guide() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 7, "Press (C) to Access the Control Guide");
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 5, "Press (P) to Access the Game Manual");
        StdDraw.text(WIDTH / 2, HEIGHT / 8 * 3, "Press (L) to Access the Game Lore");
        StdDraw.text(WIDTH / 2, HEIGHT / 8, "Press (M) to Return to the Menu.");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.toLowerCase(c) == 'c') {
                    controls();
                } else if (Character.toLowerCase(c) == 'm') {
                    interactWithKeyboard();
                } else if (Character.toLowerCase(c) == 'p') {
                    manual();
                } else if (Character.toLowerCase(c) == 'l') {
                    lore();
                }
            }
        }
    }

    private void hud() {
        TETile[][] world = flashlight();
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (mouseX >= WIDTH) {
            mouseX = WIDTH - 1;
        } else if (mouseX < 0) {
            mouseX = 0;
        }
        if (mouseY >= HEIGHT) {
            mouseY = HEIGHT - 1;
        } else if (mouseY < 0) {
            mouseY = 0;
        }
        String temp = world[mouseX][mouseY].description();
        String target = temp.toLowerCase();
        if (target.equals("wall")) {
            ter.renderFrame(world);
            description("WALL");
        } else if (target.equals("floor")) {
            ter.renderFrame(world);
            description("FLOOR");
        } else if (target.equals("locked door")) {
            ter.renderFrame(world);
            description("ANT-MAN's VAN");
        } else if (target.equals("unlocked door")) {
            ter.renderFrame(world);
            description("ENDGAME");
        } else if (target.equals("flower")) {
            ter.renderFrame(world);
            description("FLOWER");
        } else if (enemies.contains(temp)) {
            ter.renderFrame(world);
            description(temp);
        }
        displayLife();
        displayAmmo();
    }

    private void description(String desc) {
        StdDraw.enableDoubleBuffering();
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 14));
        StdDraw.setPenColor(Color.white);
        StdDraw.text(8, HEIGHT + 2, "Tile Type: " + desc);
        StdDraw.show();
    }

    private void displayLife() {
        String hearts = "Lives: ";
        for (int i = 0; i < player.getHealth(); i++) {
            hearts += "♥";
        }
        StdDraw.enableDoubleBuffering();
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 32));
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH - 10, HEIGHT + 2, hearts);
        StdDraw.show();
    }

    private void displayAmmo() {
        if (steps > lastStep && steps - lastStep >= 10 && AMMO < 5) {
            AMMO++;
            lastStep = steps;
        }
        String ammo = "Ammo: ";
        for (int i = 0; i < AMMO; i++) {
            ammo += "⊚";
        }
        StdDraw.enableDoubleBuffering();
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 32));
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, HEIGHT + 2, ammo);
        StdDraw.show();
    }

    private boolean validClick(double y1, double y2) {
        if (StdDraw.mouseX() < WIDTH / 4 && StdDraw.mouseY() > WIDTH / 4 * 3) {
        //if (StdDraw.mouseX() < 0.3 && StdDraw.mouseY() > 0.7) {
            return false;
        }
        if (StdDraw.mouseY() >= y1 && StdDraw.mouseY() <= y2) {
            return true;
        }
        return false;
    }

    private void loadInit() {
        ter.initialize(WIDTH, HEIGHT + 4, 0, 0);
        //ter.renderFrame(gameWorld.getWorld());
        ter.renderFrame(flashlight());
    }

    /** Loads in saved game file using load(). If none, quits. */
    private void loadGame() {
        Map save = load();
        if (!save.isValid()) {
            quit();
        } else {
            gameWorld = save;
        }
    }

    private static Map load() {
        File f = new File("world.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (Map) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }

        /** If no save file found, return fail world */
        return new Map(true);
    }

    private static void save(Map world) {
        File f = new File("world.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(world);
        }  catch (FileNotFoundException e) {
            System.out.println("File Not Found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private void getSeed(String input) {
        if (input.toLowerCase().contains("n") && input.toLowerCase().contains("s")) {
            int leftInd = input.toLowerCase().indexOf("n") + 1;
            int rightInd = input.toLowerCase().indexOf("s");
            try {
                seed = Long.parseLong(input.substring(leftInd, rightInd));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Seed must be an integer");
            }
        } else {
            throw new RuntimeException("Input a string starting with 'n' and ending with 's'");
        }
    }

    private void quit() {
        System.exit(0);
    }
}
