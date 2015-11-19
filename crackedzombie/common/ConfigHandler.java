//  
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; modversion 2 dated June, 1991.
// 
//  This program is distributed in the hope that it will be useful, 
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
// 
//  You should have received a copy of the GNU General Public License
//  along with this program;  if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave., Cambridge, MA 02139, USA.
//  =====================================================================
//
//
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.crackedzombie.common;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {
	
	public static Configuration config;
	private static int zombieSpawnProb;
	private static int pigzombieSpawnProb;
	private static boolean zombieSpawns;
	private static boolean pigZombieSpawns;
	private static boolean spawnCreepers;
	private static boolean spawnSkeletons;
	private static boolean spawnEnderman;
	private static boolean spawnSpiders;
	private static boolean spawnSlime;
	private static boolean spawnWitches;
	private static boolean doorBusting;
	private static boolean sickness;
	private static boolean pzSickness;
	private static boolean startWithSword;
	private static boolean enchantSword;
	private static int minSpawn;
	private static int maxSpawn;
	private static int minPZSpawn;
	private static int maxPZSpawn;
	private static double torchNoSpawnRadius;
	private static boolean allowChildSpawns;
	private static boolean allowPigZombieSpawns;
	
	static final String generalComments = CrackedZombie.name + " Config\nMichael Sheppard (crackedEgg)\n"
				+ "For Minecraft Version " + CrackedZombie.mcversion + "\n";
	static final String spawnProbComment = "zombieSpawnProb adjust to probability of zombies spawning\n"
			+ "The higher the number the more likely zombies will spawn.";
	static final String pzSpawnProbComment = "pigzombieSpawnProb adjust to probability of pigzombies spawning\n"
			+ "The higher the number the more likely pigzombies will spawn.";
	static final String zombieComment = "zombieSpawns allows/disallows vanilla zombies spawns, default is false,\n"
			+ " no vanilla minecraft zombies will spawn. Only the " + CrackedZombie.zombieName + "s will spawn.\n"
			+ " If set to true, fewer " + CrackedZombie.zombieName + "s will spawn.";
	static final String pigZombieComment = "pigZombieSpawns allows/disallows vanilla pig zombies spawns, default is false,\n"
			+ " no vanilla minecraft pig zombies will spawn. Only the " + CrackedZombie.pigzombieName + "s will spawn.\n"
			+ " If set to true, fewer " + CrackedZombie.pigzombieName + "s will spawn.";
	static final String creeperComment = "creeperSpawns, set to false to disable creeper spawning, set to true"
			+ " if you want to spawn creepers";
	static final String skeletonComment = "skeletonSpawns, set to false to disable skeleton spawning, set to true"
			+ " if you want to spawn skeletons";
	static final String endermanComment = "endermanSpawns, set to false to disable enderman spawning, set to true"
			+ " if you want to spawn enderman";
	static final String spiderComment = "spiderSpawns, set to false to disable spider spawning, set to true"
			+ " if you want to spawn spiders";
	static final String slimeComment = "slimeSpawns, set to false to disable slime spawning, set to true"
			+ " if you want to spawn slimes";
	static final String witchComment = "witchSpawns, set to false to disable witch spawning, set to true"
			+ " if you want to spawn witches";
	static final String doorBustingComment = "doorBusting, set to true to have zombies try to break down doors,"
			+ " otherwise set to false. It's quieter.";
	static final String crackedPigZombieComment = "allow CrackedPigZombies to spawn";
	static final String childComment = "allowChildSpawns, set to true to have child zombies, otherwise set to false.";
	static final String sicknessComment = "Sickness, set to true to have contact with zombies poison the player.";
	static final String minSpawnComment = "minSpawn, minimum number of crackedzombies per spawn event";
	static final String maxSpawnComment = "maxSpawn, maximum number of crackedzombies per spawn event";
	static final String pzSicknessComment = "Pig Zombie Sickness, set to true to have contact with pigzombies poison the player.";
	static final String minPZSpawnComment = "minPZSpawn, minimum number of crackedpigzombies per spawn event";
	static final String maxPZSpawnComment = "maxPZSpawn, maximum number of crackedpigzombies per spawn event";
	static final String startWithSwordComment = "Allows the player to spawn with a random type sword, handy in the apocalypse!";
	static final String enchantSwordComment = "set true to enchant the sword given to the player";
	static final String noSpawnRadiusComment = "set the radius in blocks for no spawning near torches, zero enables spawing near torches";

	public static void startConfig(FMLPreInitializationEvent event)
	{
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		updateConfigInfo();
	}
	
	public static void updateConfigInfo()
	{
		try {
			config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);
			zombieSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "zombieSpawnProb", 5, spawnProbComment).getInt();
			pigzombieSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "pigzombieSpawnProb", 5, pzSpawnProbComment).getInt();
			zombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "zombieSpawns", false, zombieComment).getBoolean(false);
			pigZombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "pigZombieSpawns", false, pigZombieComment).getBoolean(false);
			spawnCreepers = config.get(Configuration.CATEGORY_GENERAL, "spawnCreepers", false, creeperComment).getBoolean(false);
			spawnSkeletons = config.get(Configuration.CATEGORY_GENERAL, "spawnSkeletons", false, skeletonComment).getBoolean(false);
			spawnEnderman = config.get(Configuration.CATEGORY_GENERAL, "spawnEnderman", false, endermanComment).getBoolean(false);
			spawnSpiders = config.get(Configuration.CATEGORY_GENERAL, "spawnSpiders", true, spiderComment).getBoolean(true);
			spawnSlime = config.get(Configuration.CATEGORY_GENERAL, "spawnSlime", false, slimeComment).getBoolean(false);
			spawnWitches = config.get(Configuration.CATEGORY_GENERAL, "spawnWitches", true, witchComment).getBoolean(true);
			doorBusting = config.get(Configuration.CATEGORY_GENERAL, "doorBusting", false, doorBustingComment).getBoolean(false);
			sickness = config.get(Configuration.CATEGORY_GENERAL, "sickness", false, sicknessComment).getBoolean(false);
			pzSickness = config.get(Configuration.CATEGORY_GENERAL, "pzSickness", false, pzSicknessComment).getBoolean(false);
			startWithSword = config.get(Configuration.CATEGORY_GENERAL, "startWithSword", false, startWithSwordComment).getBoolean(false);
			enchantSword = config.get(Configuration.CATEGORY_GENERAL, "enchantSword", false, enchantSwordComment).getBoolean(false);
			minSpawn = config.get(Configuration.CATEGORY_GENERAL, "minSpawn", 1, minSpawnComment).getInt();
			maxSpawn = config.get(Configuration.CATEGORY_GENERAL, "maxSpawn", 5, maxSpawnComment).getInt();
			minPZSpawn = config.get(Configuration.CATEGORY_GENERAL, "minPZSpawn", 1, minPZSpawnComment).getInt();
			maxPZSpawn = config.get(Configuration.CATEGORY_GENERAL, "maxPZSpawn", 5, maxPZSpawnComment).getInt();
			torchNoSpawnRadius = config.get(Configuration.CATEGORY_GENERAL, "noSpawnTorchRadius", 3.0, noSpawnRadiusComment).getDouble();
			allowChildSpawns = config.get(Configuration.CATEGORY_GENERAL, "allowChildSpawns", true, childComment).getBoolean(true);
			allowPigZombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "allowPigZombieSpawns", true, crackedPigZombieComment).getBoolean(true);
		} catch (Exception e) {
			CrackedZombie.proxy.info("failed to load or read the config file");
		} finally {
			if (config.hasChanged()) {
				config.save();
			}
		}
	}
	
	public static boolean getSpawnCreepers()
	{
		return spawnCreepers;
	}
	
	public static boolean getSpawnSkeletons()
	{
		return spawnSkeletons;
	}
	
	public static boolean getSpawnEnderman()
	{
		return spawnEnderman;
	}
	
	public static boolean getSpawnSpiders()
	{
		return spawnSpiders;
	}
	
	public static boolean getSpawnSlime()
	{
		return spawnSlime;
	}
	
	public static boolean getSpawnWitches()
	{
		return spawnWitches;
	}
	
	public static int getMinSpawn()
	{
		return minSpawn;
	}
	
	public static int getMaxSpawn()
	{
		return maxSpawn;
	}
	
	public static int getMinPZSpawn()
	{
		return minPZSpawn;
	}
	
	public static int getMaxPZSpawn()
	{
		return maxPZSpawn;
	}
	
	public static int getZombieSpawnProbility()
	{
		return zombieSpawnProb;
	}
	
	public static int getPigZombieSpawnProbility()
	{
		return pigzombieSpawnProb;
	}
	
	public static boolean getZombieSpawns()
	{
		return zombieSpawns;
	}

	public static boolean getPigZombieSpawns()
	{
		return pigZombieSpawns;
	}
	
	public static boolean getDoorBusting()
	{
		return doorBusting;
	}
	
	public static boolean getSickness()
	{
		return sickness;
	}
	
	public static boolean getPZSickness()
	{
		return pzSickness;
	}
	
	public static boolean getStartWithSword()
	{
		return startWithSword;
	}
	
	public static boolean getEnchantSword()
	{
		return enchantSword;
	}
	
	public static int getMaximumZombiesAllowed()
	{
		return 60;
	}
	
	public static double getTorchNoSpawnRadius()
	{
		return torchNoSpawnRadius;
	}

	public static boolean getAllowChildSpawns()
	{
		return allowChildSpawns;
	}

	public static boolean getAllowPigZombieSpawns()
	{
		return allowPigZombieSpawns;
	}
}
