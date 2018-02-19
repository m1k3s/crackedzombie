/*
 * EntityCrackedZombie.java
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

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityCrackedZombie extends EntityMob {

    protected static final IAttribute reinforcementChance = (new RangedAttribute(null, "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).setDescription("Spawn Reinforcements Chance");
    private static final UUID babySpeedBoostUUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier babySpeedBoostModifier = new AttributeModifier(babySpeedBoostUUID, "Baby speed boost", 0.5D, 1);
    private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.createKey(EntityCrackedZombie.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> VILLAGER_TYPE = EntityDataManager.createKey(EntityCrackedZombie.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> CONVERTING = EntityDataManager.createKey(EntityCrackedZombie.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ARMS_RAISED = EntityDataManager.createKey(EntityCrackedZombie.class, DataSerializers.BOOLEAN);
    private final EntityAIBreakDoor breakDoor = new EntityAIBreakDoor(this);

    private final double noSpawnRadius = ConfigHandler.getTorchNoSpawnRadius();
    private final boolean allowChildSpawns = ConfigHandler.getAllowChildSpawns();
    private final boolean attackPigs = ConfigHandler.getAttackPigs();
    private final boolean attackVillagers = ConfigHandler.getAttackVillagers();
    private final boolean nightSpawnOnly = ConfigHandler.getNightSpawnOnly();

    private boolean isBreakDoorsTaskSet = ConfigHandler.getDoorBusting();
    private int conversionTime = 0;
    private float zombieWidth = -1.0f;
    private float zombieHeight;

    public EntityCrackedZombie(World world) {
        super(world);
        setSize(0.6F, 1.95F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAICrackedZombieAttack(this, 1.0D, false));
        tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        tasks.addTask(7, new EntityAIWander(this, 1.0D));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        applyEntityAI();
    }

    @SuppressWarnings("unchecked")
    private void applyEntityAI() {
        tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityCrackedPigZombie.class));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        if (attackVillagers) {
            targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
        }
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
        if (attackPigs) {
            targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityPig.class, true));
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ConfigHandler.getFollowRange()); // follow range
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ConfigHandler.getMovementSpeed()); // movement speed
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ConfigHandler.getAttackDamage());  // attack damage
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D); // armor strength
        getAttributeMap().registerAttribute(reinforcementChance).setBaseValue(rand.nextDouble() * 0.1); // reinforcements
    }

    public void setArmsRaised(boolean armsRaised) {
        getDataManager().set(ARMS_RAISED, armsRaised);
    }

    @SideOnly(Side.CLIENT)
    public boolean isArmsRaised() {
        return getDataManager().get(ARMS_RAISED);
    }

    public void setBreakDoorsAItask(boolean enabled) {
        if (isBreakDoorsTaskSet != enabled) {
            isBreakDoorsTaskSet = enabled;
            ((PathNavigateGround) getNavigator()).setBreakDoors(enabled);

            if (enabled) {
                tasks.addTask(1, breakDoor);
            } else {
                tasks.removeTask(breakDoor);
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (super.attackEntityFrom(source, amount)) {
            EntityLivingBase entitylivingbase = getAttackTarget();

            if (entitylivingbase == null && source.getTrueSource() instanceof EntityLivingBase) {
                entitylivingbase = (EntityLivingBase) source.getTrueSource();
            }

            int i = MathHelper.floor(posX);
            int j = MathHelper.floor(posY);
            int k = MathHelper.floor(posZ);

            if (entitylivingbase != null && world.getDifficulty() == EnumDifficulty.HARD && (double) rand.nextFloat() < getEntityAttribute(reinforcementChance).getAttributeValue() && world.getGameRules().getBoolean("doMobSpawning")) {
                EntityCrackedZombie entityzombie = new EntityCrackedZombie(world);

                for (int l = 0; l < 50; ++l) {
                    int i1 = i + MathHelper.getInt(rand, 7, 40) * MathHelper.getInt(rand, -1, 1);
                    int j1 = j + MathHelper.getInt(rand, 7, 40) * MathHelper.getInt(rand, -1, 1);
                    int k1 = k + MathHelper.getInt(rand, 7, 40) * MathHelper.getInt(rand, -1, 1);

                    if (world.getBlockState(new BlockPos(i1, j1 - 1, k1)).isSideSolid(world, new BlockPos(i1, j1 - 1, k1), net.minecraft.util.EnumFacing.UP) && world.getLightFromNeighbors(new BlockPos(i1, j1, k1)) < 10) {
                        entityzombie.setPosition((double) i1, (double) j1, (double) k1);

                        if (!world.isAnyPlayerWithinRangeAt((double) i1, (double) j1, (double) k1, 7.0D) && world.checkNoEntityCollision(entityzombie.getEntityBoundingBox(), entityzombie) && world.getCollisionBoxes(entityzombie, entityzombie.getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(entityzombie.getEntityBoundingBox())) {
                            world.spawnEntity(entityzombie);
                            entityzombie.setAttackTarget(entitylivingbase);
                            entityzombie.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityzombie)), null);
                            getEntityAttribute(reinforcementChance).applyModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05D, 0));
                            entityzombie.getEntityAttribute(reinforcementChance).applyModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05D, 0));
                            break;
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
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
                if (ConfigHandler.getSickness()) {
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

    @Override
    public void onLivingUpdate() {
        if (world.isDaytime() && !world.isRemote && !isChild()) {
            float brightness = getBrightness();
            BlockPos blockpos = getRidingEntity() instanceof EntityBoat ? (new BlockPos(posX, (double) Math.round(posY), posZ)).up() : new BlockPos(posX, (double) Math.round(posY), posZ);
            boolean setFire = false;

            if (brightness > 0.5F && rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && world.canSeeSky(blockpos)) {
                if (nightSpawnOnly) {
                    setFire = true;
                }
                ItemStack itemstack = getItemStackFromSlot(EntityEquipmentSlot.HEAD);

                if (!itemstack.isEmpty()) {
                    if (itemstack.isItemStackDamageable()) {
                        itemstack.setItemDamage(itemstack.getItemDamage() + rand.nextInt(2));

                        if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
                            renderBrokenItemStack(itemstack);
                            setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    setFire = false;
                }
                if (setFire) {
                    setFire(8);
                }
            }
        }

        super.onLivingUpdate();
    }

    // spawns on grass, sand, dirt, clay and occasionally spawn on stone unless
    // there are torches within the torch no-spawn radius, also won't spawn during
    // daytime if nightOnly flag is set.
    @Override
    public boolean getCanSpawnHere() {
        AxisAlignedBB entityAABB = getEntityBoundingBox();

        if (noSpawnRadius > 0.0 && foundNearbyTorches(entityAABB)) {
            return false;
        } else if (ConfigHandler.getNightSpawnOnly()) { // standard zombie spawn
            BlockPos blockpos = new BlockPos(posX, entityAABB.minY, posZ);

            if (world.getLightFor(EnumSkyBlock.SKY, blockpos) > rand.nextInt(32)) {
                return false;
            } else {
                int lightFromNeighbors = world.getLightFromNeighbors(blockpos);

                if (world.isThundering()) {
                    int skylightSubtracted = world.getSkylightSubtracted();
                    world.setSkylightSubtracted(10);
                    lightFromNeighbors = world.getLightFromNeighbors(blockpos);
                    world.setSkylightSubtracted(skylightSubtracted);
                }

                return lightFromNeighbors <= rand.nextInt(8);
            }

        } else {
            boolean notColliding = world.getCollisionBoxes(this, entityAABB).isEmpty();
            boolean isLiquid = world.containsAnyLiquid(entityAABB);
            // spawns on grass, sand, dirt, clay and very occasionally spawn on stone
            BlockPos bp = new BlockPos(posX, entityAABB.minY - 1.0, posZ);
            Block block = world.getBlockState(bp).getBlock();
            boolean isGrass = (block == Blocks.GRASS);
            boolean isSand = (block == Blocks.SAND);
            boolean isClay = ((block == Blocks.HARDENED_CLAY) || (block == Blocks.STAINED_HARDENED_CLAY));
            boolean isDirt = (block == Blocks.DIRT);
            boolean isStone = (rand.nextBoolean()) && (block == Blocks.STONE);

            return (isGrass || isSand || isStone || isClay || isDirt) && notColliding && !isLiquid;
        }
    }

    // the aabb should be the entity's boundingbox
    public boolean foundNearbyTorches(AxisAlignedBB aabb) {
        boolean result = false;

        int xMin = MathHelper.floor(aabb.minX - noSpawnRadius);
        int xMax = MathHelper.floor(aabb.maxX + noSpawnRadius);
        int yMin = MathHelper.floor(aabb.minY - noSpawnRadius);
        int yMax = MathHelper.floor(aabb.maxY + noSpawnRadius);
        int zMin = MathHelper.floor(aabb.minZ - noSpawnRadius);
        int zMax = MathHelper.floor(aabb.maxZ + noSpawnRadius);

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block instanceof BlockTorch) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(IS_CHILD, false);
        getDataManager().register(VILLAGER_TYPE, 0);
        getDataManager().register(CONVERTING, false);
        getDataManager().register(ARMS_RAISED, false);
    }

    @Override
    public int getTotalArmorValue() {
        int armor = super.getTotalArmorValue() + 2;

        if (armor > 20) {
            armor = 20;
        }

        return armor;
    }

//    @Override
//    protected boolean canDespawn() {
//        return false;
//    }

    @Override
    public boolean isChild() {
        return getDataManager().get(IS_CHILD);
    }

    public void setChild(boolean childZombie) {
        if (allowChildSpawns) {
            getDataManager().set(IS_CHILD, childZombie);

            if (world != null && !world.isRemote) {
                IAttributeInstance iattributeinstance = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                iattributeinstance.removeModifier(babySpeedBoostModifier);

                if (childZombie) {
                    iattributeinstance.applyModifier(babySpeedBoostModifier);
                }
            }
            setChildSize(childZombie);
        }
    }

    @Override
    protected final void setSize(float width, float height) {
        boolean isSizeValid = zombieWidth > 0.0F && zombieHeight > 0.0F;
        zombieWidth = width;
        zombieHeight = height;

        if (!isSizeValid) {
            multiplySize(1.0F);
        }
    }

    public void setChildSize(boolean isChild) {
        multiplySize(isChild ? 0.5F : 1.0F);
    }

    protected final void multiplySize(float size) {
        super.setSize(zombieWidth * size, zombieHeight * size);
    }

    public boolean isVillager() {
        return getDataManager().get(VILLAGER_TYPE) > 0;
    }

    public int getVillagerType() {
        return getDataManager().get(VILLAGER_TYPE) - 1;
    }

    public void setVillagerType(int villagerType) {
        getDataManager().set(VILLAGER_TYPE, villagerType + 1);
    }

    public void setToNotVillager() {
        getDataManager().set(VILLAGER_TYPE, 0);
    }

    @Override
    public void notifyDataManagerChange(@Nullable DataParameter<?> key) {
        if (IS_CHILD.equals(key)) {
            setChildSize(isChild());
        }

        super.notifyDataManagerChange(key);
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote && isConverting()) {
            int boost = getConversionTimeBoost();
            conversionTime -= boost;

            if (conversionTime <= 0) {
                convertToVillager();
            }
        }

        super.onUpdate();
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt entityLightningBolt) {
        // A little surprise... BOOM!
        world.newExplosion(this, posX, posY, posZ, 0.5F, true, true);
        dealFireDamage(5);
        setFire(8);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isVillager() ? SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT : SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return isVillager() ? SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH : SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        playSound(isVillager() ? SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP : SoundEvents.ENTITY_ZOMBIE_STEP, 0.15F, 1.0F);
    }

    @Override
    protected Item getDropItem() {
        // returns the held item or armor
        ItemStack heldItem = getHeldItem(EnumHand.MAIN_HAND);
        if (!heldItem.isEmpty()) {
            return heldItem.getItem();
        } else {
            return Items.ROTTEN_FLESH;
        }
    }

    @Nonnull
    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_ZOMBIE;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        if (isChild()) {
            tagCompound.setBoolean("IsBaby", true);
        }

        if (isVillager()) {
            tagCompound.setBoolean("IsVillager", true);
            tagCompound.setInteger("VillagerProfession", getVillagerType());
        }

        tagCompound.setInteger("ConversionTime", isConverting() ? conversionTime : -1);
        tagCompound.setBoolean("CanBreakDoors", isBreakDoorsTaskSet);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);

        if (tagCompund.getBoolean("IsBaby")) {
            setChild(true);
        } else {
            setChild(false);
        }

        if (tagCompund.getBoolean("IsVillager")) {
            if (tagCompund.hasKey("VillagerProfession", 99)) {
                setVillagerType(tagCompund.getInteger("VillagerProfession"));
            } else {
                setVillagerType(world.rand.nextInt(5));
            }
        }

        if (tagCompund.hasKey("ConversionTime", 99) && tagCompund.getInteger("ConversionTime") > -1) {
            startConversion(tagCompund.getInteger("ConversionTime"));
        }

        setBreakDoorsAItask(tagCompund.getBoolean("CanBreakDoors"));
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);

        if (rand.nextFloat() < (world.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
            switch (rand.nextInt(4)) {
                case 0:
                    setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
                    break;
                case 1:
                    setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                    break;
                case 2:
                    setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SHOVEL));
                    break;
                case 3:
                    setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
                    break;
            }
        }
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLiving) {
        super.onKillEntity(entityLiving);
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 16) {
            if (!isSilent()) {
                world.playSound(posX + 0.5D, posY + 0.5D, posZ + 0.5D, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, getSoundCategory(), 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public boolean isConverting() {
        return getDataManager().get(CONVERTING);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        float additionalDifficulty = difficulty.getClampedAdditionalDifficulty();
        setCanPickUpLoot(rand.nextFloat() < 0.55F * additionalDifficulty);

        if (livingdata == null) {
            livingdata = new EntityCrackedZombie.GroupData(world.rand.nextFloat() < net.minecraftforge.common.ForgeModContainer.zombieBabyChance, world.rand.nextFloat() < 0.05F);
        }

        if (livingdata instanceof EntityCrackedZombie.GroupData) {
            EntityCrackedZombie.GroupData entityzombie$groupdata = (EntityCrackedZombie.GroupData) livingdata;

            if (entityzombie$groupdata.isVillager) {
                setVillagerType(rand.nextInt(5));
            }

            if (entityzombie$groupdata.isChild) {
                setChild(true);

                if ((double) world.rand.nextFloat() < 0.05D) {
                    List<EntityChicken> list = world.getEntitiesWithinAABB(EntityChicken.class, getEntityBoundingBox().expand(5.0D, 3.0D, 5.0D), EntitySelectors.IS_STANDALONE);

                    if (!list.isEmpty()) {
                        EntityChicken entitychicken = list.get(0);
                        entitychicken.setChickenJockey(true);
                        startRiding(entitychicken);
                    }
                } else if ((double) world.rand.nextFloat() < 0.05D) {
                    EntityChicken entitychicken1 = new EntityChicken(world);
                    entitychicken1.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
                    entitychicken1.onInitialSpawn(difficulty, null);
                    entitychicken1.setChickenJockey(true);
                    world.spawnEntity(entitychicken1);
                    startRiding(entitychicken1);
                }
            }
        }

        setBreakDoorsAItask(isBreakDoorsTaskSet && rand.nextFloat() < additionalDifficulty * 0.1F);
        setEquipmentBasedOnDifficulty(difficulty);
        setEnchantmentBasedOnDifficulty(difficulty);

        if (getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
            Calendar calendar = Calendar.getInstance();//world.getCurrentDate();
            Calendar halloween = Calendar.getInstance();
            halloween.clear();
            halloween.set(calendar.get(Calendar.YEAR), Calendar.NOVEMBER, 31);

            if (calendar.compareTo(halloween) == 0 && rand.nextFloat() < 0.25F) {
                setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(rand.nextFloat() < 0.1F ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));
                inventoryArmorDropChances[EntityEquipmentSlot.HEAD.getIndex()] = 0.0F;
            }
        }

        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).applyModifier(new AttributeModifier("Random spawn bonus", rand.nextDouble() * 0.05000000074505806D, 0));
        double d0 = rand.nextDouble() * 1.5D * (double) additionalDifficulty;

        if (d0 > 1.0D) {
            getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random zombie-spawn bonus", d0, 2));
        }

        if (rand.nextFloat() < additionalDifficulty * 0.05F) {
            getEntityAttribute(reinforcementChance).applyModifier(new AttributeModifier("Leader zombie bonus", rand.nextDouble() * 0.25D + 0.5D, 0));
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("Leader zombie bonus", rand.nextDouble() * 3.0D + 1.0D, 2));
            setBreakDoorsAItask(true);
        }

        return livingdata;
    }

    protected void startConversion(int ticks) {
        conversionTime = ticks;
        getDataManager().set(CONVERTING, true);
        removePotionEffect(MobEffects.WEAKNESS);
        addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, ticks, Math.min(world.getDifficulty().getDifficultyId() - 1, 0)));
        world.setEntityState(this, (byte) 16);
    }

    protected void convertToVillager() {
        EntityVillager entityvillager = new EntityVillager(world);
        entityvillager.copyLocationAndAnglesFrom(this);
        entityvillager.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityvillager)), null);
        entityvillager.setLookingForHome();

        if (isChild()) {
            entityvillager.setGrowingAge(-24000);
        }

        world.removeEntity(this);
        entityvillager.setNoAI(isAIDisabled());
        entityvillager.setProfession(getVillagerType());

        if (hasCustomName()) {
            entityvillager.setCustomNameTag(getCustomNameTag());
            entityvillager.setAlwaysRenderNameTag(getAlwaysRenderNameTag());
        }

        world.spawnEntity(entityvillager);
        entityvillager.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
        world.playEvent(null, 1027, new BlockPos((int) posX, (int) posY, (int) posZ), 0);
    }

    protected int getConversionTimeBoost() {
        int boostTime = 1;

        if (rand.nextFloat() < 0.01F) {
            int count = 0;

            for (double x = posX - 4; x < posX + 4 && count < 14; ++x) {
                for (double y = posY - 4; y < posY + 4 && count < 14; ++y) {
                    for (double z = posZ - 4; z < posZ + 4 && count < 14; ++z) {
                        Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();

                        if (block == Blocks.IRON_BARS || block == Blocks.BED) {
                            if (rand.nextFloat() < 0.3F) {
                                ++boostTime;
                            }

                            ++count;
                        }
                    }
                }
            }
        }

        return boostTime;
    }

    class GroupData implements IEntityLivingData {

        public boolean isChild;
        public boolean isVillager;

        private GroupData(boolean setChild, boolean setVillager) {
            isChild = false;
            isVillager = false;
            isChild = setChild;
            isVillager = setVillager;
        }

        @SuppressWarnings("unused")
        GroupData(boolean setChild, boolean setVillager, Object object) {
            this(setChild, setVillager);
        }
    }

}
