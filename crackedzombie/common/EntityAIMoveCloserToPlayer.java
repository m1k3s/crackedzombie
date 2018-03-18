package com.crackedzombie.common;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class EntityAIMoveCloserToPlayer extends EntityAIBase {

    private final EntityCreature creature;
    private EntityLivingBase targetEntity;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private final double speed;
    private final float maxTargetDistance;

    public EntityAIMoveCloserToPlayer(EntityCreature creature, double speed, float maxTargetDistance) {
        this.creature = creature;
        this.speed = speed;
        this.maxTargetDistance = maxTargetDistance;
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        targetEntity = creature.world.getClosestPlayerToEntity(creature, maxTargetDistance);

        if (targetEntity == null) {
            return false;
        } else if (targetEntity.getDistanceSq(creature) > (double) (maxTargetDistance * maxTargetDistance)) {
            return false;
        } else {
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(creature, 16, 7, new Vec3d(targetEntity.posX, targetEntity.posY, targetEntity.posZ));

            if (vec3d == null) {
                return false;
            } else {
                movePosX = vec3d.x;
                movePosY = vec3d.y;
                movePosZ = vec3d.z;
                return true;
            }
        }
    }

    public boolean shouldContinueExecuting() {
        return !creature.getNavigator().noPath() && targetEntity.isEntityAlive() && targetEntity.getDistanceSq(creature) < (double) (maxTargetDistance * maxTargetDistance);
    }

    public void resetTask() {
        targetEntity = null;
    }

    public void startExecuting() {
        creature.getNavigator().tryMoveToXYZ(movePosX, movePosY, movePosZ, speed);
    }
}
