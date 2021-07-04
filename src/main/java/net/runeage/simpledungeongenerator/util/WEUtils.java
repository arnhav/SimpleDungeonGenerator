package net.runeage.simpledungeongenerator.util;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.runeage.simpledungeongenerator.SimpleDungeonGenerator;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;

public class WEUtils {

    public static Clipboard loadSchem(File file){
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) return null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        } catch (Exception e) {}
        return null;
    }

    public static void pasteSchem(World world, Clipboard clipboard, int x, int y, int z){
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException ignored) {}
    }

    public static void pasteFile(File folder, String fileName, World world, int x, int y, int z){
        File file = new File(folder, fileName + ".schem");
        if (file.exists()){
            Clipboard clipboard = loadSchem(file);
            pasteSchem(world, clipboard, x, y, z);
        } else {
            SimpleDungeonGenerator.instance().getLogger().warning("File: '" + fileName + "' not found!");
        }
    }
}
