package com.crackedzombie.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class CrackedSpawnEgg extends ItemMonsterPlacer
{
    @SideOnly(Side.CLIENT)
    private IIcon theIcon;
    protected int colorBase = 0x000000;
    protected int colorSpots = 0xFFFFFF;
    protected String entityToSpawnName = "";
    protected String entityToSpawnNameFull = "";
    protected EntityLiving entityToSpawn = null;

    public CrackedSpawnEgg()
    {
        super();
    }
    
    public CrackedSpawnEgg(String EntityToSpawnName, int PrimaryColor, int SecondaryColor)
    {
        setHasSubtypes(false);
        maxStackSize = 64;
        setCreativeTab(CreativeTabs.tabMisc);
        setEntityToSpawnName(EntityToSpawnName);
        colorBase = PrimaryColor;
        colorSpots = SecondaryColor;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer,
                             World world, int x, int y, int z, int idx, float unused1,
                             float unused2, float unused3)
    {
        if (world.isRemote) {
            return true;
        } else {
            Block block = world.getBlock(x, y, z);
            x += Facing.offsetsXForSide[idx];
            y += Facing.offsetsYForSide[idx];
            z += Facing.offsetsZForSide[idx];
            double d0 = 0.0D;

            if (idx == 1 && block.getRenderType() == 11) {
                d0 = 0.5D;
            }

            Entity entity = spawnEntity(world, x + 0.5D, y + d0, z + 0.5D);

            if (entity != null) {
                if (entity instanceof EntityLivingBase && itemStack.hasDisplayName()) {
                    ((EntityLiving)entity).setCustomNameTag(itemStack.getDisplayName());
                }

                if (!entityPlayer.capabilities.isCreativeMode) {
                    --itemStack.stackSize;
                }
            }

            return true;
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        if (world.isRemote) {
            return itemStack;
        } else {
            MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityPlayer, true);

            if (movingobjectposition == null) {
                return itemStack;
            } else {
                if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    int i = movingobjectposition.blockX;
                    int j = movingobjectposition.blockY;
                    int k = movingobjectposition.blockZ;

                    if (!world.canMineBlock(entityPlayer, i, j, k)) {
                        return itemStack;
                    }

                    if (!entityPlayer.canPlayerEdit(i, j, k, movingobjectposition.sideHit, itemStack)) {
                        return itemStack;
                    }

                    if (world.getBlock(i, j, k) instanceof BlockLiquid) {
                        Entity entity = spawnEntity(world, i, j, k);

                        if (entity != null) {
                            if (entity instanceof EntityLivingBase && itemStack.hasDisplayName()) {
                                ((EntityLiving)entity).setCustomNameTag(itemStack.getDisplayName());
                            }

                            if (!entityPlayer.capabilities.isCreativeMode) {
                                --itemStack.stackSize;
                            }
                        }
                    }
                }

                return itemStack;
            }
        }
    }

    public Entity spawnEntity(World world, double X, double Y, double Z)
    {
       if (!world.isRemote) {
            entityToSpawnNameFull = CrackedZombie.modid + "."+ entityToSpawnName;
            if (EntityList.stringToClassMapping.containsKey(entityToSpawnNameFull)) {
                entityToSpawn = (EntityLiving) EntityList.createEntityByName(entityToSpawnNameFull, world);
                entityToSpawn.setLocationAndAngles(X, Y, Z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
                world.spawnEntityInWorld(entityToSpawn);
                entityToSpawn.onSpawnWithEgg((IEntityLivingData)null);
                entityToSpawn.playLivingSound();
            } else {
                System.out.println("Entity not found "+entityToSpawnName);
            }
        }
      
        return entityToSpawn;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item, 1, 0));     
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int ColorType)
    {
        return (ColorType == 0) ? colorBase : colorSpots;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
    
    @Override
    // Doing this override means that there is no localization for language
    // unless you specifically check for localization here and convert
    public String getItemStackDisplayName(ItemStack itemStack)
    {
        return "Spawn "+entityToSpawnName;
    }  


    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        theIcon = iconRegister.registerIcon(getIconString() + "_overlay");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int DamageVal, int RenderPass)
    {
        return RenderPass > 0 ? theIcon : super.getIconFromDamageForRenderPass(DamageVal, RenderPass);
    }
    
    public void setColors(int ColorBase, int ColorSpots)
    {
		colorBase = ColorBase;
		colorSpots = ColorSpots;
    }
    
    public int getColorBase()
    {
		return colorBase;
    }
    
    public int getColorSpots()
    {
		return colorSpots;
    }
    
    public void setEntityToSpawnName(String EntityToSpawnName)
    {
        entityToSpawnName = EntityToSpawnName;
        entityToSpawnNameFull = CrackedZombie.modid + "." + entityToSpawnName;
    }

}
