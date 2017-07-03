/*
 * ModelCrackedZombie.java
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
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class ModelCrackedZombie extends ModelBiped {

	public ModelCrackedZombie()
	{
		this(0.0F, false);
	}

	public ModelCrackedZombie(float size, boolean isChild)
	{
		super(size, 0.0F, 64, isChild ? 32 : 64);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, @Nonnull Entity entity)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);

		boolean hasTarget = ((entity instanceof EntityCrackedZombie) && ((EntityCrackedZombie)entity).isArmsRaised());
		float rightArmRotation = MathHelper.sin(swingProgress * (float) Math.PI);
		float leftARmRotation = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * (float) Math.PI);
		bipedRightArm.rotateAngleZ = 0.0F;
		bipedLeftArm.rotateAngleZ = 0.0F;
		bipedRightArm.rotateAngleY = -(0.1F - rightArmRotation * 0.6F);
		bipedLeftArm.rotateAngleY = 0.1F - rightArmRotation * 0.6F;
		float angle = -(float) Math.PI / (hasTarget ? 1.5f : 0.5f);
		bipedRightArm.rotateAngleX = angle;
		bipedLeftArm.rotateAngleX = angle;
		bipedRightArm.rotateAngleX += rightArmRotation * 1.2F - leftARmRotation * 0.4F;
		bipedLeftArm.rotateAngleX += rightArmRotation * 1.2F - leftARmRotation * 0.4F;
		bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
	}

}
