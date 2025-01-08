package net.harupiza.commandBlockScopes;

import org.bukkit.block.Block;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.UUID;

public class CBSF {
    private final CommandBlockScopes plugin;
    Path saveTo;

    public CBSF(CommandBlockScopes cbsplugin) throws IOException {
        plugin = cbsplugin;
        saveTo = Paths.get(plugin.getDataFolder().toString(), "data.txt"); // Use Paths.get for constructing the path
        File file = saveTo.toFile();
        file.getParentFile().mkdirs();

        if (!file.exists()) file.createNewFile();
    }

    public void set(Block block, UUID uuid) throws IOException {
        Files.writeString(saveTo, MessageFormat.format("{0}:{1}:{2}:{3}\n", block.getX(), block.getY(), block.getZ(), uuid.toString()), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    public UUID get(Block block) throws IOException {
        for (String line : Files.readAllLines(saveTo)) {
            var s = line.split(":");

            if (s.length != 4) continue;

            if (Integer.parseInt(s[0]) == block.getX() &&
                    Integer.parseInt(s[1]) == block.getY() &&
                    Integer.parseInt(s[2]) == block.getZ()) {
                return UUID.fromString(s[3]);
            }
        }

        return null;
    }
}
