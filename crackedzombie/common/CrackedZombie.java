//  
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; version 2 dated June, 1991.
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
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//
package com.crackedzombie.common;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.*;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.config.Configuration;

@Mod(
		modid = CrackedZombie.modid,
		name = CrackedZombie.name,
		version = CrackedZombie.version
)

public class CrackedZombie {

	public static final String version = "1.7.10";
	public static final String modid = "crackedzombiemod";
	public static final String name = "Cracked Zombie Mod";
	public static final String zombieName = "CrackedZombie";
	public static final String pigzombieName = "CrackedPigZombie";
	private int entityID = 0;
	
	@Mod.Instance(modid)
	public static CrackedZombie instance;

	private int zombieSpawnProb;
	private int pigzombieSpawnProb;
	private boolean zombieSpawns;
	private boolean pigzombieSpawns;
	private boolean spawnCreepers;
	private boolean spawnSkeletons;
	private boolean spawnEnderman;
	private boolean spawnSpiders;
	private boolean spawnSlime;
	private boolean spawnWitches;
	private boolean doorBusting;
	private boolean allowChildSpawns;
	private boolean sickness;
	private int minSpawn;
	private int maxSpawn;
	private boolean pzSickness;
	private int minPZSpawn;
	private int maxPZSpawn;
	private double followRange;
	private double movementSpeed;
	private double attackDamage;
	private double pzMovementSpeed;
	private double pzAttackDamage;

	
	@SidedProxy(
			clientSide = "com.crackedzombie.client.ClientProxyCrackedZombie",
			serverSide = "com.crackedzombie.common.CommonProxyCrackedZombie"
	)

