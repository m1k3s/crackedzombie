package com.crackedzombie.common;

public class EntityAICrackedZombieAttack extends EntityAttackAICrackedMelee {
    private final EntityCrackedZombie entityCrackedZombie;
    private int attackTimer;

    public EntityAICrackedZombieAttack(EntityCrackedZombie crackedZombie, double speed, boolean memory) {
        super(crackedZombie, speed, memory);
        entityCrackedZombie = crackedZombie;
    }

    public void startExecuting() {
        super.startExecuting();
        attackTimer = 0;
    }

    public void resetTask() {
        super.resetTask();
        entityCrackedZombie.setArmsRaised(false);
    }

    public void updateTask() {
        super.updateTask();
        ++attackTimer;

        if (attackTimer >= 5 && attackTick < 10) {
            entityCrackedZombie.setArmsRaised(true);
        } else {
            entityCrackedZombie.setArmsRaised(false);
        }
    }

}
