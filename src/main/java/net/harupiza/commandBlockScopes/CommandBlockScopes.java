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

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public final class CommandBlockScopes extends JavaPlugin implements Listener {
    final NamespacedKey nskey = new NamespacedKey(this, "command_block_author");
    final Logger logger = getLogger();
    DataSaveTo dataSaveTo;

    private HashMap<Block, UUID> localMemory = new HashMap<>();
    private CBSF cbsf;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getServer().getPluginCommand("placecb")).setExecutor(new PlaceCBExecutor(this));

        String dataSaveToTmp = getConfig().getString("dataSaveTo");

        if (dataSaveToTmp == null) {
            dataSaveTo = DataSaveTo.CUSTOMBLOCKDATA;
            return;
        }

        switch (dataSaveToTmp) {
            case "customblockdata":
                dataSaveTo = DataSaveTo.CUSTOMBLOCKDATA;
                break;

            case "memory":
                dataSaveTo = DataSaveTo.MEMORY;
                break;

            case "originalformat":
                try {
                    cbsf = new CBSF(this);
                    dataSaveTo = DataSaveTo.ORIGINALFORMAT;
                } catch (IOException e) {
                    logger.warning("Failed to initialize \"originalformat\". Using \"customblockdata\".");
                    dataSaveTo = DataSaveTo.CUSTOMBLOCKDATA;
                }
                break;

            default:
                logger.warning("Invalid config of \"dataSaveTo\". Using \"customblockdata\".");
                dataSaveTo = DataSaveTo.CUSTOMBLOCKDATA;
                break;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType() == Material.COMMAND_BLOCK) {
            registPlacer(player, block);

            logger.info("Recorded command block installation by " + player.getUniqueId());
        }
    }

    @EventHandler
    public void onServerCommandEvent(ServerCommandEvent event) {
        if (event.getSender() instanceof BlockCommandSender blockSender) {
            // Get command block
            Block block = blockSender.getBlock();
            // Get command
            String command = event.getCommand();

            UUID uuid = getPlacer(block);
            if (uuid == null) return;

            event.setCancelled(true);
            Player author = Bukkit.getPlayer(uuid);

            if (author == null) {
                logger.warning("Unable to retrieve command block creator");
                return;
            }

            author.performCommand(command);
            logger.info("The command was executed as " + author.getName());
        }
    }

    public void registPlacer(Player player, Block block) {
        switch (dataSaveTo) {
            case CUSTOMBLOCKDATA:
                PersistentDataContainer customBlockData = new CustomBlockData(block, this);
                customBlockData.set(nskey, PersistentDataType.STRING, player.getUniqueId().toString());

            case MEMORY:
                localMemory.put(block, player.getUniqueId());
                break;

            case ORIGINALFORMAT:
                try {
                    cbsf.set(block, player.getUniqueId());
                } catch (IOException e) {
                    logger.warning("Failed to save data.");
                }
                break;
        }
    }

    public UUID getPlacer(Block block) {
        switch (dataSaveTo) {
            case CUSTOMBLOCKDATA:
                PersistentDataContainer customBlockData = new CustomBlockData(block, this);
                String uuid_string = customBlockData.get(nskey, PersistentDataType.STRING);

                if (uuid_string == null) {
                    return null;
                }

                return UUID.fromString(uuid_string);

            case MEMORY:
                return localMemory.get(block);

            case ORIGINALFORMAT:
                try {
                    return cbsf.get(block);
                } catch (IOException e) {
                    logger.warning("Failed to load data.");
                }
                break;
        }

        return null;
    }
}
