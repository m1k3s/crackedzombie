package com.crackedzombie.client;

import com.crackedzombie.common.EntityCrackedPigZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;

public class RenderCrackedPigZombie extends RenderBiped<EntityCrackedPigZombie> {

    private static final ResourceLocation crackedPigZombieSkin = new ResourceLocation("textures/entity/zombie_pigman.png");

    public RenderCrackedPigZombie(RenderManager renderManager) {
        super(renderManager, new ModelCrackedZombie(), 0.5F, 1.0F);
        addLayer(new LayerHeldItem(this));
        addLayer(new LayerBipedArmor(this) {
            @SuppressWarnings("unused")
            protected void func_177177_a() {
                modelLeggings = new ModelCrackedZombie(0.5F, true);
                modelArmor = new ModelCrackedZombie(1.0F, true);
            }
        });
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCrackedPigZombie zombie)
    {
        return crackedPigZombieSkin;
    }



}
