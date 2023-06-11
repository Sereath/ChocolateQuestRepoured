package team.cqr.cqrepoured.client.render.entity.mobs;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.processor.IBone;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.model.geo.entity.humanoid.ModelCQRMandrilGeo;
import team.cqr.cqrepoured.client.render.entity.RenderCQRBipedBaseGeo;
import team.cqr.cqrepoured.entity.mobs.EntityCQRMandril;

public class RenderCQRMandril extends RenderCQRBipedBaseGeo<EntityCQRMandril> {
	
	private static final ResourceLocation TEXTURE = CQRMain.prefix("textures/entity/mob/mandril.png");

	public RenderCQRMandril(Context rendermanagerIn) {
		super(rendermanagerIn, new ModelCQRMandrilGeo(CQRMain.prefix("geo/entity/biped_mandril.geo.json"), TEXTURE, "mob/mandril"));
	}

	@Override
	protected void calculateArmorStuffForBone(String boneName, EntityCQRMandril currentEntity) {
		standardArmorCalculationForBone(boneName, currentEntity);
	}

	@Override
	protected void calculateItemStuffForBone(String boneName, EntityCQRMandril currentEntity) {
		standardItemCalculationForBone(boneName, currentEntity);
	}

	@Override
	protected BlockState getHeldBlockForBone(String boneName, EntityCQRMandril currentEntity) {
		return null;
	}

	@Override
	protected void preRenderBlock(PoseStack stack, BlockState block, String boneName, EntityCQRMandril currentEntity) {
		
	}

	@Override
	protected void postRenderItem(PoseStack matrixStack, ItemStack item, String boneName, EntityCQRMandril currentEntity, IBone bone) {
		
	}

	@Override
	protected void postRenderBlock(PoseStack stack, BlockState block, String boneName, EntityCQRMandril currentEntity) {
		
	}

}
