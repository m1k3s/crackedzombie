/*
 * ModelCrackedZombieVillager.java
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
package com.crackedzombie.client;

import com.crackedzombie.common.EntityCrackedZombie;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelCrackedZombieVillager extends ModelBiped {

	private float armAngle;

	public ModelCrackedZombieVillager()
	{
		this(0.0F, 0.0F, false);
	}

	public ModelCrackedZombieVillager(float par1, float par2, boolean par3)
	{
		super(par1, 0.0F, 64, par3 ? 32 : 64);

		if (par3) {
			this.bipedHead = new ModelRenderer(this, 0, 0);
			this.bipedHead.addBox(-4.0F, -10.0F, -4.0F, 8, 6, 8, par1);
			this.bipedHead.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
		} else {
			this.bipedHead = new ModelRenderer(this);
			this.bipedHead.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
			this.bipedHead.setTextureOffset(0, 32).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, par1);
			this.bipedHead.setTextureOffset(24, 32).addBox(-1.0F, -3.0F, -6.0F, 2, 4, 2, par1);
		}
	}

	@SuppressWarnings("unused")
	public int getMaxCrackedZombieVillagers()
	{
		return 10;
	}

	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity)
	{
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);

		boolean hasTarget = ((entity instanceof EntityCrackedZombie) && ((EntityCrackedZombie)entity).getHasTarget());

		float var8 = MathHelper.sin(swingProgress * (float) Math.PI);
		float var9 = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * (float) Math.PI);
		bipedRightArm.rotateAngleZ = 0.0F;
		bipedLeftArm.rotateAngleZ = 0.0F;
		bipedRightArm.rotateAngleY = -(0.1F - var8 * 0.6F);
		bipedLeftArm.rotateAngleY = 0.1F - var8 * 0.6F;
		bipedRightArm.rotateAngleX = hasTarget ? -((float) Math.PI / 1.5F) : 0.0F;
		bipedLeftArm.rotateAngleX = hasTarget ? -((float) Math.PI / 1.5F) : 0.0F;
		bipedRightArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
		bipedLeftArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
		bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
		bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
		bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
		bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
	}

}
