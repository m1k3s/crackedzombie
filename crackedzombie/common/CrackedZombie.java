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

import static com.crackedzombie.common.ConfigHandler.updateConfigInfo;
//import com.google.common.base.Predicates;
//import com.google.common.collect.Iterators;
import net.minecraft.entity.monster.*;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;

@Mod( modid = CrackedZombie.modid, name = CrackedZombie.name, version = CrackedZombie.modversion, guiFactory = CrackedZombie.guifactory )

public class CrackedZombie {

	public static final String mcversion = "1.10";
	public static final String modversion = "3.5.0";
	public static final String modid = "crackedzombiemod";
	public static final String name = "Cracked Zombie Mod";
	public static final String zombieName = "CrackedZombie";
	public static final String pigzombieName = "CrackedPigZombie";
	public static final String guifactory = "com.crackedzombie.client.CrackedZombieConfigGUIFactory";
	private int entityID = 0;

	private BiomeDictionary.Type biometypes[] = { BiomeDictionary.Type.BEACH, BiomeDictionary.Type.COLD, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.DEAD,
			BiomeDictionary.Type.DENSE, BiomeDictionary.Type.DRY, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.HOT,
			BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.LUSH, BiomeDictionary.Type.MESA, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.MUSHROOM,
			BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.RIVER, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.SAVANNA, BiomeDictionary.Type.SNOWY,
			BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.WASTELAND, BiomeDictionary.Type.WATER
	};
	
	@Mod.Instance(modid)
	public static CrackedZombie instance;

	@SidedProxy(
			clientSide = "com.crackedzombie.client.ClientProxyCrackedZombie",
			serverSide = "com.crackedzombie.common.CommonProxyCrackedZombie"
	)

	public static CommonProxyCrackedZombie proxy;

	@SuppressWarnings("unused")
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ConfigHandler.startConfig(event);

