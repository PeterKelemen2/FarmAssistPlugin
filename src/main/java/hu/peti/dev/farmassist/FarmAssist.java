package hu.peti.dev.farmassist;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Map;

public final class FarmAssist extends JavaPlugin implements Listener {
//    private final FarmAssist plugin;

//    public FarmAssist(FarmAssist plugin) {
//        this.plugin = plugin;
//    }

    private final Map<Material, Material> cropMap = Map.of(
            Material.WHEAT, Material.WHEAT_SEEDS,
            Material.BEETROOTS, Material.BEETROOT_SEEDS,
            Material.CARROTS, Material.CARROT,
            Material.POTATOES, Material.POTATO,
            Material.NETHER_WART, Material.NETHER_WART
    );

    private boolean isPaused = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
//        getServer().getPluginManager().registerEvents(new FarmAssist(this), this);
        PluginCommand command = getCommand("farmassist");
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("farmassist")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
                togglePause(sender);
                return true;
            }
        }
        return false;
    }

    private void togglePause(CommandSender sender) {
        isPaused = !isPaused;
        if (isPaused) {
            sender.sendMessage("[FarmAssist] - Paused.");
        } else {
            sender.sendMessage("[FarmAssist] - Resumed");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
//        var player = event.getPlayer();

        if (isPaused) {
            return;
        }

        if (block.getBlockData() instanceof Ageable) {
            var blockMaterial = block.getBlockData().getMaterial();
            if (!cropMap.containsKey(blockMaterial)) return;
            var seedMaterial = cropMap.get(blockMaterial);
            System.out.println("Seed to plant: " + seedMaterial);
            if (event.getPlayer().getInventory().contains(seedMaterial)) {
                var seedStack = Arrays.stream(event.getPlayer().getInventory().getContents())
                        .filter(i -> i != null && i.getType().equals(seedMaterial))
                        .findFirst()
                        .orElse(null);
                if (seedStack != null && seedStack.getAmount() > 1) {
                    seedStack.setAmount(seedStack.getAmount() - 1);
                } else {
                    event.getPlayer().getInventory().remove(seedStack);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Get the block's location
                        int x = block.getX();
                        int y = block.getY();
                        int z = block.getZ();
                        Block placedBlock = block.getWorld().getBlockAt(x, y, z);
                        switch (blockMaterial) {
                            case WHEAT -> placedBlock.setType(Material.WHEAT); // Doesn't work
                            case POTATOES -> placedBlock.setType(Material.POTATOES);
                            case CARROTS -> placedBlock.setType(Material.CARROTS);
                            case BEETROOTS -> placedBlock.setType(Material.BEETROOTS);
                        }
                    }
                }.runTask(this);
            }
        }
    }
}
