package com.DestroTheGod.RedstoneRevival;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public final class Globals {
	public static Logger log;
	public static JavaPlugin plugin;
	public static String pluginPrefix = ChatColor.WHITE + "[" + ChatColor.GREEN + "RR" + ChatColor.WHITE + "]: ";
	private static List<Block> protectedBlockList = new ArrayList<Block>();
	private static HashMap<Player, Integer> machineUseLogger = new HashMap<Player, Integer>();
	private static int serverMachineLimit = 0;
	
	public static void log(String text) {
		Globals.log.info(text);
	}
	
	public static void setPlugin(JavaPlugin plugin) {
		Globals.plugin = plugin;
	}
	
	public static void setLogger(Logger x) {
		Globals.log = x;
	}
	
	public static void addBlock(Block x) {
		Globals.protectedBlockList.add(x);
	}
	
	public static void addBlocks(Block[] x) {
		for (Block y : x) {
			Globals.protectedBlockList.add(y);
		}
	}
	
	public static void removeBlock(Block x) {
		Globals.protectedBlockList.remove(x);
	}

	public static void removeBlocks(Block[] x) {
		for (Block y : x) {
			Globals.protectedBlockList.remove(y);
		}
	}
	
	public static boolean isProtected(Block x) {
		if (Globals.protectedBlockList.contains(x)) {
			return true;
		}
		
		return false;
	}
	
	public static void checkInitPlayerMachineLogging(Player p) {
		if (!Globals.machineUseLogger.containsKey(p)) {
			Globals.machineUseLogger.put(p, 0);
		}
	}
	
	public static boolean canPlaceMachine(Player p) {
		if (!Globals.machineUseLogger.containsKey(p)) {
			return true;
		}

		Globals.checkInitPlayerMachineLogging(p);
		
		PlayerPermissions pp = new PlayerPermissions(p);
		int maxMachines = pp.getMaxMachines();
		
		return (Globals.machineUseLogger.get(p) < maxMachines);
	}
	
	public static void incrementMachineCount(Player p) {
		Globals.checkInitPlayerMachineLogging(p);
		Globals.machineUseLogger.put(p, Globals.machineUseLogger.get(p) + 1);
		Globals.serverMachineLimit += 1;
	}
	
	public static void decrementMachineCount(Player p) {
		Globals.checkInitPlayerMachineLogging(p);
		
		if (Globals.machineUseLogger.get(p) <= 0) {
			Globals.log("Error, negative machines? O.o");
			return;
		}

		Globals.machineUseLogger.put(p, Globals.machineUseLogger.get(p) - 1);
		
		// Keep hashmap clean!
		if (Globals.machineUseLogger.get(p) == 0) {
			Globals.machineUseLogger.remove(p);
		}
		
		Globals.serverMachineLimit -= 1;
	}
	
	public static boolean atServerMachineLimit() {
		return (Globals.serverMachineLimit > ConfigReader.getGlobalServerMaxMachines());
	}
}
