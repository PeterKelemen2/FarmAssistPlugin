package hu.peti.dev.farmassist;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
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
            Material.WHEAT, Material.WHEAT,
            Material.BEETROOTS, Material.BEETROOT_SEEDS,
            Material.CARROTS, Material.CARROT,
            Material.POTATOES, Material.POTATO,
            Material.NETHER_WART, Material.NETHER_WART
    );

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
//        getServer().getPluginManager().registerEvents(new FarmAssist(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        System.out.println("Block destroyed: " + block);
        var player = event.getPlayer();
        if (block.getBlockData() instanceof Ageable) {
            System.out.println("Block was ageable");
            var blockMaterial = block.getBlockData().getMaterial();
            if (!cropMap.containsKey(blockMaterial)) return;
            var seedMaterial = cropMap.get(blockMaterial);
            System.out.println("Seed to plant: " + seedMaterial);
            if (event.getPlayer().getInventory().contains(seedMaterial)) {
                var seedStack = Arrays.stream(event.getPlayer().getInventory().getContents()).filter(i -> i.getType().equals(seedMaterial)).findFirst().get();
                if (seedStack.getAmount() > 1) {
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
                        System.out.println("Placing " + seedMaterial + " on " + x + " " + " " + y + " " + z + "...");
//                        placedBlock.setType(seedMaterial);
//                        placedBlock.setType(Material.WHEAT);
                        switch (blockMaterial) {
                            case WHEAT -> placedBlock.setType(Material.WHEAT_SEEDS); // Doesn't work
                            case POTATOES -> placedBlock.setType(Material.POTATOES);
                            case CARROTS -> placedBlock.setType(Material.CARROTS);
                            case BEETROOTS -> placedBlock.setType(Material.BEETROOTS);
                        }
                    }
                }.runTaskLater(this, 2);
            }
        }
    }
}
