package net.runeage.simpledungeongenerator.objects;

import net.runeage.simpledungeongenerator.objects.generation.DungeonChunk;
import net.runeage.simpledungeongenerator.objects.generation.DungeonRoom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DungeonFloor {

    private String name;
    private String tileset;
    private String world;
    private DungeonFloorConfiguration dungeonFloorConfiguration;

    private List<DungeonRoom> rooms;
    private HashSet<DungeonChunk> takenChunks;
    private HashSet<DungeonChunk> fillers;

    private boolean ready = false;

    public DungeonFloor(String name, String tileset, String world, DungeonFloorConfiguration dungeonFloorConfiguration){
        setName(name);
        setTileset(tileset);
        setWorld(world);
        setDungeonFloorConfiguration(dungeonFloorConfiguration);

        rooms = new ArrayList<>();
        takenChunks = new HashSet<>();
        fillers = new HashSet<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTileset(String tileset) {
        this.tileset = tileset;
    }

    public void setRooms(ArrayList<DungeonRoom> rooms) {
        this.rooms = rooms;
    }

    public void setTakenChunks(HashSet<DungeonChunk> takenChunks) {
        this.takenChunks = takenChunks;
    }

    public void setFillers(HashSet<DungeonChunk> fillers) {
        this.fillers = fillers;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setDungeonFloorConfiguration(DungeonFloorConfiguration dungeonFloorConfiguration) {
        this.dungeonFloorConfiguration = dungeonFloorConfiguration;
    }

    public String getName() {
        return name;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getTileset() {
        return tileset;
    }

    public List<DungeonRoom> getRooms() {
        return rooms;
    }

    public HashSet<DungeonChunk> getTakenChunks() {
        return takenChunks;
    }

    public HashSet<DungeonChunk> getFillers() {
        return fillers;
    }

    public String getWorld() {
        return world;
    }

    public DungeonFloorConfiguration getDungeonFloorConfiguration() {
        return dungeonFloorConfiguration;
    }

    public boolean isReady() {
        return ready;
    }

    public void addRoom(DungeonRoom dungeonRoom){
        rooms.add(dungeonRoom);
        addChunks(dungeonRoom.getChunks());
    }

    public void removeRoom(DungeonRoom dungeonRoom){
        rooms.remove(dungeonRoom);
    }

    public void addChunks(List<DungeonChunk> list){
        takenChunks.addAll(list);
    }

    @Override
    public String toString() {
        return "DungeonFloor{" +
                "name='" + name + '\'' +
                ", tileset='" + tileset + '\'' +
                ", world='" + world + '\'' +
                ", dungeonFloorConfiguration=" + dungeonFloorConfiguration +
                ", rooms=" + rooms +
                ", ready=" + ready +
                '}';
    }
}
