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


import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.*;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.common.BiomeDictionary;
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

import java.util.Collection;
import java.util.LinkedList;

@Mod(modid = CrackedZombie.MODID, name = CrackedZombie.NAME, version = CrackedZombie.MODVERSION)

public class CrackedZombie {

    public static final String MCVERSION = "1.12.2";
    public static final String MODVERSION = "3.8.1";
    public static final String MODID = "crackedzombiemod";
    public static final String NAME = "Cracked Zombie Mod";
    public static final String ZOMBIE_NAME = "crackedzombie";
    public static final String PIGZOMBIE_NAME = "crackedpigzombie";
    public static final String HUSK_NAME = "crackedhusk";
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

        EntityRegistry.registerModEntity(new ResourceLocation(CrackedZombie.MODID, ZOMBIE_NAME), EntityCrackedZombie.class, ZOMBIE_NAME, entityID, CrackedZombie.instance, 80, 3, true, 0x00AFAF, 0x799C45);
        EntityRegistry.registerModEntity(new ResourceLocation(CrackedZombie.MODID, PIGZOMBIE_NAME), EntityCrackedPigZombie.class, PIGZOMBIE_NAME, ++entityID, CrackedZombie.instance, 80, 3, true, 0x799C45, 0x00AFAF);
        EntityRegistry.registerModEntity(new ResourceLocation(CrackedZombie.MODID, HUSK_NAME), EntityCrackedHusk.class, HUSK_NAME, ++entityID, CrackedZombie.instance, 80, 3, true, 0x799C45, 0xcc5454);
        proxy.registerRenderers();
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void Init(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(CrackedZombie.instance);
        MinecraftForge.EVENT_BUS.register(new PlayerLoggedInEvent());

        // zombies should spawn in dungeon spawners
        DungeonHooks.addDungeonMob(new ResourceLocation(CrackedZombie.MODID, ZOMBIE_NAME), 200);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void PostInit(FMLPostInitializationEvent event) {
        proxy.info("*** Scanning for available biomes");
        Biome[] spawnBiomes = getAllSpawnBiomes();
        Biome[] desertBiomes = getBiomesFromTypes(BiomeDictionary.Type.DRY, BiomeDictionary.Type.HOT, BiomeDictionary.Type.SANDY);
        Biome[] notDesertBiomes = excludeBiomesWithTypes(BiomeDictionary.Type.DRY, BiomeDictionary.Type.HOT, BiomeDictionary.Type.SANDY);

        int zombieSpawnProb = ConfigHandler.getZombieSpawnProbility();
        int pigzombieSpawnProb = ConfigHandler.getPigZombieSpawnProbility();
        int minSpawn = ConfigHandler.getMinSpawn();
        int maxSpawn = ConfigHandler.getMaxSpawn();
        int minPZSpawn = ConfigHandler.getMinPZSpawn();
        int maxPZSpawn = ConfigHandler.getMaxPZSpawn();
        int minHuskSpawn = ConfigHandler.getMinHuskSpawn();
        int maxHuskSpawn = ConfigHandler.getMaxHuskSpawn();
        int huskspawnProb = ConfigHandler.getHuskSpawnProb();

        EntityRegistry.addSpawn(EntityCrackedZombie.class, zombieSpawnProb, minSpawn, maxSpawn, EnumCreatureType.MONSTER, notDesertBiomes);
        if (ConfigHandler.getAllowCrackedPigZombieSpawns()) {
            proxy.info("*** Allowing " + PIGZOMBIE_NAME + " spawns");
            EntityRegistry.addSpawn(EntityCrackedPigZombie.class, pigzombieSpawnProb, minPZSpawn, maxPZSpawn, EnumCreatureType.MONSTER, spawnBiomes);
        } else {
            proxy.info("*** Not allowing " + PIGZOMBIE_NAME + " spawns");
        }
        if (ConfigHandler.allowCrackedHuskSpawns()) {
            proxy.info("*** Allowing " + HUSK_NAME + " spawns");
            EntityRegistry.addSpawn(EntityCrackedHusk.class, huskspawnProb, minHuskSpawn, maxHuskSpawn, EnumCreatureType.MONSTER, desertBiomes);
        }
        if (!ConfigHandler.allowVanillaZombieSpawns()) {
            EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, spawnBiomes);
        }
        if (!ConfigHandler.allowVanillaPigzombieSpawns()) {
            EntityRegistry.removeSpawn(EntityPigZombie.class, EnumCreatureType.MONSTER, spawnBiomes);
        }
        if (!ConfigHandler.allowCrackedHuskSpawns()) {
            EntityRegistry.removeSpawn(EntityHusk.class, EnumCreatureType.MONSTER, desertBiomes);
        }
    }

    private Biome[] getAllSpawnBiomes() {
        LinkedList<Biome> list = new LinkedList<>();
        Collection<Biome> biomes = ForgeRegistries.BIOMES.getValuesCollection();
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
                    proxy.info("  >>> getAllSpawnBiomes: " + bgb.getBiomeName());
                }
            }
        }
        return list.toArray(new Biome[list.size()]);
    }

    private Biome[] getBiomesFromTypes(BiomeDictionary.Type... types) {
        LinkedList<Biome> list = new LinkedList<>();
        Collection<Biome> biomes = ForgeRegistries.BIOMES.getValuesCollection();
        for (Biome biome : biomes) {
            int count = types.length;
            int shouldAdd = 0;
            for (BiomeDictionary.Type t : types) {
                if (BiomeDictionary.hasType(biome, t)) {
                    shouldAdd++;
                }
            }
            if (!list.contains(biome) && shouldAdd == count) {
                list.add(biome);
                if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                    proxy.info("  >>> getBiomesFromTypes: " + biome.getBiomeName());
                }
            }
        }
        return list.toArray(new Biome[list.size()]);
    }

    private Biome[] excludeBiomesWithTypes(BiomeDictionary.Type... types) {
        LinkedList<Biome> list = new LinkedList<>();
        Collection<Biome> biomes = ForgeRegistries.BIOMES.getValuesCollection();
        for (Biome biome : biomes) {
            for (BiomeDictionary.Type t : types) {
                if (!BiomeDictionary.hasType(biome, t)) {
                    if (!list.contains(biome)) {
                        list.add(biome);
                        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                            proxy.info("  >>> excludeBiomesWithTypes: " + biome.getBiomeName());
                        }
                    }
                }
            }
        }
        return list.toArray(new Biome[list.size()]);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)) {
            event.getTable().getPool("main").addEntry(iron_sword);
        }
    }

}
