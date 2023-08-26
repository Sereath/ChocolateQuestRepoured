package team.cqr.cqrepoured.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import team.cqr.cqrepoured.CQRConstants;
import team.cqr.cqrepoured.client.model.entity.ModelBall;
import team.cqr.cqrepoured.entity.misc.EntityBubble;

public class RenderBubble extends EntityRenderer<EntityBubble> {

	protected final ResourceLocation TEXTURE = new ResourceLocation(CQRConstants.MODID, "textures/entity/bubble_entity.png");

	private final Model model;

	public RenderBubble(Context renderManager) {
		super(renderManager);
		this.model = new ModelBall();
	}

	@Override
	public ResourceLocation getTextureLocation(EntityBubble pEntity) {
		return this.TEXTURE;
	}
	
	@Override
	public Context getDispatcher() {
		return super.getDispatcher();
	}

	@Override
	public void render(EntityBubble entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
		matrixStack.pushPose();
		//RenderSystem.enableBlend();
		//RenderSystem.disableCull();
		//RenderSystem.enableAlphaTest(); //#TODO entity inside not reneerign 
		float scale = entity.getBbHeight() / 0.9F;
		matrixStack.scale(scale, -scale, scale);
		this.model.renderToBuffer(matrixStack, buffer.getBuffer(RenderType.entityTranslucent(TEXTURE, true)), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);

		matrixStack.popPose();
	}
}
