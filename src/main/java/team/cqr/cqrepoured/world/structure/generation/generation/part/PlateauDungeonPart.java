package team.cqr.cqrepoured.world.structure.generation.generation.part;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import team.cqr.cqrepoured.util.BlockPlacingHelper;
import team.cqr.cqrepoured.util.Perlin3D;
import team.cqr.cqrepoured.world.structure.generation.generation.DungeonPlacement;
import team.cqr.cqrepoured.world.structure.generation.generation.GeneratableDungeon;
import team.cqr.cqrepoured.world.structure.generation.generation.part.IDungeonPart.Registry.ISerializer;
import team.cqr.cqrepoured.world.structure.generation.generation.preparable.PreparableEmptyInfo;
import team.cqr.cqrepoured.world.structure.generation.generation.preparable.PreparablePosInfo;
import team.cqr.cqrepoured.world.structure.generation.structurefile.CQStructure;

public class PlateauDungeonPart implements IDungeonPart {

	private static final MutableBlockPos MUTABLE = new MutableBlockPos();
	private final long seed;
	private final int startX;
	private final int startZ;
	private final int endX;
	private final int endY;
	private final int endZ;
	private final int wallSize;
	private final IBlockState supportHillBlock;
	private final IBlockState supportHillTopBlock;
	private final Perlin3D perlin1;
	private final Perlin3D perlin2;
	private int chunkX;
	private int chunkZ;
	private int chunkX1;
	private int chunkZ1;
	private boolean generated;
	private final int[][] ground;

	protected PlateauDungeonPart(long seed, int startX, int startZ, int endX, int endY, int endZ, int wallSize, @Nullable IBlockState supportHillBlock,
			@Nullable IBlockState supportHillTopBlock, int[][] ground) {
		this.seed = seed;
		this.startX = startX;
		this.startZ = startZ;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
		this.wallSize = wallSize;
		this.supportHillBlock = supportHillBlock;
		this.supportHillTopBlock = supportHillTopBlock;
		this.perlin1 = new Perlin3D(seed, wallSize);
		this.perlin2 = new Perlin3D(seed, wallSize * 4);
		this.chunkX = this.startX >> 4;
		this.chunkZ = this.startZ >> 4;
		this.chunkX1 = this.startX >> 4;
		this.chunkZ1 = this.startZ >> 4;
		this.ground = ground;
	}

	@Override
	public void generate(World world, GeneratableDungeon dungeon) {
		for (int x = this.startX - this.wallSize; x <= this.endX + this.wallSize; x++) {
			for (int z = this.startZ - this.wallSize; z <= this.endZ + this.wallSize; z++) {
				MUTABLE.setPos(x, 0, z);
				IBlockState state1 = this.supportHillBlock;
				IBlockState state2 = this.supportHillTopBlock;
				if (state1 == null || state2 == null) {
					Biome biome = world.getBiome(MUTABLE);
					if (state1 == null) {
						state1 = biome.fillerBlock;
					}
					if (state2 == null) {
						state2 = biome.topBlock;
					}
				}

				int y = getHeight(world, x, this.endY + 1, z);
				int end = this.interpolatedHeight(x, y, z);

				MUTABLE.setY(y);
				while (MUTABLE.getY() < end - 1) {
					BlockPlacingHelper.setBlockState(world, MUTABLE, state1, null, 16, false);
					dungeon.mark(MUTABLE.getX() >> 4, MUTABLE.getY() >> 4, MUTABLE.getZ() >> 4);
					MUTABLE.setY(MUTABLE.getY() + 1);
				}
				if (MUTABLE.getY() < end) {
					BlockPlacingHelper.setBlockState(world, MUTABLE, state2, null, 16, false);
					dungeon.mark(MUTABLE.getX() >> 4, MUTABLE.getY() >> 4, MUTABLE.getZ() >> 4);
				}
			}
		}
		this.generated = true;
	}

	private int interpolatedHeight(int x, int y, int z) {
		int max = y;
		int r = this.wallSize + 1;
		for (int x1 = -r; x1 <= r; x1++) {
			if (x + x1 < this.startX || x + x1 > this.endX) {
				continue;
			}
			for (int z1 = -r; z1 <= r; z1++) {
				if (z + z1 < this.startZ || z + z1 > this.endZ) {
					continue;
				}
				double dist = Math.sqrt(x1 * x1 + z1 * z1);
				int y1 = (int) Math.round(y + (this.ground[x + x1 - this.startX][z + z1 - this.startZ] - y) * Math.max((1 - dist / this.wallSize), 0));
				if (y1 > max) {
					max = y1;
				}
			}
		}
		return max;
	}

	private static boolean isGround(World world, Chunk chunk, BlockPos pos) {
		IBlockState state = chunk.getBlockState(pos);
		Material material = state.getMaterial();
		return material.blocksMovement() && material != Material.WOOD && material != Material.LEAVES && material != Material.PLANTS;
	}

	private static int getHeight(World world, int x, int y, int z) {
		Chunk chunk = world.getChunk(x >> 4, z >> 4);
		MUTABLE.setPos(x, y, z);
		boolean upwards = isGround(world, chunk, MUTABLE);

		while (true) {
			boolean isGround = isGround(world, chunk, MUTABLE);
			if (upwards) {
				if (!isGround) {
					return MUTABLE.getY();
				}
				MUTABLE.setY(MUTABLE.getY() + 1);
			} else {
				if (isGround) {
					return MUTABLE.getY() + 1;
				}
				MUTABLE.setY(MUTABLE.getY() - 1);
			}
		}
	}

