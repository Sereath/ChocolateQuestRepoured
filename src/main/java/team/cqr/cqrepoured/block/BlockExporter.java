package team.cqr.cqrepoured.block;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.cqr.cqrepoured.client.gui.ScreenExporter;
import team.cqr.cqrepoured.init.CQRBlocks;
import team.cqr.cqrepoured.tileentity.TileEntityExporter;

public class BlockExporter extends Block {

	private static final ITextComponent SCREEN_TITLE = new TranslationTextComponent("tile.exporter.name");

	public BlockExporter() {
		super(Properties.of(Material.STONE)
				.strength(-1.0F, 3600000.0F)
				.noDrops()
				.isValidSpawn(CQRBlocks::never));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityExporter();
	}

	@Override
	public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit) {
		if (!pPlayer.isCreative()) {
			return ActionResultType.PASS;
		}
		TileEntity tileEntity = pLevel.getBlockEntity(pPos);
		if (!(tileEntity instanceof TileEntityExporter)) {
			return ActionResultType.FAIL;
		}
		if (pLevel.isClientSide) {
			this.openScreen((TileEntityExporter) tileEntity);
		}
		return ActionResultType.SUCCESS;
	}

	@OnlyIn(Dist.CLIENT)
	private void openScreen(TileEntityExporter tileEntity) {
		Minecraft.getInstance().setScreen(new ScreenExporter(SCREEN_TITLE, tileEntity));
	}

}
