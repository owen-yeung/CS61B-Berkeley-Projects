package byow.Core;

import static org.junit.Assert.*;

import byow.TileEngine.TETile;
import org.junit.Test;

public class InputStringTest {

    @Test
    /** Check if output of save and load is the same as no saving
     *
     */
    public void testSaveOutput() {
        Engine engine1 = new Engine();
        TETile[][] world1 = engine1.interactWithInputString("n1sddssddss");
        Engine engine2 = new Engine();
        engine2.interactWithInputString("n1sddss:q");
        Engine engine3 = new Engine();
        TETile[][] world2 = engine3.interactWithInputString("lddss");
        System.out.println("Started at (46, 17)");
        for (int x = 0; x < Engine.WIDTH; x++) {
            for (int y = 0; y < Engine.HEIGHT; y++) {
                assertEquals("Mismatch at (" + Integer.toString(x) + ", " + Integer.toString(y)
                        + "). Unsaved tile: " + world1[x][y].description() + " Saved tile: "
                        + world2[x][y].description(), world1[x][y], world2[x][y]);
            }
        }
    }

    @Test
    public void testSaveOnly() {
        Engine engine1 = new Engine();
        TETile[][] world1 = engine1.interactWithInputString("n1sddssddss");
        Engine engine2 = new Engine();
        TETile[][] world2 = engine2.interactWithInputString("n1sddssddss:q");
        System.out.println("Started at (46, 17)");
        for (int x = 0; x < Engine.WIDTH; x++) {
            for (int y = 0; y < Engine.HEIGHT; y++) {
                assertEquals("Mismatch at (" + Integer.toString(x) + ", " + Integer.toString(y)
                        + "). Unsaved tile: " + world1[x][y].description() + " Saved tile: "
                        + world2[x][y].description(), world1[x][y], world2[x][y]);
            }
        }
    }
}
