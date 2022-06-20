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
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.CompletableFuture;

public class WEUtils {

    public static Clipboard loadSchem(File file){
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) return null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        } catch (Exception e) {}
        return null;
    }

    public static CompletableFuture<Boolean> pasteSchem(World world, Clipboard clipboard, int x, int y, int z) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(SimpleDungeonGenerator.instance(), () -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(x, y, z))
                        .build();
                Operations.complete(operation);
                future.complete(true);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        });
        return future;
    }

    public static CompletableFuture<Boolean> pasteFile(File folder, String fileName, World world, int x, int y, int z) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        File file = new File(folder, fileName + ".schem");
        if (file.exists()){
            Clipboard clipboard = loadSchem(file);
            future = pasteSchem(world, clipboard, x, y, z);
        } else {
            SimpleDungeonGenerator.instance().getLogger().warning("File: '" + fileName + "' not found!");
        }
        return future;
    }
}
