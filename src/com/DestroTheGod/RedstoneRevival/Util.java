package com.DestroTheGod.RedstoneRevival;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Util {
	public static Block getNearbyBlock(Block b, Material mat) {
		Location loc = b.getLocation();
		
		for (int i = loc.getBlockX()-1; i < loc.getBlockX()+2; i++) {
			for (int j = loc.getBlockY()-1; j < loc.getBlockY()+2; j++) {
				for (int k = loc.getBlockZ()-1; k < loc.getBlockZ()+2; k++) {
					b = (Block)(b.getWorld().getBlockAt(i, j, k));
					
					if (b.getBlockData().getMaterial() == mat) {
						return b;
					}
				}
			}
		}
		
		return null;
	}
}
