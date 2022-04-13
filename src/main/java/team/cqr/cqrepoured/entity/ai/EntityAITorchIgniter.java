package team.cqr.cqrepoured.entity.ai;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import team.cqr.cqrepoured.block.BlockUnlitTorch;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;
import team.cqr.cqrepoured.init.CQRBlocks;
import team.cqr.cqrepoured.util.BlockPosUtil;

public class EntityAITorchIgniter extends AbstractCQREntityAI<AbstractEntityCQR> {

	private static final int SEARCH_RADIUS_HORIZONTAL = 16;
	private static final int SEARCH_RADIUS_VERTICAL = 2;
	private static final double REACH_DISTANCE_SQ = 3.0D * 3.0D;
	private BlockPos nearestTorch = null;
	private int lastTickStarted = Integer.MIN_VALUE;

	public EntityAITorchIgniter(AbstractEntityCQR entity) {
		super(entity);
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		if (!this.entity.canIgniteTorch()) {
			return false;
		}
		if (!isFlintNSteel(entity.getHeldItemMainhand()) && !isFlintNSteel(entity.getHeldItemOffhand())) {
			return false;
		}

		if (this.random.nextInt(this.lastTickStarted + 60 >= this.entity.ticksExisted ? 5 : 20) == 0) {
			BlockPos pos = new BlockPos(this.entity);
			Vec3d vec = this.entity.getPositionEyes(1.0F);
			this.nearestTorch = BlockPosUtil.getNearest(this.world, pos.getX(), pos.getY() + (MathHelper.ceil(this.entity.height) >> 1), pos.getZ(), SEARCH_RADIUS_HORIZONTAL, SEARCH_RADIUS_VERTICAL, true, true, CQRBlocks.UNLIT_TORCH, (mutablePos, state) -> {
				RayTraceResult result = this.world.rayTraceBlocks(vec, new Vec3d(mutablePos.getX() + 0.5D, mutablePos.getY() + 0.5D, mutablePos.getZ() + 0.5D), false, true, false);
				return result == null || result.getBlockPos().equals(mutablePos);
			});
		}

		return this.nearestTorch != null;
	}

	private boolean isFlintNSteel(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() == Items.FLINT_AND_STEEL;
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (this.nearestTorch == null) {
			return false;
		}
		if (this.entity.ticksExisted % 10 == 0 && this.entity.world.getBlockState(this.nearestTorch).getBlock() != CQRBlocks.UNLIT_TORCH) {
			return false;
		}
		return this.entity.hasPath();
	}

	@Override
	public void startExecuting() {
		if (this.entity.getDistanceSqToCenter(this.nearestTorch) > REACH_DISTANCE_SQ) {
			this.entity.getNavigator().tryMoveToXYZ(this.nearestTorch.getX(), this.nearestTorch.getY(), this.nearestTorch.getZ(), 1.0D);
		}
	}

	@Override
	public void resetTask() {
		this.nearestTorch = null;
		this.entity.getNavigator().clearPath();
	}

	@Override
	public void updateTask() {
		if (this.entity.getDistanceSqToCenter(this.nearestTorch) <= REACH_DISTANCE_SQ) {
			IBlockState state = this.entity.world.getBlockState(this.nearestTorch);
			if (state.getBlock() == CQRBlocks.UNLIT_TORCH) {
				BlockUnlitTorch.lightUp(this.entity.world, this.nearestTorch, state.getValue(BlockTorch.FACING));
			}
			this.nearestTorch = null;
		}
	}

}
