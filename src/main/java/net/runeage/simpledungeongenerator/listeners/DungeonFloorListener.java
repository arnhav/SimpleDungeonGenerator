package net.runeage.simpledungeongenerator.listeners;

import net.runeage.simpledungeongenerator.api.DungeonRoomPasteEvent;
import net.runeage.simpledungeongenerator.objects.generation.DungeonChunk;
import net.runeage.simpledungeongenerator.objects.generation.DungeonRoom;
import net.runeage.simpledungeongenerator.DungeonFloorManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Jigsaw;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DungeonFloorListener implements Listener {

    @EventHandler
    public void onRoomPaste(DungeonRoomPasteEvent event) {
        World w = event.getWorld();
        DungeonRoom dr = event.getDungeonRoom();
        for (DungeonChunk dc : dr.getChunks()) {
            Chunk c = w.getChunkAt(dc.getX(), dc.getZ());
            BlockState[] bss = c.getTileEntities();
            for (BlockState bs : bss) {
                BlockData bd = bs.getBlockData();
                if (!(bd instanceof Jigsaw)) continue;
                String orientation = ((Jigsaw) bd).getOrientation().toString().split("_")[0];
                if (orientation.equals("UP") || orientation.equals("DOWN")) continue;
                bs.getBlock().setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Location loc = DungeonFloorManager.playerLocations.get(p);
        if (loc == null) return;
        p.teleport(loc);
    }

}
