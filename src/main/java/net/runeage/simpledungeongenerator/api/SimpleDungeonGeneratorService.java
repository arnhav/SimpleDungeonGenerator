package net.runeage.simpledungeongenerator.api;

import net.runeage.simpledungeongenerator.objects.DungeonFloor;
import org.bukkit.World;

public interface SimpleDungeonGeneratorService {

    public DungeonFloor generateDungeonFloorLayout(String tileSet);

    public boolean createDungeonFloorWorld(DungeonFloor df);

    public boolean deleteDungeonFloorWorld(DungeonFloor df);

    public boolean isDungeonWorld(World w);
    
}
