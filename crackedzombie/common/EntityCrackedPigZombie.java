package com.crackedzombie.common;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class EntityCrackedPigZombie extends EntityCrackedZombie {
    private static final UUID uuidString = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier speedBoost = (new AttributeModifier(uuidString, "Attacking speed boost", 0.45D, 0)).setSaved(false);
    /** Above zero if this PigZombie is Angry. */
    private int angerLevel;
    /** A random delay until this PigZombie next makes a sound. */
    private int randomSoundDelay;
    private Entity entity;

    public EntityCrackedPigZombie(World p_i1739_1_)
    {
        super(p_i1739_1_);
        this.isImmuneToFire = true;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(reinforcements).setBaseValue(0.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5.0D);
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return false;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (entity != entityToAttack && !worldObj.isRemote)
        {
            IAttributeInstance iattributeinstance = getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            iattributeinstance.removeModifier(speedBoost);

            if (entityToAttack != null)
            {
                iattributeinstance.applyModifier(speedBoost);
            }
        }

        entity = entityToAttack;

        if (randomSoundDelay > 0 && --randomSoundDelay == 0)
        {
            playSound("mob.zombiepig.zpigangry", getSoundVolume() * 2.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
        }

        super.onUpdate();
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return worldObj.difficultySetting != EnumDifficulty.PEACEFUL && worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setShort("Anger", (short) angerLevel);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        angerLevel = nbt.getShort("Anger");
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        return angerLevel == 0 ? null : super.findPlayerToAttack();
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource damageSource, float damage)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            Entity entity = damageSource.getEntity();

            if (entity instanceof EntityPlayer)
            {
                List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(32.0D, 32.0D, 32.0D));

                for (Object aList : list) {
                    Entity entity1 = (Entity) aList;

                    if (entity1 instanceof EntityCrackedPigZombie) {
                        EntityCrackedPigZombie entitypigzombie = (EntityCrackedPigZombie) entity1;
                        entitypigzombie.becomeAngryAt(entity);
                    }
                }

                this.becomeAngryAt(entity);
            }

            return super.attackEntityFrom(damageSource, damage);
        }
    }

    /**
     * Causes this PigZombie to become angry at the supplied Entity (which will be a player).
     */
    private void becomeAngryAt(Entity entity)
    {
        entityToAttack = entity;
        angerLevel = 400 + rand.nextInt(400);
        randomSoundDelay = rand.nextInt(40);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.zombiepig.zpig";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.zombiepig.zpighurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.zombiepig.zpigdeath";
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    protected void dropFewItems(boolean unused, int chance)
    {
        int j = rand.nextInt(2 + chance);
        int k;

        for (k = 0; k < j; ++k)
        {
            dropItem(Items.rotten_flesh, 1);
        }

        j = rand.nextInt(2 + chance);

        for (k = 0; k < j; ++k)
        {
            dropItem(Items.gold_nugget, 1);
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer entityPlayer)
    {
        return false;
    }

    protected void dropRareDrop(int p_70600_1_)
    {
        dropItem(Items.gold_ingot, 1);
    }

    /**
     * Makes entity wear random armor based on difficulty
     */
    protected void addRandomArmor()
    {
        setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityLivingData)
    {
        super.onSpawnWithEgg(entityLivingData);
        setVillager(false);
        return entityLivingData;
    }
}
