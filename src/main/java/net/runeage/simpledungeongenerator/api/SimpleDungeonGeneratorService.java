package net.runeage.simpledungeongenerator.api;

import net.runeage.simpledungeongenerator.objects.DungeonFloor;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public interface SimpleDungeonGeneratorService {

    public CompletableFuture<DungeonFloor> generateDungeonFloorLayout(String tileSet);

    public boolean createDungeonFloorWorld(DungeonFloor df);

    public boolean deleteDungeonFloorWorld(DungeonFloor df);

    public boolean isDungeonWorld(World w);

}
