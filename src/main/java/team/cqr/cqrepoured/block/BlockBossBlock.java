package team.cqr.cqrepoured.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.level.block.state.BlockState;
import team.cqr.cqrepoured.inventory.ContainerBossBlock;
import team.cqr.cqrepoured.tileentity.BlockEntityContainer;
import team.cqr.cqrepoured.tileentity.TileEntityBoss;

public class BlockBossBlock extends Block {

	private static final ITextComponent CONTAINER_TITLE = new TranslationTextComponent("container.boss_block");

	public BlockBossBlock() {
		super(Properties.of(Material.STONE)
				.noDrops()
				.strength(-1.0F, 3600000.0F)
				.sound(SoundType.METAL)
				.noOcclusion());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityBoss();
	}

	@Override
	public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit) {
		if (!pPlayer.isCreative()) {
			return ActionResultType.PASS;
		}
		if (!pLevel.isClientSide) {
			pPlayer.openMenu(this.getMenuProvider(pState, pLevel, pPos));
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	@Nullable
	public INamedContainerProvider getMenuProvider(BlockState pState, World pLevel, BlockPos pPos) {
		TileEntity tileEntity = pLevel.getBlockEntity(pPos);
		if (!(tileEntity instanceof BlockEntityContainer)) {
			return null;
		}
		return new SimpleNamedContainerProvider((id, playerInv, player) -> {
			return new ContainerBossBlock(id, playerInv, ((BlockEntityContainer) tileEntity).getInventory());
		}, CONTAINER_TITLE);
	}

}
