package byow.Core;

import byow.TileEngine.TERenderer;

import static org.junit.Assert.*;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import org.junit.Test;
import java.util.Random;

public class TestVisual {

    @Test
    public void roomSizeTest() {
        //IMAGINARY WORLD - 10 x 10

        //3x3 Room @ (5,5), bottomRight should be (7,7)
        Room room = new Room(new Position(5, 5), 3,
                        3, 10, 10);
        Position tl = room.getTopLeft();
        assertEquals(5, tl.x());
        assertEquals(5, tl.y());
        Position tr = room.getTopRight();
        assertEquals(7, tr.x());
        assertEquals(5, tr.y());
        Position bl = room.getBottomLeft();
        assertEquals(5, bl.x());
        assertEquals(7, bl.y());
        Position br = room.getBottomRight();
        assertEquals(7, br.x());
        assertEquals(7, br.y());

        //Makes sure rooms do not exceed world boundaries
        Room room2 = new Room(new Position(5, 5), 19,
                123, 10, 10);
        Position tl2 = room2.getTopLeft();
        assertEquals(5, tl2.x());
        assertEquals(5, tl2.y());
        Position tr2 = room2.getTopRight();
        assertEquals(9, tr2.x());
        assertEquals(5, tr2.y());
        Position bl2 = room2.getBottomLeft();
        assertEquals(5, bl2.x());
        assertEquals(9, bl2.y());
        Position br2 = room2.getBottomRight();
        assertEquals(9, br2.x());
        assertEquals(9, br2.y());

    }

    @Test
    public void testGenerateRoom() {
        //Simple test on a 100x100 map
        Map myMap = new Map(100, 100, new Random(1));
        Random rando = new Random(1);
        int width = rando.nextInt(7) + 4;
        int height = rando.nextInt(7) + 4;
        int x = rando.nextInt(98);
        int y = rando.nextInt(98);
        myMap.generateRoom(false);
        TETile[][] ans = myMap.getWorld();
        assertEquals(Tileset.WALL, ans[x][y]);
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();

        ter.initialize(80, 44, 0, 0);
        Engine test = new Engine();
        test.interactWithKeyboard();
        //test.interactWithInputString("n1265sss");
        //ter.renderFram
        //e(test.interactWithInputString("n7193300625454684331saaawasdaawd:q"));
        //ter.renderFrame(test.interactWithInputString("l"));

    }
}
