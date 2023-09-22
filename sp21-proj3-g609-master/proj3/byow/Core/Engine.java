package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.io.*;

import java.awt.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 90;
    public static final int HEIGHT = 40;
    public static boolean gameOver = false;
    public static char characterSymbol;
    public static TETile characterTile = Tileset.AVATAR;
    /** source of inputs (keyboard or String) */
    public static InputSource source;
    /** Whether the game is in String mode */
    public static boolean inputString;
    /** The current working directory. (I'm assuming this is always proj3) */
    public static final File CWD = new File(System.getProperty("user.dir"));
//    /** The .gitlet directory. */
//    public static final File PERSISTENCE_DIR = GitletUtils.join(CWD, ".persistence");
    public static final File XFILE = GitletUtils.join(CWD, "atX.txt");
    public static final File YFILE = GitletUtils.join(CWD, "atY.txt");
    public static final File SEED_FILE = GitletUtils.join(CWD, "seed.txt");
    public static final File INPUTS_FILE = GitletUtils.join(CWD, "inputs.txt");
//    public static final File CHAR_FILE = GitletUtils.join(CWD, "character.txt");

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        source = new KeyboardInputSource();
        inputString = false;
//        if (!PERSISTENCE_DIR.exists()) {
//            initializePersistence();
//        }
//        initializePersistence();
        menu();
    }

