package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

/** A class which builds a randomized system of rooms and hallways in 2D space.
 *
 * Also has functionality to add these rooms and hallways onto a given
 * 2D tile array passed in as an argument to the drawWorld() method.
 */

public class GameMap {
    // Made start public for avatar spawning
    public static Coordinate start;
    private static long randomCode = 13;
    private final int WIDTH = 90;
    private final int HEIGHT = 40;

    //Determines how far away new rooms are spawned from their parent.
    private final int roomSpacingFactor = 12;

    //Determines the offset between parent and child rooms (bend required in hallway).
    private final int roomOffsetFactor = 5;

    //Determine the minimum height and width of new rooms.
    private final int minRoomHeight = 4;
    private final int minRoomWidth = 4;

    //Constant used to check if new rooms are too close to existing rooms.
    private final int overlapConstant = 13;
    private HashMap<Room, Coordinate> roomList;
    private ArrayList<Hallway> hallwayList;
    private Random randomInts;

    /** Constructor which creates random base room and spawns
     * other random rooms around that room via recursiveMakeNodes(base).
     *
     * Also initializes roomList and hallwayList for later use.
     */
    GameMap(long randomSeed) {
        randomCode = randomSeed;
        randomInts = new Random(randomCode);
        int xCoordinate = randomInts.nextInt(WIDTH - 60) + 30;
        int yCoordinate = randomInts.nextInt(HEIGHT - 20) + 10;
        int height = randomInts.nextInt(5) + minRoomHeight;
        int width = randomInts.nextInt(5) + minRoomWidth;
        roomList = new HashMap<>();
        hallwayList = new ArrayList<>();
        start = new Coordinate(xCoordinate, yCoordinate);
        Room base = new Room(start, height, width);
        recursiveMakeRooms(base, 0);
    }

    public int getStartX() {
        return start.getX();
    }

    public int getStartY() {
        return start.getY();
    }

    /** Takes a world composed of tiles as an argument
     * and writes existing rooms and hallways onto the world.
     * Adds the player character to the world at start
     */
    public void drawWorld(TETile[][] world) {
        Set<Room> existingRooms = roomList.keySet();
        for (Room existingRoom : existingRooms) {
            existingRoom.addToWorld(world);
        }

        for (Hallway hallway : hallwayList) {
            hallway.writeHallToWorld(world);
        }
        int atX = start.getX() + 1;
        int atY = start.getY() - 1;
        world[atX][atY] = Engine.characterTile;
    }

    /** Method which recursively creates new rooms above, below,
     * to the left, and to the right of the last created room.
     *
     * Makes calls to other methods which ultimately create the new rooms.
     */
    public void recursiveMakeRooms(Room base, int branchLength) {
        if (base == null) {
            return;
        }
        recursiveMakeRooms(makeRoomLeft(base), branchLength + 1);
        recursiveMakeRooms(makeRoomRight(base), branchLength + 1);
        recursiveMakeRooms(makeRoomDown(base), branchLength + 1);
        recursiveMakeRooms(makeRoomUp(base), branchLength + 1);
    }

    /** Method which creates room below existing room.
     * Randomized distance from the existing room.
     */
    public Room makeRoomDown(Room parent) {
        int yDelta = randomInts.nextInt(10) + roomSpacingFactor;
        int yCoordinate = parent.getY() - yDelta;
        int xDelta = randomInts.nextInt(2 * roomOffsetFactor) - roomOffsetFactor;
        int xCoordinate = parent.getX() + xDelta;
        Coordinate topLeft = new Coordinate(xCoordinate, yCoordinate);
        if (topLeft.isValid()) {
            int nodeHeight = randomInts.nextInt(5) + minRoomHeight;
            int nodeWidth = randomInts.nextInt(5) + minRoomWidth;
            if (topLeft.getX() + nodeWidth >= WIDTH - 1 || topLeft.getY() - nodeHeight < 1
                    || topLeft.getX() < 1 || topLeft.getY() > HEIGHT - 1) {
                return null;
            } else if (overlaps(topLeft)) {
                return null;
            }
            Room newChild = new Room(topLeft, nodeHeight, nodeWidth);
            Hallway newPath = new Hallway(parent, newChild, "down");
            hallwayList.add(newPath);
            return newChild;
        }
        return null;
    }

