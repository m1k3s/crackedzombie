//  
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; version 2 dated June, 1991.
// 
//  This program is distributed in the hope that it will be useful, 
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
// 
//  You should have received a copy of the GNU General Public License
//  along with this program;  if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave., Cambridge, MA 02139, USA.
//  =====================================================================
//
//
//
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.crackedzombie.client;

import com.crackedzombie.common.EntityCrackedZombie;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderCrackedZombie extends RenderBiped<EntityCrackedZombie> {

//	private final ModelBiped currentModel;
//	private final ModelCrackedZombieVillager zombieVillager;
//	private final List list1;
//	private final List list2;

	private static final ResourceLocation zombieSkin = new ResourceLocation("textures/entity/zombie/zombie.png");
	private static final ResourceLocation zombieVillagerSkin = new ResourceLocation("textures/entity/zombie/zombie_villager.png");

	public RenderCrackedZombie(RenderManager rm)
	{
		super(rm, new ModelCrackedZombie(), 0.5F);
		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this)	{
			protected void initArmor() {
				modelLeggings = new ModelCrackedZombie(0.5F, true);
				modelArmor = new ModelCrackedZombie(1.0F, true);
			}
		};
		addLayer(layerbipedarmor);
	}

//	@SuppressWarnings("unchecked")
//	protected void rotateCorpse(EntityCrackedZombie entityCrackedZombie, float x, float y, float z)
//	{
//		if (entityCrackedZombie.isConverting()) {
//			y += (float) (Math.cos((double) entityCrackedZombie.ticksExisted * 3.25D) * Math.PI * 0.25D);
//		}
//
//		super.rotateCorpse(entityCrackedZombie, x, y, z);
//	}
//
//	@SuppressWarnings("unchecked")
//	private void getRenderLayer(EntityCrackedZombie zombie)
//	{
//		if (zombie.isVillager()) {
//			mainModel = zombieVillager;
//			layerRenderers = list1;
//		} else {
//			mainModel = currentModel;
//			layerRenderers = list2;
//		}
//
//		modelBipedMain = (ModelBiped)mainModel;
//	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCrackedZombie zombie)
	{
		return zombie.isVillager() ? zombieVillagerSkin : zombieSkin;
	}

//	@SuppressWarnings("unchecked")
//	public void doRender(EntityCrackedZombie zombie, double x, double y, double z, float facing, float partialTicks)
//	{
//		getRenderLayer(zombie);
//		super.doRender(zombie, x, y, z, facing, partialTicks);
//	}

}
