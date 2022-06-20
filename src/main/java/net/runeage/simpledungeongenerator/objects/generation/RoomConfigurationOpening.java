package net.runeage.simpledungeongenerator.objects.generation;

import com.sk89q.worldedit.util.Direction;

public class RoomConfigurationOpening {

    private int x, y, z;
    private Direction direction;

    public RoomConfigurationOpening(int x, int y, int z, Direction direction) {
        setX(x);
        setY(y);
        setZ(z);
        setDirection(direction);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "RoomConfigurationOpening{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", direction=" + direction +
                '}';
    }
}
