/*
 * EntityAttackAICrackedMelee.java
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

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class EntityAttackAICrackedMelee extends EntityAIBase {
    private World world;
    protected EntityCreature attacker;
    int attackTick;
    private double speedTowardsTarget;
    private boolean longMemory;
    private Path entityPathEntity;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
//    protected final int field_188493_g = 20;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;

    public EntityAttackAICrackedMelee(EntityCreature creature, double speedIn, boolean useLongMemory) {
        attacker = creature;
        world = creature.world;
        speedTowardsTarget = speedIn;
        longMemory = useLongMemory;
        setMutexBits(3);
    }

    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = attacker.getAttackTarget();

        if (entitylivingbase == null) {
            return false;
        } else if (!entitylivingbase.isEntityAlive()) {
            return false;
        } else {
            if (canPenalize) {
                if (--delayCounter <= 0) {
                    entityPathEntity = attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
                    delayCounter = 4 + attacker.getRNG().nextInt(7);
                    return entityPathEntity != null;
                } else {
                    return true;
                }
            }
            entityPathEntity = attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
            return entityPathEntity != null;
        }
    }

    public boolean continueExecuting() {
        EntityLivingBase entitylivingbase = attacker.getAttackTarget();
        return entitylivingbase != null && (entitylivingbase.isEntityAlive() && (!longMemory ? !attacker.getNavigator().noPath() : (attacker.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase)) && (!(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer) entitylivingbase).isSpectator() && !((EntityPlayer) entitylivingbase).isCreative()))));
    }

    public void startExecuting() {
        attacker.getNavigator().setPath(entityPathEntity, speedTowardsTarget);
        delayCounter = 0;
    }

    public void resetTask() {
        EntityLivingBase entitylivingbase = attacker.getAttackTarget();

        if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer) entitylivingbase).isSpectator() || ((EntityPlayer) entitylivingbase).isCreative())) {
            attacker.setAttackTarget(null);
        }

        attacker.getNavigator().clearPathEntity();
    }

    public void updateTask() {
        EntityLivingBase entitylivingbase = attacker.getAttackTarget();
        attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
        double d0 = attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
        double d1 = func_179512_a(entitylivingbase);
        --delayCounter;

        if ((longMemory || attacker.getEntitySenses().canSee(entitylivingbase)) && delayCounter <= 0 && (targetX == 0.0D && targetY == 0.0D && targetZ == 0.0D || entitylivingbase.getDistanceSq(targetX, targetY, targetZ) >= 1.0D || attacker.getRNG().nextFloat() < 0.05F)) {
            targetX = entitylivingbase.posX;
            targetY = entitylivingbase.getEntityBoundingBox().minY;
            targetZ = entitylivingbase.posZ;
            delayCounter = 4 + attacker.getRNG().nextInt(7);

            if (canPenalize) {
                delayCounter += failedPathFindingPenalty;
                if (attacker.getNavigator().getPath() != null) {
                    net.minecraft.pathfinding.PathPoint finalPathPoint = attacker.getNavigator().getPath().getFinalPathPoint();
                    if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                        failedPathFindingPenalty = 0;
                    else
                        failedPathFindingPenalty += 10;
                } else {
                    failedPathFindingPenalty += 10;
                }
            }

            if (d0 > 1024.0D) {
                delayCounter += 10;
            } else if (d0 > 256.0D) {
                delayCounter += 5;
            }

            if (!attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, speedTowardsTarget)) {
                delayCounter += 15;
            }
        }

        attackTick = Math.max(attackTick - 1, 0);

        if (d0 <= d1 && attackTick <= 0) {
            attackTick = 20;
            attacker.swingArm(EnumHand.MAIN_HAND);
            attacker.attackEntityAsMob(entitylivingbase);
        }
    }

    protected double func_179512_a(EntityLivingBase attackTarget) {
        return (double) (attacker.width * 2.0F * attacker.width * 2.0F + attackTarget.width);
    }
}
