package net.harupiza.commandBlockScopes;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EditCBExecutor implements CommandExecutor {
    private final CommandBlockScopes plugin;

    EditCBExecutor(CommandBlockScopes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            Block block = player.getLocation().getBlock().getRelative(0, -1, 0);

            if (block.getType() == Material.COMMAND_BLOCK) {
                CommandBlock commandBlock = (CommandBlock) block.getState();
                UUID uuid = plugin.getPlacer(block);

                if (uuid != null) {
                    if (player.getUniqueId().equals(uuid)) {
                        commandBlock.setCommand(String.join(" ", args));
                        commandBlock.update();
                        player.sendMessage("Successfully edited command block!");
                    } else {
                        player.sendMessage("You are not the creator of this command block.");
                    }
                }
            }
        }
        return true;
    }
}
