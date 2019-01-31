package com.teamcqr.chocolatequestrepoured.objects.entity.render;

import com.teamcqr.chocolatequestrepoured.objects.entity.projectiles.ProjectileBullet;
import com.teamcqr.chocolatequestrepoured.util.Reference;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderProjectileBullet extends Render<ProjectileBullet>
{
	private final float scale;
	public ResourceLocation IRON = new ResourceLocation(Reference.MODID ,"textures/entity/bullet_iron_single.png");
	public ResourceLocation GOLD = new ResourceLocation(Reference.MODID ,"textures/entity/bullet_gold_single.png");
	public ResourceLocation DIAMOND = new ResourceLocation(Reference.MODID ,"textures/entity/bullet_diamond_single.png");
	public ResourceLocation FIRE = new ResourceLocation(Reference.MODID ,"textures/entity/bullet_fire_single.png");
			
	public RenderProjectileBullet(RenderManager renderManager, float scaleIn) 
	{
		super(renderManager);
		this.scale = scaleIn;
	}
	
	@Override
	public void doRender(ProjectileBullet entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
    	GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(.5F, .5F, .5F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        float f = 1.0F;
        float f1 = 0.5F;
        float f2 = 0.25F;
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        if(this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();

        if(this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

	@Override
	protected ResourceLocation getEntityTexture(ProjectileBullet entity) 
	{
		int type = entity.getType();
		
		if(type == 1)
		{
			return IRON;
		}
		
		if(type == 2)
		{
			return GOLD;
		}
		
		if(type == 3)
		{
			return DIAMOND;
		}
		
		if(type == 4)
		{
			return FIRE;
		}
		else
		{
			System.out.println("IT'S A BUG!!!! IF YOU SEE THIS REPORT IT TO MOD'S AUTHOR");
			return null; //#SHOULD NEVER HAPPEN
		}
	}
}