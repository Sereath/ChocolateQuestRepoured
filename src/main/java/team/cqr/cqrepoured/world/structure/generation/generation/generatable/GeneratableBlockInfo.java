package team.cqr.cqrepoured.world.structure.generation.generation.generatable;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import team.cqr.cqrepoured.util.BlockPlacingHelper;
import team.cqr.cqrepoured.world.structure.generation.generation.GeneratableDungeon;

public class GeneratableBlockInfo extends GeneratablePosInfo {

	private final IBlockState state;
	@Nullable
	private final TileEntity tileEntity;

	public GeneratableBlockInfo(int x, int y, int z, IBlockState state, @Nullable TileEntity tileEntity) {
		super(x, y, z);
		this.state = state;
		this.tileEntity = tileEntity;
	}

	public GeneratableBlockInfo(BlockPos pos, IBlockState state, @Nullable TileEntity tileEntity) {
		this(pos.getX(), pos.getY(), pos.getZ(), state, tileEntity);
	}

	@Override
	protected boolean place(World world, Chunk chunk, ExtendedBlockStorage blockStorage, BlockPos pos, GeneratableDungeon dungeon) {
		return BlockPlacingHelper.setBlockState(world, chunk, blockStorage, pos, this.state, this.tileEntity, 16, dungeon);
	}

	public IBlockState getState() {
		return this.state;
	}

	@Nullable
	public TileEntity getTileEntity() {
		return this.tileEntity;
	}

}
