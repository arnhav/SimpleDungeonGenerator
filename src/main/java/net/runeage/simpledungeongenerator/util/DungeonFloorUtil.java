package net.runeage.simpledungeongenerator.util;

import com.sk89q.worldedit.util.Direction;
import net.runeage.simpledungeongenerator.objects.DungeonFloor;
import net.runeage.simpledungeongenerator.objects.enums.RoomType;
import net.runeage.simpledungeongenerator.objects.generation.DungeonChunk;
import net.runeage.simpledungeongenerator.objects.generation.DungeonRoom;
import net.runeage.simpledungeongenerator.objects.generation.RoomConfiguration;
import net.runeage.simpledungeongenerator.objects.generation.RoomConfigurationOpening;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DungeonFloorUtil {

    public static void generateNextRoom(DungeonFloor dungeonFloor, DungeonRoom pr, int count){
        RoomConfiguration prc = pr.getRoomConfiguration();
        HashMap<Direction, RoomConfigurationOpening> prcos = prc.getOpenings();
        for (Direction direction : prcos.keySet()){
            RoomConfigurationOpening prco = prcos.get(direction);
            DungeonChunk popening = RoomConfigurationUtil.getDungeonChunkForOpening(pr.getPasteChunk(), prco);

            DungeonChunk nextChunk = getNextChunkInDirection(popening, direction, 1);

            Direction inverse = DirectionUtil.getInverse(direction);

            List<RoomConfiguration> potentialConfigs = RoomConfigurationUtil.getAllRoomsOpenInDirection(dungeonFloor, prc, inverse);

            DungeonRoom room = setRoom(dungeonFloor, potentialConfigs, nextChunk, inverse);
            if (room == null) continue;
            count++;
            if (count > dungeonFloor.getDungeonFloorConfiguration().getPathLength()) return;
            generateNextRoom(dungeonFloor, room, count);
        }
    }

    public static void generateBossRoom(DungeonFloor dungeonFloor){
        List<RoomConfiguration> bossRoomConfigs = RoomConfigurationUtil.getBossRooms(dungeonFloor.getDungeonFloorConfiguration().getRooms());
        HashMap<DungeonChunk, Direction> endCaps = getEndCapsWithDirection(dungeonFloor);
        endCaps.putAll(getAllEndCaps(dungeonFloor));
        for (RoomConfiguration brc : bossRoomConfigs) {
            if (doesDungeonHaveRoomType(dungeonFloor, RoomType.BOSS)) continue;
            for (DungeonChunk ec : endCaps.keySet()) {
                Direction direction = endCaps.get(ec);
                if (!brc.getOpenings().containsKey(direction)) continue;
                DungeonChunk nwb = RoomConfigurationUtil.getNWBMostCorner(ec, brc.getOpenings().get(direction));
                if (nwb.getLevel() < 0) continue;
                List<DungeonChunk> chunks = RoomConfigurationUtil.getChunksForRoomConfiguration(brc, nwb);
                if (areAnyChunksAlreadyRooms(dungeonFloor, chunks)) continue;

                DungeonRoom room = new DungeonRoom(chunks, brc);
                dungeonFloor.addRoom(room);
                return;
            }
        }
    }

    public static void generateEndCaps(DungeonFloor dungeonFloor){
        HashMap<DungeonChunk, Direction> endCaps = getEndCapsWithDirection(dungeonFloor);
        for (DungeonChunk ec : endCaps.keySet()) {
            Direction direction = endCaps.get(ec);
            List<RoomConfiguration> ecConfigs = RoomConfigurationUtil.getEndCapRoomsInDirection(dungeonFloor.getDungeonFloorConfiguration().getRooms(), direction);
            setRoom(dungeonFloor, ecConfigs, ec, direction);
        }
    }

    public static DungeonChunk getNextChunkInDirection(DungeonChunk chunk, Direction direction, int chunks){
        int x = chunk.getX();
        int z = chunk.getZ();
        int l = chunk.getLevel();
        if (direction == Direction.NORTH){
            z-=chunks;
        }
        if (direction == Direction.SOUTH){
            z+=chunks;
        }
        if (direction == Direction.EAST){
            x+=chunks;
        }
        if (direction == Direction.WEST) {
            x-=chunks;
        }
        return new DungeonChunk(chunk.getWorld(), x, z, l);
    }

    public static DungeonRoom getDungeonRoom(DungeonFloor dungeonFloor, DungeonChunk chunk){
        for (DungeonRoom dr : dungeonFloor.getRooms()){
            for (DungeonChunk dc : dr.getChunks()){
                if (dc.getX() == chunk.getX() && dc.getZ() == chunk.getZ() && dc.getLevel() == chunk.getLevel()) return dr;
            }
        }
        return null;
    }

    public static boolean isAlreadyRoom(DungeonFloor dungeonFloor, DungeonChunk chunk){
        return dungeonFloor.getTakenChunks().contains(chunk);
    }

    public static boolean areAnyChunksAlreadyRooms(DungeonFloor dungeonFloor, List<DungeonChunk> chunks){
        for (DungeonChunk chunk : chunks){
            if (isAlreadyRoom(dungeonFloor, chunk)) return true;
        }
        return false;
    }

    public static boolean doesDungeonHaveRoomType(DungeonFloor dungeonFloor, RoomType rt){
        for (DungeonRoom dr: dungeonFloor.getRooms()){
            RoomConfiguration rc = dr.getRoomConfiguration();
            if (rc.getRoomType() == rt) return true;
        }
        return false;
    }

    public static DungeonRoom setRoom(DungeonFloor dungeonFloor, List<RoomConfiguration> potentialConfigs, DungeonChunk nextChunk, Direction direction){
        List<RoomConfiguration> shortList = new ArrayList<>();
        for (RoomConfiguration porc : potentialConfigs){
            DungeonChunk ponwb = RoomConfigurationUtil.getNWBMostCorner(nextChunk, porc.getOpenings().get(direction));
            if (ponwb.getLevel() < 0) continue;
            List<DungeonChunk> pochunks = RoomConfigurationUtil.getChunksForRoomConfiguration(porc, ponwb);
            if (areAnyChunksAlreadyRooms(dungeonFloor, pochunks)) continue;
            shortList.add(porc);
        }
        if (shortList.isEmpty()) return null;

        RoomConfiguration rc = shortList.get(RandomUtil.randomWithRange(0, shortList.size()-1));
        if (rc == null) return null;
        HashMap<Direction, RoomConfigurationOpening> rcos = rc.getOpenings();
        RoomConfigurationOpening rco = rcos.get(direction);
        DungeonChunk nwb = RoomConfigurationUtil.getNWBMostCorner(nextChunk, rco);
        List<DungeonChunk> chunks = RoomConfigurationUtil.getChunksForRoomConfiguration(rc, nwb);
        DungeonRoom room = new DungeonRoom(chunks, rc);
        dungeonFloor.addRoom(room);
        dungeonFloor.addChunks(chunks);
        return room;
    }

    public static HashMap<DungeonChunk, Direction> getEndCapsWithDirection(DungeonFloor dungeonFloor){
        HashMap<DungeonChunk, Direction> map = new HashMap<>();
        for (DungeonRoom dr: dungeonFloor.getRooms()){
            RoomConfiguration rc = dr.getRoomConfiguration();
            HashMap<Direction, RoomConfigurationOpening> rcos = rc.getOpenings();
            for (Direction direction : rcos.keySet()){
                RoomConfigurationOpening rco = rcos.get(direction);
                DungeonChunk rchunk = RoomConfigurationUtil.getDungeonChunkForOpening(dr.getPasteChunk(), rco);
                DungeonChunk checkChunk = getNextChunkInDirection(rchunk, direction, 1);
                if (isAlreadyRoom(dungeonFloor, checkChunk)) continue;
                map.put(checkChunk, DirectionUtil.getInverse(direction));
            }
        }
        return map;
    }

    public static HashMap<DungeonChunk, Direction> getAllEndCaps(DungeonFloor dungeonFloor){
        HashMap<DungeonChunk, Direction> map = new HashMap<>();
        for (DungeonRoom dr: dungeonFloor.getRooms()){
            RoomConfiguration rc = dr.getRoomConfiguration();
            HashMap<Direction, RoomConfigurationOpening> rcos = rc.getOpenings();
            if (rcos.size() > 1) continue;
            for (Direction direction : rcos.keySet()){
                RoomConfigurationOpening rco = rcos.get(direction);
                DungeonChunk rchunk = RoomConfigurationUtil.getDungeonChunkForOpening(dr.getPasteChunk(), rco);
                map.put(rchunk, DirectionUtil.getInverse(direction));
            }
        }
        return map;
    }
}
