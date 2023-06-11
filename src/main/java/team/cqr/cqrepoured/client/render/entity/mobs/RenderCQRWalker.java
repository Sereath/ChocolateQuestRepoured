package team.cqr.cqrepoured.client.render.entity.mobs;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.core.processor.IBone;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.model.geo.entity.humanoid.ModelCQRWalkerGeo;
import team.cqr.cqrepoured.client.render.entity.RenderCQRBipedBaseGeo;
import team.cqr.cqrepoured.client.render.entity.layer.geo.LayerGlowingAreasGeo;
import team.cqr.cqrepoured.entity.mobs.EntityCQRWalker;

public class RenderCQRWalker extends RenderCQRBipedBaseGeo<EntityCQRWalker> {

	private static final ResourceLocation TEXTURE = CQRMain.prefix("textures/entity/mob/walker.png");
	
	public RenderCQRWalker(Context renderManager) {
		super(renderManager, new ModelCQRWalkerGeo(STANDARD_BIPED_GEO_MODEL, TEXTURE, "mob/walker"));
		
		this.addLayer(new LayerGlowingAreasGeo<>(this, this.TEXTURE_GETTER, this.MODEL_ID_GETTER));
	}

	@Override
	protected void calculateArmorStuffForBone(String boneName, EntityCQRWalker currentEntity) {
		standardArmorCalculationForBone(boneName, currentEntity);
	}

	@Override
	protected void calculateItemStuffForBone(String boneName, EntityCQRWalker currentEntity) {
		standardItemCalculationForBone(boneName, currentEntity);
	}

	@Override
	protected BlockState getHeldBlockForBone(String boneName, EntityCQRWalker currentEntity) {
		return null;
	}

	@Override
	protected void preRenderBlock(PoseStack stack, BlockState block, String boneName, EntityCQRWalker currentEntity) {
		
	}

	@Override
	protected void postRenderBlock(PoseStack stack, BlockState block, String boneName, EntityCQRWalker currentEntity) {
		
	}

	@Override
	protected void postRenderItem(PoseStack matrixStack, ItemStack item, String boneName, EntityCQRWalker currentEntity, IBone bone) {
		
	}


}
