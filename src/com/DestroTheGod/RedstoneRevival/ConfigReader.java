package com.DestroTheGod.RedstoneRevival;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigReader {
	public static FileConfiguration config;
	
	public static void init() {
		ConfigReader.config = Globals.plugin.getConfig();
		
		ConfigReader.config.addDefault("version", 1.0);
		ConfigReader.config.addDefault("floor_layer_max_attempts", 256);
		ConfigReader.config.addDefault("restricted_break_list", Arrays.asList(
				Material.WATER.toString(),
				Material.LAVA.toString(),
				Material.BEDROCK.toString(),
				Material.OAK_WALL_SIGN.toString(),
				Material.REDSTONE_BLOCK.toString(),
				Material.AIR.toString(),
				Material.BEDROCK.toString()
		));
		ConfigReader.config.addDefault("destroy_machines_on_end", true);
		ConfigReader.config.addDefault("default_max_size", 10);
		ConfigReader.config.addDefault("default_max_duration", 300);
		ConfigReader.config.addDefault("default_max_machines", 10);
		ConfigReader.config.addDefault("global_max_duration", 1800);
		ConfigReader.config.addDefault("global_max_size", 30);
		ConfigReader.config.addDefault("global_max_machines", 50);
		ConfigReader.config.addDefault("global_server_max_machines", 50);
		ConfigReader.config.addDefault("miner_loop_size_cutoff", 10);
		ConfigReader.config.addDefault("redstone_ore_drops_one", false);
		
		config.options().copyDefaults(true);
		Globals.plugin.saveConfig();
	}
	
	public static double getVersion() {
		return ConfigReader.config.getDouble("version");
	}
	
	public static int getFloorLayerMaxAttempts() {
		return ConfigReader.config.getInt("floor_layer_max_attempts");
	}
	
	public static List<Material> getRestrictedBreakList() {
		List<?> x = ConfigReader.config.getList("restricted_break_list");
		List<Material> restrictedMats = new ArrayList<Material>();
		
		if (x.size() < 0) {
			return Arrays.asList();
		}
		
		for (Object y : x) {
			if (y instanceof String) {
				restrictedMats.add(Material.getMaterial((String) y));
			}
		}
		
		return restrictedMats;
	}
	
	public static boolean getDestroyMachinesOnEnd() {
		return ConfigReader.config.getBoolean("destroy_machines_on_end");
	}

	public static int getDefaultMaxSize() {
		return ConfigReader.config.getInt("default_max_size");
	}
	
	public static int getDefaultMaxDuration() {
		return ConfigReader.config.getInt("default_max_duration");
	}

	public static int getDefaultMaxMachines() {
		return ConfigReader.config.getInt("default_max_machines");
	}

	public static int getGlobalMaxDuration() {
		return ConfigReader.config.getInt("global_max_duration");
	}

	public static int getGlobalMaxSize() {
		return ConfigReader.config.getInt("global_max_size");
	}

	public static int getGlobalMaxMachines() {
		return ConfigReader.config.getInt("global_max_machines");
	}

	public static int getGlobalServerMaxMachines() {
		return ConfigReader.config.getInt("global_server_max_machines");
	}

	public static int getMinerLoopCutoff() {
		return ConfigReader.config.getInt("miner_loop_size_cutoff");
	}
	
	public static boolean getRedstoneOreDropsOne() {
		return ConfigReader.config.getBoolean("redstone_ore_drops_one");
	}
}
