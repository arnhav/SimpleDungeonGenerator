package net.runeage.simpledungeongenerator.data;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.EnumProperty;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import net.runeage.simpledungeongenerator.SimpleDungeonGenerator;
import net.runeage.simpledungeongenerator.objects.DungeonFloorConfiguration;
import net.runeage.simpledungeongenerator.objects.enums.RoomType;
import net.runeage.simpledungeongenerator.objects.generation.RoomConfiguration;
import net.runeage.simpledungeongenerator.objects.generation.RoomConfigurationOpening;
import net.runeage.simpledungeongenerator.util.WEUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FileManager {

    private static File log;

    public FileManager(JavaPlugin plugin){
        createConfig(plugin);
        createLogFile(plugin);
    }

    public void createConfig(JavaPlugin plugin){
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            File file = new File(plugin.getDataFolder(), "config.yml");
            if (!file.exists()) {
                plugin.getLogger().info("config.yml not found, creating!");
                plugin.saveDefaultConfig();
            } else {
                plugin.getLogger().info("config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createLogFile(JavaPlugin plugin) {
        File logsFolder = new File(plugin.getDataFolder(), "logs");
        if (!logsFolder.exists()){
            logsFolder.mkdirs();
        }
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        log = new File(logsFolder, format.format(date) + ".txt");
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(log, true);
            PrintWriter pw = new PrintWriter(fileWriter);
            pw.println("-=== SimpleDungeons Log " + format.format(date) + " ===-");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getTilesetsFolder(){
        return new File(SimpleDungeonGenerator.instance().getDataFolder(), "tilesets");
    }

    public static DungeonFloorConfiguration readTilesetConfig(File folder){
        DungeonFloorConfiguration dungeonFloorConfiguration = new DungeonFloorConfiguration();
        File file = new File(folder, "config.yml");
        if (!file.exists()) return null;
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

        if (fileConfiguration.isSet("PathLength")) {
            int pathLength = fileConfiguration.getInt("PathLength");
            dungeonFloorConfiguration.setPathLength(pathLength);
        } else {
            fileConfiguration.set("PathLength", 20);
            save(fileConfiguration, file);
            dungeonFloorConfiguration.setPathLength(20);
        }

        ConfigurationSection roomSection = fileConfiguration.getConfigurationSection("Rooms");
        List<RoomConfiguration> rooms = new ArrayList<>();
        if (roomSection != null) {
            for (String path : roomSection.getKeys(false)) {
                RoomType roomType = RoomType.valueOf(roomSection.getString(path + ".RoomType"));
                int limit = roomSection.getInt(path + ".Limit");
                List<String> incompat = roomSection.getStringList(path + ".Incompat");
                int sx = roomSection.getInt(path + ".Size.x");
                int sy = roomSection.getInt(path + ".Size.y");
                int sz = roomSection.getInt(path + ".Size.z");

                HashMap<Direction, RoomConfigurationOpening> openings = new HashMap<>();
                ConfigurationSection openingsSection = fileConfiguration.getConfigurationSection("Rooms." + path + ".Openings");
                if (openingsSection == null) continue;
                for (String dir : openingsSection.getKeys(false)){
                    try {
                        Direction direction = Direction.valueOf(dir);
                        int x = openingsSection.getInt(dir + ".x");
                        int y = openingsSection.getInt(dir + ".y");
                        int z = openingsSection.getInt(dir + ".z");

                        RoomConfigurationOpening opening = new RoomConfigurationOpening(x, y, z, direction);
                        openings.put(direction, opening);
                    } catch (IllegalArgumentException e){
                        SimpleDungeonGenerator.instance().getLogger().severe(dir + " is not a valid direction!");
                        return null;
                    }
                }

                if (limit <= 0)
                    limit = -1;

                RoomConfiguration roomConfiguration = new RoomConfiguration(path, roomType, limit, incompat, sx, sy, sz, openings);
                rooms.add(roomConfiguration);
            }
        }
        dungeonFloorConfiguration.setRooms(rooms);
        return dungeonFloorConfiguration;
    }

    public static void createTilesetConfig(String tileSet){
        File folder = new File(getTilesetsFolder(), tileSet);
        File file = new File(folder, "config.yml");
        if (file.exists()) return;
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        File[] files = folder.listFiles();
        if (files == null) return;
        for (int i = 0; i < files.length; i++){
            File f = files[i];
            String path = f.getName().replace(".schem", "");
            fc.set("Rooms."+path+".RoomType", RoomType.GENERIC.toString());
            fc.set("Rooms."+path+".Limit", -1);

            Clipboard clipboard = WEUtils.loadSchem(f);
            if (clipboard == null) continue;
            BlockVector3 bv3 = clipboard.getDimensions();
            fc.set("Rooms."+path+".Size.x", bv3.getBlockX()/16+(bv3.getBlockX()%16==0?0:1));
            fc.set("Rooms."+path+".Size.y", bv3.getBlockY()/16+(bv3.getBlockY()%16==0?0:1));
            fc.set("Rooms."+path+".Size.z", bv3.getBlockZ()/16+(bv3.getBlockZ()%16==0?0:1));

            for (BlockVector3 point : clipboard.getRegion()){
                BaseBlock baseBlock = clipboard.getFullBlock(point);
                if (!baseBlock.getBlockType().getId().equals("minecraft:jigsaw")) continue;
                BlockType jigsaw = baseBlock.getBlockType();
                BlockState blockState = clipboard.getBlock(point);
                String o = blockState.getState(jigsaw.getProperty("orientation")).toString().split("_")[0];
                if (o.equals("up") || o.equals("down")) continue;
                int ox  = point.getX() - clipboard.getMinimumPoint().getX();
                int oy  = point.getY() - clipboard.getMinimumPoint().getY();
                int oz  = point.getZ() - clipboard.getMinimumPoint().getZ();
                String direction = o.toUpperCase();
                fc.set("Rooms."+path+".Openings."+direction+".x", ox/16);
                fc.set("Rooms."+path+".Openings."+direction+".y", oy/16);
                fc.set("Rooms."+path+".Openings."+direction+".z", oz/16);
            }
        }
        save(fc, file);
    }

    public static void log(String message){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(log, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(PrintWriter pw = new PrintWriter(fileWriter) ) {
            pw.println("[" + format.format(date) + "] " + message);
        }
    }

    private static void save(FileConfiguration fileConfiguration, File file){
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }


}
