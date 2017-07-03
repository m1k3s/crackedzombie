/*
 * CheckSpawnEvent.java
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

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.*;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class CheckSpawnEvent {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public Event.Result onCheckSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
        Event.Result result = Event.Result.DEFAULT;
        Entity entity = event.getEntity();

        if (entity instanceof EntityCrackedZombie) {
            result = Event.Result.ALLOW;
        }

        if (entity instanceof EntityZombie && !ConfigHandler.getZombieSpawns()) {
            result = Event.Result.DENY;
        }

        if (entity instanceof EntityPigZombie && !ConfigHandler.getPigZombieSpawns()) {
            result = Event.Result.DENY;
        }

        if (entity instanceof EntityCreeper && !ConfigHandler.getSpawnCreepers()) {
            result = Event.Result.DENY;
        }

        if (entity instanceof EntitySkeleton && !ConfigHandler.getSpawnSkeletons()) {
            result = Event.Result.DENY;
        }

        if (entity instanceof EntityEnderman && !ConfigHandler.getSpawnEnderman()) {
            result = Event.Result.DENY;
        }

        if (entity instanceof EntitySpider && !ConfigHandler.getSpawnSpiders()) {
            result = Event.Result.DENY;
        }

        if (entity instanceof EntityCaveSpider && !ConfigHandler.getSpawnCaveSpiders()) {
            result = Event.Result.DENY;
        }

        if (entity instanceof EntitySlime && !ConfigHandler.getSpawnSlime()) {
            result = Event.Result.DENY;
        }

        if (entity instanceof EntityWitch && !ConfigHandler.getSpawnWitches()) {
            result = Event.Result.DENY;
        }

        event.setResult(result);
        return result;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public Event.Result onDespawnEvent(LivingSpawnEvent.AllowDespawn event) {
        Event.Result result = Event.Result.DEFAULT;

        if (event.getEntity() instanceof EntityCrackedZombie) {
            result = Event.Result.DENY;
        }
        event.setResult(result);
        return result;
    }
}
