package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class Proj3Demo {
    private static int WIDTH = 90;
    private static int HEIGHT = 40;
    private static TETile[][] world;

    public static void main(String[] args) {
        if (args.length > 2) {
            System.out.println("Can only have two arguments - the flag and input string");
            System.exit(0);
        } else if (args.length == 2 && args[0].equals("-s")) {
            Engine engine = new Engine();
            world = engine.interactWithInputString(args[1]);
            System.out.println(engine.toString());
            TERenderer ter = new TERenderer();
            ter.initialize(WIDTH, HEIGHT);
            ter.renderFrame(world);

            // DO NOT CHANGE THESE LINES YET ;)
        } else if (args.length == 2 && args[0].equals("-p")) {
            System.out.println("Coming soon.");
        } else {
        // DO NOT CHANGE THESE LINES YET ;)
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        }
    }
}