	public static CommonProxyCrackedZombie proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		String generalComments = CrackedZombie.name + " Config\nMichael Sheppard (crackedEgg)\n"
				+ " For Minecraft Version " + CrackedZombie.version + "\n";
		String spawnProbComment = "zombieSpawnProb adjust to probability of zombies spawning\n"
				+ "The higher the number the more likely zombies will spawn.";
				String pzSpawnProbComment = "pigzombieSpawnProb adjust to probability of pigzombies spawning\n"
				+ "The higher the number the more likely pigzombies will spawn.";
		String zombieComment = "zombieSpawns allows/disallows default zombies spawns, default is false,\n"
				+ "no default minecraft zombies will spawn. Only the " + zombieName + "s will spawn.\n"
				+ "If set to true, fewer CrackedZombies will spawn.";
		String pigzombieComment = "pigzombieSpawns allows/disallows default pigzombies spawns, default is false,\n"
				+ "no default minecraft pigzombies will spawn. Only the " + pigzombieName + "s will spawn.\n"
				+ "If set to true, fewer CrackedPigZombies will spawn.";
		String creeperComment = "creeperSpawns, set to false to disable creeper spawning, set to true\n"
				+ "if you want to spawn creepers";
		String skeletonComment = "skeletonSpawns, set to false to disable skeleton spawning, set to true\n"
				+ "if you want to spawn skeletons";
		String endermanComment = "endermanSpawns, set to false to disable enderman spawning, set to true\n"
				+ "if you want to spawn enderman";
		String spiderComment = "spiderSpawns, set to false to disable spider spawning, set to true\n"
				+ "if you want to spawn spiders";
		String slimeComment = "slimeSpawns, set to false to disable slime spawning, set to true\n"
				+ "if you want to spawn slimes";
		String witchComment = "witchSpawns, set to false to disable witch spawning, set to true\n"
				+ "if you want to spawn witches";
		String doorBustingComment = "doorBusting, set to true to have zombies try to break down doors,\n"
				+ "otherwise set to false. It's quieter.";
		String sicknessComment = "Sickness, set to true to have contact with zombies poison the player.";
		String pzSicknessComment = "pzSickness, set to true to have contact with pigzombies poison the player.";
		String childComment = "allowChildSpawns, set to true to have child zombies, otherwise set to false.";
		String minSpawnComment = "minSpawn, minimum number of crackedzombies per spawn event";
		String maxSpawnComment = "maxSpawn, maximum number of crackedzombies per spawn event";
		String minPZSpawnComment = "minPZSpawn, minimum number of crackedpigzombies per spawn event";
		String maxPZSpawnComment = "maxPZSpawn, maximum number of crackedpigzombies per spawn event";
		String movementSpeedComment = "how fast zombies walk";
		String followRangeComment = "how far away zombies will follow you";
		String attackDamageComment = "how much damage a zombie will cause";
		String pzMoveSpeedComment = "how fast pigzombies walk";
		String pzAttackDamageComment = "how much damage a pigzombie will cause";
		

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		zombieSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "zombieSpawnProb", 15, spawnProbComment).getInt();
		pigzombieSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "pigzombieSpawnProb", 15, pzSpawnProbComment).getInt();
		zombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "zombieSpawns", false, zombieComment).getBoolean(false);
		pigzombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "pigzombieSpawns", false, pigzombieComment).getBoolean(false);
		spawnCreepers = config.get(Configuration.CATEGORY_GENERAL, "spawnCreepers", false, creeperComment).getBoolean(false);
		spawnSkeletons = config.get(Configuration.CATEGORY_GENERAL, "spawnSkeletons", false, skeletonComment).getBoolean(false);
		spawnEnderman = config.get(Configuration.CATEGORY_GENERAL, "spawnEnderman", false, endermanComment).getBoolean(false);
		spawnSpiders = config.get(Configuration.CATEGORY_GENERAL, "spawnSpiders", true, spiderComment).getBoolean(true);
		spawnSlime = config.get(Configuration.CATEGORY_GENERAL, "spawnSlime", false, slimeComment).getBoolean(false);
		spawnWitches = config.get(Configuration.CATEGORY_GENERAL, "spawnWitches", true, witchComment).getBoolean(true);
		doorBusting = config.get(Configuration.CATEGORY_GENERAL, "doorBusting", false, doorBustingComment).getBoolean(false);
		sickness = config.get(Configuration.CATEGORY_GENERAL, "sickness", false, sicknessComment).getBoolean(false);
		minSpawn = config.get(Configuration.CATEGORY_GENERAL, "minSpawn", 2, minSpawnComment).getInt();
		maxSpawn = config.get(Configuration.CATEGORY_GENERAL, "maxSpawn", 10, maxSpawnComment).getInt();
		pzSickness = config.get(Configuration.CATEGORY_GENERAL, "pzSickness", false, pzSicknessComment).getBoolean(false);
		minPZSpawn = config.get(Configuration.CATEGORY_GENERAL, "minPZSpawn", 2, minPZSpawnComment).getInt();
		maxPZSpawn = config.get(Configuration.CATEGORY_GENERAL, "maxPZSpawn", 10, maxPZSpawnComment).getInt();
		allowChildSpawns = config.get(Configuration.CATEGORY_GENERAL, "allowChildSpawns", true, childComment).getBoolean(true);
		followRange = config.get(Configuration.CATEGORY_GENERAL, "followRange", 40.0, followRangeComment).getDouble();
		movementSpeed = config.get(Configuration.CATEGORY_GENERAL, "movementSpeed", 0.23, movementSpeedComment).getDouble();
		attackDamage = config.get(Configuration.CATEGORY_GENERAL, "attackDamage", 3.0, attackDamageComment).getDouble();
		pzMovementSpeed = config.get(Configuration.CATEGORY_GENERAL, "pzMovementSpeed", 0.23, pzMoveSpeedComment).getDouble();
		pzAttackDamage = config.get(Configuration.CATEGORY_GENERAL, "pzAttackDamage", 5.0, pzAttackDamageComment).getDouble();

		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);

		config.save();

//		EntityRegistry.registerModEntity(EntityCrackedZombie.class, zombieName, entityID++, this, 80, 3, true);
//		EntityRegistry.registerModEntity(EntityCrackedPigZombie.class, pigzombieName, entityID, this, 80, 3, true);
		registerEntity(EntityCrackedZombie.class, zombieName, 0x00AFAF, 0x799C45);
		registerEntity(EntityCrackedPigZombie.class, pigzombieName, 0x00AFAF, 0xCD853F);

		proxy.registerRenderers();
//		proxy.registerWorldHandler();
	}

	@EventHandler
	public void Init(FMLInitializationEvent evt)
	{
		// zombies should spawn in dungeon spawners
		DungeonHooks.addDungeonMob(zombieName, 200);
		// add steel swords to the loot. you may need these.
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(Items.iron_sword), 1, 1, 4));
		
