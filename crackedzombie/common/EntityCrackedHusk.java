package com.crackedzombie.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;

public class EntityCrackedHusk extends EntityCrackedZombie {

    public EntityCrackedHusk(World world) {
        super(world);
    }

    @Override
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && this.world.canSeeSky(new BlockPos(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_HUSK_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_HUSK_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HUSK_DEATH;
    }

    @SuppressWarnings("unused")
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_HUSK_STEP;
    }

    @Override
    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_HUSK;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean shouldAttackAsMob = super.attackEntityAsMob(entityIn);

        if (shouldAttackAsMob && getHeldItemMainhand().isEmpty() && entityIn instanceof EntityLivingBase) {
            if (ConfigHandler.getHuskHunger()) {
                float additionalDifficulty = world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.HUNGER, 140 * (int) additionalDifficulty));
            }
        }

        return shouldAttackAsMob;
    }

    @SuppressWarnings("unused")
    protected ItemStack getSkullDrop() {
        return ItemStack.EMPTY;
    }
}
