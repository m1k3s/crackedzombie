package com.crackedzombie.client;

import com.crackedzombie.common.EntityCrackedPigZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderCrackedPigZombie extends RenderBiped {

    private static final ResourceLocation resourceLocation = new ResourceLocation("textures/entity/zombie_pigman.png");

    public RenderCrackedPigZombie(RenderManager renderManager) {
        super(renderManager, new ModelCrackedZombie(), 0.5F, 1.0F);
        addLayer(new LayerHeldItem(this));
        addLayer(new LayerBipedArmor(this) {
            protected void func_177177_a() {
                field_177189_c = new ModelCrackedZombie(0.5F, true);
                field_177186_d = new ModelCrackedZombie(1.0F, true);
            }
        });
    }

    protected ResourceLocation func_177119_a(EntityCrackedPigZombie entityCrackedPigZombie) {
        return resourceLocation;
    }

    protected ResourceLocation getEntityTexture(EntityLiving entity) {
        return func_177119_a((EntityCrackedPigZombie) entity);
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return func_177119_a((EntityCrackedPigZombie) entity);
    }
}
