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

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.LinkedList;
import java.util.List;

@Mod(modid = CrackedZombie.modid, name = CrackedZombie.name, version = CrackedZombie.modversion, guiFactory = CrackedZombie.guifactory)

public class CrackedZombie {

    public static final String mcversion = "1.11.2";
    public static final String modversion = "3.6.2";
    public static final String modid = "crackedzombiemod";
    public static final String name = "Cracked Zombie Mod";
    public static final String zombieName = "crackedzombie";
    public static final String pigzombieName = "crackedpigzombie";
    public static final String guifactory = "com.crackedzombie.client.CrackedZombieConfigGUIFactory";
    private int entityID = 0;
    private static boolean spawnInNether = ConfigHandler.getSpawnInNether();
    private static boolean spawnInEnd = ConfigHandler.getSpawnInEnd();

    @Mod.Instance(modid)
    public static CrackedZombie instance;

    @SidedProxy(
            clientSide = "com.crackedzombie.client.ClientProxyCrackedZombie",
            serverSide = "com.crackedzombie.common.CommonProxyCrackedZombie"
    )

    public static CommonProxyCrackedZombie proxy;

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.startConfig(event);

        EntityRegistry.registerModEntity(new ResourceLocation(CrackedZombie.modid, zombieName), EntityCrackedZombie.class, zombieName, entityID++, CrackedZombie.instance, 80, 3, true, 0x00AFAF, 0x799C45);
        EntityRegistry.registerModEntity(new ResourceLocation(CrackedZombie.modid, pigzombieName), EntityCrackedPigZombie.class, pigzombieName, entityID, CrackedZombie.instance, 80, 3, true, 0x799C45, 0x00AFAF);
        proxy.registerRenderers();
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void Init(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(CrackedZombie.instance);
        MinecraftForge.EVENT_BUS.register(new PlayerLoggedInEvent());
        MinecraftForge.EVENT_BUS.register(new CheckSpawnEvent());

        // zombies should spawn in dungeon spawners
        DungeonHooks.addDungeonMob(new ResourceLocation(CrackedZombie.modid, zombieName), 200);
        // add steel swords to the loot. you may need these.
//		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(Items.iron_sword), 1, 1, 4));
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void PostInit(FMLPostInitializationEvent event) {
        proxy.info("*** Scanning for available biomes");
        Biome[] spawnBiomes = getSpawnBiomes();

        int zombieSpawnProb = ConfigHandler.getZombieSpawnProbility();
        int pigzombieSpawnProb = ConfigHandler.getPigZombieSpawnProbility();
        int minSpawn = ConfigHandler.getMinSpawn();
        int maxSpawn = ConfigHandler.getMaxSpawn();
        int minPZSpawn = ConfigHandler.getMinPZSpawn();
        int maxPZSpawn = ConfigHandler.getMaxPZSpawn();
        EntityRegistry.addSpawn(EntityCrackedZombie.class, zombieSpawnProb, minSpawn, maxSpawn, EnumCreatureType.MONSTER, spawnBiomes);
        if (ConfigHandler.getAllowPigZombieSpawns()) {
            proxy.info("*** Allowing " + pigzombieName + " spawns");
            EntityRegistry.addSpawn(EntityCrackedPigZombie.class, pigzombieSpawnProb, minPZSpawn, maxPZSpawn, EnumCreatureType.MONSTER, spawnBiomes);
        } else {
            proxy.info("*** Not allowing " + pigzombieName + " spawns");
        }
    }

    public Biome[] getSpawnBiomes() {
        LinkedList<Biome> list = new LinkedList<>();
        List<Biome> biomes = ForgeRegistries.BIOMES.getValues();
        for (Biome bgb : biomes) {
            if (bgb.getBiomeName().equalsIgnoreCase("void")) {
                continue;
            }
            if (bgb.getBiomeName().equalsIgnoreCase("end") && !spawnInEnd) {
                continue;
            }
            if (bgb.getBiomeName().equalsIgnoreCase("nether") && !spawnInNether) {
                continue;
            }
            if (!list.contains(bgb)) {
                list.add(bgb);
                proxy.info("  >>> Including biome " + bgb.getBiomeName() + " for spawning");
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

//    @SubscribeEvent
//    public void onLootTableLoad(LootTableLoadEvent event) {
//        if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {
//            event.getTable().addPool(new LootPool(new WeightedRandom.Item), );
//        }
//    }

}
