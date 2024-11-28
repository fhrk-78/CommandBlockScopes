package net.harupiza.commandBlockScopes;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Logger;

public final class CommandBlockScopes extends JavaPlugin implements Listener {
    private final NamespacedKey nskey = new NamespacedKey(this, "command_block_author");
    private final Logger logger = getLogger();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType() == Material.COMMAND_BLOCK) {
            PersistentDataContainer customBlockData = new CustomBlockData(block, this);

            String uuid_string = player.getUniqueId().toString();
            customBlockData.set(nskey, PersistentDataType.STRING, uuid_string);

            logger.info("Recorded command block installation by " + uuid_string);
        }
    }

    @EventHandler
    public void onServerCommandEvent(ServerCommandEvent event) {
        if (event.getSender() instanceof BlockCommandSender blockSender) {
            event.setCancelled(true);

            // Get command block
            Block block = blockSender.getBlock();
            // Get command
            String command = event.getCommand();

            PersistentDataContainer customBlockData = new CustomBlockData(block, this);
            String uuid_string = customBlockData.get(nskey, PersistentDataType.STRING);

            if (uuid_string == null) {
                logger.warning("Command block creator not recorded");
                return;
            }

            UUID uuid = UUID.fromString(uuid_string);
            Player author = Bukkit.getPlayer(uuid);

            if (author == null) {
                logger.warning("Unable to retrieve command block creator");
                return;
            }

            author.performCommand(command);
            logger.info("The command was executed as " + author.getName());
        }
    }
}
