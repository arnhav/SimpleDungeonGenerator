package net.runeage.simpledungeongenerator.api;

import net.runeage.simpledungeongenerator.objects.DungeonFloor;
import net.runeage.simpledungeongenerator.objects.generation.DungeonRoom;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DungeonRoomPasteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private World world;
    private DungeonFloor dungeonFloor;
    private DungeonRoom dungeonRoom;

    public DungeonRoomPasteEvent(World world, DungeonFloor dungeonFloor, DungeonRoom dungeonRoom) {
        this.world = world;
        this.dungeonFloor = dungeonFloor;
        this.dungeonRoom = dungeonRoom;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public World getWorld() {
        return world;
    }

    public DungeonFloor getDungeonFloor() {
        return dungeonFloor;
    }

    public DungeonRoom getDungeonRoom() {
        return dungeonRoom;
    }
}