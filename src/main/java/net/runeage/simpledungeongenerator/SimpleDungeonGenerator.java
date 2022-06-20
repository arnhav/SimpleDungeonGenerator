package net.runeage.simpledungeongenerator;

import net.runeage.simpledungeongenerator.api.SimpleDungeonGeneratorService;
import net.runeage.simpledungeongenerator.commands.SDGCommand;
import net.runeage.simpledungeongenerator.data.FileManager;
import net.runeage.simpledungeongenerator.listeners.DungeonFloorListener;
import net.runeage.simpledungeongenerator.objects.DungeonFloor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public final class SimpleDungeonGenerator extends JavaPlugin implements SimpleDungeonGeneratorService {

    @Override
    public void onEnable() {
        // Plugin startup logic
        new FileManager(this);

        getServer().getPluginManager().registerEvents(new DungeonFloorListener(), this);

        getCommand("sdg").setExecutor(new SDGCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SimpleDungeonGenerator instance() {
        return SimpleDungeonGenerator.getPlugin(SimpleDungeonGenerator.class);
    }

    public static Logger log() {
        return instance().getLogger();
    }

    @Override
    public CompletableFuture<DungeonFloor> generateDungeonFloorLayout(String tileSet) {
        return DungeonGenerator.generateDungeon(tileSet, tileSet);
    }

    @Override
    public boolean createDungeonFloorWorld(DungeonFloor df) {
        return DungeonFloorManager.createDungeonFloorWorld(df);
    }

    @Override
    public boolean deleteDungeonFloorWorld(DungeonFloor df) {
        return DungeonFloorManager.deleteDungeonFloorWorld(df);
    }

    @Override
    public boolean isDungeonWorld(World w) {
        return DungeonFloorManager.isDungeonWorld(w);
    }
}
