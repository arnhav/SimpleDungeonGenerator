package net.runeage.simpledungeongenerator.objects;

import net.runeage.simpledungeongenerator.objects.generation.RoomConfiguration;

import java.util.List;

public class DungeonFloorConfiguration {

    private int pathLength, fillerLevel;
    private String filler;
    private List<RoomConfiguration> rooms;

    public DungeonFloorConfiguration(){}

    public void setPathLength(int pathLength) {
        this.pathLength = pathLength;
    }

    public void setFillerLevel(int fillerLevel) {
        this.fillerLevel = fillerLevel;
    }

    public void setFiller(String filler) {
        this.filler = filler;
    }

    public void setRooms(List<RoomConfiguration> rooms) {
        this.rooms = rooms;
    }

    public int getPathLength() {
        return pathLength;
    }

    public int getFillerLevel() {
        return fillerLevel;
    }

    public String getFiller() {
        return filler;
    }

    public List<RoomConfiguration> getRooms() {
        return rooms;
    }
}