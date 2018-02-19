package com.crackedzombie.client;

import com.crackedzombie.common.EntityCrackedZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class RenderCrackedHusk extends RenderCrackedZombie {

    private static final ResourceLocation HUSK_ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/husk.png");

    public RenderCrackedHusk(RenderManager rm) {
        super(rm);
    }

    protected void preRenderCallback(EntityCrackedZombie entitylivingbaseIn, float partialTickTime) {
        float f = 1.0625F;
        GlStateManager.scale(1.0625F, 1.0625F, 1.0625F);
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }

    protected ResourceLocation getEntityTexture(EntityCrackedZombie entity) {
        return HUSK_ZOMBIE_TEXTURES;
    }
}