    /** Method which creates room to the left of existing room.
     * Randomized distance from the existing room.
     */
    public Room makeRoomLeft(Room parent) {
        int yDelta = randomInts.nextInt(2 * roomOffsetFactor) - roomOffsetFactor;
        int yCoordinate = parent.getY() + yDelta;
        int xDelta = randomInts.nextInt(10) + roomSpacingFactor;
        int xCoordinate = parent.getX() - xDelta;
        Coordinate topLeft = new Coordinate(xCoordinate, yCoordinate);
        if (topLeft.isValid()) {
            int nodeHeight = randomInts.nextInt(5) + minRoomHeight;
            int nodeWidth = randomInts.nextInt(5) + minRoomWidth;
            if (topLeft.getX() + nodeWidth >= WIDTH - 1 || topLeft.getY() - nodeHeight < 1
                    || topLeft.getX() < 1 || topLeft.getY() > HEIGHT - 1) {
                return null;
            } else if (overlaps(topLeft)) {
                return null;
            }
            Room newChild = new Room(topLeft, nodeHeight, nodeWidth);
            Hallway newPath = new Hallway(parent, newChild, "left");
            hallwayList.add(newPath);
            return newChild;
        }
        return null;
    }

    /** Method which creates room above existing room.
     * Randomized distance from the existing room.
     */
    public Room makeRoomUp(Room parent) {
        int yDelta = randomInts.nextInt(10) + roomSpacingFactor;
        int yCoordinate = parent.getY() + yDelta;
        int xDelta = randomInts.nextInt(2 * roomOffsetFactor) - roomOffsetFactor;
        int xCoordinate = parent.getX() + xDelta;
        Coordinate topLeft = new Coordinate(xCoordinate, yCoordinate);
        if (topLeft.isValid()) {
            int nodeHeight = randomInts.nextInt(5) + minRoomHeight;
            int nodeWidth = randomInts.nextInt(5) + minRoomWidth;
            if (topLeft.getX() + nodeWidth >= WIDTH - 1 || topLeft.getY() - nodeHeight < 1
                    || topLeft.getX() < 1 || topLeft.getY() > HEIGHT - 1) {
                return null;
            } else if (overlaps(topLeft)) {
                return null;
            }
            Room newChild = new Room(topLeft, nodeHeight, nodeWidth);
            Hallway newPath = new Hallway(parent, newChild, "up");
            hallwayList.add(newPath);
            return newChild;
        }
        return null;
    }

    /** Method which creates room to the right of existing room.
     * Randomized distance from the existing room.
     */
    public Room makeRoomRight(Room parent) {
        int yDelta = randomInts.nextInt(2 * roomOffsetFactor) - roomOffsetFactor;
        int yCoordinate = parent.getY() + yDelta;
        int xDelta = randomInts.nextInt(10) + roomSpacingFactor;
        int xCoordinate = parent.getX() + xDelta;
        Coordinate topLeft = new Coordinate(xCoordinate, yCoordinate);
        if (topLeft.isValid()) {
            int nodeHeight = randomInts.nextInt(5) + minRoomHeight;
            int nodeWidth = randomInts.nextInt(5) + minRoomWidth;
            if (topLeft.getX() + nodeWidth >= WIDTH - 1 || topLeft.getY() - nodeHeight < 1
                    || topLeft.getX() < 1 || topLeft.getY() > HEIGHT - 1) {
                return null;
            } else if (overlaps(topLeft)) {
                return null;
            }
            Room newChild = new Room(topLeft, nodeHeight, nodeWidth);
            Hallway newPath = new Hallway(parent, newChild, "right");
            hallwayList.add(newPath);
            return newChild;
        }
        return null;
    }

