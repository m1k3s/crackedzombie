/*
 * EntityCrackedPigZombie.java
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

import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EntityCrackedPigZombie extends EntityCrackedZombie {

    private static final UUID ATTACK_SPEED_BOOST_MODIFIER_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier ATTACK_SPEED_BOOST_MODIFIER = (new AttributeModifier(ATTACK_SPEED_BOOST_MODIFIER_UUID, "Attacking speed boost", 0.05D, 0)).setSaved(false);
    private int angerLevel;
    private int randomSoundDelay;
    private UUID angerTargetUUID;

    public EntityCrackedPigZombie(World worldIn) {
        super(worldIn);
        isImmuneToFire = ConfigHandler.getIsImmuneToFire();
        applyEntityAI();
    }

    public void setRevengeTarget(EntityLivingBase livingBase) {
        super.setRevengeTarget(livingBase);

        if (livingBase != null) {
            angerTargetUUID = livingBase.getUniqueID();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void applyEntityAI() {
        if (ConfigHandler.isPzAlwaysAttackPlayers()) {
            targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        } else {
            targetTasks.addTask(1, new EntityAIMoveCloserToPlayer(this, 1.0, 16.0f));
        }
        targetTasks.addTask(1, new EntityCrackedPigZombie.AIHurtByAggressor(this));
        targetTasks.addTask(2, new EntityCrackedPigZombie.AITargetAggressor(this));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ConfigHandler.getPZMovementSpeed());
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ConfigHandler.getPZAttackDamage());
    }

    protected void updateAITasks() {
        IAttributeInstance iattributeinstance = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        if (isAngry()) {
            if (!isChild() && !iattributeinstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
                iattributeinstance.applyModifier(ATTACK_SPEED_BOOST_MODIFIER);
            }

            --angerLevel;
        } else if (iattributeinstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
            iattributeinstance.removeModifier(ATTACK_SPEED_BOOST_MODIFIER);
        }

        if (randomSoundDelay > 0 && --randomSoundDelay == 0) {
            playSound(SoundEvents.ENTITY_ZOMBIE_PIG_ANGRY, getSoundVolume() * 2.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
        }

        if (angerLevel > 0 && angerTargetUUID != null && getRevengeTarget() == null) {
            EntityPlayer entityplayer = world.getPlayerEntityByUUID(angerTargetUUID);
            setRevengeTarget(entityplayer);
            attackingPlayer = entityplayer;
            recentlyHit = getRevengeTimer();
        }

        super.updateAITasks();
    }

    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setShort("Anger", (short) angerLevel);

        if (angerTargetUUID != null) {
            tagCompound.setString("HurtBy", angerTargetUUID.toString());
        } else {
            tagCompound.setString("HurtBy", "");
        }
    }

    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        angerLevel = tagCompund.getShort("Anger");
        String s = tagCompund.getString("HurtBy");

        if (s.length() > 0) {
            angerTargetUUID = UUID.fromString(s);
            EntityPlayer entityplayer = world.getPlayerEntityByUUID(angerTargetUUID);
            setRevengeTarget(entityplayer);

            if (entityplayer != null) {
                attackingPlayer = entityplayer;
                recentlyHit = getRevengeTimer();
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (super.attackEntityAsMob(entity)) {
            if (entity instanceof EntityLivingBase) {
                byte strength = 0;

                if (world.getDifficulty() == EnumDifficulty.NORMAL) {
                    strength = 7;
                } else if (world.getDifficulty() == EnumDifficulty.HARD) {
                    strength = 15;
                }
                if (ConfigHandler.getPZSickness()) {
                    Potion poison = Potion.getPotionFromResourceLocation("poison");
                    if (poison != null) {
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(poison, strength * 20, 0));
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isEntityInvulnerable(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();

            if (entity instanceof EntityPlayer) {
                becomeAngryAt(entity);
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    private void becomeAngryAt(Entity entity) {
        angerLevel = 400 + rand.nextInt(400);
        randomSoundDelay = rand.nextInt(40);

        if (entity instanceof EntityLivingBase) {
            setRevengeTarget((EntityLivingBase) entity);
        }
    }

    public boolean isAngry() {
        return angerLevel > 0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_PIG_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_PIG_DEATH;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_ZOMBIE_PIGMAN;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance unused) {
        setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        super.onInitialSpawn(difficulty, livingdata);
        setToNotVillager();
        return livingdata;
    }

    static class AIHurtByAggressor extends EntityAIHurtByTarget {
        public AIHurtByAggressor(EntityCrackedPigZombie crackedPigZombie) {
            super(crackedPigZombie, true);
        }

        protected void setEntityAttackTarget(EntityCreature creatureIn, @Nonnull EntityLivingBase entityLivingBaseIn) {
            super.setEntityAttackTarget(creatureIn, entityLivingBaseIn);

            if (creatureIn instanceof EntityCrackedPigZombie) {
                ((EntityCrackedPigZombie) creatureIn).becomeAngryAt(entityLivingBaseIn);
            }
        }
    }

    @SuppressWarnings("unchecked")
    static class AITargetAggressor extends EntityAINearestAttackableTarget {
        public AITargetAggressor(EntityCrackedPigZombie crackedPigZombie) {
            super(crackedPigZombie, EntityPlayer.class, true);
        }

        public boolean shouldExecute() {
            return ((EntityCrackedPigZombie) taskOwner).isAngry() && super.shouldExecute();
        }
    }
}
