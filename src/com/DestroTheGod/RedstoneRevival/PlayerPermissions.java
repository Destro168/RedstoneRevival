package com.DestroTheGod.RedstoneRevival;

import org.bukkit.entity.Player;

public class PlayerPermissions {
	Player player;
	
	public PlayerPermissions(Player player) {
		this.player = player;
	}
	
	// Check if op or has op perm to return true. Else, check string.
	public boolean hasPerm() {
		return true;
	}
	
	public boolean isSuperAdmin() {
		return this.player.hasPermission("RedstoneRevival.*") || this.player.isOp();
	}
	
	public boolean canUseMachines() {		
		return this.player.hasPermission("RedstoneRevival.use_machine.*");
	}
	
	public boolean canBuildMiner() {
		if (this.isSuperAdmin() || this.canUseMachines()) {
			return true;
		}
		
		return this.player.hasPermission("RedstoneRevival.use_machine.miner");
	}
	
	public boolean canBuildFunnel() {
		if (this.isSuperAdmin() || this.canUseMachines()) {
			return true;
		}
		
		return this.player.hasPermission("RedstoneRevival.use_machine.funnel");
	}

	
	public boolean canBuildBreaker() {
		if (this.isSuperAdmin() || this.canUseMachines()) {
			return true;
		}
		
		return this.player.hasPermission("RedstoneRevival.use_machine.breaker");
	}
	
	public boolean canBuildFloorLayer() {
		if (this.isSuperAdmin() || this.canUseMachines()) {
			return true;
		}
		
		return this.player.hasPermission("RedstoneRevival.use_machine.floor_layer");
	}
	
	public int getMaxSize() {
		int maxSize = ConfigReader.getGlobalMaxSize();
		
		if (this.isSuperAdmin()) {
			return maxSize;
		}
		
		for (int i = 1; i < maxSize + 1; i++) {
			if (this.player.hasPermission("RedstoneRevival.max_size." + i)) {
				return i;
			}
		}
		
		return ConfigReader.getDefaultMaxSize();
	}

	public int getMaxDuration() {
		int maxDuration = ConfigReader.getGlobalMaxDuration();
		
		if (this.isSuperAdmin()) {
			return maxDuration;
		}
		
		for (int i = 0; i < maxDuration + 5; i += 5) {
			if (this.player.hasPermission("RedstoneRevival.max_duration." + i)) {
				return i;
			}
		}
		
		return ConfigReader.getDefaultMaxDuration();
	}
		
	public int getMaxMachines() {
		int maxMachines = ConfigReader.getGlobalMaxMachines();
		
		if (this.isSuperAdmin()) {
			return maxMachines;
		}
		
		for (int i = 0; i < maxMachines + 1; i++) {
			if (this.player.hasPermission("RedstoneRevival.max_machines." + i)) {
				return i;
			}
		}
		
		return ConfigReader.getDefaultMaxMachines();
	}
}
