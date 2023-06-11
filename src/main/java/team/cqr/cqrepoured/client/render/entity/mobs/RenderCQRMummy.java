package team.cqr.cqrepoured.client.render.entity.mobs;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.processor.IBone;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.model.geo.entity.humanoid.ModelCQRMummyGeo;
import team.cqr.cqrepoured.client.render.entity.RenderCQRBipedBaseGeo;
import team.cqr.cqrepoured.entity.mobs.EntityCQRMummy;

public class RenderCQRMummy extends RenderCQRBipedBaseGeo<EntityCQRMummy> {
	
	private static final ResourceLocation TEXTURE = CQRMain.prefix("textures/entity/mob/mummy.png");

	public RenderCQRMummy(EntityRendererManager rendermanagerIn) {
		super(rendermanagerIn, new ModelCQRMummyGeo(STANDARD_BIPED_GEO_MODEL, TEXTURE, "mob/mummy"));
	}

	@Override
	protected void calculateArmorStuffForBone(String boneName, EntityCQRMummy currentEntity) {
		standardArmorCalculationForBone(boneName, currentEntity);
	}

	@Override
	protected void calculateItemStuffForBone(String boneName, EntityCQRMummy currentEntity) {
		standardItemCalculationForBone(boneName, currentEntity);
	}

	@Override
	protected BlockState getHeldBlockForBone(String boneName, EntityCQRMummy currentEntity) {
		return null;
	}

	@Override
	protected void preRenderBlock(MatrixStack stack, BlockState block, String boneName, EntityCQRMummy currentEntity) {
		
	}

	@Override
	protected void postRenderBlock(MatrixStack stack, BlockState block, String boneName, EntityCQRMummy currentEntity) {
		
	}

	@Override
	protected void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, EntityCQRMummy currentEntity, IBone bone) {
		
	}

}
