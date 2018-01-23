/*
 * ConfigHandler.java
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

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ConfigHandler {

    public static Configuration config;
    private static int zombieSpawnProb;
    private static int pigzombieSpawnProb;
    private static boolean vanillaZombieSpawns;
    private static boolean vanillapigZombieSpawns;
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
    private static boolean allowCrackedPigZombieSpawns;
    private static double followRange;
    private static double moveSpeed;
    private static double attackDamage;
    private static double pzMoveSpeed;
    private static double pzAttackDamage;
    private static boolean attackPigs;
    private static boolean attackVillagers;
    private static boolean nightSpawnOnly;
    private static boolean spawnInNether;
    private static boolean spawnInEnd;
    private static boolean isImmuneToFire;

    private static final String generalComments = CrackedZombie.NAME + " Config\nMichael Sheppard (crackedEgg)\n"
            + "For Minecraft Version " + CrackedZombie.MCVERSION + "\n";
    private static final String spawnProbComment = "zombieSpawnProb adjust to probability of zombies spawning\n"
            + "The higher the number the more likely zombies will spawn.";
    private static final String pzSpawnProbComment = "pigzombieSpawnProb adjust to probability of pigzombies spawning\n"
            + "The higher the number the more likely pigzombies will spawn.";
    private static final String vanillaZombieComment = "vanillaZombieSpawns allows/disallows vanilla zombies spawns, default is false,\n"
            + " no vanilla minecraft zombies will spawn. Only the " + CrackedZombie.ZOMBIE_NAME + "s will spawn.\n"
            + " If set to true, fewer " + CrackedZombie.ZOMBIE_NAME + "s will spawn.";
    private static final String vanillaPigZombieComment = "vanillapigZombieSpawns allows/disallows vanilla pig zombies spawns, default is false,\n"
            + " no vanilla minecraft pig zombies will spawn. Only the " + CrackedZombie.PIGZOMBIE_NAME + "s will spawn.\n"
            + " If set to true, fewer " + CrackedZombie.PIGZOMBIE_NAME + "s will spawn.";
    private static final String doorBustingComment = "doorBusting, set to true to have zombies try to break down doors,"
            + " otherwise set to false. It's quieter.";
    private static final String crackedPigZombieComment = "allow CrackedPigZombies to spawn";
    private static final String childComment = "allowChildSpawns, set to true to have child zombies, otherwise set to false.";
    private static final String sicknessComment = "Sickness, set to true to have contact with zombies poison the player.";
    private static final String minSpawnComment = "minSpawn, minimum number of crackedzombies per spawn event";
    private static final String maxSpawnComment = "maxSpawn, maximum number of crackedzombies per spawn event";
    private static final String pzSicknessComment = "Pig Zombie Sickness, set to true to have contact with pigzombies poison the player.";
    private static final String minPZSpawnComment = "minPZSpawn, minimum number of crackedpigzombies per spawn event";
    private static final String maxPZSpawnComment = "maxPZSpawn, maximum number of crackedpigzombies per spawn event";
    private static final String startWithSwordComment = "Allows the player to spawn with a random type sword, handy in the apocalypse!";
    private static final String enchantSwordComment = "set true to enchant the sword given to the player";
    private static final String noSpawnRadiusComment = "set the radius in blocks for no spawning near torches, zero enables spawing near torches";
    private static final String followRangeComment = "set the follow range of the zombies";
    private static final String moveSpeedComment = "set the movement speed of the zombies";
    private static final String attackDamageComment = "set the initial attack damage caused by the zombies";
    private static final String pzMoveSpeedComment = "set the movement speed of the pig zombies";
    private static final String pzAttackDamageComment = "set the initial attack damage caused by the pig zombies";
    private static final String attackPigsComment = "Attack and kill pigs";
    private static final String attackVillagersComment = "Attack and convert villagers";
    private static final String nightSpawnOnlyComment = "Spawn cracked zombies at night only";
    private static final String spawnInNetherComment = "Spawn cracked zombies in the Nether";
    private static final String spawnInEndComment = "Spawn cracked zombies in the End";
    private static final String isImmuneToFireComment = "whether or not the pig zombies are immune to fire";

    public static void startConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        updateConfigInfo();
    }

    public static void updateConfigInfo() {
        try {
            config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);
            zombieSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "zombieSpawnProb", 12, spawnProbComment).getInt();
            pigzombieSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "pigzombieSpawnProb", 12, pzSpawnProbComment).getInt();
            vanillaZombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "vanillaZombieSpawns", false, vanillaZombieComment).getBoolean(false);
            vanillapigZombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "vanillapigZombieSpawns", false, vanillaPigZombieComment).getBoolean(false);
            doorBusting = config.get(Configuration.CATEGORY_GENERAL, "doorBusting", false, doorBustingComment).getBoolean(false);
            sickness = config.get(Configuration.CATEGORY_GENERAL, "sickness", false, sicknessComment).getBoolean(false);
            pzSickness = config.get(Configuration.CATEGORY_GENERAL, "pzSickness", false, pzSicknessComment).getBoolean(false);
            startWithSword = config.get(Configuration.CATEGORY_GENERAL, "startWithSword", false, startWithSwordComment).getBoolean(false);
            enchantSword = config.get(Configuration.CATEGORY_GENERAL, "enchantSword", false, enchantSwordComment).getBoolean(false);
            minSpawn = config.get(Configuration.CATEGORY_GENERAL, "minSpawn", 4, minSpawnComment).getInt();
            maxSpawn = config.get(Configuration.CATEGORY_GENERAL, "maxSpawn", 4, maxSpawnComment).getInt();
            minPZSpawn = config.get(Configuration.CATEGORY_GENERAL, "minPZSpawn", 4, minPZSpawnComment).getInt();
            maxPZSpawn = config.get(Configuration.CATEGORY_GENERAL, "maxPZSpawn", 4, maxPZSpawnComment).getInt();
            torchNoSpawnRadius = config.get(Configuration.CATEGORY_GENERAL, "noSpawnTorchRadius", 3.0, noSpawnRadiusComment).getDouble();
            allowChildSpawns = config.get(Configuration.CATEGORY_GENERAL, "allowChildSpawns", true, childComment).getBoolean(true);
            allowCrackedPigZombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "allowCrackedPigZombieSpawns", true, crackedPigZombieComment).getBoolean(true);
            followRange = config.get(Configuration.CATEGORY_GENERAL, "followRange", 35.0, followRangeComment).getDouble();
            moveSpeed =  config.get(Configuration.CATEGORY_GENERAL, "moveSpeed", 0.23, moveSpeedComment).getDouble();
            attackDamage = config.get(Configuration.CATEGORY_GENERAL, "attackDamage", 3.0, attackDamageComment).getDouble();
            pzMoveSpeed =  config.get(Configuration.CATEGORY_GENERAL, "pzMoveSpeed", 0.23, pzMoveSpeedComment).getDouble();
            pzAttackDamage = config.get(Configuration.CATEGORY_GENERAL, "pzAttackDamage", 5.0, pzAttackDamageComment).getDouble();
            attackPigs = config.get(Configuration.CATEGORY_GENERAL, "attackPigs", true, attackPigsComment).getBoolean(true);
            attackVillagers = config.get(Configuration.CATEGORY_GENERAL, "attackVillagers", true, attackVillagersComment).getBoolean(true);
            spawnInNether = config.get(Configuration.CATEGORY_GENERAL, "spawnInNether", true, spawnInNetherComment).getBoolean(true);
            spawnInEnd = config.get(Configuration.CATEGORY_GENERAL, "spawnInEnd", false, spawnInEndComment).getBoolean(false);
            nightSpawnOnly = config.get(Configuration.CATEGORY_GENERAL, "nightSpawnOnly", false, nightSpawnOnlyComment).getBoolean(false);
            isImmuneToFire = config.get(Configuration.CATEGORY_GENERAL, "isImmuneToFire", true, isImmuneToFireComment).getBoolean(true);
        } catch (Exception e) {
            CrackedZombie.proxy.info("failed to load or read the config file");
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static boolean getIsImmuneToFire() {
        return isImmuneToFire;
    }

    public static int getMinSpawn() {
        return minSpawn;
    }

    public static int getMaxSpawn() {
        return maxSpawn;
    }

    public static int getMinPZSpawn() {
        return minPZSpawn;
    }

    public static int getMaxPZSpawn() {
        return maxPZSpawn;
    }

    public static int getZombieSpawnProbility() {
        return zombieSpawnProb;
    }

    public static int getPigZombieSpawnProbility() {
        return pigzombieSpawnProb;
    }

    public static boolean allowVanillaZombieSpawns() {
        return vanillaZombieSpawns;
    }

    public static boolean allowVanillaPigzombieSpawns() {
        return vanillapigZombieSpawns;
    }

    public static boolean getDoorBusting() {
        return doorBusting;
    }

    public static boolean getSickness() {
        return sickness;
    }

    public static boolean getPZSickness() {
        return pzSickness;
    }

    public static boolean getStartWithSword() {
        return startWithSword;
    }

    public static boolean getEnchantSword() {
        return enchantSword;
    }

    public static double getTorchNoSpawnRadius() {
        return torchNoSpawnRadius;
    }

    public static boolean getAllowChildSpawns() {
        return allowChildSpawns;
    }

    public static boolean getAllowCrackedPigZombieSpawns() {
        return allowCrackedPigZombieSpawns;
    }

    public static double getMovementSpeed() {
        return moveSpeed;
    }

    public static double getPZMovementSpeed() {
        return pzMoveSpeed;
    }

    public static double getFollowRange() {
        return followRange;
    }

    public static double getAttackDamage() {
        return attackDamage;
    }

    public static double getPZAttackDamage() {
        return pzAttackDamage;
    }

    public static boolean getAttackPigs() {
        return attackPigs;
    }

    public static boolean getAttackVillagers() {
        return attackVillagers;
    }

    public static boolean getNightSpawnOnly() {
        return nightSpawnOnly;
    }

    public static boolean getSpawnInNether() {
        return spawnInNether;
    }

    public static boolean getSpawnInEnd() {
        return spawnInEnd;
    }
}
