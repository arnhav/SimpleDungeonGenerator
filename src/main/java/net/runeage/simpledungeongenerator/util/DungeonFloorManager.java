package net.runeage.simpledungeongenerator.util;

import net.runeage.simpledungeongenerator.data.FileManager;
import net.runeage.simpledungeongenerator.objects.DungeonFloor;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.HashSet;

public class DungeonFloorManager {

    public static HashSet<DungeonFloor> dungeonFloors = new HashSet<>();

    public static boolean createDungeonFloorWorld(DungeonFloor df){
        if (df == null) return false;
        String worldName = "SDG_" + dungeonFloors.size();
        if (!DungeonGenerator.createWorld(worldName)) return false;
        df.setName(worldName);
        df.setWorld(worldName);
        if (!DungeonGenerator.placeRooms(df)) return false;
        dungeonFloors.add(df);
        return true;
    }

    public static boolean deleteDungeonFloorWorld(DungeonFloor df){
        World world = Bukkit.getWorld(df.getWorld());
        if (world == null) return false;
        if (world.getPlayerCount() > 0) return false;
        Bukkit.unloadWorld(world, false);
        File worldFile = new File(Bukkit.getWorldContainer(), df.getWorld());
        FileManager.deleteDirectory(worldFile);
        dungeonFloors.remove(df);
        System.out.println("Dungeon Floor " + df.getName() + " deleted.");
        return true;
    }

    public static boolean isTileSetPresent(String tileset) {
        File[] files = FileManager.getTilesetsFolder().listFiles();
        if (files == null) return false;
        for (File file : files){
            if (file.getName().equalsIgnoreCase(tileset)) return true;
        }
        return false;
    }

    public static boolean isDungeonWorld(World world) {
        return getDungeonFloor(world) != null;
    }

    public static DungeonFloor getDungeonFloor(World world) {
        for (DungeonFloor df : dungeonFloors){
            if (world.getName().equals(df.getName())) return df;
        }
        return null;
    }

}
