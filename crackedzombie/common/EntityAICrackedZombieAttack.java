/*
 * EntityAICrackedZombieAttack.java
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
