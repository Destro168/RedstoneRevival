package com.DestroTheGod.RedstoneRevival;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class BlockBreakListener implements Listener {
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	// Prevent breaking protected blocks.
    	if ((event.getBlock().getBlockData().getMaterial() == Material.REDSTONE_BLOCK ||
    			event.getBlock().getBlockData().getMaterial() == Material.OAK_WALL_SIGN ||
    					event.getBlock().getBlockData().getMaterial() == Material.CHEST)
    					&& Globals.isProtected(event.getBlock())) {
    		event.setCancelled(true);
    		event.getPlayer().sendMessage(Globals.pluginPrefix + "You can't break this block!");
    	}

    	// Modify redstone drops if config option is set to true.
    	if (event.getBlock().getBlockData().getMaterial() == Material.REDSTONE_ORE && ConfigReader.getRedstoneOreDropsOne()) {
    		// Block normal item drops.
    		event.setDropItems(false);
    		
    		// Drop redstone if player isn't in creative mode.
    		if (event.getPlayer().getGameMode() != GameMode.CREATIVE ) {
        		event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.REDSTONE, 1));
    		}
    	}
    }
}
