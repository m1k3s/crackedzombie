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
}
