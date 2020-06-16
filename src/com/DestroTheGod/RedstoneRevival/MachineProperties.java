package com.DestroTheGod.RedstoneRevival;

public class MachineProperties {
	private static int uniqueID = -94838429;
	String[] lines;
	
	String name;
	int size;
	int duration;
	
	public MachineProperties(String[] lines) {
		this.lines = lines;
		
		this.name = "";
		this.size = MachineProperties.uniqueID;
		this.duration = MachineProperties.uniqueID;
	}
	
	public boolean parse() {
		for (String line : lines) {
			if (line.startsWith("d=")) {
				try {
					this.duration = Integer.valueOf(line.replace("d=", ""));
				}
				catch (NumberFormatException e) {
					return false;
				}
			}
			else if (line.startsWith("s=")) {
				try {
					this.size = Integer.valueOf(line.replace("s=", ""));
				}
				catch (NumberFormatException e) {
					return false;
				}
			}
		}
		
		this.name = lines[0];
		
		// Set unset fields to config defaults.
		if (this.size == MachineProperties.uniqueID) {
			this.size = ConfigReader.getDefaultMaxSize();
		}
		
		if (this.duration == MachineProperties.uniqueID) {
			this.duration = ConfigReader.getDefaultMaxDuration();
		}

		// Cap size to config maximums.
		if (this.size > ConfigReader.getGlobalMaxSize()) {
			this.size = ConfigReader.getGlobalMaxSize();
		}
		else if (this.size <= 0) {
			return false;
		}
		
		if (this.duration > ConfigReader.getGlobalMaxDuration()) {
			this.duration = ConfigReader.getGlobalMaxDuration();
		}
		else if (this.duration <= -1) {
			return false;
		}
		
		return true;
	}
	
	public void roundDurationBy5() {
		int modVal = this.duration % 5;
		
		if (modVal != 0) {
			this.duration = this.duration + (5 - modVal);
		}
	}
}
