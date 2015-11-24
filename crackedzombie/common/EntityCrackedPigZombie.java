package com.crackedzombie.common;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityCrackedPigZombie extends EntityCrackedZombie {
    private static final UUID UUIDstring = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier runningModifier = (new AttributeModifier(UUIDstring, "Attacking speed boost", 0.05D, 0)).setSaved(false);
    private int angerLevel;
    private int randomSoundDelay;
    private UUID revengeTarget;

    public EntityCrackedPigZombie(World worldIn) {
        super(worldIn);
        isImmuneToFire = true;
    }

    public void setRevengeTarget(EntityLivingBase livingBase) {
        super.setRevengeTarget(livingBase);

        if (livingBase != null) {
            revengeTarget = livingBase.getUniqueID();
        }
    }

    protected void applyEntityAI() {
        targetTasks.addTask(1, new EntityCrackedPigZombie.AIHurtByAggressor());
        targetTasks.addTask(2, new EntityCrackedPigZombie.AITargetAggressor());
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(reinforcements).setBaseValue(0.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(ConfigHandler.getPZMovementSpeed());
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(ConfigHandler.getPZAttackDamage());
    }

    public void onUpdate() {
        super.onUpdate();
    }

    protected void updateAITasks() {
        IAttributeInstance iattributeinstance = getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (isAngry()) {
            if (!isChild() && !iattributeinstance.func_180374_a(runningModifier)) {
                iattributeinstance.applyModifier(runningModifier);
            }

            --angerLevel;
        } else if (iattributeinstance.func_180374_a(runningModifier)) {
            iattributeinstance.removeModifier(runningModifier);
        }

        if (randomSoundDelay > 0 && --randomSoundDelay == 0) {
            playSound("mob.zombiepig.zpigangry", getSoundVolume() * 2.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
        }

        if (angerLevel > 0 && revengeTarget != null && getAITarget() == null) {
            EntityPlayer entityplayer = worldObj.getPlayerEntityByUUID(revengeTarget);
            setRevengeTarget(entityplayer);
            attackingPlayer = entityplayer;
            recentlyHit = getRevengeTimer();
        }

        super.updateAITasks();
    }

    public boolean getCanSpawnHere() {
        return worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    public boolean handleLavaMovement() {
        return worldObj.checkNoEntityCollision(getEntityBoundingBox(), this) && worldObj.getCollidingBoundingBoxes(this, getEntityBoundingBox()).isEmpty() && !worldObj.isAnyLiquid(getEntityBoundingBox());
    }

    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setShort("Anger", (short) angerLevel);

        if (revengeTarget != null) {
            tagCompound.setString("HurtBy", revengeTarget.toString());
        } else {
            tagCompound.setString("HurtBy", "");
        }
    }

    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        angerLevel = tagCompund.getShort("Anger");
        String s = tagCompund.getString("HurtBy");

        if (s.length() > 0) {
            revengeTarget = UUIDstring.fromString(s);
            EntityPlayer entityplayer = worldObj.getPlayerEntityByUUID(revengeTarget);
            setRevengeTarget(entityplayer);

            if (entityplayer != null) {
                attackingPlayer = entityplayer;
                recentlyHit = getRevengeTimer();
            }
        }
    }
    
	public boolean attackEntityAsMob(Entity entity)
	{
		if (super.attackEntityAsMob(entity)) {
			if (entity instanceof EntityLivingBase) {
				if (!ConfigHandler.getPZSickness()) {
					((EntityLivingBase) entity).removePotionEffect(Potion.poison.id);
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
        randomSoundDelay = rand.nextInt(40);

        if (entity instanceof EntityLivingBase) {
            setRevengeTarget((EntityLivingBase) entity);
        }
    }

    public boolean isAngry() {
        return angerLevel > 0;
    }

    protected String getLivingSound() {
        return "mob.zombiepig.zpig";
    }

    protected String getHurtSound() {
        return "mob.zombiepig.zpighurt";
    }

    protected String getDeathSound() {
        return "mob.zombiepig.zpigdeath";
    }

    protected void dropFewItems(boolean drop, int chance) {
        int j = rand.nextInt(2 + chance);
        int k;

        for (k = 0; k < j; ++k) {
            dropItem(Items.rotten_flesh, 1);
        }

        j = rand.nextInt(2 + chance);

        for (k = 0; k < j; ++k) {
            dropItem(Items.gold_nugget, 1);
        }
    }

    public boolean interact(EntityPlayer player) {
        return false;
    }

    protected void addRandomArmor() {
        dropItem(Items.gold_ingot, 1);
    }

    protected void func_180481_a(DifficultyInstance unused) {
        setCurrentItemOrArmor(0, new ItemStack(Items.diamond_sword));
    }

    @Override
    public IEntityLivingData onSpawnFirstTime(DifficultyInstance difficultyInstance, IEntityLivingData livingdata) {
        super.onSpawnFirstTime(difficultyInstance, livingdata);
        setVillager(false);
        return livingdata;
    }

    class AIHurtByAggressor extends EntityAIHurtByTarget {
        public AIHurtByAggressor() {
            super(EntityCrackedPigZombie.this, true, new Class[0]);
        }

        protected void func_179446_a(EntityCreature entityCreature, EntityLivingBase entityLivingBase) {
            super.func_179446_a(entityCreature, entityLivingBase);

            if (entityCreature instanceof EntityPigZombie) {
                ((EntityCrackedPigZombie) entityCreature).becomeAngryAt(entityLivingBase);
            }
        }
    }

    class AITargetAggressor extends EntityAINearestAttackableTarget {
        public AITargetAggressor() {
            super(EntityCrackedPigZombie.this, EntityPlayer.class, true);
        }

        public boolean shouldExecute() {
            return ((EntityPigZombie) taskOwner).isAngry() && super.shouldExecute();
        }
    }
}
