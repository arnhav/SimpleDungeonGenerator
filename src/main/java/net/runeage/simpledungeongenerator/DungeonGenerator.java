package net.runeage.simpledungeongenerator.util;

import net.runeage.simpledungeongenerator.SimpleDungeonGenerator;
import net.runeage.simpledungeongenerator.api.DungeonRoomPasteEvent;
import net.runeage.simpledungeongenerator.data.FileManager;
import net.runeage.simpledungeongenerator.objects.DungeonFloor;
import net.runeage.simpledungeongenerator.objects.DungeonFloorConfiguration;
import net.runeage.simpledungeongenerator.objects.EmptyChunkGenerator;
import net.runeage.simpledungeongenerator.objects.generation.DungeonChunk;
import net.runeage.simpledungeongenerator.objects.generation.DungeonRoom;
import net.runeage.simpledungeongenerator.objects.generation.RoomConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class DungeonGenerator {

    public static CompletableFuture<DungeonFloor> generateDungeon(String tileset, String worldName) {
        CompletableFuture<DungeonFloor> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(SimpleDungeonGenerator.instance(), ()->{
            File tilesetFolder = new File(FileManager.getTilesetsFolder(), tileset);
            DungeonFloorConfiguration dungeonFloorConfiguration = FileManager.readTilesetConfig(tilesetFolder);
            if (dungeonFloorConfiguration == null) {
                SimpleDungeonGenerator.instance().getLogger().severe("Tileset Config File not loaded!");
                return;
            }
            List<RoomConfiguration> rooms = dungeonFloorConfiguration.getRooms();
            if (rooms == null || rooms.isEmpty()) {
                SimpleDungeonGenerator.instance().getLogger().severe("Tileset Config File error!");
                return;
            }

            DungeonFloor dungeonFloor = new DungeonFloor(worldName, tileset, worldName, dungeonFloorConfiguration);

            RoomConfiguration roomConfig = RoomConfigurationUtil.findAndSelectStartRoom(rooms);
            if (roomConfig == null) {
                SimpleDungeonGenerator.instance().getLogger().severe("No Start Room defined!");
                return;
            }
            DungeonChunk startChunk = new DungeonChunk(worldName, 0, 0, 0);
            List<DungeonChunk> chunks = RoomConfigurationUtil.getChunksForRoomConfiguration(roomConfig, startChunk);
            DungeonRoom start = new DungeonRoom(chunks, roomConfig);
            dungeonFloor.addRoom(start);

            DungeonFloorUtil.generateNextRoom(dungeonFloor, start, 0);
            DungeonFloorUtil.generateBossRoom(dungeonFloor);
            DungeonFloorUtil.generateEndCaps(dungeonFloor);
            DungeonFloorUtil.updateRooms(dungeonFloor);
            DungeonFloorUtil.collectSurroundingEmptyChunks(dungeonFloor);

            dungeonFloor.setDungeonFloorConfiguration(dungeonFloorConfiguration);
            future.complete(dungeonFloor);
        });
        return future;
    }

    public static boolean createWorld(String worldName) {
        System.out.println("Creating world...");
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.generator(new EmptyChunkGenerator());

        World world = worldCreator.createWorld();
        if (world == null) {
            SimpleDungeonGenerator.instance().getLogger().severe("World Creation Error!");
            return false;
        }
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        System.out.println("World created...");
        return true;
    }

    public static boolean placeFillers(DungeonFloor dungeonFloor) {
        DungeonFloorConfiguration dfc = dungeonFloor.getDungeonFloorConfiguration();
        if (dfc == null || dfc.getFiller().equals("")) {
            dungeonFloor.setReady(true);
            return true;
        }
        File tilesetFolder = new File(FileManager.getTilesetsFolder(), dungeonFloor.getTileset());
        World world = Bukkit.getWorld(dungeonFloor.getWorld());
        if (world == null) {
            SimpleDungeonGenerator.instance().getLogger().severe("World not created!");
            return false;
        }
        System.out.println("Pasting filler...");
        LinkedBlockingQueue<DungeonChunk> toPaste = new LinkedBlockingQueue<>(dungeonFloor.getFillers());
        Bukkit.getScheduler().runTaskLater(SimpleDungeonGenerator.instance(), new Runnable() {
            @Override
            public void run() {
                if (toPaste.isEmpty()) {
                    System.out.println("Done pasting filler...");
                    dungeonFloor.setReady(true);
                    return;
                }

                int count = 0;
                while (count < 10) {
                    count++;
                    DungeonChunk chunk = toPaste.poll();
                    if (chunk == null) continue;
                    int level = dfc.getFillerLevel();
                    String fileName = dfc.getFiller();
                    world.isChunkGenerated(chunk.getX(), chunk.getZ());
                    int x = chunk.getX() * 16, y = (level * 16), z = chunk.getZ() * 16;
                    if (!world.isChunkLoaded(chunk.getX(), chunk.getZ())) {
                        world.getChunkAtAsync(chunk.getX(), chunk.getZ()).thenAccept(c -> WEUtils.pasteFile(tilesetFolder, fileName, world, x, y, z));
                    } else {
                        WEUtils.pasteFile(tilesetFolder, fileName, world, x, y, z);
                    }
                }

                Bukkit.getScheduler().runTaskLater(SimpleDungeonGenerator.instance(), this, 20);
            }
        }, 0);
        return true;
    }

    public static boolean placeRooms(DungeonFloor dungeonFloor) {
        File tilesetFolder = new File(FileManager.getTilesetsFolder(), dungeonFloor.getTileset());
        World world = Bukkit.getWorld(dungeonFloor.getWorld());
        if (world == null) {
            SimpleDungeonGenerator.instance().getLogger().severe("World not created!");
            return false;
        }
        System.out.println("Pasting rooms...");
        LinkedBlockingQueue<DungeonRoom> roomsToPaste = new LinkedBlockingQueue<>(dungeonFloor.getRooms());
        Bukkit.getScheduler().runTaskLater(SimpleDungeonGenerator.instance(), new Runnable() {
            @Override
            public void run() {
                if (roomsToPaste.isEmpty()) {
                    System.out.println("Done pasting rooms...");
                    placeFillers(dungeonFloor);
                    return;
                }

                int count = 0;
                while (count < 5) {
                    count++;
                    DungeonRoom room = roomsToPaste.poll();
                    if (room == null) continue;
                    DungeonChunk chunk = room.getPasteChunk();
                    if (chunk == null) continue;
                    int level = chunk.getLevel();
                    String fileName = room.getRoomConfiguration().getFileName();
                    world.isChunkGenerated(chunk.getX(), chunk.getZ());
                    int x = chunk.getX() * 16, y = (level * 16), z = chunk.getZ() * 16;
                    if (!world.isChunkLoaded(chunk.getX(), chunk.getZ())) {
                        world.getChunkAtAsync(chunk.getX(), chunk.getZ()).thenAccept(c ->
                                WEUtils.pasteFile(tilesetFolder, fileName, world, x, y, z).thenAccept(v -> {
                                    room.setPasted(true);
                                    DungeonRoomPasteEvent drpe = new DungeonRoomPasteEvent(world, dungeonFloor, room);
                                    Bukkit.getServer().getPluginManager().callEvent(drpe);
                                })
                        );
                    } else {
                        WEUtils.pasteFile(tilesetFolder, fileName, world, x, y, z).thenAccept(v ->{
                            room.setPasted(true);
                            DungeonRoomPasteEvent drpe = new DungeonRoomPasteEvent(world, dungeonFloor, room);
                            Bukkit.getServer().getPluginManager().callEvent(drpe);
                        });
                    }

                    FileManager.log((dungeonFloor.getRooms().indexOf(room) + 1) + "/" + dungeonFloor.getRooms().size() + " completed...");
                }

                Bukkit.getScheduler().runTaskLater(SimpleDungeonGenerator.instance(), this, 20);
            }
        }, 0);
        return true;
    }
}