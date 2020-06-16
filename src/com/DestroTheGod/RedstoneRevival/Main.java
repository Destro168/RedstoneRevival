package com.DestroTheGod.RedstoneRevival;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	// Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	Globals.setLogger(getLogger());
    	Globals.setPlugin(this);

    	// Initialize our config reader.
    	ConfigReader.init();
    	
    	getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    	getServer().getPluginManager().registerEvents(new PlayerInteractionListener(), this);
    	
    	Globals.log("Redstone Revival has been initiated! You're on version 1.0");
    }
    
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
    	Globals.log("Redstone Revival has been deactivated!");
    }
}
