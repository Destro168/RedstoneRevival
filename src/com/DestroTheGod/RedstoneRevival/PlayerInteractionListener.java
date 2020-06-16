package com.DestroTheGod.RedstoneRevival;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class PlayerInteractionListener implements Listener {
	@EventHandler
	public void onBlockPower(PlayerInteractEvent event) {
    	if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		// On right-click...
        	Block eventBlock = event.getClickedBlock();
        	Player eventPlayer = event.getPlayer();
        	
        	if (eventBlock != null && eventBlock.getBlockData().getMaterial() == Material.OAK_WALL_SIGN) {
        		Sign sign = (Sign) eventBlock.getState();
    			
        		// Prevent duplicate signs.
        		if (Globals.isProtected(event.getClickedBlock())) {
        			eventPlayer.sendMessage(Globals.pluginPrefix + "This sign is already in use.");
        			return;
        		}

    			// Check machine limit.
    			if (!Globals.canPlaceMachine(eventPlayer)) {
        			eventPlayer.sendMessage(Globals.pluginPrefix + "You are at the running machine limit.");
        			return;
    			}
    			
    			// Check server machine limit.
    			if (Globals.atServerMachineLimit()) {
    				eventPlayer.sendMessage(Globals.pluginPrefix + "The server's machine limit has been reached. Please wait a while until other machines end.");
    			}
    			
    			// Process Miner sign.
        		if (sign.getLine(0).equalsIgnoreCase("[MINER]")) {
        			// Check perms
        			PlayerPermissions playerPermissions = new PlayerPermissions(eventPlayer);
        			
        			if (!playerPermissions.canBuildMiner()) {
            			eventPlayer.sendMessage(Globals.pluginPrefix + "You don't have permision to build this.");
            			return;
        			}
        			
        			eventPlayer.sendMessage(Globals.pluginPrefix + this.processMiner(eventBlock, eventPlayer, playerPermissions));
        			return;
        		}

    			// Process Breaker sign.
        		else if (sign.getLine(0).equalsIgnoreCase("[BREAKER]")) {
        			// Check perms
        			PlayerPermissions playerPermissions = new PlayerPermissions(eventPlayer);
        			
        			if (!playerPermissions.canBuildBreaker()) {
            			eventPlayer.sendMessage(Globals.pluginPrefix + "You don't have permision to build this.");
            			return;
        			}
        			
        			eventPlayer.sendMessage(Globals.pluginPrefix + this.processBreaker(eventBlock, eventPlayer, playerPermissions));
        			return;
        		}
        		
        		// Process Funnel sign.
        		else if (sign.getLine(0).equalsIgnoreCase("[FUNNEL]")) {
        			// Check perms
        			PlayerPermissions playerPermissions = new PlayerPermissions(eventPlayer);
        			
        			if (!playerPermissions.canBuildFunnel()) {
            			eventPlayer.sendMessage(Globals.pluginPrefix + "You don't have permision to build this.");
            			return;
        			}
        			
        			eventPlayer.sendMessage(Globals.pluginPrefix + this.processFunnel(eventBlock, eventPlayer, playerPermissions));
        			return;
        		}
        		
        		// Process Floor Layer sign.
        		else if (sign.getLine(0).equalsIgnoreCase("[FLOOR_LAYER]")) {
        			// Check perms
        			PlayerPermissions playerPermissions = new PlayerPermissions(eventPlayer);
        			
        			if (!playerPermissions.canBuildFloorLayer()) {
            			eventPlayer.sendMessage(Globals.pluginPrefix + "You don't have permision to build this.");
            			return;
        			}
        			
        			eventPlayer.sendMessage(Globals.pluginPrefix + this.processFloorLayer(eventBlock));
        		}
        	}
    	}
    }
    
	private boolean isBreakable(Material mat) {
		return !ConfigReader.getRestrictedBreakList().contains(mat);
	}
	
	public String processMiner(Block eventBlock, Player player, PlayerPermissions playerPermissions) {
		Sign sign = (Sign) eventBlock.getState();
		
		// Find redstone block next to sign.
		Block redstoneBlock = Util.getNearbyBlock(eventBlock, Material.REDSTONE_BLOCK);
		
		if (redstoneBlock == null) {
			return "No redstone block found.";
		}
		
		if (Globals.isProtected(redstoneBlock)) {
			return "Redstone block is already in use for a machine.";
		}
		
		// Get machine properties.
		MachineProperties machineProperties = new MachineProperties(sign.getLines());
		
		// Parse the sign details. If it fails, return.
		if (machineProperties.parse() == false) {
			return "Bad sign details.";
		}
		
		if (machineProperties.size > playerPermissions.getMaxSize()) {
			return "You don't have permission to make a machine this big.";
		}

		if (machineProperties.duration > playerPermissions.getMaxDuration()) {
			return "You don't have permission to make a machine last this long.";
		}
		
		// Set up counter mod, which modifies our 'counter' displayed on signs.
		int counterMod;
		
		// Tick every second if size is 1.
		if (machineProperties.size == 1) {
			counterMod = 1;
		}
		// Else tick every 5 seconds for larger sizes.
		else {
			// can't have size > 1 and duration < 5. Sorry.
			if (machineProperties.duration < 5) {
				return "You must have a duration of at least 5 for signs with a size above 1.";
			}
			
			// Prettify duration for machines that tick once ever 5 seconds.
			machineProperties.roundDurationBy5();
			
			counterMod = 5;
		}
				
		// Valid machine checkpoint, so protect blocks.
		Block[] protectedBlocks = {eventBlock, redstoneBlock};
		Globals.addBlocks(protectedBlocks);
		Globals.incrementMachineCount(player);
		
		// Modify sign based on machine properties.
		sign.setLine(1, machineProperties.duration + "");
		sign.setLine(2, machineProperties.size + "");
		sign.update();
				
		// If size is greater than our miner loop cut-off (10 by default), only run once.
		if (machineProperties.size > ConfigReader.getMinerLoopCutoff() || machineProperties.duration == 0) {
			int mineSize = machineProperties.size;
			
			// Break blocks logic
			Location loc = redstoneBlock.getLocation();
			Block targetBlock;
			
			for (int i = loc.getBlockX() - mineSize; i < loc.getBlockX() + mineSize + 1; i++) {
				for (int j = loc.getBlockY() - mineSize; j < loc.getBlockY() + mineSize + 1; j++) {
					for (int k = loc.getBlockZ() - mineSize; k < loc.getBlockZ() + mineSize + 1; k++) {
						// Get target block at position.
		            	targetBlock = eventBlock.getWorld().getBlockAt(i, j, k);
		            	
		            	// Exclude block types.
						if (!(isBreakable(targetBlock.getBlockData().getMaterial()))) {
							continue;
						}
						
						// Break blocks.
						targetBlock.breakNaturally();
					}
				}
			}

    		Globals.removeBlocks(protectedBlocks);
    		Globals.decrementMachineCount(player);
    		
    		// Potentially Remove redstone block.
    		if (ConfigReader.getDestroyMachinesOnEnd()) {
        		redstoneBlock.setType(Material.AIR);
    		}
    		
			return "Successfully created [MINER] machine.";
		}
		
		// Schedule block breaking task.
		int breakTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Globals.plugin, new Runnable() {
			public int counter = machineProperties.duration;
			public int mineSize = machineProperties.size;
			
			@Override
			public void run() {
				// Break blocks logic
				Location loc = redstoneBlock.getLocation();
				
				for (int i = loc.getBlockX() - mineSize; i < loc.getBlockX() + mineSize + 1; i++) {
					for (int j = loc.getBlockY() - mineSize; j < loc.getBlockY() + mineSize + 1; j++) {
						for (int k = loc.getBlockZ() - mineSize; k < loc.getBlockZ() + mineSize + 1; k++) {
							// Get target block at position.
			            	Block targetBlock = eventBlock.getWorld().getBlockAt(i, j, k);
			            	
			            	// Exclude block types.
							if (!(isBreakable(targetBlock.getBlockData().getMaterial()))) {
								continue;
							}
							
							// Break blocks.
							targetBlock.breakNaturally();
						}
					}
				}
				
				// Sign edit logic.
				counter -= counterMod;
				sign.setLine(1, counter + "");
				sign.update();
			}
		}, 20L * counterMod, 20L * counterMod);
		
		// Schedule event to cancel block breaking task.
		Bukkit.getScheduler().scheduleSyncDelayedTask(Globals.plugin, new Runnable() {
			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(breakTaskId);
				Globals.removeBlocks(protectedBlocks);
				Globals.decrementMachineCount(player);
	    		
				// Potentially Remove redstone block.
	    		if (ConfigReader.getDestroyMachinesOnEnd()) {
	        		redstoneBlock.setType(Material.AIR);
	    		}
			}
		}, 20L * machineProperties.duration);
		
		return "Successfully created [MINER] machine.";
	}

	public String processBreaker(Block eventBlock, Player player, PlayerPermissions playerPermissions) {
		Sign sign = (Sign) eventBlock.getState();
        WallSign signData = (WallSign) eventBlock.getState().getBlockData();
        String face = signData.getFacing().toString();
        Location blockLocation = eventBlock.getLocation();
        
        // Find redstone block next to sign.
		Block redstoneBlock = Util.getNearbyBlock(eventBlock, Material.REDSTONE_BLOCK);
		
		if (redstoneBlock == null) {
			return "No redstone block found.";
		}

		if (Globals.isProtected(redstoneBlock)) {
			return "Redstone block is already in use for a machine.";
		}
		
        if (face.equalsIgnoreCase("WEST")) {
        	blockLocation.setX(blockLocation.getX() + 2);
        }
        else if (face.equalsIgnoreCase("EAST")) {
        	blockLocation.setX(blockLocation.getX() - 2);
        }
        else if (face.equalsIgnoreCase("NORTH")) {
        	blockLocation.setZ(blockLocation.getZ() + 2);
        }
        else if (face.equalsIgnoreCase("SOUTH")) {
        	blockLocation.setZ(blockLocation.getZ() - 2);
        }
        else {
        	return "Invalid sign position.";
        }
        
		// Get machine properties.
		MachineProperties machineProperties = new MachineProperties(sign.getLines());
		
		// Parse the sign details. If it fails, return.
		if (machineProperties.parse() == false) {
			return "Bad sign details.";
		}
		
		if (machineProperties.duration > playerPermissions.getMaxDuration()) {
			return "You don't have permission to make a machine last this long.";
		}
		
		// Valid machine checkpoint, so protect blocks.
        Block[] protectedBlocks = {eventBlock, redstoneBlock};
		Globals.addBlocks(protectedBlocks);
		Globals.incrementMachineCount(player);
		
		// Modify sign based on machine properties.
		sign.setLine(1, machineProperties.duration + "");
		sign.update();
		
		// Schedule block breaking task.
		int breakTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Globals.plugin, new Runnable() {
			public int counter = machineProperties.duration;
			
			@Override
			public void run() {
				// Sign edit logic.
				sign.setLine(1, --counter + "");
				sign.update();
				
				// Break blocks logic > Break block 'on other side' of redstone block, opposite of sign.
				Block b = eventBlock.getWorld().getBlockAt(blockLocation);

            	// Exclude block types.
				if (!(isBreakable(b.getBlockData().getMaterial()))) {
					return;
				}

				b.breakNaturally();
			}
		}, 20L, 20L);
		
		// Schedule event to cancel block breaking task.
		Bukkit.getScheduler().scheduleSyncDelayedTask(Globals.plugin, new Runnable() {
			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(breakTaskId);
	    		Globals.removeBlocks(protectedBlocks);
	    		Globals.decrementMachineCount(player);
	    		
	    		// Potentially Remove redstone block.
	    		if (ConfigReader.getDestroyMachinesOnEnd()) {
	        		redstoneBlock.setType(Material.AIR);
	    		}
			}
		}, 20L * machineProperties.duration);

		return "Successfully created [BREAKER] machine.";
	}
	
	public String processFunnel(Block eventBlock, Player player, PlayerPermissions playerPermissions) {
		Sign sign = (Sign) eventBlock.getState();
		
		// Find redstone block next to sign.
		Block redstoneBlock = Util.getNearbyBlock(eventBlock, Material.REDSTONE_BLOCK);
		
		if (redstoneBlock == null) {
			return "No redstone block found.";
		}

		if (Globals.isProtected(redstoneBlock)) {
			return "Redstone block is already in use for a machine.";
		}
		
		// Find chest block next to sign.
		Block chestBlock = Util.getNearbyBlock(redstoneBlock, Material.CHEST);
		
		if (chestBlock == null) {
			return "No chest block found.";
		}
		
		Chest chest = (Chest) chestBlock.getState();
		
		// Get machine properties.
		MachineProperties machineProperties = new MachineProperties(sign.getLines());
		
		// Parse the sign details. If it fails, return.
		if (machineProperties.parse() == false) {
			return "Bad sign details.";
		}
		
		if (machineProperties.duration > playerPermissions.getMaxDuration()) {
			return "You don't have permission to make a machine last this long.";
		}

		// Valid machine checkpoint, so protect blocks.
        Block[] protectedBlocks = {eventBlock, chestBlock, redstoneBlock};
		Globals.addBlocks(protectedBlocks);
		Globals.incrementMachineCount(player);

		// Prettify duration for machines that tick once ever 5 seconds.
		machineProperties.roundDurationBy5();
		
		// Modify sign based on machine properties.
		sign.setLine(1, machineProperties.duration + "");
		sign.update();

		// Schedule block breaking task.
		int breakTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Globals.plugin, new Runnable() {
			public int counter = machineProperties.duration;
			
			@Override
			public void run() {
				// Sign edit logic.
				counter -= 5;
				sign.setLine(1, counter + "");
				sign.update();
				Item entityItem;
				Location entityLoc;
				
				// Chest deposit logic.				
				for (Entity entity: eventBlock.getWorld().getNearbyEntities(redstoneBlock.getLocation(), 2, 2, 2)) {
					if (entity instanceof Item) {
						entityItem = (Item)(entity);
						entityLoc = entityItem.getLocation();
						
						HashMap<Integer, ItemStack> itemsThatDidntFit = chest.getInventory().addItem(entityItem.getItemStack());
						entityItem.remove();
						
						for (ItemStack is: itemsThatDidntFit.values()) {
							eventBlock.getWorld().dropItemNaturally(entityLoc, is);
						}
					}
				}
				
			}
		}, 20L * 5, 20L * 5);
		
		// Schedule event to cancel block breaking task.
		Bukkit.getScheduler().scheduleSyncDelayedTask(Globals.plugin, new Runnable() {
			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(breakTaskId);
				Globals.removeBlocks(protectedBlocks);
	    		Globals.decrementMachineCount(player);
	    		
	    		// Potentially Remove redstone block.
	    		if (ConfigReader.getDestroyMachinesOnEnd()) {
	        		redstoneBlock.setType(Material.AIR);
	    		}
			}
		}, 20L * machineProperties.duration);

		return "Successfully created [FUNNEL] machine.";
	}
	
	public String processFloorLayer(Block eventBlock) {		
		// Find redstone block next to sign.
		Block redstoneBlock = Util.getNearbyBlock(eventBlock, Material.REDSTONE_BLOCK);
		int floorLayerMaxAttempts = ConfigReader.getFloorLayerMaxAttempts();
		
		if (redstoneBlock == null) {
			return "No redstone block found.";
		}

		if (Globals.isProtected(redstoneBlock)) {
			return "Redstone block is already in use for a machine.";
		}
		
		// Find chest above redstone block.
		Location chestLoc = redstoneBlock.getLocation();
		chestLoc.setY(chestLoc.getY()+1);
		Block chestBlock = eventBlock.getWorld().getBlockAt(chestLoc);
		
		if (chestBlock.getBlockData().getMaterial() != Material.CHEST) {
			return "No chest block found.";
		}

		Chest chest = (Chest) chestBlock.getState();
		Material firstItemMaterial = null;
		
		// Find first non-empty block in the chest.
		for (ItemStack i : chest.getInventory()) {
			if (i != null) {
				firstItemMaterial = i.getType();
				break;
			}
		}
		
		if (firstItemMaterial == null) {
			return "Chest is empty. :(";
		}
		
		Location nextPlaceLoc = chestLoc;
		nextPlaceLoc.setY(nextPlaceLoc.getY()-2);
		
		int spiralLayerIndex = 1;
		int howManyHaveBeenPlaced = 0;
		int currentEdge = 0;
		
		int hardBreak = -1;

		spiralLayerIndex = 1;
		currentEdge = 1;
		howManyHaveBeenPlaced = 1;
		
		boolean doOnce = false;
		
		// While the chest has items...
		for (ItemStack i : chest.getInventory()) {
			if (i == null || i.getType() != firstItemMaterial || i.getAmount() < 1) {
				continue;
			}

			if (doOnce == false) {
				doOnce = true;

				if (eventBlock.getWorld().getBlockAt(nextPlaceLoc).getType() == Material.AIR) {
					// reduce item count.
					i.setAmount(i.getAmount()-1);
					eventBlock.getWorld().getBlockAt(nextPlaceLoc).setType(firstItemMaterial);
				}
				
				// If no more blocks at this point return.
				if (i.getAmount() == 0) {
					break;
				}

				nextPlaceLoc.setX(nextPlaceLoc.getX() + 1);
				
				if (eventBlock.getWorld().getBlockAt(nextPlaceLoc).getType() == Material.AIR) {
					// remove one more block and place.
					i.setAmount(i.getAmount()-1);
					eventBlock.getWorld().getBlockAt(nextPlaceLoc).setType(firstItemMaterial);
				}
			}
			
			// Place the item until there are none left.
			while (i.getAmount() > 0) {
				hardBreak++;
				
				if (hardBreak > floorLayerMaxAttempts) {
					break;
				}
				
				if (currentEdge == 1) {
					nextPlaceLoc.setZ(nextPlaceLoc.getZ() + 1);
				}
				else if (currentEdge == 2) {
					nextPlaceLoc.setX(nextPlaceLoc.getX() - 1);
				}
				else if (currentEdge == 3) {
					nextPlaceLoc.setZ(nextPlaceLoc.getZ() - 1);
				}
				else if (currentEdge == 4) {
					nextPlaceLoc.setX(nextPlaceLoc.getX() + 1);
				}

				// Increase how many blocks have been placed.
				howManyHaveBeenPlaced++;

				// If we reach the end of an edge, turn! reset blocks placed count.
				if (spiralLayerIndex == 1) {
					if (currentEdge == 4) {
						if (howManyHaveBeenPlaced == (spiralLayerIndex + 2)) {
							currentEdge = 1;
							howManyHaveBeenPlaced = 0;
							spiralLayerIndex += 1;
						}
					}
					else if (howManyHaveBeenPlaced == (spiralLayerIndex + 1)) {
						currentEdge += 1;
						howManyHaveBeenPlaced = 0;
					}
				}
				else {
					if (currentEdge == 4) {
						if (howManyHaveBeenPlaced == ((spiralLayerIndex * 2) + 1)) {
							currentEdge = 1;
							howManyHaveBeenPlaced = 0;
							spiralLayerIndex += 1;
						}
					}
					else if (currentEdge == 2 || currentEdge == 3) {
						if (howManyHaveBeenPlaced == (spiralLayerIndex * 2)) {
							currentEdge += 1;
							howManyHaveBeenPlaced = 0;
						}
					}
					else if (howManyHaveBeenPlaced == ((spiralLayerIndex * 2) - 1)) {
						currentEdge += 1;
						howManyHaveBeenPlaced = 0;
					}
				}

				// If there is an air block, place the block.
				if (eventBlock.getWorld().getBlockAt(nextPlaceLoc).getType() == Material.AIR) {
					// reduce item count.
					i.setAmount(i.getAmount()-1);
					
					// Place block.
					eventBlock.getWorld().getBlockAt(nextPlaceLoc).setType(firstItemMaterial);
				}
			}
		}

		// Potentially Remove redstone block.
		if (ConfigReader.getDestroyMachinesOnEnd()) {
    		redstoneBlock.setType(Material.AIR);
		}
		
		return "Successfully operated [FLOOR_LAYER] machine.";
	}
}