    /** Checks if a new room's top left corner is within
     * a specified distance of the top left corner of other rooms.
     *
     * Returns true if it is within that distance, and false otherwise.
     */
    private boolean overlaps(Coordinate toCheck) {
        Collection<Coordinate> cornerSet = roomList.values();
        for (Coordinate coordinate : cornerSet) {
            if (toCheck.distTo(coordinate) < overlapConstant) {
                return true;
            }
        }
        return false;
    }

    /** Private class which contains all of the functionality for creating
     * and getting information about rooms.
     *
     * Each room has a coin placed randomly at roughly the center
     */
    private class Room {
        private ArrayList<Hallway> paths;
        private final int x, y;
        private final int nodeHeight, nodeWidth;
        private final int coinX, coinY;

        Room(Coordinate topLeftCorner, int newHeight, int newWidth) {
            paths = new ArrayList<>();
            x = topLeftCorner.getX();
            y = topLeftCorner.getY();
            nodeHeight = newHeight;
            nodeWidth = newWidth;
            //Check for weird bugs where the coin gets inside the wall
            coinX = x + newWidth / 2;
            coinY = y - newHeight / 2;
            roomList.put(this, topLeftCorner);
        }

        public void addToWorld(TETile[][] world) {
            for (int i = 0; i <= nodeWidth; i++) {
                world[x + i][y] = Tileset.WALL;
                world[x + i][y - nodeHeight] = Tileset.WALL;
                for (int j = 1; j < nodeHeight; j++) {
                    world[x + i][y - j] = Tileset.FLOOR;
                }
            }

            for (int i = nodeHeight; i >= 0; i--) {
                world[x][y - i] = Tileset.WALL;
                world[x + nodeWidth][y - i] = Tileset.WALL;
            }

            world[coinX][coinY] = Tileset.COIN;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    /** Private class for keeping track of (x, y) coordinate pairs.
     * (I made this public since I need it in Engine)
     *
     * Primarily used to store corners of rooms and hallways.
     */
    public class Coordinate {
        private final int x, y;

        Coordinate(int xCoordinate, int yCoordinate) {
            x = xCoordinate;
            y = yCoordinate;
        }

        public double distTo(Coordinate other) {
            double xDist = Math.pow((this.getX() - other.getX()), 2);
            double yDist = Math.pow((this.getY() - other.getY()), 2);
            return Math.sqrt(xDist + yDist);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isValid() {
            if (x >= WIDTH || x < 0 || y >= HEIGHT || y < 0) {
                return false;
            }
            return true;
        }
    }

    /** Private class for keeping track of the different hallways between rooms.
     *
     * Currently set up to have one room as a 'parent' and one as a 'child',
     * and will be set up as the rooms are spawned from the original base room,
     * which will ensure that all rooms are connected in the final world.
     *
     * Recursively makes pathways between parent and child nodes
     * by setting doorways in specified, adjacent room walls
     * and connecting them via hallways designed to turn when
     * they get halfway to their destination.
     */
    private class Hallway {
        private final Room parent, child;
        private final String startDirection;
        private final Coordinate startHall, endHall;

        Hallway(Room buildParent, Room buildChild, String buildDirection) {
            parent = buildParent;
            child = buildChild;
            startDirection = buildDirection;
            int xStart = 0;
            int yStart = 0;
            int xEnd = 0;
            int yEnd = 0;

            switch (startDirection) {
                case "left" -> {
                    xStart = parent.getX();
                    yStart = (2 * parent.getY() - parent.nodeHeight) / 2;
                    xEnd = child.getX() + child.nodeWidth;
                    yEnd = (2 * child.getY() - child.nodeHeight) / 2;
                }
                case "right" -> {
                    xStart = parent.getX() + parent.nodeWidth;
                    yStart = (2 * parent.getY() - parent.nodeHeight) / 2;
                    xEnd = child.getX();
                    yEnd = (2 * child.getY() - child.nodeHeight) / 2;
                }
                case "up" -> {
                    xStart = (2 * parent.getX() + parent.nodeWidth) / 2;
                    yStart = parent.getY();
                    xEnd = (2 * child.getX() + child.nodeWidth) / 2;
                    yEnd = child.getY() - child.nodeHeight;
                }
                case "down" -> {
                    xStart = (2 * parent.getX() + parent.nodeWidth) / 2;
                    yStart = parent.getY() - parent.nodeHeight;
                    xEnd = (2 * child.getX() + child.nodeWidth) / 2;
                    yEnd = child.getY();
                }
                default -> System.out.println("Not a valid path.");
            }

            startHall = new Coordinate(xStart, yStart);
            endHall = new Coordinate(xEnd, yEnd);
        }

        public void writeHallToWorld(TETile[][] world) {
            switch (startDirection) {
                case "left" -> writeLeftHall(startHall, endHall, world);
                case "right" -> writeRightHall(startHall, endHall, world);
                case "up" -> writeUpHall(startHall, endHall, world);
                case "down" -> writeDownHall(startHall, endHall, world);
            }
        }

        public void writeDownHall(Coordinate current, Coordinate target, TETile[][] world) {
            if ((current.getY() == ((startHall.getY() + endHall.getY()) / 2) && current.getX() == startHall.getX()
                    && current.getX() != endHall.getX()) || current.getY() == target.getY()) {
                if (current.getX() == target.getX()) {
                    writeVerticalHallSegment(current, world);
                    return;
                } else if (current.getX() < target.getX()) {
                    northeastTurn(current, world);
                    Coordinate newCoordinate = new Coordinate(current.getX() + 1, current.getY());
                    writeRightHall(newCoordinate, target, world);
                    return;
                } else if (current.getX() > target.getX()) {
                    northwestTurn(current, world);
                    Coordinate newCoordinate = new Coordinate(current.getX() - 1, current.getY());
                    writeLeftHall(newCoordinate, target, world);
                    return;
                }
            }

            writeVerticalHallSegment(current, world);
            Coordinate newCoordinate = new Coordinate(current.getX(), current.getY() - 1);
            writeDownHall(newCoordinate, target, world);
        }

        public void writeUpHall(Coordinate current, Coordinate target, TETile[][] world) {
            if ((current.getY() == ((startHall.getY() + endHall.getY()) / 2) && current.getX() == startHall.getX()
                    && current.getX() != endHall.getX()) || current.getY() == target.getY()) {
                if (current.getX() == target.getX()) {
                    writeVerticalHallSegment(current, world);
                    return;
                } else if (current.getX() < target.getX()) {
                    southeastTurn(current, world);
                    Coordinate newCoordinate = new Coordinate(current.getX() + 1, current.getY());
                    writeRightHall(newCoordinate, target, world);
                    return;
                } else if (current.getX() > target.getX()) {
                    southwestTurn(current, world);
                    Coordinate newCoordinate = new Coordinate(current.getX() - 1, current.getY());
                    writeLeftHall(newCoordinate, target, world);
                    return;
                }
            }

            writeVerticalHallSegment(current, world);
            Coordinate newCoordinate = new Coordinate(current.getX(), current.getY() + 1);
            writeUpHall(newCoordinate, target, world);
        }

        public void writeLeftHall(Coordinate current, Coordinate target, TETile[][] world) {
            if ((current.getX() == ((startHall.getX() + endHall.getX()) / 2) && current.getY() == startHall.getY()
                    && current.getY() != endHall.getY()) || current.getX() == target.getX()) {
                if (current.getY() == target.getY()) {
                    writeHorizontalHallSegment(current, world);
                    return;
                } else if (current.getY() < target.getY()) {
                    northeastTurn(current, world);
                    Coordinate newCoordinate = new Coordinate(current.getX(), current.getY() + 1);
                    writeUpHall(newCoordinate, target, world);
                    return;
                } else if (current.getY() > target.getY()) {
                    southeastTurn(current, world);
                    Coordinate newCoordinate = new Coordinate(current.getX(), current.getY() - 1);
                    writeDownHall(newCoordinate, target, world);
                    return;
                }
            }

            writeHorizontalHallSegment(current, world);
            Coordinate newCoordinate = new Coordinate(current.getX() - 1, current.getY());
            writeLeftHall(newCoordinate, target, world);
        }

        public void writeRightHall(Coordinate current, Coordinate target, TETile[][] world) {
            if ((current.getX() == ((startHall.getX() + endHall.getX()) / 2) && current.getY() == startHall.getY()
                    && current.getY() != endHall.getY()) || current.getX() == target.getX()) {
                if (current.getY() == target.getY()) {
                    writeHorizontalHallSegment(current, world);
                    return;
                } else if (current.getY() < target.getY()) {
                    northwestTurn(current, world);
                    Coordinate newCoordinate = new Coordinate(current.getX(), current.getY() + 1);
                    writeUpHall(newCoordinate, target, world);
                    return;
                } else if (current.getY() > target.getY()) {
                    southwestTurn(current, world);
                    Coordinate newCoordinate = new Coordinate(current.getX(), current.getY() - 1);
                    writeDownHall(newCoordinate, target, world);
                    return;
                }
            }

            writeHorizontalHallSegment(current, world);
            Coordinate newCoordinate = new Coordinate(current.getX() + 1, current.getY());
            writeRightHall(newCoordinate, target, world);
        }

        private void northwestTurn(Coordinate center, TETile[][] world) {
            int xCenter = center.getX();
            int yCenter = center.getY();

            world[xCenter - 1][yCenter] = Tileset.FLOOR;
            world[xCenter][yCenter + 1] = Tileset.FLOOR;
            world[xCenter][yCenter] = Tileset.FLOOR;
            world[xCenter][yCenter - 1] = Tileset.WALL;
            world[xCenter + 1][yCenter] = Tileset.WALL;
            world[xCenter + 1][yCenter - 1] = Tileset.WALL;
        }

        private void northeastTurn(Coordinate center, TETile[][] world) {
            int xCenter = center.getX();
            int yCenter = center.getY();

            world[xCenter - 1][yCenter] = Tileset.WALL;
            world[xCenter - 1][yCenter - 1] = Tileset.WALL;
            world[xCenter][yCenter + 1] = Tileset.FLOOR;
            world[xCenter][yCenter] = Tileset.FLOOR;
            world[xCenter][yCenter - 1] = Tileset.WALL;
            world[xCenter + 1][yCenter] = Tileset.FLOOR;
        }

        private void southwestTurn(Coordinate center, TETile[][] world) {
            int xCenter = center.getX();
            int yCenter = center.getY();

            world[xCenter - 1][yCenter] = Tileset.FLOOR;
            world[xCenter][yCenter + 1] = Tileset.WALL;
            world[xCenter][yCenter] = Tileset.FLOOR;
            world[xCenter][yCenter - 1] = Tileset.FLOOR;
            world[xCenter + 1][yCenter + 1] = Tileset.WALL;
            world[xCenter + 1][yCenter] = Tileset.WALL;
        }

        private void southeastTurn(Coordinate center, TETile[][] world) {
            int xCenter = center.getX();
            int yCenter = center.getY();

            world[xCenter - 1][yCenter + 1] = Tileset.WALL;
            world[xCenter - 1][yCenter] = Tileset.WALL;
            world[xCenter][yCenter + 1] = Tileset.WALL;
            world[xCenter][yCenter] = Tileset.FLOOR;
            world[xCenter][yCenter - 1] = Tileset.FLOOR;
            world[xCenter + 1][yCenter] = Tileset.FLOOR;
        }

        private void writeVerticalHallSegment(Coordinate dest, TETile[][] world) {
            int xCenter = dest.getX();
            int yCenter = dest.getY();

            world[xCenter - 1][yCenter] = Tileset.WALL;
            world[xCenter][yCenter] = Tileset.FLOOR;
            world[xCenter + 1][yCenter] = Tileset.WALL;
        }

        private void writeHorizontalHallSegment(Coordinate dest, TETile[][] world) {
            int xCenter = dest.getX();
            int yCenter = dest.getY();

            world[xCenter][yCenter - 1] = Tileset.WALL;
            world[xCenter][yCenter] = Tileset.FLOOR;
            world[xCenter][yCenter + 1] = Tileset.WALL;
        }
    }
}