		EntityRegistry.registerModEntity(EntityCrackedZombie.class, zombieName, entityID++, CrackedZombie.instance, 80, 3, true, 0x00AFAF, 0x799C45);
		EntityRegistry.registerModEntity(EntityCrackedPigZombie.class, pigzombieName, entityID, CrackedZombie.instance, 80, 3, true, 0x799C45, 0x00AFAF);
		proxy.registerRenderers();
	}

	@SuppressWarnings("unused")
	@Mod.EventHandler
	public void Init(FMLInitializationEvent evt)
	{
		MinecraftForge.EVENT_BUS.register(CrackedZombie.instance);
		if (ConfigHandler.getStartWithSword()) {
			MinecraftForge.EVENT_BUS.register(new PlayerJoinedWorldEventHandler());
		}
		
//		proxy.registerRenderers();
		// zombies should spawn in dungeon spawners
		DungeonHooks.addDungeonMob(zombieName, 200);
		// add steel swords to the loot. you may need these.
//		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(Items.iron_sword), 1, 1, 4));
	}

	@SuppressWarnings("unused")
    @Mod.EventHandler
	public void PostInit(FMLPostInitializationEvent event)
	{
		BiomeDictionary.registerAllBiomesAndGenerateEvents();
		
		proxy.info("*** Scanning for available biomes");
//		BiomeGenBase[] allBiomes = new ArrayList<>(BiomeGenBase.explorationBiomesList).toArray(new BiomeGenBase[BiomeGenBase.explorationBiomesList.size()]);
//		printBiomeList(allBiomes);
		Biome[] allBiomes = getBiomes(biometypes);

		int zombieSpawnProb = ConfigHandler.getZombieSpawnProbility();
		int pigzombieSpawnProb = ConfigHandler.getPigZombieSpawnProbility();
		int minSpawn = ConfigHandler.getMinSpawn();
		int maxSpawn = ConfigHandler.getMaxSpawn();
		int minPZSpawn = ConfigHandler.getMinPZSpawn();
		int maxPZSpawn = ConfigHandler.getMaxPZSpawn();
		EntityRegistry.addSpawn(EntityCrackedZombie.class, zombieSpawnProb, minSpawn, maxSpawn, EnumCreatureType.MONSTER, allBiomes);
		if (ConfigHandler.getAllowPigZombieSpawns()) {
			proxy.info("*** Allowing " + pigzombieName + " spawns");
			EntityRegistry.addSpawn(EntityCrackedPigZombie.class, pigzombieSpawnProb, minPZSpawn, maxPZSpawn, EnumCreatureType.MONSTER, allBiomes);
		} else {
			proxy.info("*** Not allowing " + pigzombieName + " spawns");
		}
		
		
		// remove zombie spawning, we are replacing Minecraft zombies with CrackedZombies!
		if (!ConfigHandler.getZombieSpawns()) {
			proxy.info("*** Disabling default zombie spawns for all biomes");
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, allBiomes);
			DungeonHooks.removeDungeonMob("Zombie");
		} else {
			proxy.info("NOT disabling default zombie spawns, there will be fewer " + zombieName + "s!");
		}

		// remove pig zombie spawning, we are replacing Minecraft pig zombies with CrackedPigZombies!
		if (!ConfigHandler.getPigZombieSpawns()) {
			proxy.info("*** Disabling default pig zombie spawns for all biomes");
			EntityRegistry.removeSpawn(EntityPigZombie.class, EnumCreatureType.MONSTER, allBiomes);
		} else {
			proxy.info("NOT disabling default zombie spawns, there will be fewer " + pigzombieName + "s!");
		}
		
		// optionally remove creeper, skeleton, enderman, spiders and slime spawns for these biomes
		if (!ConfigHandler.getSpawnCreepers()) {
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.info("*** Removing creeper spawns");
		}
		if (!ConfigHandler.getSpawnSkeletons()) {
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.MONSTER, allBiomes);
			DungeonHooks.removeDungeonMob("Skeleton");
			proxy.info("*** Removing skeleton spawns and dungeon spawners");
		}
		if (!ConfigHandler.getSpawnEnderman()) {
			EntityRegistry.removeSpawn(EntityEnderman.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.info("*** Removing enderman spawns");
		}
		if (!ConfigHandler.getSpawnSpiders()) {
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, allBiomes);
			DungeonHooks.removeDungeonMob("Spider");
			proxy.info("*** Removing spider spawns and dungeon spawners");
		}
		if (!ConfigHandler.getSpawnSlime()) {
			EntityRegistry.removeSpawn(EntitySlime.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.info("*** Removing slime spawns");
		}
		
		if (!ConfigHandler.getSpawnWitches()) {
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.info("*** Removing witch spawns");
		}
	}
	
//	public void printBiomeList(Biome[] biomes)
//	{
//		for (Biome bgb : biomes) {
//			proxy.info("  >>> Including biome " + bgb.getBiomeName() + " for spawning");
//		}
//	}

	public Biome[] getBiomes(BiomeDictionary.Type... types) {
		LinkedList<Biome> list = new LinkedList<>();
		for (BiomeDictionary.Type t : types) {
			Biome[] biomes = BiomeDictionary.getBiomesForType(t);
			for (Biome bgb : biomes) {
				if (!list.contains(bgb)) {
					list.add(bgb);
					proxy.info("  >>> Including biome " + bgb.getBiomeName() + " for spawning");
				}
			}
		}
		return list.toArray(new Biome[0]);
	}
	
	// user has changed entries in the GUI config. save the results.
	@SuppressWarnings("unused")
	@SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(CrackedZombie.modid)) {
			if (event.isRequiresMcRestart()) {
				CrackedZombie.proxy.info("The configuration changes require a Minecraft restart!");
			}
			CrackedZombie.proxy.info("Configuration changes have been updated for the " + CrackedZombie.name);
            updateConfigInfo();
		}
    }
	
}
