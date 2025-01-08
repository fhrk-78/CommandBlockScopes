package net.harupiza.commandBlockScopes;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlaceCBExecutor implements CommandExecutor {
    private final CommandBlockScopes plugin;

    PlaceCBExecutor(CommandBlockScopes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            Block block = player.getLocation().getBlock();
            block.setType(Material.COMMAND_BLOCK);

            plugin.registPlacer(player, block);

            player.sendMessage("Successfully placed command block!");

            return false;
        }
        return true;
    }
}
