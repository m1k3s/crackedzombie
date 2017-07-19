/*
 * CrackedZombie.java
 *
 *  Copyright (c) 2017 Michael Sheppard
 *
 * =====GPL=============================================================
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 * =====================================================================
 */
package com.crackedzombie.common;


import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEnd;
import net.minecraft.world.biome.BiomeHell;
import net.minecraft.world.biome.BiomeVoid;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedList;
import java.util.List;

@Mod(modid = CrackedZombie.MODID, name = CrackedZombie.NAME, version = CrackedZombie.MODVERSION)

public class CrackedZombie {

    public static final String MCVERSION = "1.12";
    public static final String MODVERSION = "3.7.0";
    public static final String MODID = "crackedzombiemod";
    public static final String NAME = "Cracked Zombie Mod";
    public static final String ZOMBIE_NAME = "crackedzombie";
    public static final String PIGZOMBIE_NAME = "crackedpigzombie";
    private int entityID = 0;
    private static boolean spawnInNether = ConfigHandler.getSpawnInNether();
    private static boolean spawnInEnd = ConfigHandler.getSpawnInEnd();
    public static LootEntry iron_sword = new LootEntryItem(Items.IRON_SWORD, 100, 50, new LootFunction[0], new LootCondition[0], "iron_sword");

    @Mod.Instance(MODID)
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

        EntityRegistry.registerModEntity(new ResourceLocation(CrackedZombie.MODID, ZOMBIE_NAME), EntityCrackedZombie.class, ZOMBIE_NAME, entityID++, CrackedZombie.instance, 80, 3, true, 0x00AFAF, 0x799C45);
        EntityRegistry.registerModEntity(new ResourceLocation(CrackedZombie.MODID, PIGZOMBIE_NAME), EntityCrackedPigZombie.class, PIGZOMBIE_NAME, entityID, CrackedZombie.instance, 80, 3, true, 0x799C45, 0x00AFAF);
        proxy.registerRenderers();
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void Init(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(CrackedZombie.instance);
        MinecraftForge.EVENT_BUS.register(new PlayerLoggedInEvent());
//        MinecraftForge.EVENT_BUS.register(new CheckSpawnEvent());

        // zombies should spawn in dungeon spawners
        DungeonHooks.addDungeonMob(new ResourceLocation(CrackedZombie.MODID, ZOMBIE_NAME), 200);
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
        if (ConfigHandler.getAllowCrackedPigZombieSpawns()) {
            proxy.info("*** Allowing " + PIGZOMBIE_NAME + " spawns");
            EntityRegistry.addSpawn(EntityCrackedPigZombie.class, pigzombieSpawnProb, minPZSpawn, maxPZSpawn, EnumCreatureType.MONSTER, spawnBiomes);
        } else {
            proxy.info("*** Not allowing " + PIGZOMBIE_NAME + " spawns");
        }

        if (!ConfigHandler.allowVanillaZombieSpawns()) {
            EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, spawnBiomes);
        }
        if (!ConfigHandler.allowVanillaPigzombieSpawns()) {
            EntityRegistry.removeSpawn(EntityPigZombie.class, EnumCreatureType.MONSTER, spawnBiomes);
        }
    }

    public Biome[] getSpawnBiomes() {
        LinkedList<Biome> list = new LinkedList<>();
        List<Biome> biomes = ForgeRegistries.BIOMES.getValues();
        for (Biome bgb : biomes) {
            if (bgb instanceof BiomeVoid) {
                continue;
            }
            if (bgb instanceof BiomeEnd && !spawnInEnd) {
                continue;
            }
            if (bgb instanceof BiomeHell && !spawnInNether) {
                continue;
            }
            if (!list.contains(bgb)) {
                list.add(bgb);
                if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                    proxy.info("  >>> Including biome " + bgb.getBiomeName() + " for spawning");
                }
            }
        }
        return list.toArray(new Biome[0]);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)) {
            event.getTable().getPool("main").addEntry(iron_sword);
        }
    }
    
}