	@Override
	public boolean isGenerated() {
		return this.generated;
	}

	public int getStartX() {
		return this.startX;
	}

	public int getStartZ() {
		return this.startZ;
	}

	public int getEndX() {
		return this.endX;
	}

	public int getEndY() {
		return this.endY;
	}

	public int getEndZ() {
		return this.endZ;
	}

	public static class Builder implements IDungeonPartBuilder {

		private final int startX;
		private final int startZ;
		private final int endX;
		private final int endY;
		private final int endZ;
		private final int wallSize;
		private IBlockState supportHillBlock;
		private IBlockState supportHillTopBlock;
		private final int[][] ground;

		public Builder(int startX, int startZ, int endX, int endY, int endZ, int wallSize) {
			this.startX = Math.min(startX, endX);
			this.startZ = Math.min(startZ, endZ);
			this.endX = Math.max(startX, endX);
			this.endY = endY;
			this.endZ = Math.max(startZ, endZ);
			this.wallSize = wallSize;
			this.ground = new int[endX - startX + 1][endZ - startZ + 1];
			for (int i = 0; i < this.ground.length; i++) {
				for (int j = 0; j < this.ground[i].length; j++) {
					this.ground[i][j] = endY;
				}
			}
		}

		public Builder setSupportHillBlock(@Nullable IBlockState state) {
			this.supportHillBlock = state;
			return this;
		}

		public Builder setSupportHillTopBlock(@Nullable IBlockState state) {
			this.supportHillTopBlock = state;
			return this;
		}

		public void markGround(CQStructure structure, BlockPos pos) {
			List<PreparablePosInfo> blocks = structure.getBlockInfoList();
			BlockPos size = structure.getSize();
			for (int x = 0; x < structure.getSize().getX(); x++) {
				if (x + pos.getX() < this.startX || x + pos.getX() > this.endX) {
					continue;
				}
				for (int z = 0; z < structure.getSize().getZ(); z++) {
					if (z + pos.getZ() < this.startZ || z + pos.getZ() > this.endZ) {
						continue;
					}
					int y = this.endY + 1 - pos.getY();
					while (y >= 0 && blocks.get((x * size.getY() + y) * size.getZ() + z) instanceof PreparableEmptyInfo) {
						y--;
					}
					if (y < 0) {
						this.ground[x][z] = -1;
					} else {
						this.ground[x][z] = Math.min(pos.getY() + y + 2, this.endY + 1);
					}
				}
			}
		}

		@Override
		public PlateauDungeonPart build(World world, DungeonPlacement placement) {
			return new PlateauDungeonPart(world.getSeed(), this.startX, this.startZ, this.endX, this.endY, this.endZ, this.wallSize, this.supportHillBlock,
					this.supportHillTopBlock, this.ground);
		}

	}

	public static class Serializer implements ISerializer<PlateauDungeonPart> {

		@Override
		public NBTTagCompound write(PlateauDungeonPart part, NBTTagCompound compound) {
			compound.setLong("seed", part.seed);
			compound.setInteger("startX", part.startX);
			compound.setInteger("startZ", part.startZ);
			compound.setInteger("endX", part.endX);
			compound.setInteger("endY", part.endY);
			compound.setInteger("endZ", part.endZ);
			compound.setInteger("wallSize", part.wallSize);
			compound.setTag("supportHillBlock", NBTUtil.writeBlockState(new NBTTagCompound(), part.supportHillBlock));
			compound.setTag("supportHillTopBlock", NBTUtil.writeBlockState(new NBTTagCompound(), part.supportHillTopBlock));
			compound.setInteger("chunkX", part.chunkX);
			compound.setInteger("chunkZ", part.chunkZ);
			compound.setInteger("chunkX1", part.chunkX1);
			compound.setInteger("chunkZ1", part.chunkZ1);
			return compound;
		}

		@Override
		public PlateauDungeonPart read(World world, NBTTagCompound compound) {
			long seed = compound.getLong("seed");
			int startX = compound.getInteger("startX");
			int startZ = compound.getInteger("startZ");
			int endX = compound.getInteger("endX");
			int endY = compound.getInteger("endY");
			int endZ = compound.getInteger("endZ");
			int wallSize = compound.getInteger("wallSize");
			IBlockState supportHillBlock = NBTUtil.readBlockState(compound.getCompoundTag("supportHillBlock"));
			IBlockState supportHillTopBlock = NBTUtil.readBlockState(compound.getCompoundTag("supportHillTopBlock"));
			int chunkX = compound.getInteger("chunkX");
			int chunkZ = compound.getInteger("chunkZ");
			int chunkX1 = compound.getInteger("chunkX1");
			int chunkZ1 = compound.getInteger("chunkZ1");
			PlateauDungeonPart part = new PlateauDungeonPart(seed, startX, startZ, endX, endY, endZ, wallSize, supportHillBlock, supportHillTopBlock, null);
			part.chunkX = chunkX;
			part.chunkZ = chunkZ;
			part.chunkX1 = chunkX1;
			part.chunkZ1 = chunkZ1;
			return part;
		}

	}

}
