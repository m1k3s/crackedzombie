package com.crackedzombie.common;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
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
    private static final AttributeModifier field_110190_br = (new AttributeModifier(UUIDstring, "Attacking speed boost", 0.05D, 0)).setSaved(false);
    private int angerLevel;
    private int randomSoundDelay;
    private UUID field_175459_bn;
    private static final String __OBFID = "CL_00001693";

    public EntityCrackedPigZombie(World worldIn) {
        super(worldIn);
        this.isImmuneToFire = true;
    }

    public void setRevengeTarget(EntityLivingBase livingBase) {
        super.setRevengeTarget(livingBase);

        if (livingBase != null) {
            this.field_175459_bn = livingBase.getUniqueID();
        }
    }

    protected void applyEntityAI() {
        targetTasks.addTask(1, new EntityCrackedPigZombie.AIHurtByAggressor());
        targetTasks.addTask(2, new EntityCrackedPigZombie.AITargetAggressor());
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(reinforcements).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5.0D);
    }

    public void onUpdate() {
        super.onUpdate();
    }

    protected void updateAITasks() {
        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (this.isAngry()) {
            if (!this.isChild() && !iattributeinstance.func_180374_a(field_110190_br)) {
                iattributeinstance.applyModifier(field_110190_br);
            }

            --this.angerLevel;
        } else if (iattributeinstance.func_180374_a(field_110190_br)) {
            iattributeinstance.removeModifier(field_110190_br);
        }

        if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
            this.playSound("mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
        }

        if (this.angerLevel > 0 && this.field_175459_bn != null && this.getAITarget() == null) {
            EntityPlayer entityplayer = this.worldObj.getPlayerEntityByUUID(this.field_175459_bn);
            this.setRevengeTarget(entityplayer);
            this.attackingPlayer = entityplayer;
            this.recentlyHit = this.getRevengeTimer();
        }

        super.updateAITasks();
    }

    public boolean getCanSpawnHere() {
        return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    public boolean handleLavaMovement() {
        return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this) && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
    }

    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setShort("Anger", (short) this.angerLevel);

        if (this.field_175459_bn != null) {
            tagCompound.setString("HurtBy", this.field_175459_bn.toString());
        } else {
            tagCompound.setString("HurtBy", "");
        }
    }

    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.angerLevel = tagCompund.getShort("Anger");
        String s = tagCompund.getString("HurtBy");

        if (s.length() > 0) {
            this.field_175459_bn = UUIDstring.fromString(s);
            EntityPlayer entityplayer = this.worldObj.getPlayerEntityByUUID(this.field_175459_bn);
            this.setRevengeTarget(entityplayer);

            if (entityplayer != null) {
                this.attackingPlayer = entityplayer;
                this.recentlyHit = this.getRevengeTimer();
            }
        }
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();

            if (entity instanceof EntityPlayer) {
                this.becomeAngryAt(entity);
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    private void becomeAngryAt(Entity entity) {
        this.angerLevel = 400 + this.rand.nextInt(400);
        this.randomSoundDelay = this.rand.nextInt(40);

        if (entity instanceof EntityLivingBase) {
            this.setRevengeTarget((EntityLivingBase) entity);
        }
    }

    public boolean isAngry() {
        return this.angerLevel > 0;
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
        int j = this.rand.nextInt(2 + chance);
        int k;

        for (k = 0; k < j; ++k) {
            this.dropItem(Items.rotten_flesh, 1);
        }

        j = this.rand.nextInt(2 + chance);

        for (k = 0; k < j; ++k) {
            this.dropItem(Items.gold_nugget, 1);
        }
    }

    public boolean interact(EntityPlayer player) {
        return false;
    }

    protected void addRandomArmor() {
        this.dropItem(Items.gold_ingot, 1);
    }

    protected void func_180481_a(DifficultyInstance difficultyInstance) {
        setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
    }

    @Override
    public IEntityLivingData onSpawnFirstTime(DifficultyInstance difficultyInstance, IEntityLivingData livingdata) {
        super.onSpawnFirstTime(difficultyInstance, livingdata);
        this.setVillager(false);
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
            return ((EntityPigZombie) this.taskOwner).isAngry() && super.shouldExecute();
        }
    }
}
