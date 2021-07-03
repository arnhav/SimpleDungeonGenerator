package net.runeage.simpledungeongenerator.commands;

import net.kyori.adventure.text.Component;
import net.runeage.simpledungeongenerator.SimpleDungeonGenerator;
import net.runeage.simpledungeongenerator.objects.DungeonFloor;
import net.runeage.simpledungeongenerator.util.DungeonFloorManager;
import net.runeage.simpledungeongenerator.util.DungeonGenerator;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SDGCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;

        if (!(sender instanceof Player)) return true;

        if (args.length == 1){
            if (!args[0].equalsIgnoreCase("list")) return true;
            sender.sendMessage("Dungeons:");
            for (DungeonFloor df : DungeonFloorManager.dungeonFloors){
                sender.sendMessage(Component.text("- "+df.getName()+" | "+df.getTileset()));
            }
        }

        if (args.length == 2){
            if (args[0].equalsIgnoreCase("create")){
                if (!DungeonFloorManager.isTileSetPresent(args[1])) return false;
                DungeonFloor df = DungeonGenerator.generateDungeon(args[1], "");
                if (df == null) return false;
                DungeonFloorManager.createDungeonFloorWorld(df);
            }

            if (args[0].equalsIgnoreCase("delete")){
                World w = Bukkit.getWorld(args[1]);
                if (w == null) return false;
                DungeonFloor df = DungeonFloorManager.getDungeonFloor(w);
                if (df == null) return false;
                DungeonFloorManager.deleteDungeonFloorWorld(df);
            }

            if (args[0].equalsIgnoreCase("enter")){
                World w = Bukkit.getWorld(args[1]);
                if (w == null) return false;
                DungeonFloor df = DungeonFloorManager.getDungeonFloor(w);
                if (df == null) return false;
                Bukkit.getScheduler().runTaskLater(SimpleDungeonGenerator.instance(), new Runnable() {
                    @Override
                    public void run() {
                        if (df.isReady()){
                            ((Player) sender).setGameMode(GameMode.SPECTATOR);
                            ((Player) sender).teleport(new Location(w, 0, 1, 0));
                            return;
                        }
                        sender.sendActionBar(Component.text("Creating Dungeon Floor..."));
                        Bukkit.getScheduler().runTaskLater(SimpleDungeonGenerator.instance(), this, 20);
                    }
                }, 0);
            }
        }

        return true;
    }
}
