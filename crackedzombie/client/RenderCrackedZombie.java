/*
 * RenderCrackedZombie.java
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderCrackedZombie extends RenderBiped<EntityCrackedZombie> {

    private static final ResourceLocation zombieSkin = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation zombieVillagerSkin = new ResourceLocation("textures/entity/zombie/zombie_villager.png");

    public RenderCrackedZombie(RenderManager rm) {
        super(rm, new ModelCrackedZombie(), 0.5F);
        LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
            protected void initArmor() {
                modelLeggings = new ModelCrackedZombie(0.5F, true);
                modelArmor = new ModelCrackedZombie(1.0F, true);
            }
        };
        addLayer(layerbipedarmor);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCrackedZombie zombie) {
        return zombie.isVillager() ? zombieVillagerSkin : zombieSkin;
    }

}
