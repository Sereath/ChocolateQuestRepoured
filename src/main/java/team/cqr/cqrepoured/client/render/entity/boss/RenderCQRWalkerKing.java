package team.cqr.cqrepoured.client.render.entity.boss;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib3.core.processor.IBone;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.model.geo.entity.humanoid.boss.ModelWalkerKingGeo;
import team.cqr.cqrepoured.client.render.entity.RenderCQRBipedBaseGeo;
import team.cqr.cqrepoured.client.render.entity.layer.geo.LayerBossDeathGeo;
import team.cqr.cqrepoured.client.render.entity.layer.geo.LayerGlowingAreasGeo;
import team.cqr.cqrepoured.client.render.texture.InvisibilityTexture;
import team.cqr.cqrepoured.entity.boss.EntityCQRWalkerKing;

public class RenderCQRWalkerKing extends RenderCQRBipedBaseGeo<EntityCQRWalkerKing> {
	
	public static final ResourceLocation TEXTURE_WALKER_KING_DEFAULT = CQRMain.prefix("textures/entity/boss/walker_king.png");

	public RenderCQRWalkerKing(EntityRendererProvider.Context rendermanagerIn) {
		super(rendermanagerIn, new ModelWalkerKingGeo<>(STANDARD_BIPED_GEO_MODEL, TEXTURE_WALKER_KING_DEFAULT, "boss/walker_king"));

		this.addLayer(new LayerGlowingAreasGeo<>(this, this.TEXTURE_GETTER, this.MODEL_ID_GETTER));
		this.addLayer(new LayerBossDeathGeo<>(this, this.TEXTURE_GETTER, this.MODEL_ID_GETTER, 191, 0, 255));
	}
	
	private boolean renderingDeath = false;
	private boolean renderingDeathSecondRenderCycle = false;
	
	@Override
	public void render(EntityCQRWalkerKing entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
		if(entity.deathTime > 0) {
			this.renderingDeath = true;
			
			super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
			
			this.renderingDeathSecondRenderCycle = true;
			super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
			this.renderingDeathSecondRenderCycle = false;
			
			
			this.renderingDeath = false;
		} else {
			super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
		}
	}
	
	@Override
	public RenderType getRenderType(EntityCQRWalkerKing animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		if (this.renderingDeath) {
			 float f2 = (float)animatable.deathTime / 200.0F;
			if(this.renderingDeathSecondRenderCycle) {
				return RenderType.entityDecal(this.getTextureResource(animatable));
			}
			return RenderType.dragonExplosionAlpha(InvisibilityTexture.get(this.getTextureResource(animatable)), f2);
		}
		return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
	}

	@Override
	protected float getDeathMaxRotation(EntityCQRWalkerKing entityLivingBaseIn) {
		return 0;
	}

	@Override
	protected void calculateArmorStuffForBone(String boneName, EntityCQRWalkerKing currentEntity) {
		standardArmorCalculationForBone(boneName, currentEntity);
	}

	@Override
	protected void calculateItemStuffForBone(String boneName, EntityCQRWalkerKing currentEntity) {
		standardItemCalculationForBone(boneName, currentEntity);
	}

	@Override
	protected BlockState getHeldBlockForBone(String boneName, EntityCQRWalkerKing currentEntity) {
		return null;
	}

	@Override
	protected void preRenderBlock(PoseStack stack, BlockState block, String boneName, EntityCQRWalkerKing currentEntity) {
		
	}

	@Override
	protected void postRenderItem(PoseStack matrixStack, ItemStack item, String boneName, EntityCQRWalkerKing currentEntity, IBone bone) {
		
	}

	@Override
	protected void postRenderBlock(PoseStack stack, BlockState block, String boneName, EntityCQRWalkerKing currentEntity) {
		
	}

}
