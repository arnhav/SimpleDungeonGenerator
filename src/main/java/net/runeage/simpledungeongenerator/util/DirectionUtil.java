package net.runeage.simpledungeongenerator.util;

import com.sk89q.worldedit.util.Direction;

public class DirectionUtil {

    public static Direction getInverse(Direction direction){
        if (direction == Direction.NORTH)
            return Direction.SOUTH;
        if (direction == Direction.SOUTH)
            return Direction.NORTH;
        if (direction == Direction.EAST)
            return Direction.WEST;
        if (direction == Direction.WEST)
            return Direction.EAST;
        return Direction.NORTH;
    }
}