//    public static void makeFile(File f) {
//        try {
//            f.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    /** Remember to delete the .persistence file if you want to start from scratch */
//    private void initializePersistence() {
//        makeFile(XFILE);
//        makeFile(YFILE);
//        makeFile(SEED_FILE);
//        makeFile(INPUTS_FILE);
//        makeFile(CHAR_FILE);
//    }

    /** Renamed the old interactWithKeyboard() method into this
     * So interactWithInputString can use this method as well. */
    public TETile[][] menu() {
//        char skin = GitletUtils.readContentsAsString(CHAR_FILE).charAt(0);
//        switch (skin) {
//            case '}' -> characterTile = Tileset.ARCHER;
//            case '/' -> characterTile = Tileset.SWORDSMAN;
//            case '!' -> characterTile = Tileset.MAGE;
//            default -> characterTile = Tileset.AVATAR;
//        }
        if (!inputString) {
            StdDraw.setCanvasSize(HEIGHT * 16, HEIGHT * 16);
            Font font = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(font);
            StdDraw.setXscale(0, HEIGHT);
            StdDraw.setYscale(0, HEIGHT);
            StdDraw.clear(Color.BLACK);
            StdDraw.enableDoubleBuffering();
            double frameLength = HEIGHT;

            StdDraw.clear();
            StdDraw.text(frameLength / 2, frameLength - 5, "CS 61B: The Game");
            StdDraw.text(frameLength / 2, frameLength - 10, "New Game (N)");
            StdDraw.text(frameLength / 2, frameLength - 15, "Pick Character for New Game (P)");
            StdDraw.text(frameLength / 2, frameLength - 20, "Load Game (L)");
            StdDraw.text(frameLength / 2, frameLength - 25, "Replay Last Save (R)");
            StdDraw.text(frameLength / 2, frameLength - 30, "Quit (Q)");
            StdDraw.show();
        }
        char inputChar = source.getNextKey();
//        while (inputChar == 0) {
//            if (StdDraw.hasNextKeyTyped()) {
//                inputChar = StdDraw.nextKeyTyped();
//            }
//        }
//
//        inputChar = Character.toLowerCase(inputChar);
        return dealWithKeyboardInput(inputChar);
    }

    private TETile[][] dealWithKeyboardInput(char inputChar) {
        double frameLength = HEIGHT;

        switch (inputChar) {
            case 'n' -> {
                String randomCode = "";
                if (!inputString) {
                    StdDraw.clear();
                    StdDraw.text(frameLength / 2, frameLength - 5, "Please enter a random code");
                    StdDraw.text(frameLength / 2, frameLength - 10, "to start the game.");
                    StdDraw.text(frameLength / 2, frameLength - 25, "Press s when done entering code.");
                    StdDraw.show();
                }
                while (randomCode.length() < 20) {
                    char nextChar = source.getNextKey();
                    if (nextChar == 's') {
                        break;
                    }
                    randomCode = randomCode + nextChar;
                    if (!inputString) {
                        StdDraw.clear();
                        StdDraw.text(frameLength / 2, frameLength - 5, "Please enter a random code");
                        StdDraw.text(frameLength / 2, frameLength - 10, "to start the game.");
                        StdDraw.text(frameLength / 2, frameLength - 17, randomCode);
                        StdDraw.text(frameLength / 2, frameLength - 25, "Press s when done entering code.");
                        StdDraw.show();
                    }
//                    if (StdDraw.hasNextKeyTyped()) {
//                        char nextChar = StdDraw.nextKeyTyped();
//                        char nextCharLower = Character.toLowerCase(nextChar);
//                        if (nextCharLower == 's') {
//                            break;
//                        }
//                        randomCode = randomCode + nextCharLower;
//                    }
                }

//                System.out.println(randomCode);
                GameMap gameMap = generateMapFromSeed(randomCode);
                GitletUtils.writeContents(XFILE, Integer.toString(gameMap.getStartX() + 1)) ;
                GitletUtils.writeContents(YFILE, Integer.toString(gameMap.getStartY() - 1));
                GitletUtils.writeContents(SEED_FILE, randomCode);
                GitletUtils.writeContents(INPUTS_FILE, "");
                TETile[][] world = emptyWorld();
                gameMap.drawWorld(world);
                if (!inputString){
                    playGame(gameMap, world);
                } else {
                    String newInputs = "";
                    while (source.possibleNextInput()) {
                        newInputs = newInputs + source.getNextKey();
                    }
                    return replayGame(gameMap, world, false, newInputs);
                }
            }
            case 'l' -> {
                String inputs = GitletUtils.readContentsAsString(INPUTS_FILE);
                String seedString = GitletUtils.readContentsAsString(SEED_FILE);
                GameMap gameMap = generateMapFromSeed(seedString);
                TETile[][] world = emptyWorld();
                if (inputString) {
                    String newInputs = "";
                    while (source.possibleNextInput()) {
                        newInputs = newInputs + source.getNextKey();
                    }
                    inputs = inputs + newInputs;
                    return replayGame(gameMap, world, false, inputs);
                } else {
                    world = replayGame(gameMap, world, false, inputs);
                    playGame(gameMap, world);
                }
            }
            case 'q' -> {
                System.out.println("Quitting game.");
                System.exit(0);
            }
            case 'r' -> {
                String inputs = GitletUtils.readContentsAsString(INPUTS_FILE);
                String seedString = GitletUtils.readContentsAsString(SEED_FILE);
                GameMap gameMap = generateMapFromSeed(seedString);
                TETile[][] world = emptyWorld();
                replayGame(gameMap, world, true, inputs);
            }
            case 'p' -> {
                pickCharacter();
                System.exit(0);
            }
            default -> {
                System.out.println("That input is not recognized, please try again.");
                interactWithKeyboard();
            }
        }
        return null; //This should never be used
    }

    private TETile[][] emptyWorld() {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        return world;
    }

    /** Takes a STRING not a long */
    private GameMap generateMapFromSeed(String randomCode) {
        long worldSeed = Long.parseLong(randomCode);
        return new GameMap(worldSeed);
    }

    private void pickCharacter() {
        double frameLength = HEIGHT;

        if (!inputString) {
            StdDraw.clear();
            StdDraw.text(frameLength / 2, frameLength - 5, "Choose your character: ");
            StdDraw.text(frameLength / 2, frameLength - 10, "Archer: } => Press }");
            StdDraw.text(frameLength / 2, frameLength - 15, "Swordsman: / => Press /");
            StdDraw.text(frameLength / 2, frameLength - 20, "Mage: ! => Press !");
            StdDraw.show();
        }

//        char inputChar = 0;
//        while (inputChar == 0) {
//            if (StdDraw.hasNextKeyTyped()) {
//                inputChar = StdDraw.nextKeyTyped();
//            }
//        }
        char inputChar = source.getNextKey();

        if (inputChar != '}' && inputChar != '/' && inputChar != '!') {
            System.out.println("That input is not recognized, please try again.");
            pickCharacter();
        } else {
            characterSymbol = inputChar;
//            System.out.println("Your character is : " + characterSymbol);
            switch (characterSymbol) {
                case '}' -> characterTile = Tileset.ARCHER;
                case '/' -> characterTile = Tileset.SWORDSMAN;
                case '!' -> characterTile = Tileset.MAGE;
            }
//            GitletUtils.writeContents(CHAR_FILE, Character.toString(characterSymbol));
            dealWithKeyboardInput('n');
        }
    }

    /** Same as playGame, but:
     * render is optional
     * must take inputString
     * No mouse position tracking
     */
    private TETile[][] replayGame(GameMap gameMap, TETile[][] world, boolean render, String inputs) {
        InputSource source2 = new StringInputDevice(inputs);
        gameMap.drawWorld(world);
        //Replays always start at the spawn point
        int atX = gameMap.getStartX() + 1;
        int atY = gameMap.getStartY() - 1;
        //Render
        if (render) {
            TERenderer ter = new TERenderer();
            ter.initialize(WIDTH, HEIGHT);
            ter.renderFrame(world);
        }
        while (!gameOver && source2.possibleNextInput()) {
//            if (StdDraw.hasNextKeyTyped()) {
                char inputChar = source2.getNextKey();
                switch (inputChar) {
                    case ':':
//                        System.out.println(": entered");
//                        boolean waiting = true;
//                        while (waiting) {
////                            System.out.println("Waiting");
//                            if (StdDraw.hasNextKeyTyped()) {
////                                System.out.println("Key detected");
//                                char input2 = StdDraw.nextKeyTyped();
//                                if (input2 == 'q') {
////                                    System.out.println("q entered");
//                                    gameOver = true;
//                                }
//                                waiting = false;
//                            }
//                        }
                        if (source2.getNextKey() == 'q') {
                            GitletUtils.writeContents(XFILE, Integer.toString(atX));
                            GitletUtils.writeContents(YFILE, Integer.toString(atY));
//                            inputs = GitletUtils.readContentsAsString(INPUTS_FILE) + inputs;
                            GitletUtils.writeContents(INPUTS_FILE, inputs.substring(0, inputs.length() - 2));
                            gameOver = true;
                        }
                        break;
                    case 'w':
                        if (!world[atX][atY + 1].equals(Tileset.WALL)) {
                            world[atX][atY] = Tileset.FLOOR;
                            atY += 1;
                        }
                        break;
                    case 's':
                        if (!world[atX][atY - 1].equals(Tileset.WALL)) {
                            world[atX][atY] = Tileset.FLOOR;
                            atY -= 1;
                        }
                        break;
                    case 'a':
                        if (!world[atX - 1][atY].equals(Tileset.WALL)) {
                            world[atX][atY] = Tileset.FLOOR;
                            atX -= 1;
                        }
                        break;
                    case 'd':
                        if (!world[atX + 1][atY].equals(Tileset.WALL)) {
                            world[atX][atY] = Tileset.FLOOR;
                            atX += 1;
                        }
//                        break;
                }
                if (inputChar != ':') {
                    world[atX][atY] = characterTile;
                }
                if (render) {
                    ter.renderFrame(world);
                    StdDraw.pause(500);
                }
            }
//        }
        gameOver = false;
        return world;
    }

    private void playGame(GameMap gameMap, TETile[][] world) {
        String inputs = ""; //Save for replays/persistence
        int atX = Integer.parseInt(GitletUtils.readContentsAsString(XFILE));
        int atY = Integer.parseInt(GitletUtils.readContentsAsString(YFILE));
//        world[atX][atY] = characterTile;
        //Render
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + 3);
        ter.renderFrame(world);
        while (!gameOver) {
            // Check mouse pos
//            System.out.print("x: ");
//            System.out.println(StdDraw.mouseX());
//            System.out.print("y: ");
//            System.out.println(StdDraw.mouseY());
            int mouseX = (int) StdDraw.mouseX();
            int mouseY = (int) StdDraw.mouseY();
//            StdDraw.setPenColor(Color.white);
//            StdDraw.filledRectangle(5, HEIGHT - 4, 5, 2);
//            StdDraw.setPenColor();
            if (mouseX < WIDTH && mouseY < HEIGHT) {
                StdDraw.setPenColor();
                StdDraw.filledRectangle(3, HEIGHT + 2, 3, 0.5);
                String desc = world[mouseX][mouseY].description();
                StdDraw.setPenColor(Color.white);
                StdDraw.text(3, HEIGHT + 2, desc);
                StdDraw.show();
//                System.out.println(desc);
            }
            if (StdDraw.hasNextKeyTyped()) {
                char inputChar = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (inputChar != ':') {
                    inputs = inputs + inputChar; // Concatenating string to char, but this seems fine
                }
                switch (inputChar) {
                    case ':':
//                        System.out.println(": entered");
                        boolean waiting = true;
                        while (waiting) {
//                            System.out.println("Waiting");
                            if (StdDraw.hasNextKeyTyped()) {
//                                System.out.println("Key detected");
                                char input2 = StdDraw.nextKeyTyped();
                                if (input2 == 'q') {
//                                    System.out.println("q entered");
                                    GitletUtils.writeContents(XFILE, Integer.toString(atX));
                                    GitletUtils.writeContents(YFILE, Integer.toString(atY));
                                    inputs = GitletUtils.readContentsAsString(INPUTS_FILE) + inputs;
                                    GitletUtils.writeContents(INPUTS_FILE, inputs);
                                    gameOver = true;
                                    System.exit(0);
                                }
                                waiting = false;
                            }
                        }
                        break;
                    case 'w':
                        if (!world[atX][atY + 1].equals(Tileset.WALL)) {
                            world[atX][atY] = Tileset.FLOOR;
                            atY += 1;
                        }
                        break;
                    case 's':
                        if (!world[atX][atY - 1].equals(Tileset.WALL)) {
                            world[atX][atY] = Tileset.FLOOR;
                            atY -= 1;
                        }
                        break;
                    case 'a':
                        if (!world[atX - 1][atY].equals(Tileset.WALL)) {
                            world[atX][atY] = Tileset.FLOOR;
                            atX -= 1;
                        }
                        break;
                    case 'd':
                        if (!world[atX + 1][atY].equals(Tileset.WALL)) {
                            world[atX][atY] = Tileset.FLOOR;
                            atX += 1;
                        }
                        break;
                }
                //Re-render
                world[atX][atY] = characterTile;
                ter.renderFrame(world);
            }
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
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
//        TETile[][] world = new TETile[WIDTH][HEIGHT];
//        String inputLowerCase = input.toLowerCase();
//        // Use this for replay feature?
//        if (inputLowerCase.startsWith("n")) {
//            int stopIndex = inputLowerCase.indexOf("s");
//            String randomSeed = inputLowerCase.substring(1, stopIndex);
//            long randomCode = Long.parseLong(randomSeed);
//            GameMap gameMap = new GameMap(randomCode);
//
//            for (int x = 0; x < WIDTH; x += 1) {
//                for (int y = 0; y < HEIGHT; y += 1) {
//                    world[x][y] = Tileset.NOTHING;
//                }
//            }
//
//            gameMap.drawWorld(world);
//        }
//
//        return world;
        source = new StringInputDevice(input);
        inputString = true;
//        if (!PERSISTENCE_DIR.exists()) {
//            initializePersistence();
//        }
        return menu();
    }
}