//		FMLCommonHandler.instance().bus().register(new WorldTickHandler());
	}
	
    @EventHandler
	public void PostInit(FMLPostInitializationEvent event)
	{
		BiomeDictionary.registerAllBiomesAndGenerateEvents();
		
		proxy.print("*** Scanning for available biomes");
		BiomeGenBase[] allBiomes = Iterators.toArray(Iterators.filter(Iterators.forArray(BiomeGenBase.getBiomeGenArray()),	Predicates.notNull()), BiomeGenBase.class);
		printBiomeList(allBiomes);

		EntityRegistry.addSpawn(EntityCrackedZombie.class, zombieSpawnProb, minSpawn, maxSpawn, EnumCreatureType.monster, allBiomes);
		EntityRegistry.addSpawn(EntityCrackedPigZombie.class, pigzombieSpawnProb, minPZSpawn, maxPZSpawn, EnumCreatureType.monster, allBiomes);
		
		// remove zombie spawning, we are replacing Minecraft zombies with CrackedZombies!
		if (!zombieSpawns) {
			proxy.print("*** Disabling default zombie spawns for all biomes");
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, allBiomes);
			DungeonHooks.removeDungeonMob("Zombie");
		} else {
			proxy.print("NOT disabling default zombie spawns, there will be fewer crackedZombies!");
		}

		// remove pigzombie spawning, we are replacing Minecraft pigzombies with CrackedPigZombies!
		if (!pigzombieSpawns) {
			proxy.print("*** Disabling default pigzombie spawns for all biomes");
			EntityRegistry.removeSpawn(EntityPigZombie.class, EnumCreatureType.monster, allBiomes);
//			DungeonHooks.removeDungeonMob("PigZombie");
		} else {
			proxy.print("NOT disabling default pigzombie spawns, there will be fewer crackedPigZombies!");
		}
		
		// optionally remove creeper, skeleton, enderman, spaiders and slime spawns for these biomes
		if (!spawnCreepers) {
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, allBiomes);
			proxy.print("*** Removing creeper spawns");
		}
		if (!spawnSkeletons) {
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, allBiomes);
			DungeonHooks.removeDungeonMob("Skeleton");
			proxy.print("*** Removing skeleton spawns and dungeon spawners");
		}
		if (!spawnEnderman) {
			EntityRegistry.removeSpawn(EntityEnderman.class, EnumCreatureType.monster, allBiomes);
			proxy.print("*** Removing enderman spawns");
		}
		if (!spawnSpiders) {
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, allBiomes);
			DungeonHooks.removeDungeonMob("Spider");
			proxy.print("*** Removing spider spawns and dungeon spawners");
		}
		if (!spawnSlime) {
			EntityRegistry.removeSpawn(EntitySlime.class, EnumCreatureType.monster, allBiomes);
			proxy.print("*** Removing slime spawns");
		}
		
		if (!spawnWitches) {
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.monster, allBiomes);
			proxy.print("*** Removing witch spawns");
		}
	}
	
	public void registerEntity(Class<? extends Entity> entityClass, String entityName, int bkEggColor, int fgEggColor)
	{
		EntityRegistry.registerModEntity(entityClass, entityName, entityID++, this, 80, 3, true);
		Item spawnEgg = new CrackedSpawnEgg(entityName, bkEggColor, fgEggColor);
		spawnEgg.setUnlocalizedName("spawn_egg_" + entityName.toLowerCase());
		spawnEgg.setTextureName(modid + ":spawn_egg");
		GameRegistry.registerItem(spawnEgg, "spawnEgg" + entityName);
	}
	
	public void printBiomeList(BiomeGenBase[] biomes)
	{
		for (BiomeGenBase bgb : biomes) {
			proxy.print("  >>> Including biome " + bgb.biomeName + " for spawning");
		}
	}
	
	public boolean getDoorBusting()
	{
		return doorBusting;
	}
	
	public boolean getSickness()
	{
		return sickness;
	}
	
	public boolean getPZSickness()
	{
		return pzSickness;
	}
	
	public boolean getAllowChildSpawns()
	{
		return allowChildSpawns;
	}
	
	public double getMovementSpeed()
	{
		return movementSpeed;
	}
	
	public double getFollowRange()
	{
		return followRange;
	}
	
	public double getAttackDamage()
	{
		return attackDamage;
	}
	
	public double getPZMoveSpeed()
	{
		return pzMovementSpeed;
	}

	public double getPZAttackDamage()
	{
		return pzAttackDamage;
	}
}
