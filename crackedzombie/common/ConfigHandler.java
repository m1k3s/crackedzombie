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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {

    public static Configuration config;
    private static int zombieSpawnProb;
    private static int pigzombieSpawnProb;
    private static int huskSpawnProb;
    private static int giantZombieSpawnFactor;
    private static double giantZombieScale;
    private static boolean vanillaZombieSpawns;
    private static boolean vanillaPigZombieSpawns;
    private static boolean vanillaHuskSpawns;
    private static boolean doorBusting;
    private static boolean sickness;
    private static boolean pzSickness;
    private static boolean huskHunger;
    private static boolean startWithSword;
    private static boolean enchantSword;
    private static int minSpawn;
    private static int maxSpawn;
    private static int minPZSpawn;
    private static int maxPZSpawn;
    private static int minHuskSpawn;
    private static int maxHuskSpawn;
    private static double torchNoSpawnRadius;
    private static boolean allowChildSpawns;
    private static boolean allowCrackedPigZombieSpawns;
    private static boolean allowCrackedHuskSpawns;
    private static boolean allowGiantCrackedZombieSpawns;
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
    private static boolean pzAlwaysAttackPlayers;

    private static final String GENERAL_COMMENTS = CrackedZombie.NAME + " Config\nMichael Sheppard (crackedEgg)\n"
            + "For Minecraft Version " + CrackedZombie.MCVERSION + "\n";
    private static final String SPAWN_PROB_COMMENT = "zombieSpawnProb adjust to probability of zombies spawning\n"
            + "The higher the number the more likely zombies will spawn.";
    private static final String PZ_SPAWN_PROB_COMMENT = "pigzombieSpawnProb adjust to probability of pigzombies spawning\n"
            + "The higher the number the more likely pigzombies will spawn.";
    private static final String VANILLA_ZOMBIE_COMMENT = "vanillaZombieSpawns allows/disallows vanilla zombies spawns, default is false,\n"
            + " no vanilla minecraft zombies will spawn. Only the " + CrackedZombie.ZOMBIE_NAME + "s will spawn.\n"
            + " If set to true, fewer " + CrackedZombie.ZOMBIE_NAME + "s will spawn.";
    private static final String VANILLA_PIG_ZOMBIE_COMMENT = "vanillaPigZombieSpawns allows/disallows vanilla pig zombies spawns, default is false,\n"
            + " no vanilla minecraft pig zombies will spawn. Only the " + CrackedZombie.PIGZOMBIE_NAME + "s will spawn.\n"
            + " If set to true, fewer " + CrackedZombie.PIGZOMBIE_NAME + "s will spawn.";
    private static final String DOOR_BUSTING_COMMENT = "doorBusting, set to true to have zombies try to break down doors,"
            + " otherwise set to false. It's quieter.";
    private static final String CRACKED_PIG_ZOMBIE_COMMENT = "allow CrackedPigZombies to spawn";
    private static final String CHILD_COMMENT = "allowChildSpawns, set to true to have child zombies, otherwise set to false.";
    private static final String SICKNESS_COMMENT = "Sickness, set to true to have contact with zombies poison the player.";
    private static final String MIN_SPAWN_COMMENT = "minSpawn, minimum number of crackedzombies per spawn event";
    private static final String MAX_SPAWN_COMMENT = "maxSpawn, maximum number of crackedzombies per spawn event";
    private static final String PZ_SICKNESS_COMMENT = "Pig Zombie Sickness, set to true to have contact with pigzombies poison the player.";
    private static final String MIN_PZ_SPAWN_COMMENT = "minPZSpawn, minimum number of crackedpigzombies per spawn event";
    private static final String MAX_PZ_SPAWN_COMMENT = "maxPZSpawn, maximum number of crackedpigzombies per spawn event";
    private static final String START_WITH_SWORD_COMMENT = "Allows the player to spawn with a random type sword, handy in the apocalypse!";
    private static final String ENCHANT_SWORD_COMMENT = "set true to enchant the sword given to the player";
    private static final String NO_SPAWN_RADIUS_COMMENT = "set the radius in blocks for no spawning near torches, zero enables spawing near torches";
    private static final String FOLLOW_RANGE_COMMENT = "set the follow range of the zombies";
    private static final String MOVE_SPEED_COMMENT = "set the movement speed of the zombies";
    private static final String ATTACK_DAMAGE_COMMENT = "set the initial attack damage caused by the zombies";
    private static final String PZ_MOVE_SPEED_COMMENT = "set the movement speed of the pig zombies";
    private static final String PZ_ATTACK_DAMAGE_COMMENT = "set the initial attack damage caused by the pig zombies";
    private static final String ATTACK_PIGS_COMMENT = "Attack and kill pigs";
    private static final String ATTACK_VILLAGERS_COMMENT = "Attack and convert villagers";
    private static final String NIGHT_SPAWN_ONLY_COMMENT = "Spawn cracked zombies at night only";
    private static final String SPAWN_IN_NETHER_COMMENT = "Spawn cracked zombies in the Nether";
    private static final String SPAWN_IN_END_COMMENT = "Spawn cracked zombies in the End";
    private static final String IS_IMMUNE_TO_FIRE_COMMENT = "whether or not the pig zombies are immune to fire";
    private static final String MIN_HUSK_SPAWN_COMMENT = "minSpawn, minimum number of crackedHusks per spawn event";
    private static final String MAX_HUSK_SPAWN_COMMENT = "maxSpawn, maximum number of crackedHusks per spawn event";
    private static final String SPAWN_HUSK_PROB_COMMENT = "HuskSpawnProb adjust to probability of Husks spawning\n"
            + "The higher the number the more likely Husks will spawn.";
    private static final String CRACKED_HUSK_ZOMBIE_COMMENT = "allow CrackedHusks to spawn";
    private static final String HUSK_HUNGER_COMMENT = "allow CrackedHusks to infect player with hunger";
    private static final String VANILLA_HUSK_SPAWN_COMMENT = "vanillaHuskSpawns allows/disallows vanilla husk spawns, default is false,\n"
            + " no vanilla minecraft husks will spawn. Only the " + CrackedZombie.HUSK_NAME + "s will spawn.\n"
            + " If set to true, fewer " + CrackedZombie.HUSK_NAME + "s will spawn.";
    private static final String GIANT_SPAWN_FACTOR_COMMENT = "giantzombieSpawnFactor this factor determines chance of giantzombies spawning\n"
            + "The lower the number the more likely giantzombies will spawn.";
    private static final String ALLOW_GIANT_ZOMBIE_SPAWN_COMMENT = "allow giantCrackedZombies to spawn";
    private static final String GIANT_ZOMBIE_SCALE_COMMENT = "scale factor for the giant crackedzombie";
    private static final String PZ_ALWAYS_ATTCK_COMMENT = "if true the pigzombies will always attack players\n"
            + "Otherwise the pigzombies will only attack if provoked";

    public static void startConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        updateConfigInfo();
    }

    public static void updateConfigInfo() {
        try {
            config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, GENERAL_COMMENTS);
            zombieSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "zombieSpawnProb", 12, SPAWN_PROB_COMMENT).getInt();
            pigzombieSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "pigzombieSpawnProb", 6, PZ_SPAWN_PROB_COMMENT).getInt();
            vanillaZombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "vanillaZombieSpawns", false, VANILLA_ZOMBIE_COMMENT).getBoolean(false);
            vanillaPigZombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "vanillaPigZombieSpawns", false, VANILLA_PIG_ZOMBIE_COMMENT).getBoolean(false);
            doorBusting = config.get(Configuration.CATEGORY_GENERAL, "doorBusting", false, DOOR_BUSTING_COMMENT).getBoolean(false);
            sickness = config.get(Configuration.CATEGORY_GENERAL, "sickness", false, SICKNESS_COMMENT).getBoolean(false);
            pzSickness = config.get(Configuration.CATEGORY_GENERAL, "pzSickness", false, PZ_SICKNESS_COMMENT).getBoolean(false);
            startWithSword = config.get(Configuration.CATEGORY_GENERAL, "startWithSword", false, START_WITH_SWORD_COMMENT).getBoolean(false);
            enchantSword = config.get(Configuration.CATEGORY_GENERAL, "enchantSword", false, ENCHANT_SWORD_COMMENT).getBoolean(false);
            minSpawn = config.get(Configuration.CATEGORY_GENERAL, "minSpawn", 1, MIN_SPAWN_COMMENT).getInt();
            maxSpawn = config.get(Configuration.CATEGORY_GENERAL, "maxSpawn", 4, MAX_SPAWN_COMMENT).getInt();
            minPZSpawn = config.get(Configuration.CATEGORY_GENERAL, "minPZSpawn", 1, MIN_PZ_SPAWN_COMMENT).getInt();
            maxPZSpawn = config.get(Configuration.CATEGORY_GENERAL, "maxPZSpawn", 3, MAX_PZ_SPAWN_COMMENT).getInt();
            torchNoSpawnRadius = config.get(Configuration.CATEGORY_GENERAL, "noSpawnTorchRadius", 3.0, NO_SPAWN_RADIUS_COMMENT).getDouble();
            allowChildSpawns = config.get(Configuration.CATEGORY_GENERAL, "allowChildSpawns", true, CHILD_COMMENT).getBoolean(true);
            allowCrackedPigZombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "allowCrackedPigZombieSpawns", true, CRACKED_PIG_ZOMBIE_COMMENT).getBoolean(true);
            followRange = config.get(Configuration.CATEGORY_GENERAL, "followRange", 35.0, FOLLOW_RANGE_COMMENT).getDouble();
            moveSpeed =  config.get(Configuration.CATEGORY_GENERAL, "moveSpeed", 0.23, MOVE_SPEED_COMMENT).getDouble();
            attackDamage = config.get(Configuration.CATEGORY_GENERAL, "attackDamage", 3.0, ATTACK_DAMAGE_COMMENT).getDouble();
            pzMoveSpeed =  config.get(Configuration.CATEGORY_GENERAL, "pzMoveSpeed", 0.23, PZ_MOVE_SPEED_COMMENT).getDouble();
            pzAttackDamage = config.get(Configuration.CATEGORY_GENERAL, "pzAttackDamage", 5.0, PZ_ATTACK_DAMAGE_COMMENT).getDouble();
            attackPigs = config.get(Configuration.CATEGORY_GENERAL, "attackPigs", true, ATTACK_PIGS_COMMENT).getBoolean(true);
            attackVillagers = config.get(Configuration.CATEGORY_GENERAL, "attackVillagers", true, ATTACK_VILLAGERS_COMMENT).getBoolean(true);
            spawnInNether = config.get(Configuration.CATEGORY_GENERAL, "spawnInNether", true, SPAWN_IN_NETHER_COMMENT).getBoolean(true);
            spawnInEnd = config.get(Configuration.CATEGORY_GENERAL, "spawnInEnd", false, SPAWN_IN_END_COMMENT).getBoolean(false);
            nightSpawnOnly = config.get(Configuration.CATEGORY_GENERAL, "nightSpawnOnly", false, NIGHT_SPAWN_ONLY_COMMENT).getBoolean(false);
            isImmuneToFire = config.get(Configuration.CATEGORY_GENERAL, "isImmuneToFire", true, IS_IMMUNE_TO_FIRE_COMMENT).getBoolean(true);
            minHuskSpawn = config.get(Configuration.CATEGORY_GENERAL, "minHuskSpawn", 1, MIN_HUSK_SPAWN_COMMENT).getInt();
            maxHuskSpawn = config.get(Configuration.CATEGORY_GENERAL, "maxHuskSpawn", 4, MAX_HUSK_SPAWN_COMMENT).getInt();
            huskSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "huskSpawnProb", 12, SPAWN_HUSK_PROB_COMMENT).getInt();
            huskHunger = config.get(Configuration.CATEGORY_GENERAL, "huskHunger", true, HUSK_HUNGER_COMMENT).getBoolean(true);
            allowCrackedHuskSpawns = config.get(Configuration.CATEGORY_GENERAL, "allowCrackedHuskSpawns", true, CRACKED_HUSK_ZOMBIE_COMMENT).getBoolean(true);
            vanillaHuskSpawns = config.get(Configuration.CATEGORY_GENERAL, "vanillaHuskSpawns", false, VANILLA_HUSK_SPAWN_COMMENT).getBoolean(false);
            giantZombieSpawnFactor = config.get(Configuration.CATEGORY_GENERAL, "giantZombieSpawnFactor", 30, GIANT_SPAWN_FACTOR_COMMENT).getInt();
            allowGiantCrackedZombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "allowGiantCrackedZombieSpawns", true, ALLOW_GIANT_ZOMBIE_SPAWN_COMMENT).getBoolean();
            giantZombieScale = config.get(Configuration.CATEGORY_GENERAL, "giantZombieScale", 1.25, GIANT_ZOMBIE_SCALE_COMMENT).getDouble();
            pzAlwaysAttackPlayers = config.get(Configuration.CATEGORY_GENERAL, "pzAlwaysAttackPlayers", true, PZ_ALWAYS_ATTCK_COMMENT).getBoolean();

        } catch (Exception e) {
            CrackedZombie.instance.info("failed to load or read the config file");
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
        return vanillaPigZombieSpawns;
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

    public static int getMinHuskSpawn() {
        return minHuskSpawn;
    }

    public static int getMaxHuskSpawn() {
        return maxHuskSpawn;
    }

    public static int getHuskSpawnProbability() {
        return huskSpawnProb;
    }

    public static boolean allowCrackedHuskSpawns() {
        return allowCrackedHuskSpawns;
    }

    public static boolean allowVanillaHuskSpawns() {
        return vanillaHuskSpawns;
    }

    public static boolean getHuskHunger() {
        return huskHunger;
    }

    public static int getGiantZombieSpawnFactor() {
        return giantZombieSpawnFactor;
    }

    public static boolean allowGiantCrackedZombieSpawns() {
        return allowGiantCrackedZombieSpawns;
    }

    public static double getGiantZombieScale() {
        return giantZombieScale;
    }

    public static boolean isPzAlwaysAttackPlayers() {
        return pzAlwaysAttackPlayers;
    }
}
