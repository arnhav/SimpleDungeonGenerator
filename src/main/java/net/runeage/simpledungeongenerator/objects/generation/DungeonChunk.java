package net.runeage.simpledungeongenerator.objects.generation;

import java.util.Objects;

public class DungeonChunk {

    private String world;
    private int x, z, level;

    public DungeonChunk(String world, int x, int z, int level){
        setWorld(world);
        setX(x);
        setZ(z);
        setLevel(level);
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DungeonChunk that = (DungeonChunk) o;
        return x == that.x &&
                z == that.z &&
                level == that.level &&
                world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, z, level);
    }

    @Override
    public String toString() {
        return "DungeonChunk{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", z=" + z +
                ", level=" + level +
                '}';
    }
}
