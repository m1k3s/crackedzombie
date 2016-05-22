package com.crackedzombie.common;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.Potion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.UUID;

public class EntityCrackedPigZombie extends EntityCrackedZombie {
    private static final UUID ATTACK_SPEED_BOOST_MODIFIER_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier ATTACK_SPEED_BOOST_MODIFIER = (new AttributeModifier(ATTACK_SPEED_BOOST_MODIFIER_UUID, "Attacking speed boost", 0.05D, 0)).setSaved(false);
    private int angerLevel;
    private int randomSoundDelay;
    private UUID angerTargetUUID;

    public EntityCrackedPigZombie(World worldIn) {
        super(worldIn);
        isImmuneToFire = true;
    }

    public void setRevengeTarget(EntityLivingBase livingBase) {
        super.setRevengeTarget(livingBase);

        if (livingBase != null) {
            angerTargetUUID = livingBase.getUniqueID();
        }
    }

    protected void applyEntityAI() {
        targetTasks.addTask(1, new EntityCrackedPigZombie.AIHurtByAggressor(this));
        targetTasks.addTask(2, new EntityCrackedPigZombie.AITargetAggressor(this));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(reinforcementChance).setBaseValue(0.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ConfigHandler.getPZMovementSpeed());
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ConfigHandler.getPZAttackDamage());
    }

    public void onUpdate() {
        super.onUpdate();
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

        if (angerLevel > 0 && angerTargetUUID != null && getAITarget() == null) {
            EntityPlayer entityplayer = worldObj.getPlayerEntityByUUID(angerTargetUUID);
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
            EntityPlayer entityplayer = worldObj.getPlayerEntityByUUID(angerTargetUUID);
            setRevengeTarget(entityplayer);

            if (entityplayer != null) {
                attackingPlayer = entityplayer;
                recentlyHit = getRevengeTimer();
            }
        }
    }

    public boolean attackEntityAsMob(Entity entity) {
        if (super.attackEntityAsMob(entity)) {
            if (entity instanceof EntityLivingBase) {
                if (!ConfigHandler.getPZSickness()) {
                    ((EntityLivingBase) entity).removePotionEffect(Potion.getPotionFromResourceLocation("poison"));
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
            Entity entity = source.getEntity();

            if (entity instanceof EntityPlayer) {
                becomeAngryAt(entity);
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    private void becomeAngryAt(Entity entity) {
        angerLevel = 400 + rand.nextInt(400);
//        randomSoundDelay = rand.nextInt(40);

        if (entity instanceof EntityLivingBase) {
            setRevengeTarget((EntityLivingBase) entity);
        }
    }

    public boolean isAngry() {
        return angerLevel > 0;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_PIG_AMBIENT;
    }

    protected SoundEvent getHurtSound() {
        return SoundEvents.ENTITY_ZOMBIE_PIG_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_PIG_DEATH;
    }

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

        protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn) {
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
