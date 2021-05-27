package net.runeage.simpledungeongenerator.objects.generation;

import com.sk89q.worldedit.util.Direction;
import net.runeage.simpledungeongenerator.objects.enums.RoomType;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RoomConfiguration {

    private String fileName;
    private RoomType roomType;
    private int limit;
    private List<String> incompat;
    private int sizeX, sizeY, sizeZ;
    private HashMap<Direction, RoomConfigurationOpening> openings;

    public RoomConfiguration(String fileName, RoomType roomType, int limit, List<String> incompat, int sizeX, int sizeY, int sizeZ, HashMap<Direction, RoomConfigurationOpening> openings){
        setFileName(fileName);
        setRoomType(roomType);
        setLimit(limit);
        setIncompat(incompat);
        setSizeX(sizeX);
        setSizeY(sizeY);
        setSizeZ(sizeZ);
        setOpenings(openings);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setIncompat(List<String> incompat) {
        this.incompat = incompat;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public void setSizeZ(int sizeZ) {
        this.sizeZ = sizeZ;
    }

    public void setOpenings(HashMap<Direction, RoomConfigurationOpening> openings) {
        this.openings = openings;
    }

    public String getFileName() {
        return fileName;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public int getLimit() {
        return limit;
    }

    public List<String> getIncompat() {
        return incompat;
    }

    public int getSizeX() {
        return sizeX-1;
    }

    public int getSizeY() {
        return sizeY-1;
    }

    public int getSizeZ() {
        return sizeZ-1;
    }

    public HashMap<Direction, RoomConfigurationOpening> getOpenings() {
        return openings;
    }

    @Override
    public String toString() {
        return "RoomConfiguration{" +
                "fileName='" + fileName + '\'' +
                ", sizeX=" + sizeX +
                ", sizeY=" + sizeY +
                ", sizeZ=" + sizeZ +
                ", roomType=" + roomType +
                ", openings=" + openings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomConfiguration that = (RoomConfiguration) o;
        return sizeX == that.sizeX &&
                sizeY == that.sizeY &&
                sizeZ == that.sizeZ &&
                fileName.equals(that.fileName) &&
                roomType == that.roomType &&
                openings.equals(that.openings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, sizeX, sizeY, sizeZ, roomType, openings);
    }
}
